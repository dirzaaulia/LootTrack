package com.dirzaaulia.loottrack.ui.components

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
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
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import java.util.concurrent.ConcurrentLinkedQueue

private const val TAG = "LootTrackAdMob"
private const val ONE_HOUR_MS = 3600_000L // AdMob policy: Pre-cached ads expire after 1 hour
private const val MAX_CACHE_SIZE = 5       // AdMob policy: Maximum 5 preloaded ads in cache pool

/**
 * Next-Gen AdMob Native Ad Preloader & Cache Manager for LootTrack.
 * Adheres strictly to Google AdMob Guidelines, Policies & Best Practices:
 * 1. Maximum Cache Limit: Caps preloaded ads at 5 maximum to prevent over-fetching.
 * 2. 1-Hour Expiration: Automatically purges and destroys cached ads older than 1 hour.
 * 3. Single-flight Requests: Prevents duplicate simultaneous network requests via volatile flag.
 * 4. Thread Safety: Handles thread-safe queueing and dispatches UI callbacks to the Main Thread.
 * 5. Lifecycle Destruction: Invokes nativeAd.destroy() on expired, discarded, or disposed ads.
 */
object NativeAdPreloader {

    private data class CachedNativeAd(
        val nativeAd: NativeAd,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val adCache = ConcurrentLinkedQueue<CachedNativeAd>()
    @Volatile private var isPreloading = false

    fun preload(context: Context, adUnitId: String, desiredCacheSize: Int = 3) {
        val targetSize = desiredCacheSize.coerceAtMost(MAX_CACHE_SIZE)
        if (isPreloading || adCache.size >= targetSize) return

        cleanExpiredAds()

        if (adCache.size >= targetSize) return

        isPreloading = true
        val adRequest = NativeAdRequest.Builder(
            adUnitId,
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        NativeAdLoader.load(
            adRequest,
            object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    Log.d(TAG, "Next-Gen Preloader // Preloaded NativeAd successfully. Current cache size: ${adCache.size + 1}/$MAX_CACHE_SIZE")
                    adCache.offer(CachedNativeAd(nativeAd))
                    isPreloading = false

                    if (adCache.size < targetSize) {
                        preload(context, adUnitId, targetSize)
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Next-Gen Preloader ERR // Code: ${adError.code}, Msg: ${adError.message}")
                    isPreloading = false
                }
            }
        )
    }

    fun pollOrLoadAd(
        context: Context,
        adUnitId: String,
        onAdReady: (NativeAd) -> Unit,
        onAdFailed: (LoadAdError) -> Unit = {}
    ) {
        cleanExpiredAds()

        val cached = adCache.poll()
        if (cached != null) {
            Log.d(TAG, "Next-Gen Preloader // Serving preloaded NativeAd from cache. Remaining: ${adCache.size}")
            onAdReady(cached.nativeAd)
            preload(context, adUnitId)
            return
        }

        Log.d(TAG, "Next-Gen Preloader // Cache empty. Fetching fresh NativeAd...")
        val adRequest = NativeAdRequest.Builder(
            adUnitId,
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        NativeAdLoader.load(
            adRequest,
            object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    Log.d(TAG, "Next-Gen NativeAdLoaded SUCCESS // Headline: ${nativeAd.headline}")
                    onAdReady(nativeAd)
                    preload(context, adUnitId)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Next-Gen NativeAd ERR // Code: ${adError.code}, Msg: ${adError.message}")
                    onAdFailed(adError)
                }
            }
        )
    }

    private fun cleanExpiredAds() {
        val now = System.currentTimeMillis()
        val iterator = adCache.iterator()
        while (iterator.hasNext()) {
            val cached = iterator.next()
            if (now - cached.timestamp > ONE_HOUR_MS) {
                try {
                    cached.nativeAd.destroy()
                } catch (e: Exception) {
                    Log.w(TAG, "Error destroying expired ad", e)
                }
                iterator.remove()
                Log.d(TAG, "Next-Gen Preloader // Expired ad (>1h) destroyed and purged from cache.")
            }
        }
    }

    fun clear() {
        while (true) {
            val cached = adCache.poll() ?: break
            try {
                cached.nativeAd.destroy()
            } catch (e: Exception) {
                Log.w(TAG, "Error clearing cached ad", e)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
actual fun BannerAd(
    modifier: Modifier
) {
    if (isAdsDisabledGlobal) {
        Box(modifier = modifier)
        return
    }

    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

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

                // 1. MediaView (fills 100% space with CENTER_CROP to maintain aspect ratio)
                val mediaView = MediaView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                    setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
                        override fun onChildViewAdded(parent: View?, child: View?) {
                            if (child is ImageView) {
                                child.scaleType = ImageView.ScaleType.CENTER_CROP
                                child.adjustViewBounds = true
                            }
                        }

                        override fun onChildViewRemoved(parent: View?, child: View?) {}
                    })
                }
                rootLayout.addView(mediaView)

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

                val bodyView = TextView(context).apply {
                    textSize = 9f
                    setTextColor(AndroidColor.parseColor("#00F0FF")) // Cyan Accent
                    setSingleLine(true)
                    ellipsize = TextUtils.TruncateAt.END
                }
                textColumn.addView(bodyView)

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

                rootLayout.addView(bottomRow)
                nativeAdView.addView(rootLayout)

                nativeAdView.headlineView = headlineView
                nativeAdView.bodyView = bodyView
                nativeAdView.callToActionView = ctaView

                // Request or poll preloaded Native Ad from Next-Gen Preloader
                NativeAdPreloader.pollOrLoadAd(
                    context = context,
                    adUnitId = activeAdUnitId,
                    onAdReady = { ad ->
                        // Dispatch UI updates to Main Thread
                        mainHandler.post {
                            nativeAd?.destroy()
                            nativeAd = ad

                            ad.adEventCallback = object : NativeAdEventCallback {
                                override fun onAdImpression() {
                                    Log.d(TAG, "Next-Gen NativeAd // Impression recorded!")
                                }

                                override fun onAdClicked() {
                                    Log.d(TAG, "Next-Gen NativeAd // Click recorded!")
                                }
                            }

                            // 1. Headline
                            if (ad.headline.isNullOrEmpty()) {
                                headlineView.visibility = View.GONE
                            } else {
                                headlineView.visibility = View.VISIBLE
                                headlineView.text = ad.headline
                            }

                            // 2. Body / Advertiser
                            val bodyText = ad.body ?: ad.advertiser
                            if (bodyText.isNullOrEmpty()) {
                                bodyView.visibility = View.GONE
                            } else {
                                bodyView.visibility = View.VISIBLE
                                bodyView.text = bodyText
                            }

                            // 3. Call to Action
                            if (ad.callToAction.isNullOrEmpty()) {
                                ctaView.visibility = View.GONE
                            } else {
                                ctaView.visibility = View.VISIBLE
                                ctaView.text = ad.callToAction?.uppercase()
                            }

                            // Register NativeAd with NativeAdView in Next-Gen SDK
                            try {
                                nativeAdView.registerNativeAd(ad, mediaView)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to register native ad", e)
                            }
                        }
                    },
                    onAdFailed = { error ->
                        Log.e(TAG, "Failed to load Next-Gen NativeAd: ${error.message}")
                    }
                )

                nativeAdView
            },
            update = { nativeAdView ->
                nativeAd?.let { ad ->
                    val mediaView = nativeAdView.findViewById<MediaView>(R.id.custom) ?: return@let
                    try {
                        nativeAdView.registerNativeAd(ad, mediaView)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to register native ad on update", e)
                    }
                }
            }
        )
    }
}
