package com.dirzaaulia.loottrack.ui.deals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.ui.components.BannerAd

@Composable
fun FlashDealsCarousel(
    deals: List<CheapSharkDeal>,
    formatPrice: (String) -> String,
    onDealClick: (CheapSharkDeal) -> Unit,
    isAlertSet: (CheapSharkDeal) -> Boolean = { false }
) {
    val topDeals = deals.sortedByDescending { it.savings.toFloatOrNull() ?: 0f }.take(6)

    if (topDeals.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FLASH DEALS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = NeonPinkPrimary
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(topDeals, key = { index, deal -> "flash_${deal.dealId}_$index" }) { index, deal ->
                    FlashDealCard(
                        deal = deal,
                        formattedSalePrice = formatPrice(deal.salePrice),
                        formattedNormalPrice = formatPrice(deal.normalPrice),
                        isAlertSet = isAlertSet(deal),
                        onClick = { onDealClick(deal) }
                    )

                    // Inline Flash Deal Ad Card following exact same card dimensions & design
                    if (index == 2 && !com.dirzaaulia.loottrack.ui.components.isAdsDisabledGlobal) {
                        FlashDealAdCard()
                    }
                }
            }
        }
    }
}

@Composable
fun FlashDealAdCard() {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(210.dp)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(CyanAccent, ElectricPurpleSecondary)
                ),
                shape = RectangleShape
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        BannerAd(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun FlashDealCard(
    deal: CheapSharkDeal,
    formattedSalePrice: String,
    formattedNormalPrice: String,
    isAlertSet: Boolean,
    onClick: () -> Unit
) {
    val savings = deal.savings.toFloatOrNull()?.toInt() ?: 0

    Box(
        modifier = Modifier
            .width(180.dp)
            .height(210.dp)
            .border(
                width = if (isAlertSet) 1.dp else 1.dp,
                brush = Brush.verticalGradient(
                    if (isAlertSet) listOf(CyanAccent, CyanAccent.copy(alpha = 0.3f))
                    else listOf(NeonPinkPrimary.copy(alpha = 0.8f), Color.Transparent)
                ),
                shape = RectangleShape
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
            ) {
                AsyncImage(
                    model = deal.thumb,
                    contentDescription = deal.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(NeonPinkPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "-$savings%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                if (isAlertSet) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color(0xFF17122B))
                            .border(1.dp, CyanAccent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ALERT SET",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = CyanAccent
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = deal.title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formattedSalePrice,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPinkPrimary
                        )
                        Text(
                            text = formattedNormalPrice,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, if (isAlertSet) CyanAccent else NeonPinkPrimary)
                            .background(if (isAlertSet) CyanAccent.copy(alpha = 0.15f) else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isAlertSet) "ACTIVE" else "GET",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = if (isAlertSet) CyanAccent else NeonPinkPrimary
                        )
                    }
                }
            }
        }
    }
}
