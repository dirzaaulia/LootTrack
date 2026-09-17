package com.dirzaaulia.loottrack.data

import com.dirzaaulia.loottrack.model.AppCurrency
import kotlinx.browser.window

actual fun getSystemDefaultCurrency(): AppCurrency {
    val lang = try {
        window.navigator.language.uppercase()
    } catch (e: Exception) {
        ""
    }

    return when {
        lang.contains("ID") -> AppCurrency.IDR
        lang.contains("US") -> AppCurrency.USD
        lang.contains("GB") -> AppCurrency.GBP
        lang.contains("JP") -> AppCurrency.JPY
        lang.contains("DE") || lang.contains("FR") || lang.contains("IT") || lang.contains("ES") -> AppCurrency.EUR
        lang.contains("SG") -> AppCurrency.SGD
        lang.contains("MY") -> AppCurrency.MYR
        lang.contains("AU") -> AppCurrency.AUD
        lang.contains("CA") -> AppCurrency.CAD
        else -> AppCurrency.IDR
    }
}

actual class PreferenceStorage actual constructor() {
    actual fun getString(key: String, defaultValue: String): String {
        return try {
            window.localStorage.getItem(key) ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    actual fun saveString(key: String, value: String) {
        try {
            window.localStorage.setItem(key, value)
        } catch (e: Exception) {
            // Ignore
        }
    }
}
