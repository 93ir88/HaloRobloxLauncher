package com.halo.roblox.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

const val ROBLOX_PACKAGE = "com.roblox.client"

object GameLauncher {

    fun isInstalled(ctx: Context): Boolean = try {
        ctx.packageManager.getPackageInfo(ROBLOX_PACKAGE, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) { false }

    fun installedVersion(ctx: Context): String = try {
        ctx.packageManager.getPackageInfo(ROBLOX_PACKAGE, 0).versionName ?: "Unknown"
    } catch (_: PackageManager.NameNotFoundException) { "Not installed" }

    /**
     * Launch a specific game by placeId.
     * Tries deep link first, falls back to package launch.
     */
    fun launchGame(ctx: Context, placeId: Long) {
        val deepLink = Intent(Intent.ACTION_VIEW).apply {
            data    = Uri.parse("roblox://placeId=$placeId")
            setPackage(ROBLOX_PACKAGE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            ctx.startActivity(deepLink)
            return
        } catch (_: Exception) {}

        // Fallback — open roblox.com game page which triggers the app
        val fallback = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.roblox.com/games/$placeId")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(fallback)
    }

    /** Open Roblox home without a specific game */
    fun launchHome(ctx: Context) {
        ctx.packageManager.getLaunchIntentForPackage(ROBLOX_PACKAGE)
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            ?.let { ctx.startActivity(it) }
    }
}
