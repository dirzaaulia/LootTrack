package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary

@Composable
fun AbstractSearchBox(
    query: String,
    isFiltered: Boolean,
    onQueryChange: (String) -> Unit,
    onOpenFilterSheet: () -> Unit,
    modifier: Modifier = Modifier,
    showFilterButton: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .border(
                    width = 0.8.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = "Search games or stores...",
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(NeonPinkPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (showFilterButton) {
            Spacer(modifier = Modifier.width(10.dp))

            // Dedicated Advanced Filter Button
            Box(
                modifier = Modifier
                    .height(42.dp)
                    .border(
                        width = if (isFiltered) 1.dp else 0.8.dp,
                        color = if (isFiltered) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    .background(if (isFiltered) NeonPinkPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                    .clickable(onClick = onOpenFilterSheet)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFiltered) "FILTER *" else "FILTER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = if (isFiltered) NeonPinkPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
