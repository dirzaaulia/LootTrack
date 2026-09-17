package com.dirzaaulia.loottrack.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedAlert(
    val gameId: String,
    val dealId: String,
    val gameTitle: String,
    val thumb: String,
    val targetPriceUsd: String,
    val initialSalePriceUsd: String,
    val userEmail: String,
    val createdAtTimestamp: Long = 0L
)
