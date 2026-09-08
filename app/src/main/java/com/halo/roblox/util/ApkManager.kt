package com.halo.roblox.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

const val APK_DOWNLOAD_URL = "https://delta.filenetwork.vip/file/Delta-2.736.1408-02.apk"
const val ROBLOX_PACKAGE   = "com.roblox.client"
const val APK_FILE_NAME    = "roblox_halo.apk"

sealed class LaunchState {
    object Idle        : LaunchState()
    object Downloading : LaunchState()
    object Installing  : LaunchState()
    object Launching   : LaunchState()
    object Error       : LaunchState()
}

class ApkManager(private val ctx: Context) {

    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .build()

    fun isInstalled(): Boolean = try {
        ctx.packageManager.getPackageInfo(ROBLOX_PACKAGE, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) { false }

    fun installedVersion(): String = try {
        ctx.packageManager.getPackageInfo(ROBLOX_PACKAGE, 0).versionName ?: "Unknown"
    } catch (_: PackageManager.NameNotFoundException) { "Not installed" }

    fun launch() {
        ctx.packageManager.getLaunchIntentForPackage(ROBLOX_PACKAGE)
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            ?.let { ctx.startActivity(it) }
    }

    suspend fun download(
        onProgress : (Float) -> Unit,
        onSuccess  : suspend (File) -> Unit,
        onError    : (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder()
                .url(APK_DOWNLOAD_URL)
                .header("User-Agent", "HaloRobloxLauncher/1.0 Android")
                .build()

            val resp = http.newCall(req).execute()
            if (!resp.isSuccessful) {
                withContext(Dispatchers.Main) { onError("HTTP ${resp.code}") }
                return@withContext
            }

            val body = resp.body ?: run {
                withContext(Dispatchers.Main) { onError("Empty body") }
                return@withContext
            }

            val total   = body.contentLength()
            val outFile = File(ctx.cacheDir, APK_FILE_NAME)

            body.byteStream().use { input ->
                FileOutputStream(outFile).use { out ->
                    val buf = ByteArray(8 * 1024)
                    var read: Int
                    var loaded = 0L
                    while (input.read(buf).also { read = it } != -1) {
                        out.write(buf, 0, read)
                        loaded += read
                        if (total > 0) {
                            val pct = loaded.toFloat() / total
                            withContext(Dispatchers.Main) { onProgress(pct) }
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) { onSuccess(outFile) }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { onError(e.message ?: "Download failed") }
        }
    }

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
