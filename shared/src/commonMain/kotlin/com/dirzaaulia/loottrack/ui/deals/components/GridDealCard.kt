package com.dirzaaulia.loottrack.ui.deals.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary

@Composable
fun GridDealCard(
    deal: CheapSharkDeal,
    formattedSalePrice: String,
    formattedNormalPrice: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAlertSet: Boolean = false
) {
    val savings = deal.savings.toFloatOrNull()?.toInt() ?: 0

    Box(
        modifier = modifier
            .height(160.dp)
            .border(
                width = if (isAlertSet) 1.dp else 0.5.dp,
                color = if (isAlertSet) CyanAccent.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        // Full Card Artwork Image
        AsyncImage(
            model = deal.thumb,
            contentDescription = deal.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-Blur Translucent Dark Gradient Overlay for Readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC0F0C1B),
                            Color(0xFA0F0C1B)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Top Discount Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(NeonPinkPrimary)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "-$savings%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        // Top-Right Alert Active Badge
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

        // Bottom Text Info Column
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = deal.title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedSalePrice,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonPinkPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedNormalPrice,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Text(
                    text = "RATING ${deal.dealRating}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
