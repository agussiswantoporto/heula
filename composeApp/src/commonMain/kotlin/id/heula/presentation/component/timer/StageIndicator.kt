package id.heula.presentation.component.timer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.heula.domain.model.STAGES
import id.heula.domain.model.Stage
import id.heula.domain.model.TimelineEntry
import id.heula.domain.model.TimelineState
import id.heula.domain.model.nextStageForElapsed
import id.heula.domain.model.timelineForElapsed
import id.heula.presentation.theme.BrandPrimary

/**
 * Visual metadata for a stage: pure vector icon and vibrant iOS system palette.
 * Completely immune to Skiko / Web Canvas emoji font fallback issues.
 */
data class StageVisual(
    val icon: ImageVector,
    val primaryColor: Color,
    val containerColor: Color,
)

fun getStageVisual(stageKey: String): StageVisual {
    return when (stageKey) {
        "pencernaan" -> StageVisual(
            icon = Icons.Default.Restaurant,
            primaryColor = Color(0xFF007AFF), // iOS System Blue
            containerColor = Color(0xFF007AFF).copy(alpha = 0.12f),
        )
        "bakar_lemak" -> StageVisual(
            icon = Icons.Default.LocalFireDepartment,
            primaryColor = Color(0xFFFF9500), // iOS System Orange
            containerColor = Color(0xFFFF9500).copy(alpha = 0.14f),
        )
        "ketosis" -> StageVisual(
            icon = Icons.Default.Bolt,
            primaryColor = Color(0xFFFFCC00), // iOS System Amber/Yellow
            containerColor = Color(0xFFFFCC00).copy(alpha = 0.16f),
        )
        "autophagy" -> StageVisual(
            icon = Icons.Default.Autorenew,
            primaryColor = Color(0xFF34C759), // iOS System Green
            containerColor = Color(0xFF34C759).copy(alpha = 0.14f),
        )
        "deep_fast" -> StageVisual(
            icon = Icons.Default.EmojiEvents,
            primaryColor = Color(0xFFAF52DE), // iOS System Purple
            containerColor = Color(0xFFAF52DE).copy(alpha = 0.14f),
        )
        else -> StageVisual(
            icon = Icons.Default.Bolt,
            primaryColor = Color(0xFF34C759),
            containerColor = Color(0xFF34C759).copy(alpha = 0.12f),
        )
    }
}

/**
 * Modern iOS-style Stage Indicator card for HomeScreen.
 * Displays current fasting stage, segmented progress bar, next milestone,
 * and launches the responsive Timeline Modal on tap.
 */
@Composable
fun StageIndicator(
    stage: Stage,
    elapsedMs: Long,
    s: id.heula.presentation.util.Strings = id.heula.presentation.util.IndonesianStrings,
    modifier: Modifier = Modifier,
) {
    var showTimelineModal by remember { mutableStateOf(false) }

    val nextInfo = remember(elapsedMs) { nextStageForElapsed(elapsedMs) }
    val currentIndex = remember(stage) { STAGES.indexOfFirst { it.key == stage.key }.coerceAtLeast(0) }
    val visual = remember(stage.key) { getStageVisual(stage.key) }

    // Pulsing animation for active indicator
    val infiniteTransition = rememberInfiniteTransition(label = "stagePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Surface(
        onClick = { showTimelineModal = true },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header: Category, Stage Title with Vector Icon, and Action Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .scale(pulseScale)
                            .clip(RoundedCornerShape(13.dp))
                            .background(visual.containerColor)
                            .border(
                                width = 1.dp,
                                color = visual.primaryColor.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(13.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = stage.label,
                            tint = visual.primaryColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "PERJALANAN PUASA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = visual.primaryColor,
                            letterSpacing = 0.6.sp,
                        )
                        Text(
                            text = "Lagi ${stage.label}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.3).sp,
                        )
                    }
                }

                // iOS-style Pill action button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandPrimary.copy(alpha = 0.10f),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = "Detail Alur ›",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandPrimary,
                        )
                    }
                }
            }

            // Casual explanation
            Text(
                text = stage.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )

            // Segmented Progress Bar (5 stages of fasting)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                STAGES.indices.forEach { index ->
                    val isPassed = index < currentIndex
                    val isCurrent = index == currentIndex
                    val segVisual = getStageVisual(STAGES[index].key)

                    val segmentColor = when {
                        isCurrent -> segVisual.primaryColor
                        isPassed -> BrandPrimary.copy(alpha = 0.65f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(segmentColor)
                    )
                }
            }

            // Footer info: Next milestone & stage counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (nextInfo != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = visual.primaryColor,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${nextInfo.remainingFormatted} lagi menuju ${nextInfo.stage.label}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = visual.primaryColor,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kamu sudah di fase puncak puasa!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = visual.primaryColor,
                        )
                    }
                }

                Text(
                    text = "Fase ${currentIndex + 1} dari 5",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }

    if (showTimelineModal) {
        id.heula.presentation.component.metabolism.MetabolismJournalModal(
            currentStage = stage,
            elapsedMs = elapsedMs,
            s = s,
            onDismiss = { showTimelineModal = false },
        )
    }
}

/**
 * Full-featured iOS-style Fasting Journey Modal Dialog.
 * Adapts seamlessly between Mobile (Bottom Sheet feel) and Web (Floating Grouped Modal).
 */
@Composable
private fun FastingJourneyModal(
    currentStage: Stage,
    elapsedMs: Long,
    onDismiss: () -> Unit,
) {
    val timeline = remember(elapsedMs) { timelineForElapsed(elapsedMs) }
    val totalHours = elapsedMs / 3_600_000.0
    val totalH = totalHours.toInt()
    val totalM = ((totalHours - totalH) * 60).toInt()
    val currentVisual = remember(currentStage.key) { getStageVisual(currentStage.key) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.50f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        ) {
            val isWide = maxWidth >= 620.dp

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = if (isWide) Alignment.Center else Alignment.BottomCenter,
            ) {
                Surface(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}, // Prevent dismiss when tapping inside
                        )
                        .then(
                            if (isWide) {
                                Modifier
                                    .width(580.dp)
                                    .fillMaxHeight(0.88f)
                                    .padding(vertical = 24.dp)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.92f)
                            }
                        ),
                    shape = if (isWide) RoundedCornerShape(26.dp) else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    ) {
                        // Grabber bar (prominent in mobile view)
                        if (!isWide) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 12.dp)
                                    .size(width = 38.dp, height = 5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                            )
                        }

                        // Header with Title, Friendly Subtitle, and Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Perjalanan Puasamu",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.6).sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Yuk intip proses luar biasa di tubuhmu detik demi detik!",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            // iOS circular close button
                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(30.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Current Progress Highlight Card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = currentVisual.containerColor,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = currentVisual.primaryColor.copy(alpha = 0.28f),
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(currentVisual.primaryColor),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = currentVisual.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Waktu Puasa: ${totalH} jam ${totalM} menit",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "Sekarang di fase ${currentStage.label}. Tubuhmu lagi kerja hebat!",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Vertical Timeline List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            itemsIndexed(timeline) { index, entry ->
                                val isLast = index == timeline.lastIndex
                                FastingTimelineNode(entry = entry, isLast = isLast)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom action button
                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPrimary,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        ) {
                            Text(
                                text = "Keren, Semangat Lanjut Puasa!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Node and Stage Content in the Vertical Fasting Timeline.
 */
@Composable
private fun FastingTimelineNode(
    entry: TimelineEntry,
    isLast: Boolean,
) {
    val stage = entry.stage
    val isCurrent = entry.state == TimelineState.CURRENT
    val isPassed = entry.state == TimelineState.PASSED
    val visual = remember(stage.key) { getStageVisual(stage.key) }

    val lineColor = when {
        isPassed -> BrandPrimary.copy(alpha = 0.6f)
        isCurrent -> visual.primaryColor.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        // Left Column: Node Icon & Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isPassed -> BrandPrimary
                            isCurrent -> visual.containerColor
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (isCurrent) 2.dp else 1.dp,
                        color = when {
                            isCurrent -> visual.primaryColor
                            isPassed -> Color.Transparent
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    isPassed -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selesai",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    isCurrent -> {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = stage.label,
                            tint = visual.primaryColor,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Right Column: iOS Grouped Stage Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isCurrent) {
                visual.containerColor
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            },
            border = if (isCurrent) {
                androidx.compose.foundation.BorderStroke(1.dp, visual.primaryColor.copy(alpha = 0.35f))
            } else {
                null
            },
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 8.dp else 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Title with Vector Icon, Time Range, and Status Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = null,
                            tint = if (isCurrent) visual.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = stage.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) visual.primaryColor else MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isCurrent -> visual.primaryColor.copy(alpha = 0.15f)
                            isPassed -> BrandPrimary.copy(alpha = 0.10f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            if (isCurrent) {
                                Icon(
                                    imageVector = visual.icon,
                                    contentDescription = null,
                                    tint = visual.primaryColor,
                                    modifier = Modifier.size(12.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sedang Berjalan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = visual.primaryColor,
                                )
                            } else if (isPassed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(12.dp),
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Selesai",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                )
                            } else {
                                Text(
                                    text = stage.hourRange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // What Happens (Bahasa Santai)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Yang lagi terjadi di tubuhmu:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stage.whatHappens,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp,
                    )
                }

                // Friendly Tip Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCurrent) {
                        visual.primaryColor.copy(alpha = 0.10f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isCurrent) visual.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp).padding(top = 1.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tips santai: ${stage.tip}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isCurrent) visual.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp,
                        )
                    }
                }
            }
        }
    }
}
