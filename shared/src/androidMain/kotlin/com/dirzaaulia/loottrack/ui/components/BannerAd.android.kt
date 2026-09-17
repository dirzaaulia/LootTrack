package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun BannerAd(
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.FLUID)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
