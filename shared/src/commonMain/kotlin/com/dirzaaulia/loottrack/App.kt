package com.dirzaaulia.loottrack

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dirzaaulia.loottrack.theme.LootTrackTheme
import com.dirzaaulia.loottrack.ui.deals.DealsScreen
import com.dirzaaulia.loottrack.ui.splash.SplashScreen
import com.dirzaaulia.loottrack.utils.isComposeSplashEnabled
import org.koin.compose.KoinContext

@Composable
fun App() {
    val systemDark = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemDark) }
    var showSplash by remember { mutableStateOf(isComposeSplashEnabled()) }

    LootTrackTheme(darkTheme = isDarkTheme) {
        KoinContext {
            if (showSplash) {
                SplashScreen(
                    onSplashFinished = { showSplash = false }
                )
            } else {
                DealsScreen(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}
