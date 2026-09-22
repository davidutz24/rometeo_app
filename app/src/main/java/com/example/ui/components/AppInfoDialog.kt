package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.SatelliteAlt
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage

@Composable
fun AppInfoDialog(
    onDismiss: () -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val contactEmail = "rometeotechnologies@gmail.com"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
                .heightIn(max = 680.dp)
                .testTag("app_info_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Bar with Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = when (lang) {
                                AppLanguage.ROMANIAN -> "Despre ROmeteo"
                                AppLanguage.ENGLISH -> "About ROmeteo"
                                AppLanguage.HUNGARIAN -> "A ROmeteo névjegye"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_info_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // App Hero Branding Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0284C7).copy(alpha = 0.15f),
                                        Color(0xFF06B6D4).copy(alpha = 0.08f)
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                shape = RoundedCornerShape(18.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF0284C7),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Cloud,
                                        contentDescription = "ROmeteo Logo",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Text(
                                text = "ROmeteo",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Text(
                                text = when (lang) {
                                    AppLanguage.ROMANIAN -> "Prognoză Multi-Model • Radar Doppler ANM • Satelit METEOSAT"
                                    AppLanguage.ENGLISH -> "Multi-Model Forecast • ANM Doppler Radar • METEOSAT Satellite"
                                    AppLanguage.HUNGARIAN -> "Többmodelles előrejelzés • ANM Doppler radar • METEOSAT műhold"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0284C7).copy(alpha = 0.15f),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "© 2026 ROmeteo",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7)
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Creator & Contact Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = when (lang) {
                                        AppLanguage.ROMANIAN -> "Creator & Dezvoltare"
                                        AppLanguage.ENGLISH -> "Creator & Development"
                                        AppLanguage.HUNGARIAN -> "Készítő és fejlesztés"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (lang) {
                                        AppLanguage.ROMANIAN -> "Creator:"
                                        AppLanguage.ENGLISH -> "Creator:"
                                        AppLanguage.HUNGARIAN -> "Készítő:"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "David Marica",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (lang) {
                                        AppLanguage.ROMANIAN -> "Contact:"
                                        AppLanguage.ENGLISH -> "Contact:"
                                        AppLanguage.HUNGARIAN -> "Kapcsolat:"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = contactEmail,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            // Email action buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:$contactEmail")
                                                putExtra(Intent.EXTRA_SUBJECT, "[ROmeteo] Mesaj / Feedback")
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Nu s-a putut deschide clientul de email", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (lang) {
                                            AppLanguage.ROMANIAN -> "Trimite Email"
                                            AppLanguage.ENGLISH -> "Send Email"
                                            AppLanguage.HUNGARIAN -> "E-mail küldése"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(contactEmail))
                                        Toast.makeText(
                                            context,
                                            when (lang) {
                                                AppLanguage.ROMANIAN -> "Adresă de email copiată!"
                                                AppLanguage.ENGLISH -> "Email address copied!"
                                                AppLanguage.HUNGARIAN -> "E-mail cím lemásolva!"
                                            },
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (lang) {
                                            AppLanguage.ROMANIAN -> "Copiază"
                                            AppLanguage.ENGLISH -> "Copy"
                                            AppLanguage.HUNGARIAN -> "Másolás"
                                        },
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // Sources & Copyright Section Header
                    Text(
                        text = when (lang) {
                            AppLanguage.ROMANIAN -> "Surse de Date & Drepturi de Autor (Copyright)"
                            AppLanguage.ENGLISH -> "Data Sources & Copyright"
                            AppLanguage.HUNGARIAN -> "Adatforrások és szerzői jogok"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    // Source 1: ANM
                    SourceDetailItem(
                        icon = Icons.Rounded.Radar,
                        iconTint = Color(0xFF0284C7),
                        title = "Administrația Națională de Meteorologie (ANM)",
                        badge = "România • Oficial",
                        description = when (lang) {
                            AppLanguage.ROMANIAN -> "Date radar Doppler din Rețeaua Națională Integrată (7 stații operaționale: ORA, BOB, BUC, CRA, MED, BAR, TIM + Mozaic Compozit Național), avertizări meteorologice Nowcasting (Cod Galben/Portocaliu/Roșu), diagnoză climatologică și portal OpenData (HDF5 / OPERA)."
                            AppLanguage.ENGLISH -> "National Integrated Doppler Radar Network (7 operational stations + Composite Mosaic), official Nowcasting weather warnings, climatology diagnosis, and OpenData portal (HDF5 / OPERA)."
                            AppLanguage.HUNGARIAN -> "Országos Integrált Doppler radarhálózat (7 állomás + országos kompozit), hivatalos Nowcasting riasztások, klimatológiai termékek és OpenData portál."
                        },
                        url = "https://opendata.meteoromania.ro/radar/",
                        urlLabel = "opendata.meteoromania.ro/radar/",
                        onOpenUrl = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )

                    // Source 2: EUMETSAT / METEOSAT
                    SourceDetailItem(
                        icon = Icons.Rounded.SatelliteAlt,
                        iconTint = Color(0xFF8B5CF6),
                        title = "EUMETSAT / Meteosat",
                        badge = "Europa • © EUMETSAT",
                        description = when (lang) {
                            AppLanguage.ROMANIAN -> "Imagini satelitare geostaționare multispectrale MSG (Meteosat-10 / Meteosat-11 cu instrumentul SEVIRI: Air Mass RGB, Vizibil 0.6 µm, Infraroșu Termic 10.8 µm, HRV 0.7 µm) și pregătire pentru noua generație MTG (Meteosat Third Generation - FCI / Lightning Imager). Toate drepturile de autor rezervate de EUMETSAT."
                            AppLanguage.ENGLISH -> "Geostationary multispectral satellite imagery MSG (Meteosat-10/11 SEVIRI: Air Mass RGB, Visible 0.6 µm, Thermal IR 10.8 µm, HRV 0.7 µm) and transition to MTG (Meteosat Third Generation FCI / Lightning Imager). Copyright © EUMETSAT."
                            AppLanguage.HUNGARIAN -> "Geostacionárius többcsatornás műholdképek MSG (Meteosat-10/11 SEVIRI) és MTG (Meteosat Third Generation FCI). Szerzői jog: © EUMETSAT."
                        },
                        url = "https://www.eumetsat.int/",
                        urlLabel = "eumetsat.int",
                        onOpenUrl = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )

                    // Source 3: RainViewer
                    SourceDetailItem(
                        icon = Icons.Rounded.Public,
                        iconTint = Color(0xFF10B981),
                        title = "RainViewer",
                        badge = "Global Radar API",
                        description = when (lang) {
                            AppLanguage.ROMANIAN -> "Mozaic radar global internațional sincronizat în timp real și model de propagare a averselor prin advecție pe termen scurt (nowcasting 30-120 min)."
                            AppLanguage.ENGLISH -> "Global multi-country composite radar imagery and short-term advective precipitation nowcasting."
                            AppLanguage.HUNGARIAN -> "Nemzetközi kompozit radarhálózat és rövid távú csapadék-advekciós nowcasting előrejelzés."
                        },
                        url = "https://www.rainviewer.com/",
                        urlLabel = "rainviewer.com",
                        onOpenUrl = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )

                    // Source 4: OpenStreetMap
                    SourceDetailItem(
                        icon = Icons.Rounded.Map,
                        iconTint = Color(0xFFF59E0B),
                        title = "OpenStreetMap",
                        badge = "© OpenStreetMap • ODbL",
                        description = when (lang) {
                            AppLanguage.ROMANIAN -> "Hărți de fundal geografice, relief, granițe administrative și date geospațiale. Date furnizate de contribuitorii OpenStreetMap sub licența Open Database License (ODbL)."
                            AppLanguage.ENGLISH -> "Geographic basemaps, relief, administrative boundaries, and geospatial context provided by OpenStreetMap contributors under the Open Database License (ODbL)."
                            AppLanguage.HUNGARIAN -> "Földrajzi alaptérképek és közigazgatási határok az OpenStreetMap közreműködőitől (ODbL licenc)."
                        },
                        url = "https://www.openstreetmap.org/copyright",
                        urlLabel = "openstreetmap.org/copyright",
                        onOpenUrl = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )

                    // Source 5: NWP & AI Models
                    SourceDetailItem(
                        icon = Icons.Rounded.Language,
                        iconTint = Color(0xFFEC4899),
                        title = "Modele Numerice Globale (NWP) & AI",
                        badge = "Google • ECMWF • DWD • Météo-France • NOAA",
                        description = when (lang) {
                            AppLanguage.ROMANIAN -> "• Google WeatherNext 3 (Inteligență Artificială DeepMind / ECMWF)\n• ECMWF IFS & AIFS (Centrul European pentru Prognoze pe Termen Mediu)\n• ICON (Deutscher Wetterdienst - DWD, Germania)\n• ARPEGE (Météo-France, Franța)\n• GFS (NOAA / NCEP, SUA)\n• Open-Meteo API (Distribuție și interpolare date deschise)"
                            AppLanguage.ENGLISH -> "• Google WeatherNext 3 (DeepMind / ECMWF AI)\n• ECMWF IFS & AIFS (European Centre for Medium-Range Weather Forecasts)\n• ICON (Deutscher Wetterdienst - DWD)\n• ARPEGE (Météo-France)\n• GFS (NOAA / NCEP)\n• Open-Meteo API (Open weather data delivery)"
                            AppLanguage.HUNGARIAN -> "• Google WeatherNext 3 (DeepMind AI)\n• ECMWF IFS & AIFS\n• ICON (Deutscher Wetterdienst)\n• ARPEGE (Météo-France)\n• GFS (NOAA / NCEP)\n• Open-Meteo API"
                        },
                        url = "https://open-meteo.com/",
                        urlLabel = "open-meteo.com",
                        onOpenUrl = { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )

                    // Disclaimer Card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = when (lang) {
                                    AppLanguage.ROMANIAN -> "Notă de responsabilitate: Aplicația ROmeteo furnizează date meteorologice și imagini de radar/satelit în scop exclusiv informativ. În cazul apariției unor fenomene meteo periculoase, consultați întotdeauna comunicatele emise de Administrația Națională de Meteorologie (ANM) și mesajele RO-ALERT emise de IGSU."
                                    AppLanguage.ENGLISH -> "Disclaimer: ROmeteo provides meteorological forecasts and radar/satellite imagery for informational purposes only. In case of severe weather hazards, always follow official emergency alerts from national authorities."
                                    AppLanguage.HUNGARIAN -> "Felelősségkizárás: A ROmeteo tájékoztató jelleggel jeleníti meg a meteorológiai adatokat. Veszélyes időjárási helyzetekben mindig kövesse a hivatalos hatósági riasztásokat."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Close Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_info_done_button")
                ) {
                    Text(
                        text = when (lang) {
                            AppLanguage.ROMANIAN -> "Am înțeles"
                            AppLanguage.ENGLISH -> "Done"
                            AppLanguage.HUNGARIAN -> "Kész"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceDetailItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    badge: String,
    description: String,
    url: String,
    urlLabel: String,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = iconTint.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenUrl(url) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = urlLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}
