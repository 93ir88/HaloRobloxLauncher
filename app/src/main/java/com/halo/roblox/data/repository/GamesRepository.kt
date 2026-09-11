package com.halo.roblox.data.repository

import com.halo.roblox.data.api.RobloxApi
import com.halo.roblox.data.model.Game
import com.halo.roblox.data.model.toDomain

class GamesRepository {

    // Fetch games + stitch in thumbnails in one shot
    private suspend fun withThumbnails(games: List<Game>): List<Game> {
        if (games.isEmpty()) return games
        return try {
            val ids  = games.map { it.universeId }
            val resp = RobloxApi.getThumbnails(ids)
            val map  = resp.data.associate { ut ->
                ut.universeId to (ut.thumbnails.firstOrNull()?.imageUrl)
            }
            games.map { it.copy(thumbnailUrl = map[it.universeId]) }
        } catch (_: Exception) {
            games
        }
    }

    suspend fun getPopularGames(): Result<List<Game>> = runCatching {
        val dtos  = RobloxApi.getPopularGames().games
        val games = dtos.map { it.toDomain() }
        withThumbnails(games)
    }

    suspend fun getFeaturedGames(): Result<List<Game>> = runCatching {
        val dtos  = RobloxApi.getFeaturedGames().games
        val games = dtos.map { it.toDomain() }
        withThumbnails(games)
    }

    suspend fun searchGames(query: String): Result<List<Game>> = runCatching {
        val dtos  = RobloxApi.searchGames(query).games
        val games = dtos.map { it.toDomain() }
        withThumbnails(games)
    }

    suspend fun getGameDetail(universeId: Long): Result<Game> = runCatching {
        val base   = RobloxApi.getGameDetails(listOf(universeId)).data.first()
        val thumb  = runCatching {
            RobloxApi.getThumbnails(listOf(universeId))
                .data.firstOrNull()
                ?.thumbnails?.firstOrNull()
                ?.imageUrl
        }.getOrNull()

        Game(
            universeId  = base.id,
            name        = base.name,
            placeId     = base.rootPlaceId,
            playerCount = base.playing,
            upVotes     = 0,
            downVotes   = 0,
            creatorName = base.creator.name,
            price       = 0,
            thumbnailUrl = thumb,
            description = base.description,
            visits      = base.visits,
            genre       = base.genre
        )
    }
}
