package id.heula.presentation.component.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.Stage
import id.heula.domain.model.TimelineEntry
import id.heula.domain.model.TimelineState
import id.heula.domain.model.timelineForElapsed
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.theme.BrandPrimary

@Composable
fun StageIndicator(
    stage: Stage,
    elapsedMs: Long,
    modifier: Modifier = Modifier,
) {
    var showTimelineModal by remember { mutableStateOf(false) }

    HeulaCard(
        modifier = modifier,
        onClick = { showTimelineModal = true },
    ) {
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Fase",
                        tint = BrandPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Fase Sekarang: ${stage.label}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stage.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = "Lihat Fase →",
                fontSize = 12.sp,
                color = BrandPrimary,
                fontWeight = FontWeight.Medium,
            )
        }
    }

    if (showTimelineModal) {
        val timeline = remember(elapsedMs) { timelineForElapsed(elapsedMs) }

        AlertDialog(
            onDismissRequest = { showTimelineModal = false },
            confirmButton = {
                TextButton(onClick = { showTimelineModal = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = BrandPrimary)
                }
            },
            title = {
                Text(
                    text = "Perjalanan Puasamu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(timeline) { entry ->
                        TimelineRow(entry)
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Composable
private fun TimelineRow(entry: TimelineEntry) {
    val stage = entry.stage
    val isCurrent = entry.state == TimelineState.CURRENT
    val isPassed = entry.state == TimelineState.PASSED

    val indicatorColor = when {
        isCurrent -> BrandPrimary
        isPassed -> BrandPrimary.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(indicatorColor),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "${stage.label} (${stage.minHours.toInt()} jam+)",
                    fontSize = 14.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isCurrent) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                )
                if (isCurrent) {
                    Text(
                        text = "Sedang Berjalan",
                        fontSize = 11.sp,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                text = stage.whatHappens,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (isCurrent) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tip: ${stage.tip}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                )
            }
        }
    }
}
