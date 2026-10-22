package id.heula.presentation.screen.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.FastingSession
import id.heula.domain.model.WeightRecord
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.store.AppStore
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.store.calculateStreak
import id.heula.presentation.util.getStrings
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun StatsScreen(
    store: AppStore,
    modifier: Modifier = Modifier,
) {
    val sessions by store.sessions.collectAsState()
    val weights by store.weights.collectAsState()
    val settings by store.settings.collectAsState()
    val s = getStrings(settings.language)

    var showWeightDialog by remember { mutableStateOf(false) }

    val totalFasts = sessions.size
    val totalMs = sessions.sumOf { it.durationMs }
    val totalHours = totalMs / 3600000.0
    val avgHours = if (totalFasts > 0) totalHours / totalFasts else 0.0
    val streak = calculateStreak(sessions)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth >= 840.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                // Left Column: Fasting summary & Weekly chart
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Text(s.statsTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    SummaryGrid(
                        streak = streak,
                        totalFasts = totalFasts,
                        totalHours = totalHours,
                        avgHours = avgHours,
                        s = s,
                    )

                    WeeklyChartCard(sessions = sessions, s = s)
                }

                // Right Column: Weight tracking & Achievements
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    WeightSection(
                        weights = weights,
                        heightCm = settings.profiling?.heightCm ?: 170.0,
                        targetWeightKg = settings.profiling?.targetWeightKg,
                        initialWeightKg = settings.profiling?.weightKg ?: weights.firstOrNull()?.weightKg,
                        s = s,
                        onAddWeight = { showWeightDialog = true },
                    )

                    AchievementsSection(totalFasts = totalFasts, totalHours = totalHours, s = s)
                }
            }
        } else {
            // Mobile: Single column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(s.statsTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                SummaryGrid(
                    streak = streak,
                    totalFasts = totalFasts,
                    totalHours = totalHours,
                    avgHours = avgHours,
                    s = s,
                )

                WeeklyChartCard(sessions = sessions, s = s)

                WeightSection(
                    weights = weights,
                    heightCm = settings.profiling?.heightCm ?: 170.0,
                    targetWeightKg = settings.profiling?.targetWeightKg,
                    initialWeightKg = settings.profiling?.weightKg ?: weights.firstOrNull()?.weightKg,
                    s = s,
                    onAddWeight = { showWeightDialog = true },
                )

                AchievementsSection(totalFasts = totalFasts, totalHours = totalHours, s = s)
            }
        }
    }

    if (showWeightDialog) {
        AddWeightDialog(
            onSave = { kg ->
                store.logWeight(kg)
                showWeightDialog = false
            },
            onDismiss = { showWeightDialog = false },
        )
    }
}

@Composable
private fun SummaryGrid(
    streak: Int,
    totalFasts: Int,
    totalHours: Double,
    avgHours: Double,
    s: id.heula.presentation.util.Strings,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatCard(
                title = s.streakStatTitle,
                value = if (streak > 0) "$streak Hari" else "0 Hari",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                title = s.totalCompletedStatTitle,
                value = "$totalFasts kali",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatCard(
                title = s.totalDurationStatTitle,
                value = "${totalHours.toInt()} jam",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                title = s.averageFastStatTitle,
                value = "${(avgHours * 10).toInt() / 10.0} jam",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    HeulaCard(modifier = modifier) {
        Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeeklyChartCard(
    sessions: List<FastingSession>,
    s: id.heula.presentation.util.Strings,
) {
    val now = Clock.System.now().toEpochMilliseconds()
    val oneDayMs = 86400000L

    val dayLabels = (6 downTo 0).map { daysAgo ->
        val instant = Instant.fromEpochMilliseconds(now - daysAgo * oneDayMs)
        val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        when (ldt.dayOfWeek.name) {
            "MONDAY" -> "Sen"
            "TUESDAY" -> "Sel"
            "WEDNESDAY" -> "Rab"
            "THURSDAY" -> "Kam"
            "FRIDAY" -> "Jum"
            "SATURDAY" -> "Sab"
            "SUNDAY" -> "Min"
            else -> ldt.dayOfWeek.name.take(3)
        }
    }

    val dynamicValues = (6 downTo 0).map { daysAgo ->
        val dayStart = (now / oneDayMs - daysAgo) * oneDayMs
        val dayEnd = dayStart + oneDayMs
        val daySessions = sessions.filter {
            it.startMs in dayStart until dayEnd || (it.startMs < dayEnd && it.endMs > dayStart)
        }
        val dayMs = daySessions.sumOf { it.durationMs }
        (dayMs / 3600000.0).toFloat()
    }

    val maxVal = maxOf(24f, dynamicValues.maxOrNull() ?: 16f)

    HeulaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(s.last7DaysTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            val avgWeek = if (dynamicValues.isNotEmpty()) dynamicValues.average() else 0.0
            Text(
                "Rerata: ${(avgWeek * 10).toInt() / 10.0}j/hari",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        val barColor = BrandPrimary
        val trackColor = MaterialTheme.colorScheme.surfaceVariant

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            dayLabels.forEachIndexed { i, day ->
                val v = dynamicValues.getOrElse(i) { 0f }
                val ratio = (v / maxVal).coerceIn(0f, 1f)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f),
                ) {
                    if (v > 0f) {
                        Text(
                            text = "${v.toInt()}j",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(trackColor),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        if (ratio > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(ratio)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(barColor),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(day, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun WeightSection(
    weights: List<WeightRecord>,
    heightCm: Double,
    targetWeightKg: Double?,
    initialWeightKg: Double?,
    s: id.heula.presentation.util.Strings,
    onAddWeight: () -> Unit,
) {
    val latestWeight = weights.lastOrNull()?.weightKg ?: initialWeightKg
    val bmi = if (latestWeight != null && heightCm > 0) {
        val hMeter = heightCm / 100.0
        latestWeight / (hMeter * hMeter)
    } else null

    HeulaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(s.weightTrackingTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (latestWeight != null) {
                    Text(
                        text = "Terkini: ${latestWeight} kg ${if (bmi != null) "• BMI: ${(bmi * 10).toInt() / 10.0}" else ""}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text("Belum ada catatan berat", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Button(
                onClick = onAddWeight,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(s.logWeightButton, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        if (targetWeightKg != null && latestWeight != null) {
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            val startKg = initialWeightKg ?: latestWeight
            val deltaTotal = kotlin.math.abs(startKg - targetWeightKg)
            val deltaCurrent = kotlin.math.abs(latestWeight - targetWeightKg)
            val progress = if (deltaTotal > 0.05) {
                ((deltaTotal - deltaCurrent) / deltaTotal).coerceIn(0.0, 1.0).toFloat()
            } else 1f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Target: ${targetWeightKg} kg", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                val diffToTarget = latestWeight - targetWeightKg
                val statusText = when {
                    kotlin.math.abs(diffToTarget) <= 0.2 -> "Target tercapai!"
                    diffToTarget > 0 -> "Sisa ${(diffToTarget * 10).toInt() / 10.0} kg lagi"
                    else -> "Kurang ${((-diffToTarget) * 10).toInt() / 10.0} kg lagi"
                }
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimary,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = BrandPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun AchievementsSection(
    totalFasts: Int,
    totalHours: Double,
    s: id.heula.presentation.util.Strings,
) {
    HeulaCard {
        Text(s.achievementsTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        val milestones = listOf(
            Triple("Puasa Pertama", "Selesaikan puasa pertamamu", totalFasts >= 1),
            Triple("10 Kali Puasa", "Konsistensi mulai terbentuk", totalFasts >= 10),
            Triple("100 Jam Puasa", "Total 100 jam berpuasa", totalHours >= 100),
            Triple("Target 24 Jam", "Berhasil puasa satu hari penuh", totalHours >= 24),
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            milestones.forEach { (title, desc, unlocked) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (unlocked) HeulaIcons.Trophy else Icons.Default.Lock,
                        contentDescription = if (unlocked) "Tercapai" else "Terkunci",
                        tint = if (unlocked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (unlocked) {
                        HeulaBadge(text = "Raih")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWeightDialog(
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var weightText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Berat Badan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Masukkan berat badanmu hari ini (kg):", fontSize = 14.sp)
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    placeholder = { Text("Contoh: 68.5") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val kg = weightText.toDoubleOrNull()
                    if (kg != null && kg > 0) {
                        onSave(kg)
                    }
                }
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold, color = BrandPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        shape = RoundedCornerShape(20.dp),
    )
}
