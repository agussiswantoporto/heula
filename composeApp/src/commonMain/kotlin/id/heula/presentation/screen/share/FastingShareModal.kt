package id.heula.presentation.screen.share

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.heula.domain.model.*
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.theme.BrandPrimary
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun FastingShareModal(
    session: FastingSession,
    onDismiss: () -> Unit,
    onExport: (ShareCustomization, (Result<Unit>) -> Unit) -> Unit,
) {
    var config by remember { mutableStateOf(ShareCustomization()) }
    var isExporting by remember { mutableStateOf(false) }
    var showCustomizationSettings by remember { mutableStateOf(false) }

    val durMinutes = (session.durationMs / 60000L).toInt()
    val hours = durMinutes / 60
    val mins = durMinutes % 60
    val durFormatted = "${hours}:${if (mins < 10) "0$mins" else "$mins"}"

    val startDt = Instant.fromEpochMilliseconds(session.startMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val endDt = Instant.fromEpochMilliseconds(session.endMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val startStr = "${if (startDt.hour < 10) "0" else ""}${startDt.hour}:${if (startDt.minute < 10) "0" else ""}${startDt.minute}"
    val endStr = "${if (endDt.hour < 10) "0" else ""}${endDt.hour}:${if (endDt.minute < 10) "0" else ""}${endDt.minute}"
    val dateStr = "${endDt.dayOfMonth} ${endDt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${endDt.year}"

    Dialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.92f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "Bagikan Jejak Puasa",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Format PNG transparan ala Strava",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isExporting,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content Area: Preview + Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // 1. Checkerboard Live Transparent Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (config.aspectRatio == ShareAspectRatio.STORY) 340.dp else 260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Background Checkerboard Pattern
                        CheckerboardPattern(modifier = Modifier.fillMaxSize())

                        // Render Card Overlay based on Template
                        ShareCardPreview(
                            session = session,
                            config = config,
                            durFormatted = durFormatted,
                            hours = hours,
                            mins = mins,
                            startStr = startStr,
                            endStr = endStr,
                            dateStr = dateStr,
                        )
                    }

                    // 2. Format Switcher (Story vs Feed)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        ShareAspectRatio.entries.forEach { ratio ->
                            val selected = config.aspectRatio == ratio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) BrandPrimary else Color.Transparent)
                                    .clickable { config = config.copy(aspectRatio = ratio) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = ratio.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // 3. 8 Template Horizontal Selector
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PILIHAN DESAIN (${config.template.displayName})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ShareTemplate.entries.forEach { tpl ->
                                val isSelected = config.template == tpl
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.5.dp,
                                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { config = config.copy(template = tpl) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                ) {
                                    Text(
                                        text = tpl.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }

                    // 4. Quick Toggles / Customization Accordion
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .clickable { showCustomizationSettings = !showCustomizationSettings }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandPrimary)
                            Text("Kustomisasi Tampilan & Elemen", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = if (showCustomizationSettings) "Tutup ▲" else "Buka ▼",
                            fontSize = 12.sp,
                            color = BrandPrimary,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    if (showCustomizationSettings) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            // Toggles
                            ToggleRow(
                                title = "Tampilkan Tanggal",
                                checked = config.showDate,
                                onCheckedChange = { config = config.copy(showDate = it) }
                            )
                            ToggleRow(
                                title = "Tampilkan Target Pola (16:8)",
                                checked = config.showTarget,
                                onCheckedChange = { config = config.copy(showTarget = it) }
                            )
                            ToggleRow(
                                title = "Tampilkan Jam Mulai & Selesai",
                                checked = config.showTimes,
                                onCheckedChange = { config = config.copy(showTimes = it) }
                            )
                            ToggleRow(
                                title = "Tampilkan Heula Watermark",
                                checked = config.showBranding,
                                onCheckedChange = { config = config.copy(showBranding = it) }
                            )

                            // Color Palette
                            Text("Warna Teks:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                ShareTextColor.entries.forEach { col ->
                                    val isColorSelected = config.textColor == col
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(col.hex))
                                            .border(
                                                width = if (isColorSelected) 2.5.dp else 1.dp,
                                                color = if (isColorSelected) BrandPrimary else Color.Gray.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                            .clickable { config = config.copy(textColor = col) },
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom CTA Button: BAGIKAN HASIL PUASA
                HeulaButton(
                    text = if (isExporting) "Menyiapkan Gambar..." else "BAGIKAN HASIL PUASA",
                    onClick = {
                        isExporting = true
                        onExport(config) {
                            isExporting = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isExporting,
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.height(24.dp),
        )
    }
}

@Composable
private fun CheckerboardPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val squareSize = 14.dp.toPx()
        val numCols = (size.width / squareSize).toInt() + 1
        val numRows = (size.height / squareSize).toInt() + 1

        val lightColor = Color(0xFFF3F4F6)
        val darkColor = Color(0xFFE5E7EB)

        for (i in 0 until numCols) {
            for (j in 0 until numRows) {
                val color = if ((i + j) % 2 == 0) lightColor else darkColor
                drawRect(
                    color = color,
                    topLeft = Offset(i * squareSize, j * squareSize),
                    size = Size(squareSize, squareSize),
                )
            }
        }
    }
}

@Composable
private fun ShareCardPreview(
    session: FastingSession,
    config: ShareCustomization,
    durFormatted: String,
    hours: Int,
    mins: Int,
    startStr: String,
    endStr: String,
    dateStr: String,
) {
    val textColor = Color(config.textColor.hex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (config.template) {
            ShareTemplate.MINIMALIST -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (config.showBranding) {
                        Text(
                            text = "H E U L A",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            letterSpacing = 3.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = durFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        lineHeight = 44.sp,
                    )
                    Text(
                        text = "JAM BERPUASA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor.copy(alpha = 0.85f),
                        letterSpacing = 1.sp,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(60.dp).height(1.5.dp).background(textColor))
                    Spacer(modifier = Modifier.height(6.dp))
                    if (config.showTarget) {
                        Text("Target ${session.planLabel} • Selesai", fontSize = 11.sp, color = textColor)
                    }
                    if (config.showTimes) {
                        Text("$startStr — $endStr", fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
                    }
                    if (config.showDate) {
                        Text(dateStr, fontSize = 9.sp, color = textColor.copy(alpha = 0.65f))
                    }
                }
            }

            ShareTemplate.MODERN_GRADIENT -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (config.showBranding) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFFFC5200), Color(0xFFFF7A00))))
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                        ) {
                            Text("FASTING ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Text(
                        text = durFormatted,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 48.sp,
                    )
                    Text(
                        text = "BERHASIL BERPUASA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFC5200),
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (config.showTarget) {
                            Column {
                                Text("POLA", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                                Text(session.planLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        if (config.showTimes) {
                            Column {
                                Text("WAKTU", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                                Text("$startStr - $endStr", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    if (config.showDate) {
                        Text(dateStr, fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            ShareTemplate.GLASS_STYLE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (config.showBranding) {
                            Text("HEULA FASTING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text(
                            text = "${hours}j ${mins}m",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                        if (config.showTarget) {
                            Text("Target ${session.planLabel} Tuntas", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                        if (config.showTimes) {
                            Text("$startStr — $endStr", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        if (config.showDate) {
                            Text(dateStr, fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            ShareTemplate.TYPOGRAPHY -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    if (config.showBranding) {
                        Text("FASTING RECORD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = textColor)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$hours",
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor,
                            lineHeight = 62.sp,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("HOURS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Text("$mins MIN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                    }
                    if (config.showTarget || config.showTimes) {
                        Text(
                            "${session.planLabel} • $startStr — $endStr",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                        )
                    }
                    if (config.showDate) {
                        Text(dateStr, fontSize = 9.sp, color = textColor.copy(alpha = 0.8f))
                    }
                }
            }

            ShareTemplate.EDITORIAL -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (config.showBranding) {
                        Text("— HEULA JOURNAL —", fontSize = 10.sp, fontFamily = FontFamily.Serif, color = textColor)
                    }
                    Text(
                        text = "${hours}h ${mins}m",
                        fontSize = 38.sp,
                        fontFamily = FontFamily.Serif,
                        color = textColor,
                    )
                    Text("Intermittent Fasting Completed", fontSize = 11.sp, fontFamily = FontFamily.Serif, color = textColor)
                    Box(modifier = Modifier.width(40.dp).height(1.dp).background(textColor))
                    if (config.showTarget) {
                        Text("Target: ${session.planLabel}", fontSize = 10.sp, color = textColor)
                    }
                    if (config.showDate) {
                        Text(dateStr, fontSize = 9.sp, color = textColor.copy(alpha = 0.7f))
                    }
                }
            }

            ShareTemplate.NEON -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (config.showBranding) {
                        Text("// HEULA.PRO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFCCFF00))
                    }
                    Text(
                        text = durFormatted,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )
                    Text("FASTING GOAL UNLOCKED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCCFF00))
                    if (config.showTarget) {
                        Text("TARGET ${session.planLabel}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F0FF))
                    }
                    if (config.showTimes) {
                        Text("$startStr — $endStr", fontSize = 10.sp, color = Color.White)
                    }
                    if (config.showDate) {
                        Text(dateStr, fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            ShareTemplate.DARK_MODE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF141416).copy(alpha = 0.88f))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (config.showBranding) {
                            Text("HEULA ACTIVITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFC5200))
                        }
                        Text(
                            text = "${hours}h ${mins}m",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Text("Target ${session.planLabel} Selesai", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF34C759))
                        if (config.showTimes) {
                            Text("$startStr — $endStr", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                        if (config.showDate) {
                            Text(dateStr, fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            ShareTemplate.CLEAN_WHITE -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (config.showBranding) {
                        Text("HEULA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(
                        text = durFormatted,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text("TARGET ${session.planLabel} COMPLETED", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    if (config.showTimes) {
                        Text("$startStr — $endStr", fontSize = 10.sp, color = Color.White)
                    }
                    if (config.showDate) {
                        Text(dateStr, fontSize = 9.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
        }
    }
}
