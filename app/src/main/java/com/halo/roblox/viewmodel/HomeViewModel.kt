package com.halo.roblox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halo.roblox.data.model.Game
import com.halo.roblox.data.repository.GamesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val featured  : List<Game> = emptyList(),
    val popular   : List<Game> = emptyList(),
    val isLoading : Boolean    = true,
    val error     : String?    = null
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo   = GamesRepository()
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Parallel fetch
                val featuredDeferred = async { repo.getTopGames() }
                val popularDeferred  = async { repo.getPopularGames() }

                val featured = featuredDeferred.await().getOrElse { emptyList() }
                val popular  = popularDeferred.await().getOrThrow()

                _state.update {
                    it.copy(
                        featured  = featured,
                        popular   = popular,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error     = e.message ?: "Failed to load games"
                    )
                }
            }
        }
    }
}
