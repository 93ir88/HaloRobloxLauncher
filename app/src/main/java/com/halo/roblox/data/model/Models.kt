package com.halo.roblox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Game list from games.roblox.com ──────────────────────────────────────────

@Serializable
data class GamesListResponse(
    val games: List<GameDto> = emptyList(),
    val nextPageExclusiveStartKey: String? = null
)

@Serializable
data class GameDto(
    val universeId: Long = 0L,
    val name: String = "",
    val placeId: Long = 0L,
    val playerCount: Int = 0,
    val totalUpVotes: Int = 0,
    val totalDownVotes: Int = 0,
    val creatorName: String = "",
    val creatorType: String = "",
    val price: Int = 0
)

// ── Thumbnails from thumbnails.roblox.com ─────────────────────────────────────

@Serializable
data class ThumbnailsResponse(
    val data: List<UniverseThumbnail> = emptyList()
)

@Serializable
data class UniverseThumbnail(
    val universeId: Long = 0L,
    val thumbnails: List<ThumbnailData> = emptyList()
)

@Serializable
data class ThumbnailData(
    val targetId: Long = 0L,
    val state: String = "",
    val imageUrl: String = ""
)

// ── Game detail from games.roblox.com/v1/games ───────────────────────────────

@Serializable
data class GameDetailsResponse(
    val data: List<GameDetailDto> = emptyList()
)

@Serializable
data class GameDetailDto(
    val id: Long = 0L,
    val rootPlaceId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val creator: Creator = Creator(),
    val playing: Int = 0,
    val visits: Long = 0L,
    val maxPlayers: Int = 0,
    val created: String = "",
    val updated: String = "",
    val favoritedCount: Long = 0L,
    val genre: String = ""
)

@Serializable
data class Creator(
    val id: Long = 0L,
    val name: String = "",
    val type: String = ""
)

// ── Domain model used across the app ─────────────────────────────────────────

data class Game(
    val universeId: Long,
    val name: String,
    val placeId: Long,
    val playerCount: Int,
    val upVotes: Int,
    val downVotes: Int,
    val creatorName: String,
    val price: Int,
    val thumbnailUrl: String? = null,
    val description: String? = null,
    val visits: Long? = null,
    val genre: String? = null
) {
    val likeRatio: Float get() {
        val total = upVotes + downVotes
        return if (total == 0) 0f else upVotes.toFloat() / total
    }

    val formattedPlayers: String get() = when {
        playerCount >= 1_000_000 -> "${playerCount / 1_000_000}M playing"
        playerCount >= 1_000     -> "${playerCount / 1_000}K playing"
        else                     -> "$playerCount playing"
    }

    val formattedVisits: String get() {
        val v = visits ?: 0L
        return when {
            v >= 1_000_000_000 -> "${v / 1_000_000_000}B visits"
            v >= 1_000_000     -> "${v / 1_000_000}M visits"
            v >= 1_000         -> "${v / 1_000}K visits"
            else               -> "$v visits"
        }
    }
}

fun GameDto.toDomain() = Game(
    universeId   = universeId,
    name         = name,
    placeId      = placeId,
    playerCount  = playerCount,
    upVotes      = totalUpVotes,
    downVotes    = totalDownVotes,
    creatorName  = creatorName,
    price        = price
)
