package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary

/**
 * Cyberpunk Modal Side Sheet component for Big Screen / WASM layout.
 * Anchors a side sheet panel to the right edge with a dark translucent backdrop scrim.
 */
@Composable
fun ModalSideSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest
            )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            val sheetWidth = if (maxWidth > 600.dp) 480.dp else maxWidth * 0.88f

            Box(
                modifier = modifier
                    .width(sheetWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(NeonPinkPrimary, ElectricPurpleSecondary, CyanAccent)
                        ),
                        shape = RectangleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Prevent click through to backdrop scrim */ }
            ) {
                content()
            }
        }
    }
}
