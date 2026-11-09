package id.heula.presentation.screen.stats

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.FastingSession
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.util.Strings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SmartTrendsCard(
    sessions: List<FastingSession>,
    s: Strings,
    modifier: Modifier = Modifier,
) {
    // If no sessions, hide or show friendly starter card
    val totalSessions = sessions.size
    if (totalSessions == 0) return

    val metrics = remember(sessions) {
        val completedList = sessions.filter { it.completed }
        val completionRate = if (totalSessions > 0) ((completedList.size.toFloat() / totalSessions.toFloat()) * 100).toInt() else 0

        // Average duration
        val totalDurationMs = sessions.sumOf { it.durationMs }
        val avgDurationHours = if (totalSessions > 0) (totalDurationMs / (totalSessions * 3600000.0)) else 0.0

        // Fat burning hours (>4 hours mark in each session)
        val fatBurnMs = sessions.sumOf {
            val h = it.durationMs - (4 * 3600000L)
            if (h > 0) h else 0L
        }
        val fatBurnHours = (fatBurnMs / 3600000.0).toInt()

        // Golden start hour
        val hourCounts = mutableMapOf<Int, Int>()
        sessions.forEach {
            val ldt = Instant.fromEpochMilliseconds(it.startMs).toLocalDateTime(TimeZone.currentSystemDefault())
            val h = ldt.hour
            hourCounts[h] = (hourCounts[h] ?: 0) + 1
        }
        val topHour = hourCounts.maxByOrNull { it.value }?.key ?: 20
        val topHourEnd = (topHour + 1) % 24

        Triple(avgDurationHours, completionRate, Pair(fatBurnHours, "${topHour.toString().padStart(2, '0')}:00 - ${topHourEnd.toString().padStart(2, '0')}:00"))
    }

    val (avgDuration, completionRate, extra) = metrics
    val (fatBurnHours, goldenHourStr) = extra

    HeulaCard(modifier = modifier) {
        // Section Header with Apple Trends Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(BrandPrimary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = HeulaIcons.TrendingUp,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column {
                    Text(
                        text = s.trendsSectionTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Highlight pola kebiasaan & metabolisme",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF34C759).copy(alpha = 0.12f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF34C759)))
                    Text(
                        text = "$completionRate% Sukses",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34C759),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Column HIG Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TrendItem(
                icon = HeulaIcons.Flame,
                iconColor = Color(0xFFFF9500),
                title = "Bakar Lemak",
                value = "${fatBurnHours}j",
                subtitle = "Total fase lumer",
                modifier = Modifier.weight(1f),
            )

            TrendItem(
                icon = HeulaIcons.TrendingUp,
                iconColor = BrandPrimary,
                title = "Rerata Puasa",
                value = "${(avgDuration * 10).toInt() / 10.0}j",
                subtitle = "Per sesi puasa",
                modifier = Modifier.weight(1f),
            )

            TrendItem(
                icon = HeulaIcons.Moon,
                iconColor = Color(0xFF5856D6),
                title = "Jam Emas",
                value = goldenHourStr.take(5),
                subtitle = "Mulai favorit",
                modifier = Modifier.weight(1.1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Casual Gojek-Style Insight Callout
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = HeulaIcons.Sparkles,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Paling sering mulai di sekitar jam ${goldenHourStr}. Jam ini terbukti paling konsisten bikin puasamu tembus target!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 15.sp,
                )
            }
        }
    }
}

@Composable
private fun TrendItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.3).sp,
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            )
        }
    }
}
