package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.viewmodel.DealCategoryFilter

@Composable
fun AbstractCategoryBar(
    activeCategory: DealCategoryFilter,
    isFiltered: Boolean,
    formatPrice: (String) -> String,
    onSelectCategory: (DealCategoryFilter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(DealCategoryFilter.entries) { category ->
            val selected = category == activeCategory && !isFiltered
            val labelText = if (category == DealCategoryFilter.UNDER_10) {
                "UNDER ${formatPrice("10")}".uppercase()
            } else {
                category.label.uppercase()
            }

            Box(
                modifier = Modifier
                    .border(
                        width = if (selected) 1.5.dp else 0.5.dp,
                        color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    .background(
                        if (selected) NeonPinkPrimary.copy(alpha = 0.15f) else Color.Transparent
                    )
                    .clickable { onSelectCategory(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = labelText,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
