package com.dirzaaulia.loottrack

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dirzaaulia.loottrack.theme.LootTrackTheme
import com.dirzaaulia.loottrack.ui.deals.DealsScreen
import com.dirzaaulia.loottrack.ui.splash.SplashScreen
import com.dirzaaulia.loottrack.utils.isComposeSplashEnabled
import com.dirzaaulia.loottrack.viewmodel.DealsUiState
import com.dirzaaulia.loottrack.viewmodel.DealsViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    viewModel: DealsViewModel = koinInject()
) {
    val systemDark = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemDark) }
    var showSplash by remember { mutableStateOf(isComposeSplashEnabled()) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isDataReady = uiState !is DealsUiState.Loading

    LootTrackTheme(darkTheme = isDarkTheme) {
        KoinContext {
            if (showSplash) {
                SplashScreen(
                    isDataReady = isDataReady,
                    onSplashFinished = { showSplash = false }
                )
            } else {
                DealsScreen(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                    viewModel = viewModel
                )
            }
        }
    }
}
