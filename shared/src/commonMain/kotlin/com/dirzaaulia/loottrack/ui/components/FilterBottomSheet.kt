package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.model.AppCurrency
import com.dirzaaulia.loottrack.model.CheapSharkStore
import com.dirzaaulia.loottrack.model.FilterOptions
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterOptions,
    stores: List<CheapSharkStore>,
    selectedCurrency: AppCurrency,
    onApplyFilter: (FilterOptions) -> Unit,
    onResetFilter: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    isSideSheet: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()

    val onCloseAction: () -> Unit = {
        if (!isSideSheet) {
            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
        } else {
            onDismiss()
        }
    }

    if (isSideSheet) {
        ModalSideSheet(onDismissRequest = onDismiss) {
            FilterSheetContent(
                currentFilter = currentFilter,
                stores = stores,
                selectedCurrency = selectedCurrency,
                onApplyFilter = { options ->
                    onApplyFilter(options)
                    onCloseAction()
                },
                onResetFilter = {
                    onResetFilter()
                    onCloseAction()
                },
                onClose = onCloseAction
            )
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RectangleShape
        ) {
            FilterSheetContent(
                currentFilter = currentFilter,
                stores = stores,
                selectedCurrency = selectedCurrency,
                onApplyFilter = { options ->
                    onApplyFilter(options)
                    onCloseAction()
                },
                onResetFilter = {
                    onResetFilter()
                    onCloseAction()
                },
                onClose = onCloseAction
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSheetContent(
    currentFilter: FilterOptions,
    stores: List<CheapSharkStore>,
    selectedCurrency: AppCurrency,
    onApplyFilter: (FilterOptions) -> Unit,
    onResetFilter: () -> Unit,
    onClose: () -> Unit
) {
    var selectedSortBy by remember { mutableStateOf(currentFilter.sortBy) }
    var lowerPriceText by remember { mutableStateOf(currentFilter.lowerPriceStr) }
    var upperPriceText by remember { mutableStateOf(currentFilter.upperPriceStr) }
    var selectedSteamRating by remember { mutableStateOf(currentFilter.minSteamRating) }
    var selectedMetacritic by remember { mutableStateOf(currentFilter.minMetacritic) }
    var selectedStoreId by remember { mutableStateOf(currentFilter.selectedStoreId) }
    var selectedOnSaleOnly by remember { mutableStateOf(currentFilter.onSaleOnly) }
    var queryTitleText by remember { mutableStateOf(currentFilter.queryTitle) }

    val sortOptions = listOf("Deal Rating", "Title", "Savings", "Price", "Metacritic", "Reviews", "Release", "Store", "Recent")
    val steamOptions = listOf(null to "ANY", 70 to "70%+", 80 to "80%+", 90 to "90%+")
    val metacriticOptions = listOf(null to "ANY", 60 to "60+", 75 to "75%+", 85 to "85%+")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Sheet Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ADVANCED FILTERS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "LOOTTRACK CATALOG FILTERS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = NeonPinkPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, NeonPinkPrimary)
                        .clickable(onClick = onClose)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CLOSE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = NeonPinkPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Game Title Search Input
            item {
                Column {
                    Text(
                        text = "FILTER BY GAME TITLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (queryTitleText.isEmpty()) {
                            Text("Filter game title...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        }
                        BasicTextField(
                            value = queryTitleText,
                            onValueChange = { queryTitleText = it },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                            cursorBrush = SolidColor(NeonPinkPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 2. Sort By
            item {
                Column {
                    Text(
                        text = "SORT BY PARAMETER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sortOptions.forEach { sort ->
                            val selected = selectedSortBy == sort
                            FilterChipPill(
                                label = sort.uppercase(),
                                selected = selected,
                                onClick = { selectedSortBy = sort }
                            )
                        }
                    }
                }
            }

            // 3. Custom Price Range (in user's currency)
            item {
                Column {
                    Text(
                        text = "CUSTOM PRICE RANGE [${selectedCurrency.code.uppercase()}]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Min Price
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MIN PRICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (lowerPriceText.isEmpty()) Text("0", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                                BasicTextField(
                                    value = lowerPriceText,
                                    onValueChange = { lowerPriceText = it },
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPinkPrimary),
                                    cursorBrush = SolidColor(NeonPinkPrimary)
                                )
                            }
                        }

                        // Max Price
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MAX PRICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (upperPriceText.isEmpty()) Text("No Limit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                                BasicTextField(
                                    value = upperPriceText,
                                    onValueChange = { upperPriceText = it },
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPinkPrimary),
                                    cursorBrush = SolidColor(NeonPinkPrimary)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Store Filter (CheapShark /stores) - ALL Stores listed
            if (stores.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "STORE PLATFORM (${stores.size} STORES)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val allSelected = selectedStoreId == null
                            FilterChipPill(
                                label = "ALL STORES",
                                selected = allSelected,
                                onClick = { selectedStoreId = null }
                            )

                            stores.forEach { store ->
                                val selected = selectedStoreId == store.storeId
                                FilterChipPill(
                                    label = store.storeName.uppercase(),
                                    selected = selected,
                                    onClick = { selectedStoreId = store.storeId }
                                )
                            }
                        }
                    }
                }
            }

            // 5. Minimum Steam Rating
            item {
                Column {
                    Text(
                        text = "MINIMUM STEAM RATING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        steamOptions.forEach { (rating, label) ->
                            val selected = selectedSteamRating == rating
                            FilterChipPill(
                                label = label,
                                selected = selected,
                                onClick = { selectedSteamRating = rating }
                            )
                        }
                    }
                }
            }

            // 6. Minimum Metacritic Score
            item {
                Column {
                    Text(
                        text = "MINIMUM METACRITIC SCORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        metacriticOptions.forEach { (score, label) ->
                            val selected = selectedMetacritic == score
                            FilterChipPill(
                                label = label,
                                selected = selected,
                                onClick = { selectedMetacritic = score }
                            )
                        }
                    }
                }
            }

            // 7. On Sale Only Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ONLY SHOW ITEMS ON SALE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FilterChipPill(
                        label = if (selectedOnSaleOnly) "ON SALE ONLY [ACTIVE]" else "SHOW ALL",
                        selected = selectedOnSaleOnly,
                        onClick = { selectedOnSaleOnly = !selectedOnSaleOnly }
                    )
                }
            }

            // 8. Action Buttons (Apply / Reset)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .clickable {
                                selectedSortBy = "Deal Rating"
                                lowerPriceText = ""
                                upperPriceText = ""
                                selectedSteamRating = null
                                selectedMetacritic = null
                                selectedStoreId = null
                                selectedOnSaleOnly = false
                                queryTitleText = ""
                                onResetFilter()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RESET",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .border(1.dp, NeonPinkPrimary)
                            .background(NeonPinkPrimary)
                            .clickable {
                                val options = FilterOptions(
                                    sortBy = selectedSortBy,
                                    lowerPriceStr = lowerPriceText,
                                    upperPriceStr = upperPriceText,
                                    minSteamRating = selectedSteamRating,
                                    minMetacritic = selectedMetacritic,
                                    selectedStoreId = selectedStoreId,
                                    onSaleOnly = selectedOnSaleOnly,
                                    queryTitle = queryTitleText
                                )
                                onApplyFilter(options)
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "APPLY FILTERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = if (selected) 1.dp else 0.5.dp,
                color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
            .background(if (selected) NeonPinkPrimary.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
            letterSpacing = 1.sp,
            color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
