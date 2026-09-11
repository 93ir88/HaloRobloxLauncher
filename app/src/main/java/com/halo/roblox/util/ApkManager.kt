package com.halo.roblox.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val ASSET_NAME   = "roblox_delta.apk"
private const val CACHE_NAME   = "roblox_delta.apk"
private const val PREFS_NAME   = "halo_prefs"
private const val KEY_EXTRACTED = "apk_extracted"

class ApkManager(private val ctx: Context) {

    private val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** True if we've already extracted the APK to cache this install */
    val isExtracted: Boolean
        get() = cachedApk.exists()

    private val cachedApk: File
        get() = File(ctx.cacheDir, CACHE_NAME)

    /**
     * Extract the bundled APK from assets into cache dir.
     * Reports progress 0.0–1.0 via [onProgress].
     */
    suspend fun extractFromAssets(
        onProgress: (Float) -> Unit,
        onSuccess : suspend (File) -> Unit,
        onError   : (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val assetManager = ctx.assets
            val assetFd      = assetManager.openFd(ASSET_NAME)
            val total        = assetFd.length
            val out          = cachedApk

            assetFd.createInputStream().use { input ->
                FileOutputStream(out).use { fos ->
                    val buf    = ByteArray(256 * 1024) // 256KB chunks
                    var copied = 0L
                    var read: Int
                    while (input.read(buf).also { read = it } != -1) {
                        fos.write(buf, 0, read)
                        copied += read
                        val pct = if (total > 0) copied.toFloat() / total else 0f
                        withContext(Dispatchers.Main) { onProgress(pct) }
                    }
                }
            }
            withContext(Dispatchers.Main) { onSuccess(out) }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Extraction failed: ${e.message}")
            }
        }
    }

    /** Trigger Android package installer for the cached APK */
    suspend fun install(file: File) = withContext(Dispatchers.Main) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        ctx.startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
