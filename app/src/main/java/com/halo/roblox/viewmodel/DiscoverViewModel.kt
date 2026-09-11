package com.halo.roblox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halo.roblox.data.model.Game
import com.halo.roblox.data.repository.GamesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class DiscoverState(
    val results   : List<Game> = emptyList(),
    val query     : String     = "",
    val isLoading : Boolean    = false,
    val error     : String?    = null
)

class DiscoverViewModel(app: Application) : AndroidViewModel(app) {

    private val repo   = GamesRepository()
    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init { loadDefault() }

    private fun loadDefault() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repo.getPopularGames().onSuccess { games ->
                _state.update { it.copy(results = games, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        searchJob?.cancel()
        if (q.isBlank()) { loadDefault(); return }

        searchJob = viewModelScope.launch {
            delay(400) // debounce
            _state.update { it.copy(isLoading = true, error = null) }
            repo.searchGames(q).onSuccess { games ->
                _state.update { it.copy(results = games, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
