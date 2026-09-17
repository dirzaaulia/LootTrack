package com.dirzaaulia.loottrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheapSharkDeal(
    @SerialName("dealID") val dealId: String,
    @SerialName("gameID") val gameId: String? = null,
    val title: String,
    val salePrice: String,
    val normalPrice: String,
    val savings: String,
    val dealRating: String,
    val thumb: String,
    val metacriticScore: String? = null,
    val steamRatingPercent: String? = null
)
