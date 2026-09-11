package com.halo.roblox.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.halo.roblox.data.model.Game
import com.halo.roblox.ui.components.GameCard
import com.halo.roblox.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onGameClick: (Game) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val state   by vm.state.collectAsStateWithLifecycle()
    val primary = MaterialTheme.colorScheme.primary

    val inf = rememberInfiniteTransition(label = "bg")
    val shift by inf.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(8000), RepeatMode.Reverse),
        label         = "shift"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * (0.2f + shift * 0.6f), size.height * 0.15f),
                        radius = size.minDimension * 0.8f
                    ),
                    radius = size.minDimension * 0.8f
                )
            }
    ) {
        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(msg = state.error!!, onRetry = { vm.load() })
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Header
                    item {
                        Column(modifier = Modifier.padding(start = 20.dp, top = 52.dp, bottom = 8.dp)) {
                            Text(
                                text       = "☠️ HALO",
                                style      = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color      = MaterialTheme.colorScheme.primary,
                                letterSpacing = 4.sp
                            )
                            Text(
                                text  = "Roblox Launcher",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Featured hero carousel
                    if (state.featured.isNotEmpty()) {
                        item {
                            SectionHeader("Featured")
                        }
                        item {
                            LazyRow(
                                contentPadding    = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.featured) { game ->
                                    FeaturedCard(game = game, onClick = { onGameClick(game) })
                                }
                            }
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }

                    // Popular section header
                    item { SectionHeader("Popular Right Now") }

                    // Popular horizontal scroll
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.popular.take(15)) { game ->
                                GameCard(
                                    game    = game,
                                    onClick = { onGameClick(game) }
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                    item { SectionHeader("All Games") }

                    // Full grid
                    items(
                        items = state.popular.chunked(2),
                        key   = { row -> row.first().universeId }
                    ) { row ->
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { game ->
                                GameCard(
                                    game     = game,
                                    onClick  = { onGameClick(game) },
                                    modifier = Modifier.weight(1f),
                                    width    = 0.dp, // weight handles width
                                    height   = 110.dp
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedCard(game: Game, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        if (game.thumbnailUrl != null) {
            AsyncImage(
                model              = game.thumbnailUrl,
                contentDescription = game.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text(
                text       = game.name,
                color      = Color.White,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = "${game.formattedPlayers} • ${game.creatorName}",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier   = Modifier.padding(start = 20.dp, bottom = 10.dp, top = 4.dp)
    )
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(msg: String, onRetry: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Something went wrong", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(msg, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
