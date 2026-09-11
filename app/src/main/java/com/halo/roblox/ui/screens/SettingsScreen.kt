package com.halo.roblox.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halo.roblox.viewmodel.InstallState
import com.halo.roblox.viewmodel.InstallViewModel

@Composable
fun SettingsScreen(vm: InstallViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        Text(
            text       = "Settings",
            style      = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(24.dp))

        // Roblox install card
        ElevatedCard(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Roblox Runtime", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        if (state.installed) {
                            Text(
                                "v${state.version}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        } else {
                            Text(
                                "Not installed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    val chipColor = if (state.installed)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                    val chipText  = if (state.installed) "✓ Ready" else "Missing"
                    Surface(shape = RoundedCornerShape(50.dp), color = chipColor) {
                        Text(
                            text     = chipText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style    = MaterialTheme.typography.labelMedium,
                            color    = if (state.installed)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                when (state.installState) {
                    is InstallState.Idle -> {
                        val label = if (state.installed) "Reinstall Roblox" else "Install Roblox"
                        Button(
                            onClick  = { vm.downloadAndInstall() },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, null)
                            Spacer(Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                    is InstallState.Downloading -> {
                        Text(
                            "Downloading… ${(state.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { state.progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is InstallState.Installing -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text("Installing…", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    is InstallState.Done -> {
                        Text("✓ Installed", color = MaterialTheme.colorScheme.primary)
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1500)
                            vm.refresh()
                        }
                    }
                    is InstallState.Error -> {
                        Text(
                            "⚠️ ${(state.installState as InstallState.Error).msg}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick  = { vm.clearError() },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp)
                        ) { Text("Dismiss") }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // About card
        ElevatedCard(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("About Halo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                AboutRow(label = "Launcher Version", value = "1.0.0")
                AboutRow(label = "Runtime Source",   value = "Delta v2.736")
                AboutRow(label = "Built with",       value = "Jetpack Compose")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Footer
        Text(
            text  = "☠️🥀 Halo Roblox Launcher",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
