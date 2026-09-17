package com.dirzaaulia.loottrack.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import com.dirzaaulia.loottrack.utils.openUrl

@Composable
fun InfoScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Version Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonPinkPrimary)
                    .background(NeonPinkPrimary.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "LOOTTRACK v1.0.0",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "GAME DEALS & PRICE TRACKING ENGINE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = NeonPinkPrimary
                    )
                }
            }
        }

        // Section 1: Data Source Information
        item {
            InfoSectionCard(
                title = "DATA SOURCE ATTRIBUTION",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "• CheapShark REST API: Powers game deal lookup, cross-store comparison, price alert engine, and historical lowest prices.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• FawazAhmed Currency API: Powers real-time global exchange rate conversion across 200+ currencies.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }

        // Section 2: Developer Info
        item {
            InfoSectionCard(
                title = "DEVELOPER INFORMATION",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "DEVELOPED BY: Dirza Aulia",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }

        // Section 3: Support the Developer
        item {
            InfoSectionCard(
                title = "SUPPORT THE DEVELOPER",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Website
                        SupportLinkPill(
                            label = "WEBSITE",
                            urlText = "dirzaaulia.com",
                            url = "https://dirzaaulia.com",
                            color = CyanAccent
                        )

                        // Ko-fi
                        SupportLinkPill(
                            label = "KO-FI",
                            urlText = "ko-fi.com/dirzaaulia",
                            url = "https://ko-fi.com/dirzaaulia",
                            color = NeonPinkPrimary
                        )

                        // Saweria
                        SupportLinkPill(
                            label = "SAWERIA",
                            urlText = "saweria.co/dirzaaulia",
                            url = "https://saweria.co/dirzaaulia",
                            color = ElectricPurpleSecondary
                        )

                        // GitHub
                        SupportLinkPill(
                            label = "GITHUB",
                            urlText = "github.com/dirzaaulia",
                            url = "https://github.com/dirzaaulia",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }

        // Section 4: Legal & Compliance
        item {
            InfoSectionCard(
                title = "LEGAL & COMPLIANCE",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyanAccent)
                                .background(CyanAccent.copy(alpha = 0.12f))
                                .clickable { openUrl("https://lt.dirzaaulia.com/privacy") }
                                .padding(12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("PRIVACY POLICY", fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = CyanAccent)
                                Text("lt.dirzaaulia.com/privacy ➔", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CyanAccent)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SupportLinkPill(
    label: String,
    urlText: String,
    url: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color)
            .background(color.copy(alpha = 0.1f))
            .clickable { openUrl(url) }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = color
            )
            Text(
                text = urlText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InfoSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = NeonPinkPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
