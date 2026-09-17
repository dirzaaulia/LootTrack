package com.dirzaaulia.loottrack.data

import com.dirzaaulia.loottrack.model.AppCurrency

expect fun getSystemDefaultCurrency(): AppCurrency

expect class PreferenceStorage() {
    fun getString(key: String, defaultValue: String): String
    fun saveString(key: String, value: String)
}
