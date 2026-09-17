package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.theme.CyanAccent

@Composable
actual fun BannerAd(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyanAccent.copy(alpha = 0.08f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SPONSORED ADVERTISEMENT",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = CyanAccent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "LOOTTRACK GAMING ENGINE SPONSOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
