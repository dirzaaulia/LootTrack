package com.dirzaaulia.loottrack.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.BoxWithConstraints
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
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val widthInDp = maxWidth.value.toInt()
        val heightInDp = maxHeight.value.toInt()

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val adSize = when {
                    widthInDp >= 280 && heightInDp >= 180 -> AdSize.MEDIUM_RECTANGLE
                    widthInDp > 0 -> AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthInDp)
                    else -> AdSize.BANNER
                }

                AdView(context).apply {
                    setAdSize(adSize)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { adView ->
                adView.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        )
    }
}
