package com.halo.roblox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.halo.roblox.ui.navigation.HaloNavHost
import com.halo.roblox.ui.screens.SplashScreen
import com.halo.roblox.ui.theme.HaloRobloxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaloRobloxTheme {
                var splashDone by remember { mutableStateOf(false) }
                if (!splashDone) {
                    SplashScreen(onFinished = { splashDone = true })
                } else {
                    HaloNavHost()
                }
            }
        }
    }
}
