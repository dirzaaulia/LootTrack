package com.dirzaaulia.loottrack.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.dirzaaulia.loottrack.model.CheapSharkGameDetail
import com.dirzaaulia.loottrack.model.CheapSharkStore
import com.dirzaaulia.loottrack.model.SavedAlert
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.utils.openUrl
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailBottomSheet(
    deal: CheapSharkDeal,
    stores: List<CheapSharkStore>,
    onFetchGameDetails: suspend (String) -> CheapSharkGameDetail?,
    formatPrice: (String) -> String,
    onOpenAlertModal: (CheapSharkDeal) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    isAlertSet: Boolean = false,
    existingAlert: SavedAlert? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var gameDetail by remember { mutableStateOf<CheapSharkGameDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val gameId = deal.gameId ?: deal.dealId

    LaunchedEffect(gameId) {
        isLoading = true
        gameDetail = onFetchGameDetails(gameId)
        isLoading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RectangleShape
    ) {
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
                            text = "GAME PRICE TRACKER",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "HISTORICAL LOWS & STORE COMPARISON",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = NeonPinkPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, NeonPinkPrimary)
                            .clickable {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("CLOSE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NeonPinkPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonPinkPrimary, strokeWidth = 2.dp, modifier = Modifier.size(36.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Game Cover & Title
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (isAlertSet) CyanAccent else NeonPinkPrimary, CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = deal.thumb,
                                    contentDescription = deal.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deal.title.uppercase(),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "CURRENT: ${formatPrice(deal.salePrice)} (WAS ${formatPrice(deal.normalPrice)})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPinkPrimary
                                )
                            }
                        }
                    }

                    // Active Price Alert Status Banner (if alert already set)
                    if (existingAlert != null || isAlertSet) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CyanAccent)
                                    .background(CyanAccent.copy(alpha = 0.12f))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🔔 PRICE ALERT ACTIVE FOR THIS GAME",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.2.sp,
                                            color = CyanAccent
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val priceLabel = existingAlert?.let { formatPrice(it.targetPriceUsd) } ?: formatPrice(deal.salePrice)
                                        Text(
                                            text = "TARGET NOTIFY PRICE: $priceLabel",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        existingAlert?.userEmail?.takeIf { it.isNotBlank() }?.let { email ->
                                            Text(
                                                text = "EMAIL: $email",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, CyanAccent)
                                            .background(CyanAccent)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "ALERT SET",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Historical All-Time Lowest Price Block
                    gameDetail?.cheapestPriceEver?.let { cheapest ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, ElectricPurpleSecondary)
                                    .background(ElectricPurpleSecondary.copy(alpha = 0.12f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "ALL-TIME HISTORICAL CHEAPEST PRICE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp,
                                        color = ElectricPurpleSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatPrice(cheapest.price),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val isRecord = deal.salePrice.toDoubleOrNull() != null &&
                                                cheapest.price.toDoubleOrNull() != null &&
                                                deal.salePrice.toDouble() <= cheapest.price.toDouble()

                                        if (isRecord) {
                                            Box(
                                                modifier = Modifier
                                                    .border(1.dp, NeonPinkPrimary)
                                                    .background(NeonPinkPrimary)
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "MATCHES ALL-TIME LOW!",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Store Deals Comparison Header
                    item {
                        Text(
                            text = "STORE COMPARISON (${gameDetail?.deals?.size ?: 0} STORES)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Store Deals List
                    gameDetail?.deals?.let { dealList ->
                        items(dealList) { storeDeal ->
                            val storeName = stores.firstOrNull { it.storeId == storeDeal.storeId }?.storeName ?: "Store #${storeDeal.storeId}"
                            val dealUrl = "https://www.cheapshark.com/redirect?dealID=${storeDeal.dealId}"

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                    .clickable { openUrl(dealUrl) }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(storeName.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("Savings: ${storeDeal.savings.substringBefore(".")}%", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(formatPrice(storeDeal.price), fontSize = 15.sp, fontWeight = FontWeight.Black, color = NeonPinkPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(formatPrice(storeDeal.retailPrice), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = TextDecoration.LineThrough)
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (!isAlertSet) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, NeonPinkPrimary)
                                        .clickable {
                                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                                onDismiss()
                                                onOpenAlertModal(deal)
                                            }
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("SET PRICE ALERT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = NeonPinkPrimary)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, NeonPinkPrimary)
                                    .background(NeonPinkPrimary)
                                    .clickable {
                                        openUrl("https://www.cheapshark.com/redirect?dealID=${deal.dealId}")
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("CLAIM DEAL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
