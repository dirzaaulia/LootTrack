package com.dirzaaulia.loottrack.model

data class FilterOptions(
    val sortBy: String = "Deal Rating",
    val lowerPriceStr: String = "",
    val upperPriceStr: String = "",
    val minSteamRating: Int? = null,
    val minMetacritic: Int? = null,
    val selectedStoreId: String? = null,
    val onSaleOnly: Boolean = false,
    val queryTitle: String = ""
) {
    fun isFiltered(): Boolean {
        return sortBy != "Deal Rating" ||
                lowerPriceStr.isNotBlank() ||
                upperPriceStr.isNotBlank() ||
                minSteamRating != null ||
                minMetacritic != null ||
                selectedStoreId != null ||
                onSaleOnly ||
                queryTitle.isNotBlank()
    }
}
