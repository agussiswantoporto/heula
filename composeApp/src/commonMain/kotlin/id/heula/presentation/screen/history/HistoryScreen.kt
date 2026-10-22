package id.heula.presentation.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
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
import id.heula.presentation.component.ui.ConfirmDialog
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaCard
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
    var sessionToEditNote by remember { mutableStateOf<FastingSession?>(null) }

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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(sessions, key = { it.id }) { session ->
                        SessionItemCard(
                            session = session,
                            targetCompletedText = s.targetCompleted,
                            endedEarlyText = s.endedEarly,
                            noteMoodButtonText = s.noteMoodButton,
                            deleteButtonText = s.deleteButton,
                            onDelete = { sessionToDelete = session },
                            onEditNote = { sessionToEditNote = session },
                        )
                    }
                }
            }
        }
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

    if (sessionToEditNote != null) {
        EditMoodNoteDialog(
            session = sessionToEditNote!!,
            onSave = { mood, note ->
                store.updateMoodNote(sessionToEditNote!!.id, mood, note)
                sessionToEditNote = null
            },
            onDismiss = { sessionToEditNote = null },
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
    onDelete: () -> Unit,
    onEditNote: () -> Unit,
) {
    val durationHours = session.durationMs / 3600000
    val durationMins = (session.durationMs % 3600000) / 60000

    val startDate = Instant.fromEpochMilliseconds(session.startMs)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val dateStr = "${startDate.dayOfMonth} ${startDate.month.name.lowercase().take(3)} ${startDate.year}"

    HeulaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = "${durationHours}j ${durationMins}m",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Metode ${session.planLabel} • $dateStr",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HeulaBadge(
                text = if (session.completed) targetCompletedText else endedEarlyText,
                backgroundColor = if (session.completed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (session.completed) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (session.note != null || session.mood != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (session.mood != null) {
                    Text(
                        text = when (session.mood) {
                            Mood.ENERGIK -> "⚡ Energik"
                            Mood.BIASA -> "🙂 Biasa"
                            Mood.LEMES -> "🥱 Lemas"
                            Mood.LAPAR -> "🤤 Lapar"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (!session.note.isNullOrBlank()) {
                    Text(
                        text = "\"${session.note}\"",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val hasNoteOrMood = session.mood != null || !session.note.isNullOrBlank()
            // Button is hidden once note or mood is already filled!
            if (!hasNoteOrMood) {
                TextButton(onClick = onEditNote) {
                    Text(noteMoodButtonText, fontSize = 12.sp, color = BrandPrimary)
                }
            }
            TextButton(onClick = onDelete) {
                Text(deleteButtonText, fontSize = 12.sp, color = DangerRed)
            }
        }
    }
}

@Composable
private fun EditMoodNoteDialog(
    session: FastingSession,
    onSave: (Mood?, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedMood by remember { mutableStateOf(session.mood) }
    var noteText by remember { mutableStateOf(session.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catatan Puasa", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Bagaimana perasaanmu selama puasa?", fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Mood.entries.forEach { mood ->
                        val isSelected = selectedMood == mood
                        Button(
                            onClick = { selectedMood = mood },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = when (mood) {
                                    Mood.ENERGIK -> "⚡"
                                    Mood.BIASA -> "🙂"
                                    Mood.LEMES -> "🥱"
                                    Mood.LAPAR -> "🤤"
                                },
                                fontSize = 16.sp,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Tulis catatan atau menu buka puasa...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedMood, noteText.ifBlank { null }) }
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
