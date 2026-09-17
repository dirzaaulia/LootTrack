package com.dirzaaulia.loottrack.ui.components

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.Log
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

private const val TAG = "LootTrackAdMob"

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
                val density = context.resources.displayMetrics.density
                val padHorizontal = (16 * density).toInt()
                val padVertical = (10 * density).toInt()
                val badgeMargin = (12 * density).toInt()
                val textGap = (12 * density).toInt()

                val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
                val activeAdUnitId = if (isDebug) {
                    "ca-app-pub-3940256099942544/2247696110" // Official Google Test Native Ad Unit ID
                } else {
                    "ca-app-pub-6717632447198427/6575141423" // Production LootTrack AdMob Native Ad Unit ID
                }

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

                // 1. MediaView (Artwork - CENTER_CROP fills 100% of the given card area)
                val mediaView = MediaView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                }
                rootLayout.addView(mediaView)
                nativeAdView.mediaView = mediaView

                // 2. Cyberpunk Ad Attribution Badge ("AD")
                val badgeDrawable = GradientDrawable().apply {
                    setColor(AndroidColor.parseColor("#FF007A")) // Neon Pink
                    setStroke((1.5f * density).toInt(), AndroidColor.parseColor("#00F0FF")) // Cyan Accent border
                }
                val adBadge = TextView(context).apply {
                    text = "AD"
                    textSize = 9f
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                    setTextColor(AndroidColor.WHITE)
                    background = badgeDrawable
                    setPadding((8 * density).toInt(), (4 * density).toInt(), (8 * density).toInt(), (4 * density).toInt())
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.TOP or Gravity.START
                        setMargins(badgeMargin, badgeMargin, 0, 0)
                    }
                }
                rootLayout.addView(adBadge)

                // 3. Bottom Row Overlay
                val bottomRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(padHorizontal, padVertical, padHorizontal, padVertical)
                    setBackgroundColor(AndroidColor.parseColor("#E60F0C1B")) // Dark Translucent
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM
                    }
                }

                val textColumn = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1.0f
                    ).apply {
                        setMargins(0, 0, textGap, 0)
                    }
                }

                val headlineView = TextView(context).apply {
                    textSize = 11f
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                    setTextColor(AndroidColor.WHITE)
                    setSingleLine(true)
                    ellipsize = TextUtils.TruncateAt.END
                }
                textColumn.addView(headlineView)
                nativeAdView.headlineView = headlineView

                val bodyView = TextView(context).apply {
                    textSize = 9f
                    setTextColor(AndroidColor.parseColor("#00F0FF")) // Cyan Accent
                    setSingleLine(true)
                    ellipsize = TextUtils.TruncateAt.END
                }
                textColumn.addView(bodyView)
                nativeAdView.bodyView = bodyView

                bottomRow.addView(textColumn)

                // Call To Action Button
                val ctaDrawable = GradientDrawable().apply {
                    setColor(AndroidColor.parseColor("#FF007A")) // Neon Pink
                    setCornerRadius(6f * density)
                }
                val ctaView = TextView(context).apply {
                    textSize = 9f
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                    setTextColor(AndroidColor.WHITE)
                    background = ctaDrawable
                    setPadding((10 * density).toInt(), (5 * density).toInt(), (10 * density).toInt(), (5 * density).toInt())
                    setSingleLine(true)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                bottomRow.addView(ctaView)
                nativeAdView.callToActionView = ctaView

                rootLayout.addView(bottomRow)
                nativeAdView.addView(rootLayout)

                Log.d(TAG, "Requesting Native Ad (isDebug=$isDebug) with unit: $activeAdUnitId")

                val adLoader = AdLoader.Builder(context, activeAdUnitId)
                    .forNativeAd { ad ->
                        Log.d(TAG, "SUCCESS // Native Ad loaded! Headline: ${ad.headline}, Advertiser: ${ad.advertiser}")
                        nativeAd?.destroy()
                        nativeAd = ad

                        headlineView.text = ad.headline ?: "Sponsored Offer"
                        bodyView.text = ad.body ?: ad.advertiser ?: "Partner Promo"
                        ctaView.text = (ad.callToAction ?: "GET").uppercase()

                        nativeAdView.setNativeAd(ad)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "AdListener.onAdLoaded triggered for unit: $activeAdUnitId")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(
                                TAG,
                                "ERR // Native Ad failed! Code: ${error.code}, Msg: ${error.message}, Domain: ${error.domain}"
                            )
                            Log.e(TAG, "Response Info: ${error.responseInfo}")
                        }

                        override fun onAdImpression() {
                            Log.d(TAG, "AdMob Impression recorded!")
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "AdMob Click recorded!")
                        }
                    })
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
