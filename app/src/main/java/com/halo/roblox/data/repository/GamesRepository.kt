package com.halo.roblox.data.repository

import com.halo.roblox.data.api.RobloxApi
import com.halo.roblox.data.model.Game
import com.halo.roblox.data.model.toDomain

class GamesRepository {

    private suspend fun withThumbnails(games: List<Game>): List<Game> {
        if (games.isEmpty()) return games
        return try {
            val ids  = games.map { it.universeId }
            val resp = RobloxApi.getThumbnails(ids)
            val map  = resp.data.associate { ut ->
                ut.universeId to ut.thumbnails.firstOrNull()?.imageUrl
            }
            games.map { it.copy(thumbnailUrl = map[it.universeId]) }
        } catch (_: Exception) {
            games
        }
    }

    suspend fun getPopularGames(): Result<List<Game>> = runCatching {
        val games = RobloxApi.getPopularGames().games.map { it.toDomain() }
        withThumbnails(games)
    }

    suspend fun getTopGames(): Result<List<Game>> = runCatching {
        val games = RobloxApi.getTopGames().games.map { it.toDomain() }
        withThumbnails(games)
    }

    suspend fun searchGames(query: String): Result<List<Game>> = runCatching {
        val games = RobloxApi.searchGames(query).games.map { it.toDomain() }
        withThumbnails(games)
    }

    suspend fun getGameDetail(universeId: Long): Result<Game> = runCatching {
        val detail = RobloxApi.getGameDetails(listOf(universeId)).data.first()
        val thumb  = runCatching {
            RobloxApi.getThumbnails(listOf(universeId))
                .data.firstOrNull()
                ?.thumbnails?.firstOrNull()
                ?.imageUrl
        }.getOrNull()

        Game(
            universeId   = detail.id,
            name         = detail.name,
            placeId      = detail.rootPlaceId,
            playerCount  = detail.playing,
            upVotes      = 0,
            downVotes    = 0,
            creatorName  = detail.creator.name,
            price        = 0,
            thumbnailUrl = thumb,
            description  = detail.description,
            visits       = detail.visits,
            genre        = detail.genre
        )
    }
}
