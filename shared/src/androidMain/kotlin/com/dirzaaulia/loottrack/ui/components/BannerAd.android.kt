package com.dirzaaulia.loottrack.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.math.max

@Composable
actual fun BannerAd(
    modifier: Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val containerWidth = maxWidth.value
        val containerHeight = maxHeight.value

        // AdMob Banner base dimensions: Medium Rectangle (300x250) or Standard Banner (320x50)
        val isMedium = containerWidth >= 260f && containerHeight >= 160f
        val adWidth = if (isMedium) 300f else 320f
        val adHeight = if (isMedium) 250f else 50f

        val scaleX = containerWidth / adWidth
        val scaleY = containerHeight / adHeight
        val fillScale = max(scaleX, scaleY)

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.scaleX = fillScale
                    this.scaleY = fillScale
                },
            factory = { context ->
                val adSize = if (isMedium) AdSize.MEDIUM_RECTANGLE else AdSize.BANNER

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
