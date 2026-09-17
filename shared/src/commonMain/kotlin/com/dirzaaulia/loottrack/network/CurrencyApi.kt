package com.dirzaaulia.loottrack.network

import com.dirzaaulia.loottrack.model.CurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val DefaultFallbackRates = mapOf(
    "idr" to 15800.0,
    "eur" to 0.92,
    "gbp" to 0.79,
    "jpy" to 152.0,
    "sgd" to 1.34,
    "myr" to 4.42,
    "aud" to 1.52,
    "cad" to 1.38
)

class CurrencyApi(engine: HttpClientEngine) {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getUsdExchangeRates(): Map<String, Double> {
        return try {
            val response: CurrencyResponse = client
                .get("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json")
                .body()
            response.usd.ifEmpty { DefaultFallbackRates }
        } catch (e: Exception) {
            try {
                val response: CurrencyResponse = client
                    .get("https://latest.currency-api.pages.dev/v1/currencies/usd.json")
                    .body()
                response.usd.ifEmpty { DefaultFallbackRates }
            } catch (_: Exception) {
                DefaultFallbackRates
            }
        }
    }
}
