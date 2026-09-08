package com.halo.roblox.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halo.roblox.util.LaunchState
import com.halo.roblox.viewmodel.LauncherState
import com.halo.roblox.viewmodel.LauncherViewModel

@Composable
fun HomeScreen(vm: LauncherViewModel = viewModel()) {
    val state   by vm.state.collectAsStateWithLifecycle()
    val primary = MaterialTheme.colorScheme.primary
    val bg      = MaterialTheme.colorScheme.background

    val inf      = rememberInfiniteTransition(label = "blob")
    val blobShift by inf.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(7000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blobShift"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val r = size.minDimension * 0.75f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(
                            x = size.width  * (0.25f + blobShift * 0.5f),
                            y = size.height * (0.15f + blobShift * 0.25f)
                        ),
                        radius = r
                    ),
                    radius = r
                )
            }
            .background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))
            HeroHeader()
            Spacer(Modifier.height(36.dp))
            InfoCard(state = state)
            Spacer(Modifier.height(28.dp))
            ActionButton(state = state, onLaunch = { vm.onLaunch() })
            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = state.launchState is LaunchState.Error,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier              = Modifier.padding(16.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text     = "⚠️ ${state.errorMsg}",
                            style    = MaterialTheme.typography.bodyLarge,
                            color    = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { vm.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))
            Text(
                text      = "☠️ Halo v1.0.0 • Powered by Delta",
                style     = MaterialTheme.typography.labelLarge,
                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroHeader() {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .alpha(anim.value)
            .scale(0.75f + anim.value * 0.25f)
    ) {
        Text("☠️🥀", fontSize = 60.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text          = "HALO",
            style         = MaterialTheme.typography.displayLarge,
            color         = MaterialTheme.colorScheme.primary,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 8.sp
        )
        Text(
            text  = "Roblox Launcher",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun InfoCard(state: LauncherState) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text       = "Roblox",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "Version: ${state.version}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }

                val chipColor = if (state.installed) Color(0xFF4CAF50) else Color(0xFFFF5449)
                val chipLabel = if (state.installed) "Installed" else "Not Installed"

                Surface(
                    shape  = CircleShape,
                    color  = chipColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, chipColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(Modifier.size(8.dp).background(chipColor, CircleShape))
                        Text(
                            text       = chipLabel,
                            style      = MaterialTheme.typography.labelLarge,
                            color      = chipColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            AnimatedVisibility(visible = state.launchState is LaunchState.Downloading) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text  = "Downloading… ${(state.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(state: LauncherState, onLaunch: () -> Unit) {
    val busy = state.launchState !is LaunchState.Idle &&
               state.launchState !is LaunchState.Error

    val scale by animateFloatAsState(
        targetValue   = if (busy) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label         = "btnScale"
    )

    val label = when (state.launchState) {
        is LaunchState.Downloading -> "Downloading…"
        is LaunchState.Installing  -> "Installing…"
        is LaunchState.Launching   -> "Launching…"
        else -> if (state.installed) "Launch Roblox" else "Download & Install"
    }

    Button(
        onClick  = { if (!busy) onLaunch() },
        enabled  = !busy,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale),
        shape  = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor         = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        AnimatedContent(
            targetState  = label,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label        = "btnContent"
        ) { txt ->
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (busy) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color       = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text       = txt,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
