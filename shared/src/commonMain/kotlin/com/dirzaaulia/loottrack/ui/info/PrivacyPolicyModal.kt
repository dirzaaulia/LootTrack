package com.dirzaaulia.loottrack.ui.info

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.utils.openUrl

@Composable
fun PrivacyPolicyModal(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RectangleShape,
            border = BorderStroke(1.dp, NeonPinkPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRIVACY POLICY",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "GOOGLE PLAY STORE COMPLIANCE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = NeonPinkPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .border(1.dp, NeonPinkPrimary)
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("CLOSE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NeonPinkPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Effective Date: May 2024\nDeveloper: Dirza Aulia (contact@dirzaaulia.com)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = "1. OVERVIEW\nLootTrack is a video game deal aggregation and price tracking application. We respect your privacy and are committed to protecting any information processed by the app.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        Text(
                            text = "2. DATA COLLECTION & PRICE ALERTS\nLootTrack does not collect, store, or sell personal identifying data, location, or device contacts.\n\nWhen you voluntarily register a Price Alert, your email address and target price are securely transmitted to our LootTrack notification server solely to send automated email alerts when a game price drops to your target.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        Text(
                            text = "3. THIRD-PARTY SERVICES\nLootTrack integrates public APIs to display deal data and currency rates:\n• CheapShark API (Game deal lookup & comparison)\n• FawazAhmed Currency API (Global currency conversion rates)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        Text(
                            text = "4. DATA SECURITY & RIGHTS\nYou may delete any active price alert at any time directly within the Watchlist tab in the app. Deleting an alert immediately removes your alert entry.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NeonPinkPrimary)
                                .clickable { openUrl("https://dirzaaulia.com/privacy") }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "OPEN ONLINE PRIVACY POLICY (DIRZAAULIA.COM)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonPinkPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
