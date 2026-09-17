package com.dirzaaulia.loottrack.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

private var appContext: Context? = null

fun initUrlLauncherContext(context: Context) {
    appContext = context.applicationContext
}

actual fun openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext?.startActivity(intent)
    } catch (e: Exception) {
        // Ignore launch errors
    }
}
