package com.halo.roblox.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var trigger by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue   = if (trigger) 1f else 0.5f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label         = "scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue   = if (trigger) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "alpha"
    )
    val subAlpha by animateFloatAsState(
        targetValue   = if (trigger) 1f else 0f,
        animationSpec = tween(700, delayMillis = 400, easing = EaseOutCubic),
        label         = "subAlpha"
    )

    LaunchedEffect(Unit) {
        trigger = true
        delay(2200)
        onFinished()
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("☠️", fontSize = 76.sp,
                modifier = Modifier.scale(logoScale).alpha(logoAlpha))
            Text(
                text          = "HALO",
                style         = MaterialTheme.typography.displayLarge,
                color         = MaterialTheme.colorScheme.primary,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 10.sp,
                modifier      = Modifier.scale(logoScale).alpha(logoAlpha)
            )
            Text(
                text      = "Roblox Launcher",
                style     = MaterialTheme.typography.titleLarge,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(subAlpha)
            )
        }
    }
}
