package com.dirzaaulia.loottrack

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.dirzaaulia.loottrack.data.initAndroidContext
import com.dirzaaulia.loottrack.di.initKoin
import com.dirzaaulia.loottrack.ui.components.NativeAdPreloader
import com.dirzaaulia.loottrack.utils.initUrlLauncherContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initAndroidContext(this)
        initUrlLauncherContext(this)

        if (GlobalContext.getOrNull() == null) {
            initKoin()
        }

        val isSystemDemoOn = try {
            android.provider.Settings.Global.getInt(contentResolver, "sysui_demo_allowed", 0) == 1 &&
            android.provider.Settings.Global.getString(contentResolver, "sysui_tuner_demo_on") == "1"
        } catch (_: Exception) { false }

        if (isSystemDemoOn || intent?.getBooleanExtra("hide_ads", false) == true || intent?.getBooleanExtra("demo_mode", false) == true) {
            com.dirzaaulia.loottrack.ui.components.isAdsDisabledGlobal = true
        }

        // Initialize Google Mobile Ads Next-Gen SDK on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            val appId = if (isDebug) {
                "ca-app-pub-3940256099942544~3347511713" // Official AdMob Test App ID
            } else {
                "ca-app-pub-6717632447198427~5793376945" // Production LootTrack AdMob App ID
            }

            val activeAdUnitId = if (isDebug) {
                "ca-app-pub-3940256099942544/2247696110" // Official Google Test Native Ad Unit ID
            } else {
                "ca-app-pub-6717632447198427/6575141423" // Production LootTrack AdMob Native Ad Unit ID
            }

            try {
                val config = InitializationConfig.Builder(appId).build()
                MobileAds.initialize(this@MainActivity, config) { status ->
                    val statusMap = status.adapterStatusMap
                    for ((adapter, adapterStatus) in statusMap) {
                        Log.d(
                            "LootTrackAdMob",
                            "Next-Gen MobileAds Init Adapter: $adapter, State: ${adapterStatus.initializationState}, Desc: ${adapterStatus.description}"
                        )
                    }
                    // Start preloading native ads immediately after MobileAds initialization
                    NativeAdPreloader.preload(this@MainActivity, activeAdUnitId)
                }
            } catch (e: Exception) {
                Log.e("LootTrackAdMob", "Error initializing Next-Gen MobileAds SDK", e)
            }
        }

        checkAndRequestNotificationPermission()

        setContent {
            App()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra("hide_ads", false) || intent.getBooleanExtra("demo_mode", false)) {
            com.dirzaaulia.loottrack.ui.components.isAdsDisabledGlobal = true
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
