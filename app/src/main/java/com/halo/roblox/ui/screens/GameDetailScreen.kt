package com.halo.roblox.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.halo.roblox.util.GameLauncher
import com.halo.roblox.viewmodel.GameDetailViewModel
import com.halo.roblox.viewmodel.InstallState
import com.halo.roblox.viewmodel.InstallViewModel

@Composable
fun GameDetailScreen(
    universeId : Long,
    placeId    : Long,
    onBack     : () -> Unit,
    detailVm   : GameDetailViewModel = viewModel(),
    installVm  : InstallViewModel    = viewModel()
) {
    val ctx          = LocalContext.current
    val detailState  by detailVm.state.collectAsStateWithLifecycle()
    val installState by installVm.state.collectAsStateWithLifecycle()

    LaunchedEffect(universeId) { detailVm.load(universeId) }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            detailState.isLoading -> LoadingScreen()
            detailState.error != null && detailState.game == null ->
                ErrorScreen(msg = detailState.error!!, onRetry = { detailVm.load(universeId) })
            detailState.game != null -> {
                val game = detailState.game!!

                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                    // Hero thumbnail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        if (game.thumbnailUrl != null) {
                            AsyncImage(
                                model              = game.thumbnailUrl,
                                contentDescription = game.name,
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                Modifier.fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                        // Gradient bottom fade
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        ),
                                        startY = 300f
                                    )
                                )
                        )
                        // Back button
                        IconButton(
                            onClick  = onBack,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.45f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                        // Title
                        Text(
                            text       = game.name,
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = "by ${game.creatorName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                        )

                        Spacer(Modifier.height(16.dp))

                        // Stats row
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatChip(icon = Icons.Default.Person,    label = game.formattedPlayers)
                            StatChip(icon = Icons.Default.Visibility, label = game.formattedVisits)
                            if (game.genre?.isNotBlank() == true) {
                                StatChip(icon = Icons.Default.Category, label = game.genre)
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Play / Install button
                        if (!installState.installed) {
                            InstallSection(
                                state     = installState.installState,
                                progress  = installState.progress,
                                onInstall = { installVm.downloadAndInstall() },
                                onDismiss = { installVm.clearError() }
                            )
                        } else {
                            Button(
                                onClick  = { GameLauncher.launchGame(ctx, placeId) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Play Now",
                                    style      = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Description
                        if (!game.description.isNullOrBlank()) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text       = "About",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text  = game.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                            )
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                modifier           = Modifier.size(14.dp),
                tint               = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InstallSection(
    state     : InstallState,
    progress  : Float,
    onInstall : () -> Unit,
    onDismiss : () -> Unit
) {
    when (state) {
        is InstallState.Idle -> {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Roblox not installed",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Download and install to play this game.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick  = onInstall,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.InstallMobile, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Install Roblox")
                    }
                }
            }
        }
        is InstallState.Extracting -> {
            Column {
                Text(
                    "Preparing Roblox… ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                )
            }
        }
        is InstallState.Installing -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(12.dp))
                Text("Installing…", style = MaterialTheme.typography.labelLarge)
            }
        }
        is InstallState.Done -> {
            Text("Installed! Tap Play to launch.", color = MaterialTheme.colorScheme.primary)
        }
        is InstallState.Error -> {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier          = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⚠️ ${state.msg}",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismiss) { Text("Retry") }
                }
            }
        }
    }
}
