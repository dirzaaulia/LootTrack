package com.dirzaaulia.loottrack.data

import android.content.Context
import com.dirzaaulia.loottrack.model.AppCurrency
import java.util.Locale

actual fun getSystemDefaultCurrency(): AppCurrency {
    val country = try {
        Locale.getDefault().country.uppercase()
    } catch (e: Exception) {
        ""
    }

    return when (country) {
        "ID" -> AppCurrency.IDR
        "US" -> AppCurrency.USD
        "GB" -> AppCurrency.GBP
        "JP" -> AppCurrency.JPY
        "DE", "FR", "IT", "ES", "NL", "BE", "AT", "IE", "FI" -> AppCurrency.EUR
        "SG" -> AppCurrency.SGD
        "MY" -> AppCurrency.MYR
        "AU" -> AppCurrency.AUD
        "CA" -> AppCurrency.CAD
        else -> AppCurrency.IDR
    }
}

private var appContext: Context? = null

fun initAndroidContext(context: Context) {
    appContext = context.applicationContext
}

fun getAndroidContext(): Context? = appContext

actual class PreferenceStorage actual constructor() {
    private val prefs by lazy {
        appContext?.getSharedPreferences("loottrack_prefs", Context.MODE_PRIVATE)
    }

    actual fun getString(key: String, defaultValue: String): String {
        return prefs?.getString(key, defaultValue) ?: defaultValue
    }

    actual fun saveString(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }
}
