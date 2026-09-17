package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary

@Composable
fun AbstractFloatingBottomBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    // Sharp Wireframe Rectangular Box Container
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = NeonPinkPrimary
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxyNavTab(
                label = "DEALS",
                selected = selectedIndex == 0,
                onClick = { onSelect(0) }
            )

            // Sharp Vertical Divider
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .width(0.8.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(4.dp))

            BoxyNavTab(
                label = "SAVED",
                selected = selectedIndex == 1,
                onClick = { onSelect(1) }
            )

            // Sharp Vertical Divider
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .width(0.8.dp)
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(4.dp))

            BoxyNavTab(
                label = "INFO",
                selected = selectedIndex == 2,
                onClick = { onSelect(2) }
            )
        }
    }
}

@Composable
private fun BoxyNavTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = if (selected) 1.dp else 0.5.dp,
                color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RectangleShape
            )
            .background(if (selected) NeonPinkPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            letterSpacing = 2.sp,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
