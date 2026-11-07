package id.heula.presentation.screen.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.FastingSession
import id.heula.domain.model.Mood
import id.heula.presentation.component.icon.getMoodVisual
import id.heula.presentation.component.ui.ConfirmDialog
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.screen.share.FastingShareModal
import id.heula.presentation.store.AppStore
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.theme.DangerRed
import id.heula.presentation.util.getStrings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HistoryScreen(
    store: AppStore,
    modifier: Modifier = Modifier,
) {
    val sessions by store.sessions.collectAsState()
    val settings by store.settings.collectAsState()
    val s = getStrings(settings.language)

    var sessionToDelete by remember { mutableStateOf<FastingSession?>(null) }
    var sessionToEdit by remember { mutableStateOf<FastingSession?>(null) }
    var sessionToShare by remember { mutableStateOf<FastingSession?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth >= 768.dp
        val horizontalPadding = if (isWide) 64.dp else 20.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 20.dp),
        ) {
            Text(
                text = s.historyTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Riwayat Kosong",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = s.emptyHistoryTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = s.emptyHistoryDesc,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(sessions, key = { it.id }) { session ->
                        SessionItemCard(
                            session = session,
                            targetCompletedText = s.targetCompleted,
                            endedEarlyText = s.endedEarly,
                            noteMoodButtonText = s.noteMoodButton,
                            deleteButtonText = s.deleteButton,
                            isEnglish = settings.language == id.heula.domain.model.AppLanguage.ENGLISH,
                            onDelete = { sessionToDelete = session },
                            onEdit = { sessionToEdit = session },
                            onShare = { sessionToShare = session },
                        )
                    }
                }
            }
        }
    }

    if (sessionToShare != null) {
        FastingShareModal(
            session = sessionToShare!!,
            onDismiss = { sessionToShare = null },
            onExport = { config, onResult ->
                store.exportShareImage(sessionToShare!!, config, onResult)
            }
        )
    }

    if (sessionToDelete != null) {
        ConfirmDialog(
            show = true,
            title = if (settings.language == id.heula.domain.model.AppLanguage.ENGLISH) "Delete this record?" else "Hapus catatan ini?",
            body = if (settings.language == id.heula.domain.model.AppLanguage.ENGLISH) "This fasting session will be permanently deleted." else "Catatan puasa ini bakal dihapus permanen dan gak bisa dibalikin.",
            confirmText = if (settings.language == id.heula.domain.model.AppLanguage.ENGLISH) "Yes, delete" else "Ya, hapus",
            dismissText = s.cancel,
            isDanger = true,
            onConfirm = {
                store.deleteSession(sessionToDelete!!.id)
                sessionToDelete = null
            },
            onDismiss = { sessionToDelete = null },
        )
    }

    if (sessionToEdit != null) {
        EditSessionDialog(
            session = sessionToEdit!!,
            isEnglish = settings.language == id.heula.domain.model.AppLanguage.ENGLISH,
            onSave = { updated ->
                store.updateSession(updated)
                sessionToEdit = null
            },
            onDismiss = { sessionToEdit = null },
        )
    }
}

@Composable
private fun SessionItemCard(
    session: FastingSession,
    targetCompletedText: String,
    endedEarlyText: String,
    noteMoodButtonText: String,
    deleteButtonText: String,
    isEnglish: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
) {
    val durationHours = session.durationMs / 3600000
    val durationMins = (session.durationMs % 3600000) / 60000

    val startInstant = Instant.fromEpochMilliseconds(session.startMs)
    val startLdt = startInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val endInstant = Instant.fromEpochMilliseconds(session.endMs)
    val endLdt = endInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    val monthName = startLdt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val dateStr = "${startLdt.dayOfMonth} $monthName ${startLdt.year}"
    val startTimeStr = "${startLdt.hour.toString().padStart(2, '0')}:${startLdt.minute.toString().padStart(2, '0')}"
    val endTimeStr = "${endLdt.hour.toString().padStart(2, '0')}:${endLdt.minute.toString().padStart(2, '0')}"

    var menuExpanded by remember { mutableStateOf(false) }

    HeulaCard(
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        // Top Row: Plan Pill + Target Badge (Left) & iOS 3-dots Menu (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Method pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = session.planLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }

                // Target completion status pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (session.completed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    ) {
                        if (session.completed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(11.dp),
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = if (session.completed) targetCompletedText else endedEarlyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (session.completed) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Top-right: Three dots (overflow) menu button
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Opsi Puasa",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isEnglish) "Edit Fast" else "Edit Puasa",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        },
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = deleteButtonText,
                                fontSize = 13.sp,
                                color = DangerRed,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = DangerRed,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large prominent duration
        Text(
            text = "${durationHours}j ${durationMins}m",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Date and time range row
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(13.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$dateStr • $startTimeStr – $endTimeStr",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Mood & Note callout (if present)
        if (session.note != null || session.mood != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    if (session.mood != null) {
                        val moodVisual = getMoodVisual(session.mood)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = moodVisual.color.copy(alpha = 0.14f),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            ) {
                                Icon(
                                    imageVector = moodVisual.icon,
                                    contentDescription = moodVisual.label,
                                    tint = moodVisual.color,
                                    modifier = Modifier.size(12.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = moodVisual.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = moodVisual.color,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (!session.note.isNullOrBlank()) {
                        Text(
                            text = "\"${session.note}\"",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom row: "+ Catat Mood" (left) and "BAGIKAN" button (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val hasNoteOrMood = session.mood != null || !session.note.isNullOrBlank()
            if (!hasNoteOrMood) {
                Surface(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = noteMoodButtonText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandPrimary,
                        )
                    }
                }
            } else {
                Text(
                    text = "Target ${session.targetHours.toInt()} jam",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }

            // Sleek iOS-style BAGIKAN (Share) button on the right
            Surface(
                onClick = onShare,
                shape = RoundedCornerShape(12.dp),
                color = BrandPrimary.copy(alpha = 0.12f),
                border = BorderStroke(0.5.dp, BrandPrimary.copy(alpha = 0.28f)),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Bagikan",
                        tint = BrandPrimary,
                        modifier = Modifier.size(13.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEnglish) "Share" else "Bagikan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickTimeButton(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun EditSessionDialog(
    session: FastingSession,
    isEnglish: Boolean,
    onSave: (FastingSession) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedMood by remember { mutableStateOf(session.mood) }
    var noteText by remember { mutableStateOf(session.note ?: "") }
    var startMs by remember { mutableStateOf(session.startMs) }
    var endMs by remember { mutableStateOf(session.endMs) }

    val durationMs = (endMs - startMs).coerceAtLeast(60000L)
    val durationHours = durationMs / 3600000
    val durationMins = (durationMs % 3600000) / 60000
    val isCompleted = durationMs >= (session.targetHours * 3600000L).toLong()

    val startLdt = Instant.fromEpochMilliseconds(startMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val endLdt = Instant.fromEpochMilliseconds(endMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val startStr = "${startLdt.hour.toString().padStart(2, '0')}:${startLdt.minute.toString().padStart(2, '0')}"
    val endStr = "${endLdt.hour.toString().padStart(2, '0')}:${endLdt.minute.toString().padStart(2, '0')}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isEnglish) "Edit Fasting Record" else "Edit Catatan Puasa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = session.planLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Section 1: Duration & Adjust Time
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (isEnglish) "Duration" else "Durasi Puasa",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${durationHours}j ${durationMins}m" + if (isCompleted) " (Target Tuntas)" else "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        )

                        // Adjust Start Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = if (isEnglish) "Start Time" else "Waktu Mulai",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = startStr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                QuickTimeButton("-30m") {
                                    startMs = (startMs - 1800000L).coerceAtMost(endMs - 60000L)
                                }
                                QuickTimeButton("+30m") {
                                    startMs = (startMs + 1800000L).coerceAtMost(endMs - 60000L)
                                }
                            }
                        }

                        // Adjust End Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = if (isEnglish) "End Time" else "Waktu Selesai",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = endStr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                QuickTimeButton("-30m") {
                                    endMs = (endMs - 1800000L).coerceAtLeast(startMs + 60000L)
                                }
                                QuickTimeButton("+30m") {
                                    endMs = endMs + 1800000L
                                }
                            }
                        }
                    }
                }

                // Section 2: Mood Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEnglish) "How did you feel?" else "Bagaimana perasaanmu selama puasa?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Mood.entries.forEach { mood ->
                            val visual = getMoodVisual(mood)
                            val isSelected = selectedMood == mood
                            Surface(
                                onClick = { selectedMood = if (isSelected) null else mood },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                border = if (isSelected) BorderStroke(1.5.dp, BrandPrimary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
                                ) {
                                    Icon(
                                        imageVector = visual.icon,
                                        contentDescription = visual.label,
                                        tint = if (isSelected) BrandPrimary else visual.color,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = visual.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: Notes
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isEnglish) "Journal & Notes" else "Catatan & Menu Buka Puasa",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = {
                            Text(
                                text = if (isEnglish) "Jot down your meal or mood..." else "Tulis catatan atau menu buka puasa...",
                                fontSize = 13.sp,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = session.copy(
                        startMs = startMs,
                        endMs = endMs,
                        durationMs = durationMs,
                        completed = isCompleted,
                        mood = selectedMood,
                        note = noteText.ifBlank { null },
                    )
                    onSave(updated)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            ) {
                Text(
                    text = if (isEnglish) "Save" else "Simpan",
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isEnglish) "Cancel" else "Batal")
            }
        },
        shape = RoundedCornerShape(22.dp),
    )
}
