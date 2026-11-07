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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
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
    onGeneratePreview: (suspend (ShareCustomization) -> ImageBitmap?)? = null,
) {
    var config by remember { mutableStateOf(ShareCustomization()) }
    var isExporting by remember { mutableStateOf(false) }
    var showCustomizationSettings by remember { mutableStateOf(false) }

    val previewBitmap by produceState<ImageBitmap?>(null, session, config) {
        value = onGeneratePreview?.invoke(config)
    }

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
                    // 1. Checkerboard Live Transparent Preview (Exact Aspect Ratio 9:16 Story / 1:1 Feed)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val isStory = config.aspectRatio == ShareAspectRatio.STORY
                        val previewHeight = if (isStory) 360.dp else 280.dp
                        val previewWidth = if (isStory) 202.dp else 280.dp

                        Box(
                            modifier = Modifier
                                .width(previewWidth)
                                .height(previewHeight)
                                .clip(RoundedCornerShape(if (isStory) 24.dp else 18.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(if (isStory) 24.dp else 18.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            // Background Checkerboard Pattern
                            CheckerboardPattern(modifier = Modifier.fillMaxSize())

                            if (previewBitmap != null) {
                                Image(
                                    bitmap = previewBitmap!!,
                                    contentDescription = "Share Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                )
                            } else {
                                // Fallback Compose layout
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
                        }
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

                    // 3. 8 Template Horizontal Selector (Strava Card Style Swatches)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "PILIHAN TEMA STRAVA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp,
                            )
                            Text(
                                text = config.template.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimary,
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ShareTemplate.entries.forEach { tpl ->
                                val isSelected = config.template == tpl
                                Box(
                                    modifier = Modifier
                                        .width(115.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 0.5.dp,
                                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { config = config.copy(template = tpl) }
                                        .padding(8.dp),
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        // Mini preview swatch
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when (tpl) {
                                                        ShareTemplate.MINIMALIST -> Color(0xFF1E1E24)
                                                        ShareTemplate.MODERN_GRADIENT -> Color(0xFF121418)
                                                        ShareTemplate.DARK_MODE -> Color(0xFF0A0A0E)
                                                        ShareTemplate.GLASS_STYLE -> Color(0xFF2A2D36)
                                                        ShareTemplate.TYPOGRAPHY -> Color(0xFF18181B)
                                                        ShareTemplate.EDITORIAL -> Color(0xFF1A1A1E)
                                                        ShareTemplate.NEON -> Color(0xFF0C0E14)
                                                        ShareTemplate.CLEAN_WHITE -> Color(0xFF22242A)
                                                    }
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = when (tpl) {
                                                        ShareTemplate.NEON -> Color(0xFFCCFF00).copy(alpha = 0.6f)
                                                        ShareTemplate.GLASS_STYLE -> Color.White.copy(alpha = 0.4f)
                                                        ShareTemplate.MODERN_GRADIENT -> Color(0xFFFC5200).copy(alpha = 0.6f)
                                                        else -> Color.White.copy(alpha = 0.15f)
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            when (tpl) {
                                                ShareTemplate.MINIMALIST -> {
                                                    Text("16h 24m", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                }
                                                ShareTemplate.MODERN_GRADIENT -> {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                        StravaMarkIcon(color = Color(0xFFFC5200), modifier = Modifier.size(9.dp))
                                                        Text("16:24", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                    }
                                                }
                                                ShareTemplate.DARK_MODE -> {
                                                    Text("16:24 ●", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34C759))
                                                }
                                                ShareTemplate.GLASS_STYLE -> {
                                                    Text("16h 24m", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                                ShareTemplate.TYPOGRAPHY -> {
                                                    Text("16 JAM", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFFC5200))
                                                }
                                                ShareTemplate.EDITORIAL -> {
                                                    Text("16h 24m", fontSize = 10.sp, fontFamily = FontFamily.Serif, color = Color.White)
                                                }
                                                ShareTemplate.NEON -> {
                                                    Text("16:24", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFCCFF00))
                                                }
                                                ShareTemplate.CLEAN_WHITE -> {
                                                    Text("16:24", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                }
                                            }
                                        }

                                        Text(
                                            text = tpl.displayName,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
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
private fun StravaMarkIcon(color: Color, modifier: Modifier = Modifier.size(16.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width * 0.72f
        val h = size.height
        val ox = (size.width - w) / 2f
        val p1 = Path().apply {
            moveTo(ox + w * 0.5f, 0f)
            lineTo(ox + w, h * 0.52f)
            lineTo(ox + w * 0.72f, h * 0.52f)
            lineTo(ox + w * 0.5f, h * 0.26f)
            lineTo(ox + w * 0.28f, h * 0.52f)
            lineTo(ox, h * 0.52f)
            close()
        }
        drawPath(p1, color)
        val p2 = Path().apply {
            moveTo(ox + w * 0.5f, h * 0.44f)
            lineTo(ox + w * 0.86f, h)
            lineTo(ox + w * 0.64f, h)
            lineTo(ox + w * 0.5f, h * 0.74f)
            lineTo(ox + w * 0.36f, h)
            lineTo(ox + w * 0.14f, h)
            close()
        }
        drawPath(p2, color)
    }
}

@Composable
private fun StravaStatsRow(
    col1Label: String, col1Val: String,
    col2Label: String, col2Val: String,
    col3Label: String, col3Val: String,
    textColor: Color,
    subColor: Color,
    dividerColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(col1Label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = subColor, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(col1Val, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
        }
        Box(modifier = Modifier.width(1.dp).height(20.dp).background(dividerColor))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(col2Label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = subColor, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(col2Val, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
        }
        Box(modifier = Modifier.width(1.dp).height(20.dp).background(dividerColor))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text(col3Label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = subColor, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(col3Val, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
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
    val minsStr = if (mins < 10) "0$mins" else "$mins"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (config.template) {
            ShareTemplate.MINIMALIST -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (config.showBranding) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                StravaMarkIcon(color = Color(0xFFFC5200), modifier = Modifier.size(14.dp))
                                Text("HEULA", fontSize = 12.sp, fontWeight = FontWeight.Black, color = textColor)
                            }
                            Text("INTERMITTENT FASTING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f), letterSpacing = 1.sp)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.25f)))
                    }
                    Text("WAKTU BERPUASA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFC5200), letterSpacing = 2.sp)
                    Text(
                        text = "${hours}h ${minsStr}m",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor,
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.25f)))
                    if (config.showTarget || config.showTimes) {
                        StravaStatsRow(
                            col1Label = "POLA", col1Val = session.planLabel,
                            col2Label = "MULAI", col2Val = startStr,
                            col3Label = "SELESAI", col3Val = endStr,
                            textColor = textColor,
                            subColor = Color.White.copy(alpha = 0.6f),
                            dividerColor = Color.White.copy(alpha = 0.2f),
                        )
                    }
                    if (config.showDate) {
                        Text("$dateStr  •  TARGET TUNTAS ✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            ShareTemplate.MODERN_GRADIENT -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF101216).copy(alpha = 0.90f))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                StravaMarkIcon(color = Color(0xFFFC5200), modifier = Modifier.size(13.dp))
                                Text("HEULA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(Brush.horizontalGradient(listOf(Color(0xFFFC5200), Color(0xFFFF7A00))))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text("✓ TARGET TUNTAS", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                        Text("TOTAL WAKTU BERPUASA", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.65f), letterSpacing = 1.sp)
                        Text(
                            text = durFormatted,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.White.copy(alpha = 0.12f)))
                        StravaStatsRow(
                            col1Label = "POLA", col1Val = session.planLabel,
                            col2Label = "MULAI", col2Val = startStr,
                            col3Label = "SELESAI", col3Val = endStr,
                            textColor = Color.White,
                            subColor = Color.White.copy(alpha = 0.55f),
                            dividerColor = Color.White.copy(alpha = 0.12f),
                        )
                        if (config.showDate) {
                            Text(dateStr, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.45f))
                        }
                    }
                }
            }

            ShareTemplate.DARK_MODE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0A0A0E).copy(alpha = 0.95f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.width(3.dp).height(12.dp).background(Color(0xFFFC5200), RoundedCornerShape(2.dp)))
                                Text("INTERMITTENT FASTING", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Text("HEULA PRO", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFC5200))
                        }
                        Text(
                            text = "${hours}j ${minsStr}m",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(Color(0xFF34C759).copy(alpha = 0.16f))
                                .border(0.5.dp, Color(0xFF34C759).copy(alpha = 0.4f), RoundedCornerShape(99.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text("● 100% TARGET PUASA TUNTAS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34C759))
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.White.copy(alpha = 0.10f)))
                        StravaStatsRow(
                            col1Label = "POLA", col1Val = session.planLabel,
                            col2Label = "MULAI", col2Val = startStr,
                            col3Label = "SELESAI", col3Val = endStr,
                            textColor = Color.White,
                            subColor = Color.White.copy(alpha = 0.50f),
                            dividerColor = Color.White.copy(alpha = 0.10f),
                        )
                    }
                }
            }

            ShareTemplate.GLASS_STYLE -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.16f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.38f), RoundedCornerShape(20.dp))
                        .padding(14.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("⚡ HEULA ATHLETICS", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            if (config.showDate) {
                                Text(dateStr, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.75f))
                            }
                        }
                        Text(
                            text = "${hours}h ${minsStr}m",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                        Text("Target ${session.planLabel} Selesai Sempurna", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
                                .padding(vertical = 6.dp),
                        ) {
                            StravaStatsRow(
                                col1Label = "POLA", col1Val = session.planLabel,
                                col2Label = "MULAI", col2Val = startStr,
                                col3Label = "SELESAI", col3Val = endStr,
                                textColor = Color.White,
                                subColor = Color.White.copy(alpha = 0.70f),
                                dividerColor = Color.White.copy(alpha = 0.18f),
                            )
                        }
                    }
                }
            }

            ShareTemplate.TYPOGRAPHY -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.width(3.dp).height(14.dp).background(Color(0xFFFC5200), RoundedCornerShape(2.dp)))
                        Text("HEULA // FASTING MILESTONE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFFC5200), letterSpacing = 1.sp)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$hours",
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Black,
                            color = textColor,
                            lineHeight = 52.sp,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("JAM BERPUASA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = textColor)
                            Text("+ $minsStr MENIT", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFC5200))
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFFC5200)))
                    StravaStatsRow(
                        col1Label = "TARGET", col1Val = session.planLabel,
                        col2Label = "WAKTU", col2Val = "$startStr → $endStr",
                        col3Label = "TANGGAL", col3Val = dateStr,
                        textColor = textColor,
                        subColor = Color.White.copy(alpha = 0.6f),
                        dividerColor = Color.White.copy(alpha = 0.2f),
                    )
                }
            }

            ShareTemplate.EDITORIAL -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0E0F12).copy(alpha = 0.92f))
                        .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("—  HEULA ATHLETIC JOURNAL  —", fontSize = 9.sp, fontFamily = FontFamily.Serif, color = Color.White.copy(alpha = 0.75f), letterSpacing = 2.sp)
                        Text("VOLUME 24  •  $dateStr", fontSize = 8.sp, color = Color.White.copy(alpha = 0.45f))
                        Text(
                            text = "${hours}h ${minsStr}m",
                            fontSize = 36.sp,
                            fontFamily = FontFamily.Serif,
                            color = Color.White,
                        )
                        Text("Aktivitas Berpuasa Terverifikasi", fontSize = 9.sp, fontFamily = FontFamily.Serif, color = Color.White.copy(alpha = 0.8f))
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.White.copy(alpha = 0.15f)))
                        StravaStatsRow(
                            col1Label = "POLA", col1Val = session.planLabel,
                            col2Label = "MULAI", col2Val = startStr,
                            col3Label = "SELESAI", col3Val = endStr,
                            textColor = Color.White,
                            subColor = Color.White.copy(alpha = 0.50f),
                            dividerColor = Color.White.copy(alpha = 0.15f),
                        )
                    }
                }
            }

            ShareTemplate.NEON -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0A0C10).copy(alpha = 0.94f))
                        .border(1.5.dp, Color(0xFFCCFF00), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("⚡ HEULA NIGHT FAST", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFCCFF00))
                            Text("RECORD: UNLOCKED", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00F0FF))
                        }
                        Text(
                            text = durFormatted,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                        Text("TARGET ${session.planLabel} COMPLETED", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFCCFF00))
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFF00F0FF).copy(alpha = 0.35f)))
                        StravaStatsRow(
                            col1Label = "POLA", col1Val = session.planLabel,
                            col2Label = "MULAI", col2Val = startStr,
                            col3Label = "SELESAI", col3Val = endStr,
                            textColor = Color.White,
                            subColor = Color(0xFFCCFF00),
                            dividerColor = Color(0xFF00F0FF).copy(alpha = 0.30f),
                        )
                    }
                }
            }

            ShareTemplate.CLEAN_WHITE -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (config.showBranding) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                StravaMarkIcon(color = Color.White, modifier = Modifier.size(14.dp))
                                Text("HEULA", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Text("FASTING ACTIVITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.40f)))
                    }
                    Text("DURASI BERPUASA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 2.sp)
                    Text(
                        text = "${hours}h ${minsStr}m",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.40f)))
                    StravaStatsRow(
                        col1Label = "POLA", col1Val = session.planLabel,
                        col2Label = "MULAI", col2Val = startStr,
                        col3Label = "SELESAI", col3Val = endStr,
                        textColor = Color.White,
                        subColor = Color.White.copy(alpha = 0.80f),
                        dividerColor = Color.White.copy(alpha = 0.35f),
                    )
                    if (config.showDate) {
                        Text("$dateStr  •  TARGET TUNTAS ✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
