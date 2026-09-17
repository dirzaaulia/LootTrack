package com.dirzaaulia.loottrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheapSharkStore(
    @SerialName("storeID") val storeId: String,
    @SerialName("storeName") val storeName: String,
    @SerialName("isActive") val isActive: Int = 1
)
