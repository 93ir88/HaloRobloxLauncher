package com.halo.roblox.data.api

import com.halo.roblox.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

private val json = Json {
    ignoreUnknownKeys = true
    isLenient         = true
    coerceInputValues = true
}

private val http = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    })
    .build()

private suspend fun get(url: String): String = withContext(Dispatchers.IO) {
    val req  = Request.Builder().url(url)
        .header("User-Agent", "HaloRobloxLauncher/1.0 Android")
        .build()
    http.newCall(req).execute().use { resp ->
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $url")
        resp.body?.string() ?: error("Empty body: $url")
    }
}

object RobloxApi {

    // ── Games list ──────────────────────────────────────────────────────────

    suspend fun getPopularGames(maxRows: Int = 30): GamesListResponse {
        val url = "https://games.roblox.com/v1/games/list" +
            "?model.sortToken=" +
            "&model.gameFilter=default" +
            "&model.timeFilter=0" +
            "&model.genreFilter=1" +
            "&model.maxRows=$maxRows" +
            "&model.isPaginatedRequest=false"
        return json.decodeFromString(get(url))
    }

    suspend fun getFeaturedGames(maxRows: Int = 10): GamesListResponse {
        val url = "https://games.roblox.com/v1/games/list" +
            "?model.sortToken=" +
            "&model.gameFilter=featured" +
            "&model.timeFilter=0" +
            "&model.genreFilter=1" +
            "&model.maxRows=$maxRows" +
            "&model.isPaginatedRequest=false"
        return json.decodeFromString(get(url))
    }

    suspend fun searchGames(query: String, maxRows: Int = 30): GamesListResponse {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "https://games.roblox.com/v1/games/list" +
            "?model.keyword=$encoded" +
            "&model.maxRows=$maxRows" +
            "&model.isPaginatedRequest=false"
        return json.decodeFromString(get(url))
    }

    // ── Thumbnails ──────────────────────────────────────────────────────────

    suspend fun getThumbnails(universeIds: List<Long>): ThumbnailsResponse {
        if (universeIds.isEmpty()) return ThumbnailsResponse()
        val ids = universeIds.joinToString(",")
        val url = "https://thumbnails.roblox.com/v1/games/multiget/thumbnails" +
            "?universeIds=$ids" +
            "&countPerUniverse=1" +
            "&defaults=true" +
            "&size=768x432" +
            "&format=Webp" +
            "&isCircular=false"
        return json.decodeFromString(get(url))
    }

    // ── Game detail ─────────────────────────────────────────────────────────

    suspend fun getGameDetails(universeIds: List<Long>): GameDetailsResponse {
        if (universeIds.isEmpty()) return GameDetailsResponse()
        val ids = universeIds.joinToString(",")
        val url = "https://games.roblox.com/v1/games?universeIds=$ids"
        return json.decodeFromString(get(url))
    }
}
