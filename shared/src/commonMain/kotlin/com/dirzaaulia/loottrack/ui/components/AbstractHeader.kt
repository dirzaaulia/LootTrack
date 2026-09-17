package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.model.AppCurrency
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary

@Composable
fun AbstractEditorialHeader(
    isDarkTheme: Boolean,
    selectedCurrency: AppCurrency,
    onToggleTheme: () -> Unit,
    onOpenCurrencySheet: () -> Unit
) {
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
                    text = "LOOTTRACK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "GAME DEALS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = NeonPinkPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Currency Selector Pill
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = NeonPinkPrimary.copy(alpha = 0.8f)
                        )
                        .clickable(onClick = onOpenCurrencySheet)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selectedCurrency.symbol,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = NeonPinkPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Dark/Light Theme Toggle Pill
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                        .clickable(onClick = onToggleTheme)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isDarkTheme) "DARK" else "LIGHT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
