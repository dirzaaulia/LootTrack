package com.dirzaaulia.loottrack.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponse(
    val date: String = "",
    val usd: Map<String, Double> = emptyMap()
)

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val label: String,
    val currencyName: String
) {
    USD("usd", "$", "USD ($)", "United States Dollar"),
    IDR("idr", "Rp ", "IDR (Rp)", "Indonesian Rupiah"),
    EUR("eur", "EUR ", "EUR (EUR)", "Euro"),
    GBP("gbp", "GBP ", "GBP (GBP)", "British Pound Sterling"),
    JPY("jpy", "JPY ", "JPY (JPY)", "Japanese Yen"),
    SGD("sgd", "S$", "SGD (S$)", "Singapore Dollar"),
    MYR("myr", "RM ", "MYR (RM)", "Malaysian Ringgit"),
    AUD("aud", "A$", "AUD (A$)", "Australian Dollar"),
    CAD("cad", "CA$", "CAD (CA$)", "Canadian Dollar");

    companion object {
        fun fromCode(code: String): AppCurrency {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: USD
        }
    }
}
