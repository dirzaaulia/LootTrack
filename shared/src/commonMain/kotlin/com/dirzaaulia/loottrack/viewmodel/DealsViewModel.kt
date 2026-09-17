package com.dirzaaulia.loottrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dirzaaulia.loottrack.data.PreferenceStorage
import com.dirzaaulia.loottrack.data.getSystemDefaultCurrency
import com.dirzaaulia.loottrack.model.AppCurrency
import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.model.CheapSharkGameDetail
import com.dirzaaulia.loottrack.model.CheapSharkStore
import com.dirzaaulia.loottrack.model.FilterOptions
import com.dirzaaulia.loottrack.model.SavedAlert
import com.dirzaaulia.loottrack.network.CheapSharkApi
import com.dirzaaulia.loottrack.network.CurrencyApi
import com.dirzaaulia.loottrack.paging.LootTrackPagingSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class DealCategoryFilter(val label: String, val sortBy: String, val maxPriceUsd: Int? = null) {
    TOP_RATED("Top Rated", "Deal Rating"),
    BEST_SAVINGS("Best Savings", "Savings"),
    UNDER_10("Under $10", "Price", maxPriceUsd = 10),
    RECENT("Recent", "Recent")
}

sealed interface DealsUiState {
    data object Loading : DealsUiState
    data class Success(
        val deals: List<CheapSharkDeal>,
        val featuredDeal: CheapSharkDeal? = null,
        val isLastPage: Boolean = false,
        val isLoadingNextPage: Boolean = false
    ) : DealsUiState
    data class Error(val message: String) : DealsUiState
}

class DealsViewModel(
    private val api: CheapSharkApi,
    private val currencyApi: CurrencyApi,
    private val preferenceStorage: PreferenceStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<DealsUiState>(DealsUiState.Loading)
    val uiState: StateFlow<DealsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeCategory = MutableStateFlow(DealCategoryFilter.TOP_RATED)
    val activeCategory: StateFlow<DealCategoryFilter> = _activeCategory.asStateFlow()

    private val _filterOptions = MutableStateFlow(FilterOptions())
    val filterOptions: StateFlow<FilterOptions> = _filterOptions.asStateFlow()

    private val _stores = MutableStateFlow<List<CheapSharkStore>>(emptyList())
    val stores: StateFlow<List<CheapSharkStore>> = _stores.asStateFlow()

    private val _savedAlerts = MutableStateFlow<List<SavedAlert>>(emptyList())
    val savedAlerts: StateFlow<List<SavedAlert>> = _savedAlerts.asStateFlow()

    // Multiplatform Paging Engine
    private val pagingSource = LootTrackPagingSource<CheapSharkDeal> { page ->
        val filter = _filterOptions.value
        val activeQuery = _searchQuery.value.ifBlank { null } ?: filter.queryTitle.ifBlank { null }

        val lowerUsd = convertLocalPriceToUsd(filter.lowerPriceStr)?.toInt()
        val upperUsd = convertLocalPriceToUsd(filter.upperPriceStr)?.toInt()

        api.searchDeals(
            query = activeQuery,
            sortBy = filter.sortBy,
            lowerPrice = lowerUsd,
            upperPrice = upperUsd,
            steamRating = filter.minSteamRating,
            metacritic = filter.minMetacritic,
            storeId = filter.selectedStoreId,
            onSale = if (filter.onSaleOnly) true else null,
            pageNumber = page
        )
    }

    // Currency Management
    private val initialCurrencyCode = preferenceStorage.getString("selected_currency", "")
    private val defaultCurrency = if (initialCurrencyCode.isNotBlank()) {
        AppCurrency.fromCode(initialCurrencyCode)
    } else {
        getSystemDefaultCurrency().also {
            preferenceStorage.saveString("selected_currency", it.code)
        }
    }

    private val _selectedCurrency = MutableStateFlow(defaultCurrency)
    val selectedCurrency: StateFlow<AppCurrency> = _selectedCurrency.asStateFlow()

    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val exchangeRates: StateFlow<Map<String, Double>> = _exchangeRates.asStateFlow()

    val savedEmail: String
        get() = preferenceStorage.getString("user_email", "")

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    init {
        loadSavedAlerts()
        fetchStores()
        fetchExchangeRates()

        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            _searchQuery
                .debounce(400L)
                .collect { query ->
                    fetchDeals(query = query, resetPage = true)
                }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun loadSavedAlerts() {
        val jsonStr = preferenceStorage.getString("saved_alerts_json", "")
        if (jsonStr.isNotBlank()) {
            try {
                val list = json.decodeFromString<List<SavedAlert>>(jsonStr)
                _savedAlerts.value = list
            } catch (e: Exception) {
                _savedAlerts.value = emptyList()
            }
        }
    }

    private fun persistSavedAlerts(list: List<SavedAlert>) {
        _savedAlerts.value = list
        try {
            val jsonStr = json.encodeToString(list)
            preferenceStorage.saveString("saved_alerts_json", jsonStr)
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun fetchStores() {
        viewModelScope.launch {
            val activeStores = api.getStores().filter { it.isActive == 1 }
            _stores.value = activeStores
        }
    }

    private fun fetchExchangeRates() {
        viewModelScope.launch {
            val rates = currencyApi.getUsdExchangeRates()
            _exchangeRates.value = rates
            // Re-render UI to update currency formatting immediately
            if (_uiState.value is DealsUiState.Success) {
                fetchDeals(query = _searchQuery.value, resetPage = false)
            }
        }
    }

    suspend fun fetchGameDetails(gameId: String): CheapSharkGameDetail? {
        return api.getGameDetails(gameId)
    }

    fun isAlertSet(deal: CheapSharkDeal): Boolean {
        val gId = deal.gameId?.takeIf { it.isNotBlank() } ?: deal.dealId
        return _savedAlerts.value.any { it.gameId == gId || it.dealId == deal.dealId }
    }

    fun getSavedAlert(deal: CheapSharkDeal): SavedAlert? {
        val gId = deal.gameId?.takeIf { it.isNotBlank() } ?: deal.dealId
        return _savedAlerts.value.firstOrNull { it.gameId == gId || it.dealId == deal.dealId }
    }

    fun selectCurrency(currency: AppCurrency) {
        _selectedCurrency.value = currency
        preferenceStorage.saveString("selected_currency", currency.code)
    }

    fun setCategory(category: DealCategoryFilter) {
        _activeCategory.value = category
        _filterOptions.value = _filterOptions.value.copy(
            sortBy = category.sortBy,
            upperPriceStr = category.maxPriceUsd?.toString() ?: ""
        )
        fetchDeals(query = _searchQuery.value, resetPage = true)
    }

    fun applyFilterOptions(filter: FilterOptions) {
        _filterOptions.value = filter
        fetchDeals(query = _searchQuery.value, resetPage = true)
    }

    fun resetFilterOptions() {
        val defaultCategory = _activeCategory.value
        _filterOptions.value = FilterOptions(
            sortBy = defaultCategory.sortBy,
            upperPriceStr = defaultCategory.maxPriceUsd?.toString() ?: ""
        )
        fetchDeals(query = _searchQuery.value, resetPage = true)
    }

    private fun convertLocalPriceToUsd(localPriceStr: String): Double? {
        val localVal = localPriceStr.toDoubleOrNull() ?: return null
        val currentCurrency = _selectedCurrency.value
        if (currentCurrency == AppCurrency.USD) return localVal

        val rates = _exchangeRates.value
        val rate = rates[currentCurrency.code.lowercase()] ?: rates[currentCurrency.code] ?: return null
        if (rate <= 0) return null

        return localVal / rate
    }

    fun fetchDeals(query: String? = _searchQuery.value, resetPage: Boolean = true) {
        if (resetPage) {
            pagingSource.reset()
            _uiState.value = DealsUiState.Loading
        }

        viewModelScope.launch {
            try {
                pagingSource.loadNextPage()
                val deals = pagingSource.itemsFlow.value
                val featured = deals.maxByOrNull { it.savings.toFloatOrNull() ?: 0f } ?: deals.firstOrNull()

                _uiState.value = DealsUiState.Success(
                    deals = deals,
                    featuredDeal = featured,
                    isLastPage = pagingSource.isLastPage,
                    isLoadingNextPage = pagingSource.isLoadingPage.value
                )
            } catch (e: Exception) {
                if (resetPage) {
                    _uiState.value = DealsUiState.Error(e.message ?: "Failed to fetch deals")
                }
            }
        }
    }

    fun loadNextPage() {
        if (!pagingSource.isLastPage) {
            viewModelScope.launch {
                pagingSource.loadNextPage()
                val deals = pagingSource.itemsFlow.value
                val featured = deals.maxByOrNull { it.savings.toFloatOrNull() ?: 0f } ?: deals.firstOrNull()

                _uiState.value = DealsUiState.Success(
                    deals = deals,
                    featuredDeal = featured,
                    isLastPage = pagingSource.isLastPage,
                    isLoadingNextPage = false
                )
            }
        }
    }

    fun registerFreePriceAlert(
        deal: CheapSharkDeal,
        email: String,
        targetPriceInput: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            preferenceStorage.saveString("user_email", email)
            val gameId = deal.gameId?.takeIf { it.isNotBlank() } ?: deal.dealId

            // Convert target price from local currency input back to USD string
            val usdTargetPriceVal = convertLocalPriceToUsd(targetPriceInput)
            val usdTargetPriceStr = if (usdTargetPriceVal != null) {
                val rounded = ((usdTargetPriceVal * 100).toInt() / 100.0)
                rounded.toString()
            } else {
                targetPriceInput
            }

            val newAlert = SavedAlert(
                gameId = gameId,
                dealId = deal.dealId,
                gameTitle = deal.title,
                thumb = deal.thumb,
                targetPriceUsd = usdTargetPriceStr,
                initialSalePriceUsd = deal.salePrice,
                userEmail = email
            )
            val currentList = _savedAlerts.value.filterNot { it.gameId == gameId }.toMutableList()
            currentList.add(0, newAlert)
            persistSavedAlerts(currentList)

            // Register directly with LootTrack Cloudflare Server Infrastructure
            api.registerLootTrackServerAlert(email = email, gameId = gameId, targetPriceUsd = usdTargetPriceStr)

            onResult(true)
        }
    }

    fun deletePriceAlert(alert: SavedAlert) {
        viewModelScope.launch {
            api.deleteLootTrackServerAlert(email = alert.userEmail, gameId = alert.gameId)
            val updated = _savedAlerts.value.filterNot { it.gameId == alert.gameId }
            persistSavedAlerts(updated)
        }
    }

    fun formatPrice(usdPriceStr: String, targetCurrency: AppCurrency = _selectedCurrency.value): String {
        val usdValue = usdPriceStr.toDoubleOrNull() ?: return if (usdPriceStr == "0" || usdPriceStr == "0.00") "FREE" else "$$usdPriceStr"
        if (usdValue == 0.0) return "FREE"
        if (targetCurrency == AppCurrency.USD) return "$$usdPriceStr"

        val rates = _exchangeRates.value
        val rate = rates[targetCurrency.code.lowercase()] ?: rates[targetCurrency.code] ?: return "$$usdPriceStr"
        val converted = usdValue * rate

        return when (targetCurrency) {
            AppCurrency.IDR -> {
                val rounded = converted.toInt()
                val formatted = rounded.toString().reversed().chunked(3).joinToString(".").reversed()
                "Rp $formatted"
            }
            AppCurrency.JPY -> "JPY ${converted.toInt()}"
            AppCurrency.EUR -> "EUR ${(converted * 100).toInt() / 100.0}"
            AppCurrency.GBP -> "GBP ${(converted * 100).toInt() / 100.0}"
            else -> "${targetCurrency.symbol}${(converted * 100).toInt() / 100.0}"
        }
    }
}
