package com.halo.roblox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.halo.roblox.data.model.Game

@Composable
fun GameCard(
    game    : Game,
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    width   : Dp = 180.dp,
    height  : Dp = 120.dp
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // Thumbnail
        if (game.thumbnailUrl != null) {
            AsyncImage(
                model             = game.thumbnailUrl,
                contentDescription = game.name,
                contentScale      = ContentScale.Crop,
                modifier          = Modifier.fillMaxSize()
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                        startY = 0.3f * height.value * 3f
                    )
                )
        )

        // Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text       = game.name,
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = game.formattedPlayers,
                color    = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun GameListItem(
    game    : Game,
    onClick : () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp, 54.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (game.thumbnailUrl != null) {
                AsyncImage(
                    model             = game.thumbnailUrl,
                    contentDescription = game.name,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = game.name,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "${game.formattedPlayers} • ${game.creatorName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
