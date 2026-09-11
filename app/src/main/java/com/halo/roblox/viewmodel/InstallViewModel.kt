package com.halo.roblox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halo.roblox.util.ApkManager
import com.halo.roblox.util.GameLauncher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class InstallState {
    object Idle        : InstallState()
    object Downloading : InstallState()
    object Installing  : InstallState()
    object Done        : InstallState()
    data class Error(val msg: String) : InstallState()
}

data class InstallUiState(
    val installed    : Boolean      = false,
    val version      : String       = "",
    val installState : InstallState = InstallState.Idle,
    val progress     : Float        = 0f
)

class InstallViewModel(app: Application) : AndroidViewModel(app) {

    private val apk    = ApkManager(app)
    private val _state = MutableStateFlow(InstallUiState())
    val state: StateFlow<InstallUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        val app  = getApplication<Application>()
        val inst = GameLauncher.isInstalled(app)
        _state.update { it.copy(
            installed    = inst,
            version      = if (inst) GameLauncher.installedVersion(app) else "",
            installState = InstallState.Idle
        )}
    }

    fun downloadAndInstall() {
        viewModelScope.launch {
            _state.update { it.copy(installState = InstallState.Downloading, progress = 0f) }
            apk.download(
                onProgress = { p -> _state.update { it.copy(progress = p) } },
                onSuccess  = { file ->
                    _state.update { it.copy(installState = InstallState.Installing) }
                    apk.install(file)
                    refresh()
                },
                onError = { msg ->
                    _state.update { it.copy(installState = InstallState.Error(msg)) }
                }
            )
        }
    }

    fun clearError() = _state.update { it.copy(installState = InstallState.Idle) }
}
