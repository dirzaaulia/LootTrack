package com.dirzaaulia.loottrack.ui.deals.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.ui.components.BannerAd
import kotlinx.coroutines.delay

@Composable
fun HeroCarousel(
    featuredDeal: CheapSharkDeal,
    formattedSalePrice: String,
    formattedNormalPrice: String,
    isAlertSet: Boolean,
    onOpenAlertModal: (CheapSharkDeal) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }

    // Auto-cycle between Featured Deal Drop (Page 0) and Hero Sponsored Ad (Page 1) every 4.5s
    LaunchedEffect(Unit) {
        while (true) {
            delay(4500L)
            currentPage = (currentPage + 1) % 2
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "HeroCarouselTransition"
        ) { page ->
            if (page == 0) {
                AbstractFeaturedDropHero(
                    deal = featuredDeal,
                    formattedSalePrice = formattedSalePrice,
                    formattedNormalPrice = formattedNormalPrice,
                    isAlertSet = isAlertSet,
                    onOpenAlertModal = onOpenAlertModal
                )
            } else {
                HeroAdCard()
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Carousel Navigation Dots / Indicator Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .border(
                            width = if (currentPage == 0) 1.dp else 0.5.dp,
                            color = if (currentPage == 0) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RectangleShape
                        )
                        .background(if (currentPage == 0) NeonPinkPrimary else Color.Transparent)
                        .clickable { currentPage = 0 }
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "FEATURED DROP",
                        fontSize = 8.sp,
                        fontWeight = if (currentPage == 0) FontWeight.Black else FontWeight.Bold,
                        color = if (currentPage == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .border(
                            width = if (currentPage == 1) 1.dp else 0.5.dp,
                            color = if (currentPage == 1) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RectangleShape
                        )
                        .background(if (currentPage == 1) CyanAccent.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { currentPage = 1 }
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "SPONSORED AD",
                        fontSize = 8.sp,
                        fontWeight = if (currentPage == 1) FontWeight.Black else FontWeight.Bold,
                        color = if (currentPage == 1) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun HeroAdCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = CyanAccent.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(CyanAccent.copy(alpha = 0.9f), ElectricPurpleSecondary.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Color(0xFF130F22)),
                contentAlignment = Alignment.Center
            ) {
                BannerAd(modifier = Modifier.fillMaxSize())

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPinkPrimary)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "HOT SPONSORED PROMO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LOOTTRACK FEATURED SPONSOR",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "EXPLORE EXCLUSIVE PARTNER OFFER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanAccent.copy(alpha = 0.2f))
                        .border(1.dp, CyanAccent, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SPONSORED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanAccent
                    )
                }
            }
        }
    }
}
