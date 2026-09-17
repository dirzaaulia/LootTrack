package com.dirzaaulia.loottrack.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@SuppressLint("MissingPermission")
@Composable
actual fun BannerAd(
    modifier: Modifier
) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            nativeAd?.destroy()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val nativeAdView = NativeAdView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                // Root Container
                val rootLayout = FrameLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                // 1. MediaView (Background Media / Artwork)
                val mediaView = MediaView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                }
                rootLayout.addView(mediaView)
                nativeAdView.mediaView = mediaView

                // 2. Cyberpunk Ad Attribution Badge ("AD") - Styled to Match LootTrack Aesthetic
                val badgeDrawable = GradientDrawable().apply {
                    setColor(AndroidColor.parseColor("#FF007A")) // Neon Pink
                    setStroke(2, AndroidColor.parseColor("#00F0FF")) // Cyan Accent border
                }
                val adBadge = TextView(context).apply {
                    text = "AD"
                    textSize = 9f
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                    setTextColor(AndroidColor.WHITE)
                    background = badgeDrawable
                    setPadding(14, 6, 14, 6)
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.TOP or Gravity.START
                        setMargins(14, 14, 0, 0)
                    }
                }
                rootLayout.addView(adBadge)

                // 3. Headline & Body Column (Bottom Banner Overlay)
                val bottomColumn = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 10, 16, 12)
                    setBackgroundColor(AndroidColor.parseColor("#E60F0C1B")) // Dark Translucent
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM
                    }
                }

                val headlineView = TextView(context).apply {
                    textSize = 12f
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                    setTextColor(AndroidColor.WHITE)
                    setSingleLine()
                }
                bottomColumn.addView(headlineView)
                nativeAdView.headlineView = headlineView

                val bodyView = TextView(context).apply {
                    textSize = 9f
                    setTextColor(AndroidColor.parseColor("#00F0FF")) // Cyan Accent
                    setSingleLine()
                }
                bottomColumn.addView(bodyView)
                nativeAdView.bodyView = bodyView

                rootLayout.addView(bottomColumn)
                nativeAdView.addView(rootLayout)

                val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd { ad ->
                        nativeAd?.destroy()
                        nativeAd = ad

                        // Populate Native Ad Assets
                        headlineView.text = ad.headline ?: "Sponsored Promotion"
                        bodyView.text = ad.body ?: ad.advertiser ?: "LootTrack Partner Offer"

                        nativeAdView.setNativeAd(ad)
                    }
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())

                nativeAdView
            },
            update = { nativeAdView ->
                nativeAd?.let { ad ->
                    nativeAdView.setNativeAd(ad)
                }
            }
        )
    }
}
