package com.dirzaaulia.loottrack.ui.deals.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dirzaaulia.loottrack.model.AppCurrency
import com.dirzaaulia.loottrack.model.CheapSharkStore
import com.dirzaaulia.loottrack.model.FilterOptions
import com.dirzaaulia.loottrack.model.SavedAlert
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.viewmodel.DealCategoryFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesktopFilterSidebar(
    filterOptions: FilterOptions,
    activeCategory: DealCategoryFilter,
    stores: List<CheapSharkStore>,
    selectedCurrency: AppCurrency,
    savedAlerts: List<SavedAlert>,
    formatPrice: (String) -> String,
    onSelectCategory: (DealCategoryFilter) -> Unit,
    onApplyFilter: (FilterOptions) -> Unit,
    onResetFilter: () -> Unit,
    onDeleteAlert: (SavedAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        // Sidebar Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DASHBOARD FILTERS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "REAL-TIME PARAMETERS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = NeonPinkPrimary
                )
            }

            if (filterOptions.isFiltered()) {
                Box(
                    modifier = Modifier
                        .border(1.dp, NeonPinkPrimary)
                        .clickable(onClick = onResetFilter)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "RESET",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonPinkPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Quick Presets (Top Rated, Best Savings, Under $10, Recent)
            item {
                Column {
                    Text(
                        text = "CATEGORY PRESETS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DealCategoryFilter.entries.forEach { category ->
                            val selected = activeCategory == category
                            val labelText = if (category == DealCategoryFilter.UNDER_10) {
                                "UNDER ${formatPrice("10")}"
                            } else {
                                category.label.uppercase()
                            }
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = if (selected) 1.dp else 0.5.dp,
                                        color = if (selected) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                    .background(if (selected) CyanAccent.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { onSelectCategory(category) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = labelText,
                                    fontSize = 8.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = if (selected) CyanAccent else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Quick Active Watchlist Section (If user has saved alerts)
            if (savedAlerts.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f))
                            .background(CyanAccent.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔔 ACTIVE WATCHLIST (${savedAlerts.size})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = CyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        savedAlerts.take(3).forEach { alert ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AsyncImage(
                                        model = alert.thumb,
                                        contentDescription = alert.gameTitle,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .border(0.5.dp, CyanAccent)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = alert.gameTitle.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "TARGET: ${formatPrice(alert.targetPriceUsd)}",
                                            fontSize = 8.sp,
                                            color = CyanAccent
                                        )
                                    }
                                }

                                Text(
                                    text = "✕",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonPinkPrimary,
                                    modifier = Modifier
                                        .clickable { onDeleteAlert(alert) }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Sort By Options
            item {
                Column {
                    Text(
                        text = "SORT DEALS BY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val sortOptions = listOf("Deal Rating", "Savings", "Price", "Title", "Metacritic")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sortOptions.forEach { sort ->
                            val selected = filterOptions.sortBy == sort
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = if (selected) 1.dp else 0.5.dp,
                                        color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                    .background(if (selected) NeonPinkPrimary else Color.Transparent)
                                    .clickable { onApplyFilter(filterOptions.copy(sortBy = sort)) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = sort.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Store Filter Grid
            item {
                Column {
                    Text(
                        text = "STORE PLATFORMS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // "ALL STORES" option
                        val allSelected = filterOptions.selectedStoreId == null
                        Box(
                            modifier = Modifier
                                .border(
                                    width = if (allSelected) 1.dp else 0.5.dp,
                                    color = if (allSelected) ElectricPurpleSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                                .background(if (allSelected) ElectricPurpleSecondary else Color.Transparent)
                                .clickable { onApplyFilter(filterOptions.copy(selectedStoreId = null)) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "ALL STORES",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (allSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        stores.take(12).forEach { store ->
                            val selected = filterOptions.selectedStoreId == store.storeId
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = if (selected) 1.dp else 0.5.dp,
                                        color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                    .background(if (selected) NeonPinkPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        val newId = if (selected) null else store.storeId
                                        onApplyFilter(filterOptions.copy(selectedStoreId = newId))
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = store.storeName.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                    color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Price Range Inputs
            item {
                Column {
                    Text(
                        text = "PRICE RANGE (${selectedCurrency.symbol})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (filterOptions.lowerPriceStr.isEmpty()) {
                                Text("MIN", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            }
                            BasicTextField(
                                value = filterOptions.lowerPriceStr,
                                onValueChange = { onApplyFilter(filterOptions.copy(lowerPriceStr = it)) },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                                cursorBrush = SolidColor(NeonPinkPrimary)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (filterOptions.upperPriceStr.isEmpty()) {
                                Text("MAX", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                            }
                            BasicTextField(
                                value = filterOptions.upperPriceStr,
                                onValueChange = { onApplyFilter(filterOptions.copy(upperPriceStr = it)) },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                                cursorBrush = SolidColor(NeonPinkPrimary)
                            )
                        }
                    }
                }
            }

            // Minimum Ratings
            item {
                Column {
                    Text(
                        text = "MIN METACRITIC SCORE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(null to "ALL", 70 to "70+", 80 to "80+", 90 to "90+").forEach { (score, label) ->
                            val selected = filterOptions.minMetacritic == score
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = if (selected) 1.dp else 0.5.dp,
                                        color = if (selected) ElectricPurpleSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                    .background(if (selected) ElectricPurpleSecondary else Color.Transparent)
                                    .clickable { onApplyFilter(filterOptions.copy(minMetacritic = score)) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // On Sale Only Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ON SALE ONLY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hide full-price titles",
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = filterOptions.onSaleOnly,
                        onCheckedChange = { onApplyFilter(filterOptions.copy(onSaleOnly = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonPinkPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
