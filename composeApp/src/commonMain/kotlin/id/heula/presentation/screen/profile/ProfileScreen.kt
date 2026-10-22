package id.heula.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import id.heula.presentation.component.ui.ButtonVariant
import id.heula.presentation.component.ui.ConfirmDialog
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.component.ui.HeulaSegmentedControl
import id.heula.presentation.store.AppStore
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.theme.DangerRed
import id.heula.presentation.util.getStrings

@Composable
fun ProfileScreen(
    store: AppStore,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settings by store.settings.collectAsState()
    val s = getStrings(settings.language)

    var showResetDialog by remember { mutableStateOf(false) }
    var showPlanSelector by remember { mutableStateOf(false) }
    var showCustomHoursDialog by remember { mutableStateOf(false) }

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
                        AppLanguage.INDONESIAN to "🇮🇩 Indonesia",
                        AppLanguage.ENGLISH to "🇬🇧 English",
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
}
