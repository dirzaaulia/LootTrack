package com.dirzaaulia.loottrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.model.AppCurrency
import com.dirzaaulia.loottrack.model.CheapSharkDeal
import com.dirzaaulia.loottrack.model.SavedAlert
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AlertTriggerType(val label: String) {
    ANY_DISCOUNT("ANY DISCOUNT"),
    TARGET_PRICE("TARGET PRICE")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceAlertBottomSheet(
    deal: CheapSharkDeal,
    savedEmail: String,
    selectedCurrency: AppCurrency,
    onSetAlert: (email: String, price: String, onResult: (Boolean) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit = onDismiss,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    existingAlert: SavedAlert? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf(existingAlert?.userEmail?.ifBlank { savedEmail } ?: savedEmail) }
    var alertType by remember { mutableStateOf(AlertTriggerType.ANY_DISCOUNT) }
    var targetPrice by remember { mutableStateOf(existingAlert?.targetPriceUsd ?: deal.salePrice) }
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    val isEmailValid = email.contains("@") && email.contains(".") && email.trim().length >= 5
    val isTargetPriceValid = (alertType == AlertTriggerType.ANY_DISCOUNT) ||
            (targetPrice.isNotBlank() && targetPrice.toDoubleOrNull() != null && (targetPrice.toDoubleOrNull() ?: 0.0) > 0)
    val isFormValid = isEmailValid && isTargetPriceValid && !isLoading

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RectangleShape
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Sheet Header
            item {
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
                                text = "LOOTTRACK PRICE ALERT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (existingAlert != null) "UPDATE EXISTING GAME ALERT" else "AUTOMATIC PRICE DROP NOTIFICATION",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = if (existingAlert != null) CyanAccent else NeonPinkPrimary
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
            }

            // Form Body
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = deal.title.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Existing Active Alert Info Banner
                    if (existingAlert != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyanAccent)
                                .background(CyanAccent.copy(alpha = 0.12f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🔔 ACTIVE ALERT IS ALREADY SET FOR THIS GAME",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = CyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Alert Condition Pills
                    Text(
                        text = "ALERT CONDITION",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AlertTriggerType.entries.forEach { type ->
                            val selected = alertType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (selected) 1.dp else 0.5.dp,
                                        color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                    .background(if (selected) NeonPinkPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        alertType = type
                                        if (type == AlertTriggerType.ANY_DISCOUNT) {
                                            targetPrice = deal.normalPrice
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type.label,
                                    fontSize = 9.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                    letterSpacing = 1.sp,
                                    color = if (selected) NeonPinkPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Input
                    Text(
                        text = "YOUR EMAIL ADDRESS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (email.isEmpty()) {
                            Text("your.email@example.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        }
                        BasicTextField(
                            value = email,
                            onValueChange = { email = it },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                            cursorBrush = SolidColor(NeonPinkPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (alertType == AlertTriggerType.TARGET_PRICE) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "TARGET PRICE (${selectedCurrency.symbol})",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = targetPrice,
                                onValueChange = { targetPrice = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPinkPrimary),
                                cursorBrush = SolidColor(NeonPinkPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    resultMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                                .clickable {
                                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("CANCEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .border(
                                    width = 1.dp,
                                    color = if (isFormValid) NeonPinkPrimary else NeonPinkPrimary.copy(alpha = 0.35f)
                                )
                                .background(if (isFormValid) NeonPinkPrimary else NeonPinkPrimary.copy(alpha = 0.35f))
                                .clickable(enabled = isFormValid) {
                                    isLoading = true
                                    val effectivePrice = if (alertType == AlertTriggerType.ANY_DISCOUNT) deal.normalPrice else targetPrice
                                    onSetAlert(email, effectivePrice) { success ->
                                        isLoading = false
                                        if (success) {
                                            resultMessage = "LOOTTRACK ALERT ACTIVATED // WE WILL NOTIFY YOU"
                                            coroutineScope.launch {
                                                delay(700L)
                                                sheetState.hide()
                                                onSuccess()
                                            }
                                        } else {
                                            resultMessage = "ERR // UNABLE TO REGISTER ALERT"
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    text = if (existingAlert != null) "UPDATE LOOTTRACK ALERT" else "ACTIVATE LOOTTRACK ALERT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isFormValid) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
