package com.dirzaaulia.loottrack.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

var isAdsDisabledGlobal by mutableStateOf(false)

@Composable
expect fun BannerAd(
    modifier: Modifier = Modifier
)

