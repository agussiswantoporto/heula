package id.heula.presentation.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.*
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.icon.IndonesiaFlag
import id.heula.presentation.component.icon.UkFlag
import id.heula.presentation.component.ui.ButtonVariant
import id.heula.presentation.component.ui.ConfirmDialog
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.component.ui.HeulaSegmentedControl
import id.heula.presentation.component.ui.SegmentItem
import id.heula.presentation.store.AppStore
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.theme.DangerRed
import id.heula.presentation.util.Strings
import id.heula.presentation.util.getStrings

@Composable
fun ProfileScreen(
    store: AppStore,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settings by store.settings.collectAsState()
    val currentUser by store.currentUser.collectAsState()
    val syncState by store.syncState.collectAsState()
    val lastSyncedAt by store.lastSyncedAt.collectAsState()
    val healthStatus by store.healthStatus.collectAsState()
    val stravaConfig by store.stravaConfig.collectAsState()
    val s = getStrings(settings.language)

    var isSyncingHealth by remember { mutableStateOf(false) }
    var showStravaConfigModal by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showPlanSelector by remember { mutableStateOf(false) }
    var showCustomHoursDialog by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = s.settingsTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Google Account & Cloud Sync Card (iOS Grouped HIG Style)
            HeulaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = HeulaIcons.Google,
                            contentDescription = "Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = s.cloudSyncSectionTitle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    if (currentUser != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF34A853).copy(alpha = 0.12f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34A853))
                                )
                                Text(
                                    text = if (syncState == SyncState.SYNCING) s.syncStatusSyncing else s.syncStatusSynced,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF34A853),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val user = currentUser
                if (user == null) {
                    Text(
                        text = s.cloudSyncDesc,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            authErrorMessage = null
                            store.signInWithGoogle { res ->
                                res.onFailure { err ->
                                    authErrorMessage = err.message ?: "Gagal terhubung ke Google."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        if (syncState == SyncState.SYNCING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = BrandPrimary,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = s.syncStatusSyncing,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            Icon(
                                imageVector = HeulaIcons.Google,
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = s.signInWithGoogle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    if (!authErrorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = authErrorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary),
                            contentAlignment = Alignment.Center,
                        ) {
                            val initial = (user.displayName ?: user.email).firstOrNull()?.uppercase() ?: "H"
                            Text(
                                text = initial,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.displayName ?: "Pengguna Heula",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = user.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val nowMs = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                    Text(
                        text = formatLastSynced(lastSyncedAt, nowMs, settings.language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { store.syncAllData() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = syncState != SyncState.SYNCING,
                            contentPadding = PaddingValues(vertical = 10.dp),
                        ) {
                            if (syncState == SyncState.SYNCING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = BrandPrimary,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (settings.language == AppLanguage.ENGLISH) "Syncing..." else "Sinkron...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandPrimary,
                                )
                            } else {
                                Text(
                                    text = s.syncNowButton,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandPrimary,
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { store.signOut() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DangerRed,
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                        ) {
                            Text(
                                text = s.signOut,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DangerRed,
                            )
                        }
                    }
                }
            }

            // Connected Services / Sambungkan Layanan Section (Strava, Google Health, Apple Health)
            ConnectedServicesCard(
                stravaConfig = stravaConfig,
                healthStatus = healthStatus,
                isSyncingHealth = isSyncingHealth,
                language = settings.language,
                s = s,
                onConfigureStrava = { showStravaConfigModal = true },
                onConnectHealth = {
                    store.requestHealthAuthorization {
                        store.syncHealthData()
                    }
                },
                onSyncHealth = {
                    isSyncingHealth = true
                    store.syncHealthData {
                        isSyncingHealth = false
                    }
                },
            )

            // Profile info card
            settings.profiling?.let { prof ->
                HeulaCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                        Text(s.profileCardTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${if (settings.language == AppLanguage.ENGLISH) "Goal: " else "Tujuan: "}${
                            when (prof.goal) {
                                Goal.LOSE_WEIGHT -> if (settings.language == AppLanguage.ENGLISH) "Lose Weight" else "Turunkan Berat Badan"
                                Goal.MAINTAIN -> if (settings.language == AppLanguage.ENGLISH) "Maintain Weight" else "Jaga Berat Badan"
                                Goal.FEEL_BETTER -> if (settings.language == AppLanguage.ENGLISH) "Feel More Energetic" else "Badan Lebih Segar"
                                Goal.METABOLIC -> if (settings.language == AppLanguage.ENGLISH) "Metabolic Health" else "Kesehatan Metabolik"
                            }
                        }",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${if (settings.language == AppLanguage.ENGLISH) "Height: " else "Tinggi: "}${prof.heightCm.toInt()} cm • ${if (settings.language == AppLanguage.ENGLISH) "Start Weight: " else "Berat Awal: "}${prof.weightKg} kg",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Compact Revamped Fasting Plan Card (Eliminates bulky list taking too much space)
            val currentPlan = METHODS[settings.chosenMethod] ?: METHODS.values.first()
            val planLabel = if (settings.chosenMethod == MethodId.CUSTOM) {
                "Custom (${settings.customFastingHours.toInt()}:${(24 - settings.customFastingHours).toInt()})"
            } else currentPlan.label

            HeulaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                        Text(s.fastingMethodTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                    Button(
                        onClick = { showPlanSelector = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(s.changeMethod, fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = planLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BrandPrimary,
                        )
                        Text(
                            text = currentPlan.tagline,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Language Switcher Section (Bahasa Indonesia / English)
            HeulaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(HeulaIcons.Language, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    Text(s.languageSectionTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                HeulaSegmentedControl(
                    items = listOf(
                        SegmentItem(
                            key = AppLanguage.INDONESIAN,
                            label = "Indonesia",
                            leadingIcon = { IndonesiaFlag() },
                        ),
                        SegmentItem(
                            key = AppLanguage.ENGLISH,
                            label = "English",
                            leadingIcon = { UkFlag() },
                        ),
                    ),
                    selectedItem = settings.language,
                    onItemSelected = { store.setLanguage(it) },
                )
            }

            // Theme selector
            HeulaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    Text(s.themeSectionTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                HeulaSegmentedControl(
                    items = listOf(
                        Theme.SYSTEM to s.themeSystem,
                        Theme.LIGHT to s.themeLight,
                        Theme.DARK to s.themeDark,
                    ),
                    selectedItem = settings.theme,
                    onItemSelected = { store.setTheme(it) },
                )
            }

            // Reminders
            HeulaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                    Text(s.notificationTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(s.reminderFastEnd, fontSize = 14.sp)
                        Text(s.reminderFastEndDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.reminders.endReminder,
                        onCheckedChange = { store.setReminders(endReminder = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandPrimary,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = Color.Transparent,
                        ),
                    )
                }
            }

            // Reset Section
            HeulaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                    Text(s.resetDataTitle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = DangerRed)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = s.resetDataDesc,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))
                HeulaButton(
                    text = s.resetDataButton,
                    onClick = { showResetDialog = true },
                    variant = ButtonVariant.Danger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showPlanSelector) {
        AlertDialog(
            onDismissRequest = { showPlanSelector = false },
            title = { Text(if (settings.language == AppLanguage.ENGLISH) "Choose Fasting Method" else "Pilih Metode Puasa", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (settings.language == AppLanguage.ENGLISH) {
                                "24-Hour Principle: The 1st number is fasting hours (including sleep), and the 2nd is your eating window."
                            } else {
                                "Prinsip 24 Jam: Angka pertama adalah jam berpuasa (termasuk jam tidur malam), dan angka kedua adalah jendela waktu kamu boleh makan."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp,
                        )
                    }

                    METHOD_ORDER.forEach { id ->
                        val m = METHODS[id] ?: return@forEach
                        val isSelected = id == settings.chosenMethod
                        HeulaCard(
                            onClick = {
                                if (id == MethodId.CUSTOM) {
                                    showCustomHoursDialog = true
                                } else {
                                    store.setMethod(id)
                                }
                                showPlanSelector = false
                            },
                            borderColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = m.label,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = m.tagline,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlanSelector = false }) { Text(s.cancel, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            shape = RoundedCornerShape(22.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }

    if (showCustomHoursDialog) {
        var hours by remember { mutableStateOf(settings.customFastingHours.toInt().coerceIn(12, 23)) }

        AlertDialog(
            onDismissRequest = { showCustomHoursDialog = false },
            title = { Text(if (settings.language == AppLanguage.ENGLISH) "Custom Fasting Hours" else "Atur Jam Puasa Custom", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (settings.language == AppLanguage.ENGLISH) "Set your daily target fasting hours:" else "Tentukan durasi puasa harian yang kamu inginkan:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(
                            onClick = { if (hours > 12) hours-- },
                            enabled = hours > 12,
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "$hours ${if (settings.language == AppLanguage.ENGLISH) "Hours" else "Jam"}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        IconButton(
                            onClick = { if (hours < 23) hours++ },
                            enabled = hours < 23,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }

                    HeulaCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(
                            text = if (settings.language == AppLanguage.ENGLISH) {
                                "Daily Cycle: $hours hrs fasting • ${24 - hours} hrs eating"
                            } else {
                                "Siklus Harian: $hours jam puasa • ${24 - hours} jam makan"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        store.setCustomFastingHours(hours.toDouble())
                        store.setMethod(MethodId.CUSTOM)
                        showCustomHoursDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(if (settings.language == AppLanguage.ENGLISH) "Apply" else "Terapkan", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomHoursDialog = false }) { Text(s.cancel) }
            },
            shape = RoundedCornerShape(22.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }

    ConfirmDialog(
        show = showResetDialog,
        title = if (settings.language == AppLanguage.ENGLISH) "Reset all data?" else "Reset semua data?",
        body = if (settings.language == AppLanguage.ENGLISH) "All fasting records and profile settings will be deleted. This cannot be undone." else "Semua catatan puasa dan pengaturan bakal dihapus. Aksi ini tidak bisa dibatalkan.",
        confirmText = if (settings.language == AppLanguage.ENGLISH) "Yes, reset" else "Ya, reset",
        dismissText = s.cancel,
        isDanger = true,
        onConfirm = {
            store.resetAll()
            showResetDialog = false
            onResetOnboarding()
        },
        onDismiss = { showResetDialog = false },
    )

    if (showStravaConfigModal) {
        StravaConfigModal(
            config = stravaConfig,
            language = settings.language,
            s = s,
            onConnect = { token, onRes ->
                store.verifyAndConnectStrava(token, onRes)
            },
            onDisconnect = {
                store.disconnectStrava()
            },
            onUpdateConfig = { updated ->
                store.updateStravaConfig(updated)
            },
            onTestPost = { onRes ->
                val sampleSession = FastingSession(
                    planId = MethodId.SIXTEEN_EIGHT,
                    planLabel = "16:8",
                    targetHours = 16.0,
                    startMs = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - 57600000L,
                    endMs = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
                    durationMs = 57600000L,
                    completed = true,
                    createdAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
                    mood = Mood.ENERGIK,
                    note = "Uji coba posting puasa Heula ke Strava."
                )
                store.postSessionToStrava(sampleSession, onRes)
            },
            onDismiss = { showStravaConfigModal = false },
        )
    }
}

fun formatLastSynced(timestamp: Long?, now: Long, language: AppLanguage): String {
    if (timestamp == null || timestamp <= 0L) {
        return if (language == AppLanguage.ENGLISH) "Never synced to cloud" else "Belum pernah disinkronkan ke cloud"
    }
    val diffSeconds = (now - timestamp) / 1000L
    return when {
        diffSeconds < 60 -> if (language == AppLanguage.ENGLISH) "Last synced: Just now" else "Terakhir sinkron: Baru saja"
        diffSeconds < 3600 -> {
            val mins = diffSeconds / 60
            if (language == AppLanguage.ENGLISH) "Last synced: $mins min ago" else "Terakhir sinkron: $mins mnt lalu"
        }
        diffSeconds < 86400 -> {
            val hours = diffSeconds / 3600
            if (language == AppLanguage.ENGLISH) "Last synced: $hours hrs ago" else "Terakhir sinkron: $hours jam lalu"
        }
        else -> {
            val days = diffSeconds / 86400
            if (language == AppLanguage.ENGLISH) "Last synced: $days days ago" else "Terakhir sinkron: $days hari lalu"
        }
    }
}

@Composable
private fun ConnectedServicesCard(
    stravaConfig: StravaConfig,
    healthStatus: HealthSyncStatus,
    isSyncingHealth: Boolean,
    language: AppLanguage,
    s: Strings,
    onConfigureStrava: () -> Unit,
    onConnectHealth: () -> Unit,
    onSyncHealth: () -> Unit,
) {
    HeulaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = s.integrationsSectionTitle,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = s.integrationsSectionDesc,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 1. Strava Integration
            IntegrationRowItem(
                name = s.stravaTitle,
                subtitle = if (stravaConfig.isConnected) {
                    "${s.stravaConnectedAs} ${stravaConfig.athleteName.ifBlank { "Atlet Strava" }}"
                } else {
                    s.stravaDesc
                },
                icon = HeulaIcons.Strava,
                iconTint = Color(0xFFFC4C02),
                iconBg = Color(0xFFFC4C02).copy(alpha = 0.12f),
                isConnected = stravaConfig.isConnected,
                s = s,
                onClick = onConfigureStrava,
                actionButtonText = if (stravaConfig.isConnected) s.configureButton else s.connectButton,
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 1.dp,
            )

            // 2. Google Health Connect / Google Fit
            val isHealthConnectConnected = healthStatus.state == HealthPermissionState.CONNECTED &&
                healthStatus.platformType == HealthPlatformType.HEALTH_CONNECT
            IntegrationRowItem(
                name = "Google Health Connect",
                subtitle = s.healthConnectSectionDesc,
                icon = HeulaIcons.HealthConnect,
                iconTint = Color(0xFF00897B),
                iconBg = Color(0xFF00897B).copy(alpha = 0.12f),
                isConnected = isHealthConnectConnected,
                s = s,
                onClick = {
                    if (isHealthConnectConnected) onSyncHealth() else onConnectHealth()
                },
                actionButtonText = if (isHealthConnectConnected) {
                    if (isSyncingHealth) "..." else s.healthConnectSyncBtn
                } else {
                    s.connectButton
                },
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 1.dp,
            )

            // 3. Apple Health
            val isAppleHealthConnected = healthStatus.state == HealthPermissionState.CONNECTED &&
                healthStatus.platformType == HealthPlatformType.APPLE_HEALTH
            IntegrationRowItem(
                name = "Apple Health",
                subtitle = if (language == AppLanguage.ENGLISH) "Sync fasting, hydration & weight records" else "Sinkronkan puasa, timbangan & air ke Apple Health",
                icon = HeulaIcons.AppleHealth,
                iconTint = Color(0xFFFF2D55),
                iconBg = Color(0xFFFF2D55).copy(alpha = 0.12f),
                isConnected = isAppleHealthConnected,
                s = s,
                onClick = {
                    if (isAppleHealthConnected) onSyncHealth() else onConnectHealth()
                },
                actionButtonText = if (isAppleHealthConnected) {
                    if (isSyncingHealth) "..." else s.healthConnectSyncBtn
                } else {
                    s.connectButton
                },
            )
        }
    }
}

@Composable
private fun IntegrationRowItem(
    name: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    isConnected: Boolean,
    s: Strings,
    onClick: () -> Unit,
    actionButtonText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isConnected) Color(0xFF34C759).copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnected) Color(0xFF34C759) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            )
                            Text(
                                text = if (isConnected) s.connectedStatus else s.notConnectedStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isConnected) Color(0xFF248A3D) else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp,
                    maxLines = 2,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) MaterialTheme.colorScheme.surfaceVariant
                else BrandPrimary.copy(alpha = 0.15f),
                contentColor = if (isConnected) MaterialTheme.colorScheme.onSurface
                else BrandPrimary,
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp),
        ) {
            Text(
                text = actionButtonText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun StravaConfigModal(
    config: StravaConfig,
    language: AppLanguage,
    s: Strings,
    onConnect: (String, (Result<StravaAthlete>) -> Unit) -> Unit,
    onDisconnect: () -> Unit,
    onUpdateConfig: (StravaConfig) -> Unit,
    onTestPost: ((Result<StravaActivityResult>) -> Unit) -> Unit,
    onDismiss: () -> Unit,
) {
    var tokenInput by remember { mutableStateOf(config.accessToken) }
    var isConnecting by remember { mutableStateOf(false) }
    var isTestingPost by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showDisconnectConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFC4C02).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = HeulaIcons.Strava,
                            contentDescription = "Strava",
                            tint = Color(0xFFFC4C02),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Text(
                        text = s.stravaConfigModalTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (config.isConnected) {
                    // Connected Athlete Profile Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF34C759).copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color(0xFF34C759).copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFC4C02)),
                                contentAlignment = Alignment.Center,
                            ) {
                                val initial = config.athleteName.firstOrNull()?.uppercase() ?: "S"
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = config.athleteName.ifBlank { "Atlet Strava" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    text = if (config.athleteUsername.isNotBlank()) "@${config.athleteUsername}" else if (config.athleteId.isNotBlank()) "ID: ${config.athleteId}" else "Akun Terhubung",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF34C759).copy(alpha = 0.18f),
                            ) {
                                Text(
                                    text = s.connectedStatus,
                                    color = Color(0xFF248A3D),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }

                    // Auto-share switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.stravaAutoPostOption,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                            Text(
                                text = s.stravaAutoPostDesc,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp,
                            )
                        }
                        Switch(
                            checked = config.autoPostOnFinish,
                            onCheckedChange = { onUpdateConfig(config.copy(autoPostOnFinish = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFFC4C02),
                                checkedBorderColor = Color.Transparent,
                            ),
                        )
                    }

                    // Activity Type Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (language == AppLanguage.ENGLISH) "Activity Type on Strava" else "Jenis Aktivitas di Strava",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        ) {
                            Text(
                                text = config.defaultActivityType.ifBlank { "Workout" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFC4C02),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }

                    // Test Post Button
                    OutlinedButton(
                        onClick = {
                            isTestingPost = true
                            errorMessage = null
                            successMessage = null
                            onTestPost { res ->
                                isTestingPost = false
                                res.onSuccess {
                                    successMessage = s.stravaPostSuccess
                                }.onFailure { err ->
                                    errorMessage = "${s.stravaPostFailed}: ${err.message}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isTestingPost,
                        border = BorderStroke(1.dp, Color(0xFFFC4C02).copy(alpha = 0.5f)),
                    ) {
                        if (isTestingPost) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFFFC4C02),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (language == AppLanguage.ENGLISH) "Posting sample..." else "Mengunggah contoh...", color = Color(0xFFFC4C02), fontSize = 13.sp)
                        } else {
                            Icon(
                                imageVector = HeulaIcons.Strava,
                                contentDescription = null,
                                tint = Color(0xFFFC4C02),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s.stravaTestPost, color = Color(0xFFFC4C02), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    // Disconnect Button
                    TextButton(
                        onClick = { showDisconnectConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(s.disconnectButton, color = DangerRed, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                } else {
                    // Not connected setup view
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFC4C02).copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Color(0xFFFC4C02).copy(alpha = 0.25f)),
                    ) {
                        Text(
                            text = s.stravaDesc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(12.dp),
                        )
                    }

                    Text(
                        text = s.stravaConnectHelp,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp,
                    )

                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = {
                            tokenInput = it
                            errorMessage = null
                        },
                        placeholder = { Text(s.stravaTokenPlaceholder, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                    )

                    // Quick connect demo helper
                    OutlinedButton(
                        onClick = {
                            val demoToken = "demo_strava_token"
                            tokenInput = demoToken
                            isConnecting = true
                            errorMessage = null
                            onConnect(demoToken) { res ->
                                isConnecting = false
                                res.onSuccess {
                                    successMessage = if (language == AppLanguage.ENGLISH) "Connected to Strava!" else "Berhasil terhubung ke Strava!"
                                }.onFailure { err ->
                                    errorMessage = err.message
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        contentPadding = PaddingValues(vertical = 8.dp),
                    ) {
                        Text(
                            text = "⚡ " + s.stravaQuickDemo,
                            fontSize = 12.sp,
                            color = BrandPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Button(
                        onClick = {
                            val clean = tokenInput.trim()
                            if (clean.isBlank()) {
                                errorMessage = if (language == AppLanguage.ENGLISH) "Please enter your Strava access token." else "Harap masukkan access token Strava."
                                return@Button
                            }
                            isConnecting = true
                            errorMessage = null
                            onConnect(clean) { res ->
                                isConnecting = false
                                res.onSuccess {
                                    successMessage = if (language == AppLanguage.ENGLISH) "Connected to Strava!" else "Berhasil terhubung ke Strava!"
                                }.onFailure { err ->
                                    errorMessage = err.message ?: "Gagal verifikasi token Strava."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC4C02)),
                        enabled = !isConnecting,
                        contentPadding = PaddingValues(vertical = 12.dp),
                    ) {
                        if (isConnecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s.stravaConnecting, color = Color.White, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text(s.connectButton + " ke Strava", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }

                if (!successMessage.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF34C759).copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = successMessage ?: "",
                            color = Color(0xFF248A3D),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )

    if (showDisconnectConfirm) {
        ConfirmDialog(
            show = showDisconnectConfirm,
            title = s.stravaDisconnectConfirm,
            body = if (language == AppLanguage.ENGLISH) "This will remove your Strava access token and stop auto-sharing fasting records to Strava." else "Koneksi ke akun Strava akan dicabut dan puasa kamu tidak akan otomatis diposting lagi ke Strava.",
            confirmText = s.disconnectButton,
            dismissText = s.cancel,
            isDanger = true,
            onConfirm = {
                onDisconnect()
                showDisconnectConfirm = false
            },
            onDismiss = { showDisconnectConfirm = false },
        )
    }
}
