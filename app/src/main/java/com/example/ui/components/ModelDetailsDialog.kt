package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.Translations
import com.example.model.WeatherModel

@Composable
fun ModelDetailsDialog(
    model: WeatherModel,
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("model_details_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(model.accentColor)
                        )
                        Column {
                            Text(
                                text = model.displayName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = model.agency,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle Tagline Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = model.accentColor.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = model.subtitleTagline,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = model.accentColor
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description in active language
                Text(
                    text = model.getDescription(lang),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                )

                if (model == WeatherModel.WEATHERNEXT_3) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E3A8A).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = when (lang) {
                                    AppLanguage.ROMANIAN -> "Structură Straturi Date (Google DeepMind):"
                                    AppLanguage.HUNGARIAN -> "Adatrétegek struktúrája (Google DeepMind):"
                                    AppLanguage.ENGLISH -> "Data Layer Structure (Google DeepMind):"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = when (lang) {
                                    AppLanguage.ROMANIAN -> "• 5 km (Station-Head): Strict temperatură 2m & umiditate pe micro-orografie/altitudine (fără radar Doppler terestru).\n• 10 km: Precipitații (ploaie/ninsoare), vânt 10m, radiație solară și grad noros.\n• 25 km: Straturi înalte din atmosferă (ansamblu global 64 membri).\n• Refresh: Nowcasting orar (0–48h) & cicluri principale (până la 15 zile)."
                                    AppLanguage.HUNGARIAN -> "• 5 km: Kizárólag 2 m-es hőmérséklet és páratartalom domborzati korrekcióval (radar nélkül).\n• 10 km: Csapadék, 10 m-es szél, felhőzet és sugárzás.\n• 25 km: Magaslégköri rétegek (64 tagú szinoptikus együttes).\n• Frissítés: Óránkénti (0–48 óra) és fő futások (15 napig)."
                                    AppLanguage.ENGLISH -> "• 5 km (Station-Head): Strictly 2m temperature & humidity downscaled for micro-orography (no Doppler radar).\n• 10 km: Gridded precipitation (rain/snow), 10m wind, cloud cover, and solar radiation.\n• 25 km: Upper-air atmospheric layers (64-member global ensemble).\n• Refresh: Hourly nowcasting (0–48h) & main synoptic cycles (up to 15 days)."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Technical Specifications Grid
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpecRow(
                            label = Translations.get("resolution", lang),
                            value = model.resolution
                        )
                        SpecRow(
                            label = Translations.get("architecture", lang),
                            value = model.architectureType
                        )
                        SpecRow(
                            label = Translations.get("update_freq", lang),
                            value = model.updateCycle
                        )
                        SpecRow(
                            label = Translations.get("agency", lang),
                            value = model.agency
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = model.accentColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = Translations.get("close", lang),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
