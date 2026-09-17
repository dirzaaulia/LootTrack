package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.model.AppCurrency
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import loottrack.shared.generated.resources.Res
import loottrack.shared.generated.resources.logo_vector
import org.jetbrains.compose.resources.painterResource

@Composable
fun DesktopTopNavBar(
    selectedNavIndex: Int,
    searchQuery: String,
    selectedCurrency: AppCurrency,
    isDarkTheme: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectNavIndex: (Int) -> Unit,
    onOpenCurrencySheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassBg = if (isDarkTheme) Color(0xFF0D0A18).copy(alpha = 0.92f) else Color(0xFFFAF8FC).copy(alpha = 0.95f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(NeonPinkPrimary.copy(alpha = 0.6f), ElectricPurpleSecondary.copy(alpha = 0.4f), CyanAccent.copy(alpha = 0.6f))
                ),
                shape = RectangleShape
            )
            .background(glassBg)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSelectNavIndex(0) }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonPinkPrimary.copy(alpha = 0.2f), ElectricPurpleSecondary.copy(alpha = 0.2f))
                            )
                        )
                        .border(1.dp, NeonPinkPrimary)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo_vector),
                        contentDescription = "LootTrack Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "LOOTTRACK",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "GAME DEALS ENGINE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.8.sp,
                        color = CyanAccent
                    )
                }
            }

            // Embedded Header Search Input
            Box(
                modifier = Modifier
                    .width(360.dp)
                    .height(38.dp)
                    .border(
                        width = 1.dp,
                        color = if (searchQuery.isNotBlank()) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEARCH",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = NeonPinkPrimary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Type game title...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(NeonPinkPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "X",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPinkPrimary,
                            modifier = Modifier
                                .clickable { onSearchQueryChange("") }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Navigation Segmented Tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberTabPill("CATALOG", selected = selectedNavIndex == 0) { onSelectNavIndex(0) }
                CyberTabPill("WATCHLIST", selected = selectedNavIndex == 1) { onSelectNavIndex(1) }
                CyberTabPill("SYSTEM INFO", selected = selectedNavIndex == 2) { onSelectNavIndex(2) }
            }

            // Right Actions: Currency Selector Only
            Box(
                modifier = Modifier
                    .border(1.dp, CyanAccent)
                    .background(CyanAccent.copy(alpha = 0.12f))
                    .clickable(onClick = onOpenCurrencySheet)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                val symbolText = if (selectedCurrency.symbol.isNotBlank()) selectedCurrency.symbol.trim() else ""
                val currencyLabel = if (symbolText.isNotBlank() && symbolText != selectedCurrency.code.uppercase()) {
                    "$symbolText ${selectedCurrency.code.uppercase()}"
                } else {
                    selectedCurrency.code.uppercase()
                }
                Text(
                    text = "$currencyLabel v",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = CyanAccent
                )
            }
        }
    }
}

@Composable
private fun CyberTabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = if (selected) 1.dp else 0.5.dp,
                color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RectangleShape
            )
            .background(if (selected) NeonPinkPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            letterSpacing = 1.8.sp,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
