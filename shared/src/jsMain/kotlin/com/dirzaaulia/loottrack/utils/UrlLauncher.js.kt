package com.dirzaaulia.loottrack.utils

import kotlinx.browser.window

actual fun openUrl(url: String) {
    try {
        window.open(url, "_blank")
    } catch (e: Exception) {
        // Ignore
    }
}
