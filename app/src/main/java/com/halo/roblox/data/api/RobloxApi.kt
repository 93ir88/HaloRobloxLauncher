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
    .build()

private suspend fun get(url: String): String = withContext(Dispatchers.IO) {
    val req = Request.Builder()
        .url(url)
        .header("User-Agent", "Roblox/Android HaloLauncher/1.0")
        .header("Accept", "application/json")
        .build()
    http.newCall(req).execute().use { resp ->
        if (!resp.isSuccessful) error("HTTP ${resp.code}: $url")
        resp.body?.string() ?: error("Empty body")
    }
}

object RobloxApi {

    // ── Games ──────────────────────────────────────────────────────────────

    /** Popular / default chart — no auth needed */
    suspend fun getPopularGames(maxRows: Int = 30): GamesListResponse {
        val url = buildString {
            append("https://games.roblox.com/v1/games/list")
            append("?model.maxRows=$maxRows")
            append("&model.isPaginatedRequest=false")
        }
        return json.decodeFromString(get(url))
    }

    /** Top earning — different sort, gives a "featured-like" set */
    suspend fun getTopGames(maxRows: Int = 12): GamesListResponse {
        val url = buildString {
            append("https://games.roblox.com/v1/games/list")
            append("?model.maxRows=$maxRows")
            append("&model.isPaginatedRequest=false")
            append("&model.sortOrder=2") // Top Earning
        }
        return runCatching { json.decodeFromString<GamesListResponse>(get(url)) }
            .getOrDefault(GamesListResponse())
    }

    /** Search by keyword */
    suspend fun searchGames(query: String, maxRows: Int = 30): GamesListResponse {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        val url = buildString {
            append("https://games.roblox.com/v1/games/list")
            append("?model.keyword=$encoded")
            append("&model.maxRows=$maxRows")
            append("&model.isPaginatedRequest=false")
        }
        return json.decodeFromString(get(url))
    }

    // ── Thumbnails ─────────────────────────────────────────────────────────

    suspend fun getThumbnails(universeIds: List<Long>): ThumbnailsResponse {
        if (universeIds.isEmpty()) return ThumbnailsResponse()
        val ids = universeIds.joinToString(",")
        val url = buildString {
            append("https://thumbnails.roblox.com/v1/games/multiget/thumbnails")
            append("?universeIds=$ids")
            append("&countPerUniverse=1")
            append("&defaults=true")
            append("&size=768x432")
            append("&format=Webp")
            append("&isCircular=false")
        }
        return runCatching { json.decodeFromString<ThumbnailsResponse>(get(url)) }
            .getOrDefault(ThumbnailsResponse())
    }

    // ── Game detail ────────────────────────────────────────────────────────

    suspend fun getGameDetails(universeIds: List<Long>): GameDetailsResponse {
        if (universeIds.isEmpty()) return GameDetailsResponse()
        val ids = universeIds.joinToString(",")
        return runCatching {
            json.decodeFromString<GameDetailsResponse>(
                get("https://games.roblox.com/v1/games?universeIds=$ids")
            )
        }.getOrDefault(GameDetailsResponse())
    }
}
