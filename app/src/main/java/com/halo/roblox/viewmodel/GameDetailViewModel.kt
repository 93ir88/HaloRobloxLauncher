package com.halo.roblox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halo.roblox.data.model.Game
import com.halo.roblox.data.repository.GamesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GameDetailState(
    val game      : Game?   = null,
    val isLoading : Boolean = true,
    val error     : String? = null
)

class GameDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repo   = GamesRepository()
    private val _state = MutableStateFlow(GameDetailState())
    val state: StateFlow<GameDetailState> = _state.asStateFlow()

    fun load(universeId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getGameDetail(universeId).onSuccess { game ->
                _state.update { it.copy(game = game, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
