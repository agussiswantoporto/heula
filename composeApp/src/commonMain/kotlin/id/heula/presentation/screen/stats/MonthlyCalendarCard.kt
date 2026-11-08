package id.heula.presentation.screen.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.AppLanguage
import id.heula.domain.model.FastingSession
import id.heula.presentation.component.icon.getMoodVisual
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.theme.BrandPrimary
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MonthlyCalendarCard(
    sessions: List<FastingSession>,
    language: AppLanguage,
    modifier: Modifier = Modifier,
) {
    val isEnglish = language == AppLanguage.ENGLISH
    val tz = remember { TimeZone.currentSystemDefault() }
    val nowLdt = remember {
        Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()).toLocalDateTime(tz)
    }

    var selectedYear by remember { mutableStateOf(nowLdt.year) }
    var selectedMonth by remember { mutableStateOf(nowLdt.monthNumber) }
    var selectedDay by remember { mutableStateOf<Int?>(nowLdt.dayOfMonth) }

    // Map sessions to dayOfMonth for the selected year & month
    val sessionsByDay = remember(sessions, selectedYear, selectedMonth) {
        val map = mutableMapOf<Int, MutableList<FastingSession>>()
        sessions.forEach { s ->
            val endLdt = Instant.fromEpochMilliseconds(s.endMs).toLocalDateTime(tz)
            if (endLdt.year == selectedYear && endLdt.monthNumber == selectedMonth) {
                map.getOrPut(endLdt.dayOfMonth) { mutableListOf() }.add(s)
            }
        }
        map
    }

    val daysInMonth = remember(selectedYear, selectedMonth) {
        getDaysInMonth(selectedYear, selectedMonth)
    }

    val firstDayOffset = remember(selectedYear, selectedMonth) {
        getFirstDayOffset(selectedYear, selectedMonth)
    }

    // Monthly Summary metrics
    val totalFastingDaysInMonth = sessionsByDay.keys.size
    val completedDaysInMonth = sessionsByDay.count { (_, daySessions) ->
        daySessions.any { it.completed } || daySessions.sumOf { it.durationMs } >= (daySessions.firstOrNull()?.targetHours ?: 16.0) * 3600000L
    }
    val totalHoursInMonth = sessionsByDay.values.flatten().sumOf { it.durationMs } / 3600000.0
    val successRate = if (totalFastingDaysInMonth > 0) ((completedDaysInMonth.toDouble() / totalFastingDaysInMonth) * 100).toInt() else 0

    val monthNamesId = listOf("", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    val monthNamesEn = listOf("", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val monthName = if (isEnglish) monthNamesEn[selectedMonth] else monthNamesId[selectedMonth]

    val dayHeaders = if (isEnglish) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    } else {
        listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    }

    HeulaCard(modifier = modifier) {
        // 1. Header with Month/Year Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "$monthName $selectedYear",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    HeulaBadge(text = if (isEnglish) "Calendar" else "Kalender Puasa")
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isEnglish) "$completedDaysInMonth days completed" else "$completedDaysInMonth hari tuntas puasa",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Today reset button if navigated away
                if (selectedYear != nowLdt.year || selectedMonth != nowLdt.monthNumber) {
                    IconButton(
                        onClick = {
                            selectedYear = nowLdt.year
                            selectedMonth = nowLdt.monthNumber
                            selectedDay = nowLdt.dayOfMonth
                        },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Hari Ini",
                            tint = BrandPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                // Prev Month
                IconButton(
                    onClick = {
                        if (selectedMonth == 1) {
                            selectedMonth = 12
                            selectedYear -= 1
                        } else {
                            selectedMonth -= 1
                        }
                        selectedDay = null
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Bulan Sebelumnya",
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Next Month
                IconButton(
                    onClick = {
                        if (selectedMonth == 12) {
                            selectedMonth = 1
                            selectedYear += 1
                        } else {
                            selectedMonth += 1
                        }
                        selectedDay = null
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Bulan Berikutnya",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Day of Week Header Row (7 Cols)
        Row(modifier = Modifier.fillMaxWidth()) {
            dayHeaders.forEach { header ->
                Text(
                    text = header,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Days Grid (Rows of 7 items)
        val totalCells = firstDayOffset + daysInMonth
        val numRows = (totalCells + 6) / 7

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (row in 0 until numRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val day = cellIndex - firstDayOffset + 1

                        if (day in 1..daysInMonth) {
                            val daySessions = sessionsByDay[day] ?: emptyList()
                            val hasFasted = daySessions.isNotEmpty()
                            val isCompleted = hasFasted && (daySessions.any { it.completed } || daySessions.sumOf { it.durationMs } >= (daySessions.firstOrNull()?.targetHours ?: 16.0) * 3600000L)
                            val isToday = (selectedYear == nowLdt.year && selectedMonth == nowLdt.monthNumber && day == nowLdt.dayOfMonth)
                            val isSelected = selectedDay == day

                            val cellBg = when {
                                isSelected -> BrandPrimary.copy(alpha = 0.22f)
                                isCompleted -> BrandPrimary.copy(alpha = 0.14f)
                                hasFasted -> Color(0xFFFF9500).copy(alpha = 0.12f)
                                else -> Color.Transparent
                            }

                            val cellBorder = when {
                                isSelected -> BorderStroke(1.5.dp, BrandPrimary)
                                isToday -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                isCompleted -> BorderStroke(0.5.dp, BrandPrimary.copy(alpha = 0.35f))
                                hasFasted -> BorderStroke(0.5.dp, Color(0xFFFF9500).copy(alpha = 0.35f))
                                else -> null
                            }

                            val numColor = when {
                                isSelected -> BrandPrimary
                                isCompleted -> BrandPrimary
                                hasFasted -> Color(0xFFFF9500)
                                isToday -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(cellBg)
                                    .then(if (cellBorder != null) Modifier.border(cellBorder, RoundedCornerShape(10.dp)) else Modifier)
                                    .clickable { selectedDay = day },
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = "$day",
                                        fontSize = 12.sp,
                                        fontWeight = if (hasFasted || isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = numColor,
                                    )
                                    if (hasFasted) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isCompleted) BrandPrimary else Color(0xFFFF9500))
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        } else {
                            // Blank cell for alignment
                            Box(modifier = Modifier.weight(1f).height(42.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Selected Day Detail Banner
        val currentSelectedDay = selectedDay
        if (currentSelectedDay != null) {
            val daySessions = sessionsByDay[currentSelectedDay] ?: emptyList()
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val dateHeaderStr = "$currentSelectedDay $monthName $selectedYear"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = dateHeaderStr,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (daySessions.isNotEmpty()) {
                            val isCompleted = daySessions.any { it.completed } || daySessions.sumOf { it.durationMs } >= (daySessions.firstOrNull()?.targetHours ?: 16.0) * 3600000L
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCompleted) BrandPrimary.copy(alpha = 0.15f) else Color(0xFFFF9500).copy(alpha = 0.15f),
                            ) {
                                Text(
                                    text = if (isCompleted) (if (isEnglish) "Completed ✓" else "Target Tuntas ✓") else (if (isEnglish) "Partial" else "Parsial"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) BrandPrimary else Color(0xFFFF9500),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (daySessions.isNotEmpty()) {
                        val first = daySessions.first()
                        val totalMs = daySessions.sumOf { it.durationMs }
                        val hours = totalMs / 3600000
                        val mins = (totalMs % 3600000) / 60000

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${hours}j ${mins}m",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                            )
                            Text(
                                text = "•  ${first.planLabel}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (first.mood != null) {
                                val moodVisual = getMoodVisual(first.mood)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                ) {
                                    Icon(
                                        imageVector = moodVisual.icon,
                                        contentDescription = moodVisual.label,
                                        tint = moodVisual.color,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = moodVisual.label,
                                        fontSize = 11.sp,
                                        color = moodVisual.color,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }

                        if (!first.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${first.note}\"",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            )
                        }
                    } else {
                        Text(
                            text = if (isEnglish) "No fasting record recorded on this date." else "Tidak ada catatan puasa pada tanggal ini.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Monthly Metrics Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isEnglish) "Completed" else "Hari Tuntas",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$completedDaysInMonth ${if (isEnglish) "Days" else "Hari"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                )
            }

            Box(modifier = Modifier.height(24.dp).width(0.5.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isEnglish) "Total Hours" else "Total Waktu",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${totalHoursInMonth.toInt()} ${if (isEnglish) "Hours" else "Jam"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(modifier = Modifier.height(24.dp).width(0.5.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isEnglish) "Success Rate" else "Tingkat Sukses",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$successRate%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (successRate >= 75) BrandPrimary else Color(0xFFFF9500),
                )
            }
        }
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

/**
 * Returns 0 for Monday, 1 for Tuesday ... 6 for Sunday
 */
private fun getFirstDayOffset(year: Int, month: Int): Int {
    val date = kotlinx.datetime.LocalDate(year, month, 1)
    return when (date.dayOfWeek.name) {
        "MONDAY" -> 0
        "TUESDAY" -> 1
        "WEDNESDAY" -> 2
        "THURSDAY" -> 3
        "FRIDAY" -> 4
        "SATURDAY" -> 5
        "SUNDAY" -> 6
        else -> 0
    }
}
