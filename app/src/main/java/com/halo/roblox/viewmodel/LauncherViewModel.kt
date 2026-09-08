package com.halo.roblox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halo.roblox.util.ApkManager
import com.halo.roblox.util.LaunchState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LauncherState(
    val launchState : LaunchState = LaunchState.Idle,
    val progress    : Float       = 0f,
    val installed   : Boolean     = false,
    val version     : String      = "Not installed",
    val errorMsg    : String?     = null
)

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val apk    = ApkManager(app)
    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        val inst = apk.isInstalled()
        _state.update {
            it.copy(
                installed   = inst,
                version     = if (inst) apk.installedVersion() else "Not installed",
                launchState = LaunchState.Idle
            )
        }
    }

    fun onLaunch() {
        viewModelScope.launch {
            if (apk.isInstalled()) {
                _state.update { it.copy(launchState = LaunchState.Launching) }
                apk.launch()
                _state.update { it.copy(launchState = LaunchState.Idle) }
            } else {
                download()
            }
        }
    }

    private suspend fun download() {
        _state.update { it.copy(launchState = LaunchState.Downloading, progress = 0f) }
        apk.download(
            onProgress = { p -> _state.update { it.copy(progress = p) } },
            onSuccess  = { file ->
                _state.update { it.copy(launchState = LaunchState.Installing) }
                apk.install(file)
                refresh()
            },
            onError = { msg ->
                _state.update { it.copy(launchState = LaunchState.Error, errorMsg = msg) }
            }
        )
    }

    fun clearError() = _state.update {
        it.copy(launchState = LaunchState.Idle, errorMsg = null)
    }
}
