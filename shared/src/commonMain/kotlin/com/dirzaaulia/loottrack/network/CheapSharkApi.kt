package com.dirzaaulia.loottrack.network

import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.model.CheapSharkGameDetail
import com.dirzaaulia.loottrack.model.CheapSharkStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLQueryComponent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CheapSharkApi(engine: HttpClientEngine) {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getStores(): List<CheapSharkStore> {
        return try {
            client.get("https://www.cheapshark.com/api/1.0/stores").body()
        } catch (e: Exception) {
            try {
                val proxyUrl = "https://api.allorigins.win/raw?url=" + "https://www.cheapshark.com/api/1.0/stores".encodeURLQueryComponent()
                client.get(proxyUrl).body()
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getGameDetails(gameId: String): CheapSharkGameDetail? {
        return try {
            client.get("https://www.cheapshark.com/api/1.0/games") {
                parameter("id", gameId)
            }.body()
        } catch (e: Exception) {
            try {
                val target = "https://www.cheapshark.com/api/1.0/games?id=$gameId"
                val proxyUrl = "https://api.allorigins.win/raw?url=" + target.encodeURLQueryComponent()
                client.get(proxyUrl).body()
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun searchDeals(
        query: String? = null,
        sortBy: String = "Deal Rating",
        lowerPrice: Int? = null,
        upperPrice: Int? = null,
        steamRating: Int? = null,
        metacritic: Int? = null,
        storeId: String? = null,
        onSale: Boolean? = null,
        pageNumber: Int = 0
    ): List<CheapSharkDeal> {
        return try {
            client.get("https://www.cheapshark.com/api/1.0/deals") {
                query?.takeIf { it.isNotBlank() }?.let { parameter("title", it) }
                parameter("sortBy", sortBy)
                lowerPrice?.let { parameter("lowerPrice", it) }
                upperPrice?.let { parameter("upperPrice", it) }
                steamRating?.let { parameter("steamRating", it) }
                metacritic?.let { parameter("metacritic", it) }
                storeId?.takeIf { it.isNotBlank() }?.let { parameter("storeID", it) }
                onSale?.let { parameter("onSale", if (it) 1 else 0) }
                parameter("pageNumber", pageNumber)
                parameter("pageSize", 24)
            }.body()
        } catch (e: Exception) {
            try {
                val sb = StringBuilder("https://www.cheapshark.com/api/1.0/deals?pageSize=24&pageNumber=$pageNumber&sortBy=" + sortBy.encodeURLQueryComponent())
                query?.takeIf { it.isNotBlank() }?.let { sb.append("&title=").append(it.encodeURLQueryComponent()) }
                lowerPrice?.let { sb.append("&lowerPrice=").append(it) }
                upperPrice?.let { sb.append("&upperPrice=").append(it) }
                steamRating?.let { sb.append("&steamRating=").append(it) }
                metacritic?.let { sb.append("&metacritic=").append(it) }
                storeId?.takeIf { it.isNotBlank() }?.let { sb.append("&storeID=").append(it) }
                onSale?.let { sb.append("&onSale=").append(if (it) 1 else 0) }

                val proxyUrl = "https://api.allorigins.win/raw?url=" + sb.toString().encodeURLQueryComponent()
                client.get(proxyUrl).body()
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    suspend fun registerLootTrackServerAlert(email: String, gameId: String, targetPriceUsd: String): Boolean {
        return try {
            client.post("https://loottrack-worker.dirzaaulia11.workers.dev/api/alerts/register") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("gameId" to gameId, "userEmail" to email, "targetPriceUsd" to targetPriceUsd))
            }
            true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun deleteLootTrackServerAlert(email: String, gameId: String): Boolean {
        return try {
            client.post("https://loottrack-worker.dirzaaulia11.workers.dev/api/alerts/delete") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("gameId" to gameId, "userEmail" to email))
            }
            true
        } catch (e: Exception) {
            true
        }
    }
}
