package com.dirzaaulia.loottrack.ui.deals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.ui.components.AbstractCategoryBar
import com.dirzaaulia.loottrack.ui.components.AbstractEditorialHeader
import com.dirzaaulia.loottrack.ui.components.AbstractFloatingBottomBar
import com.dirzaaulia.loottrack.ui.components.AbstractSearchBox
import com.dirzaaulia.loottrack.ui.components.CurrencyBottomSheet
import com.dirzaaulia.loottrack.ui.components.DesktopTopNavBar
import com.dirzaaulia.loottrack.ui.components.FilterBottomSheet
import com.dirzaaulia.loottrack.ui.components.GameDetailBottomSheet
import com.dirzaaulia.loottrack.ui.components.GameDetailModal
import com.dirzaaulia.loottrack.ui.components.PriceAlertBottomSheet
import com.dirzaaulia.loottrack.ui.deals.components.DesktopFilterSidebar
import com.dirzaaulia.loottrack.ui.deals.components.FlashDealsCarousel
import com.dirzaaulia.loottrack.ui.deals.components.GridDealAdCard
import com.dirzaaulia.loottrack.ui.deals.components.GridDealCard
import com.dirzaaulia.loottrack.ui.deals.components.HeroCarousel
import com.dirzaaulia.loottrack.ui.deals.components.SavedAlertsWatchlist
import com.dirzaaulia.loottrack.ui.info.InfoScreen
import com.dirzaaulia.loottrack.viewmodel.DealsUiState
import com.dirzaaulia.loottrack.viewmodel.DealsViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: DealsViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeCategory by viewModel.activeCategory.collectAsStateWithLifecycle()
    val filterOptions by viewModel.filterOptions.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val stores by viewModel.stores.collectAsStateWithLifecycle()
    val savedAlerts by viewModel.savedAlerts.collectAsStateWithLifecycle()

    var activeNavIndex by remember { mutableStateOf(0) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedAlertDeal by remember { mutableStateOf<CheapSharkDeal?>(null) }
    var selectedDetailDeal by remember { mutableStateOf<CheapSharkDeal?>(null) }
    var openedFromDetailDeal by remember { mutableStateOf<CheapSharkDeal?>(null) }

    val bgGradient = if (isDarkTheme) {
        Brush.verticalGradient(
            listOf(Color(0xFF090710), Color(0xFF0F0C1B), Color(0xFF07050D))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFF6F4F8), Color(0xFFECE7F2), Color(0xFFF4F0F8))
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        val isWideScreen = maxWidth > 840.dp
        val showSidebar = maxWidth > 1150.dp
        val gridColumnCount = if (maxWidth > 1500.dp) 4 else if (maxWidth > 1150.dp) 3 else if (isWideScreen) 3 else 2

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            ) {
                // Top Navigation Bar
                if (isWideScreen) {
                    DesktopTopNavBar(
                        selectedNavIndex = activeNavIndex,
                        searchQuery = searchQuery,
                        selectedCurrency = selectedCurrency,
                        isDarkTheme = isDarkTheme,
                        onSearchQueryChange = { query -> viewModel.onSearchQueryChanged(query) },
                        onSelectNavIndex = { activeNavIndex = it },
                        onOpenCurrencySheet = { showCurrencySheet = true }
                    )
                } else {
                    AbstractEditorialHeader(
                        isDarkTheme = isDarkTheme,
                        selectedCurrency = selectedCurrency,
                        onToggleTheme = onToggleTheme,
                        onOpenCurrencySheet = { showCurrencySheet = true }
                    )
                }

                // Main Content View Area
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (isWideScreen) 16.dp else 0.dp)
                ) {
                    // Left / Main Section
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        when (activeNavIndex) {
                            1 -> {
                                // SAVED WATCHLIST TAB
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = if (isWideScreen) 20.dp else 70.dp)
                                ) {
                                    SavedAlertsWatchlist(
                                        alerts = savedAlerts,
                                        formatPrice = { price -> viewModel.formatPrice(price) },
                                        onDeleteAlert = { alert -> viewModel.deletePriceAlert(alert) }
                                    )
                                }
                            }
                            2 -> {
                                // APP & DEVELOPER INFO TAB
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = if (isWideScreen) 20.dp else 70.dp)
                                ) {
                                    InfoScreen()
                                }
                            }
                            else -> {
                                // DEALS CATALOG TAB
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Mobile search & categories bar
                                    if (!isWideScreen) {
                                        AbstractSearchBox(
                                            query = searchQuery,
                                            isFiltered = filterOptions.isFiltered(),
                                            onQueryChange = { query ->
                                                viewModel.onSearchQueryChanged(query)
                                            },
                                            onOpenFilterSheet = { showFilterSheet = true }
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        AbstractCategoryBar(
                                            activeCategory = activeCategory,
                                            isFiltered = filterOptions.isFiltered(),
                                            formatPrice = { price -> viewModel.formatPrice(price) },
                                            onSelectCategory = { cat ->
                                                viewModel.setCategory(cat)
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                    } else if (!showSidebar) {
                                        // Medium wide screens (without sidebar)
                                        AbstractCategoryBar(
                                            activeCategory = activeCategory,
                                            isFiltered = filterOptions.isFiltered(),
                                            formatPrice = { price -> viewModel.formatPrice(price) },
                                            onSelectCategory = { cat ->
                                                viewModel.setCategory(cat)
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    Box(modifier = Modifier.weight(1f)) {
                                        when (val current = state) {
                                            is DealsUiState.Loading -> {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = NeonPinkPrimary,
                                                        strokeWidth = 2.dp,
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(14.dp))
                                                    Text(
                                                        text = "LOADING DEALS ENGINE",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 2.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            is DealsUiState.Error -> {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "UNABLE TO LOAD DEALS",
                                                        color = MaterialTheme.colorScheme.error,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 1.sp
                                                    )
                                                }
                                            }
                                            is DealsUiState.Success -> {
                                                if (current.deals.isEmpty()) {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "NO MATCHING DEALS FOUND",
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            fontSize = 11.sp,
                                                            letterSpacing = 1.5.sp
                                                        )
                                                    }
                                                } else {
                                                    LazyVerticalGrid(
                                                        columns = GridCells.Fixed(gridColumnCount),
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentPadding = PaddingValues(
                                                            start = if (isWideScreen) 8.dp else 16.dp,
                                                            end = if (isWideScreen) 8.dp else 16.dp,
                                                            bottom = if (isWideScreen) 140.dp else 140.dp
                                                        ),
                                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                                    ) {
                                                        // Featured Hero Drop & Sponsored Ad Auto-Cycling Carousel
                                                        current.featuredDeal?.let { featured ->
                                                            item(span = { GridItemSpan(gridColumnCount) }) {
                                                                HeroCarousel(
                                                                    featuredDeal = featured,
                                                                    formattedSalePrice = viewModel.formatPrice(featured.salePrice),
                                                                    formattedNormalPrice = viewModel.formatPrice(featured.normalPrice),
                                                                    isAlertSet = viewModel.isAlertSet(featured),
                                                                    onOpenAlertModal = { deal -> selectedAlertDeal = deal }
                                                                )
                                                            }
                                                        }

                                                        // Flash Deals Carousel Row (Contains inline FlashDealAdCard)
                                                        item(span = { GridItemSpan(gridColumnCount) }) {
                                                            FlashDealsCarousel(
                                                                deals = current.deals,
                                                                formatPrice = { price -> viewModel.formatPrice(price) },
                                                                isAlertSet = { deal -> viewModel.isAlertSet(deal) },
                                                                onDealClick = { deal -> selectedDetailDeal = deal }
                                                            )
                                                        }

                                                        // All Deals Section Header
                                                        item(span = { GridItemSpan(gridColumnCount) }) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                                                    .border(
                                                                        width = 0.5.dp,
                                                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                                                    )
                                                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                                            ) {
                                                                Text(
                                                                    text = "ALL DEALS CATALOG [${selectedCurrency.code.uppercase()}]",
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Black,
                                                                    letterSpacing = 2.sp,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                )
                                                            }
                                                        }

                                                        // Deals Grid Cards with GridDealAdCard spanning full grid width for ample room
                                                        val dealChunks = current.deals.chunked(8)
                                                        dealChunks.forEachIndexed { chunkIndex, chunkDeals ->
                                                            items(chunkDeals, key = { deal -> "deal_${deal.dealId}" }) { deal ->
                                                                val globalIndex = current.deals.indexOf(deal)
                                                                if (globalIndex >= current.deals.size - 4 && !current.isLastPage && !current.isLoadingNextPage) {
                                                                    LaunchedEffect(globalIndex) {
                                                                        viewModel.loadNextPage()
                                                                    }
                                                                }

                                                                GridDealCard(
                                                                    deal = deal,
                                                                    formattedSalePrice = viewModel.formatPrice(deal.salePrice),
                                                                    formattedNormalPrice = viewModel.formatPrice(deal.normalPrice),
                                                                    isAlertSet = viewModel.isAlertSet(deal),
                                                                    onClick = { selectedDetailDeal = deal }
                                                                )
                                                            }

                                                            if (chunkIndex < dealChunks.size - 1) {
                                                                item(span = { GridItemSpan(gridColumnCount) }) {
                                                                    GridDealAdCard()
                                                                }
                                                            }
                                                        }

                                                        if (current.isLoadingNextPage) {
                                                            item(span = { GridItemSpan(gridColumnCount) }) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(top = 12.dp, bottom = 24.dp),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    CircularProgressIndicator(
                                                                        color = NeonPinkPrimary,
                                                                        strokeWidth = 2.dp,
                                                                        modifier = Modifier.size(24.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Desktop Embedded Filter & Watchlist Sidebar
                    if (showSidebar && activeNavIndex == 0) {
                        Spacer(modifier = Modifier.width(14.dp))
                        DesktopFilterSidebar(
                            filterOptions = filterOptions,
                            activeCategory = activeCategory,
                            stores = stores,
                            selectedCurrency = selectedCurrency,
                            savedAlerts = savedAlerts,
                            formatPrice = { price -> viewModel.formatPrice(price) },
                            onSelectCategory = { cat -> viewModel.setCategory(cat) },
                            onApplyFilter = { options -> viewModel.applyFilterOptions(options) },
                            onResetFilter = { viewModel.resetFilterOptions() },
                            onDeleteAlert = { alert -> viewModel.deletePriceAlert(alert) },
                            modifier = Modifier
                                .width(340.dp)
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Floating bottom bar on mobile / compact screens
            if (!isWideScreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    AbstractFloatingBottomBar(
                        selectedIndex = activeNavIndex,
                        onSelect = { activeNavIndex = it }
                    )
                }
            }
        }

        if (showCurrencySheet) {
            CurrencyBottomSheet(
                selectedCurrency = selectedCurrency,
                onSelectCurrency = { currency ->
                    viewModel.selectCurrency(currency)
                },
                onDismiss = { showCurrencySheet = false }
            )
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                currentFilter = filterOptions,
                stores = stores,
                selectedCurrency = selectedCurrency,
                onApplyFilter = { options ->
                    viewModel.applyFilterOptions(options)
                },
                onResetFilter = {
                    viewModel.resetFilterOptions()
                },
                onDismiss = { showFilterSheet = false }
            )
        }

        selectedDetailDeal?.let { deal ->
            if (isWideScreen) {
                GameDetailModal(
                    deal = deal,
                    stores = stores,
                    isAlertSet = viewModel.isAlertSet(deal),
                    existingAlert = viewModel.getSavedAlert(deal),
                    onFetchGameDetails = { gameId -> viewModel.fetchGameDetails(gameId) },
                    formatPrice = { price -> viewModel.formatPrice(price) },
                    onOpenAlertModal = { alertDeal ->
                        openedFromDetailDeal = deal
                        selectedDetailDeal = null
                        selectedAlertDeal = alertDeal
                    },
                    onDismiss = { selectedDetailDeal = null }
                )
            } else {
                GameDetailBottomSheet(
                    deal = deal,
                    stores = stores,
                    isAlertSet = viewModel.isAlertSet(deal),
                    existingAlert = viewModel.getSavedAlert(deal),
                    onFetchGameDetails = { gameId ->
                        viewModel.fetchGameDetails(gameId)
                    },
                    formatPrice = { price -> viewModel.formatPrice(price) },
                    onOpenAlertModal = { alertDeal ->
                        openedFromDetailDeal = deal
                        selectedDetailDeal = null
                        selectedAlertDeal = alertDeal
                    },
                    onDismiss = { selectedDetailDeal = null }
                )
            }
        }

        selectedAlertDeal?.let { deal ->
            PriceAlertBottomSheet(
                deal = deal,
                savedEmail = viewModel.savedEmail,
                selectedCurrency = selectedCurrency,
                existingAlert = viewModel.getSavedAlert(deal),
                onSetAlert = { email, targetPrice, onResult ->
                    viewModel.registerFreePriceAlert(
                        deal = deal,
                        email = email,
                        targetPriceInput = targetPrice,
                        onResult = onResult
                    )
                },
                onSuccess = {
                    val prevDetail = openedFromDetailDeal
                    selectedAlertDeal = null
                    openedFromDetailDeal = null
                    if (prevDetail != null) {
                        selectedDetailDeal = prevDetail
                    }
                },
                onDismiss = {
                    selectedAlertDeal = null
                    openedFromDetailDeal = null
                }
            )
        }
    }
}
