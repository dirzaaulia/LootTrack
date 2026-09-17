package com.dirzaaulia.loottrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheapSharkGameDetail(
    val info: GameInfo? = null,
    val cheapestPriceEver: CheapestPriceEver? = null,
    val deals: List<GameDealInfo> = emptyList()
)

@Serializable
data class GameInfo(
    val title: String = "",
    @SerialName("steamAppID") val steamAppId: String? = null,
    val thumb: String = ""
)

@Serializable
data class CheapestPriceEver(
    val price: String = "",
    val date: Long = 0
)

@Serializable
data class GameDealInfo(
    @SerialName("dealID") val dealId: String,
    @SerialName("storeID") val storeId: String,
    val price: String,
    val retailPrice: String,
    val savings: String
)
