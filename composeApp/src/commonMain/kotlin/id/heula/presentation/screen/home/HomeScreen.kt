package id.heula.presentation.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.heula.domain.model.*
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.icon.getMoodVisual
import id.heula.presentation.component.timer.ProgressRing
import id.heula.presentation.component.timer.StageIndicator
import id.heula.presentation.component.animation.CelebrationOverlay
import id.heula.presentation.util.playZenGongSound
import id.heula.presentation.screen.share.FastingShareModal
import id.heula.presentation.component.ui.ButtonVariant
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.store.AppStore
import id.heula.presentation.store.calculateStreak
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.theme.DangerRed
import id.heula.presentation.util.getStrings
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeScreen(
    store: AppStore,
    modifier: Modifier = Modifier,
) {
    val settings by store.settings.collectAsState()
    val activeFast by store.activeFast.collectAsState()
    val sessions by store.sessions.collectAsState()
    val stravaConfig by store.stravaConfig.collectAsState()
    val s = getStrings(settings.language)

    var currentTimeMs by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }
    var stravaFeedbackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(stravaFeedbackMessage) {
        if (stravaFeedbackMessage != null) {
            delay(4500)
            stravaFeedbackMessage = null
        }
    }

    LaunchedEffect(activeFast) {
        while (true) {
            currentTimeMs = Clock.System.now().toEpochMilliseconds()
            delay(1000)
        }
    }

    val streak = remember(sessions) { calculateStreak(sessions) }

    val eatingStart = settings.eatingWindowStartMs
    val eatingHours = settings.eatingWindowHours
    val isEatingWindow = activeFast == null && eatingStart != null && (currentTimeMs - eatingStart) < (eatingHours * 3600000L)

    var showFinishFastModal by remember { mutableStateOf(false) }
    var showEditStartDialog by remember { mutableStateOf(false) }
    var showPlanSelector by remember { mutableStateOf(false) }
    var showCustomHoursDialog by remember { mutableStateOf(false) }
    var showHungerSosModal by remember { mutableStateOf(false) }
    var sessionToShare by remember { mutableStateOf<FastingSession?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth >= 768.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                // Left Column: Timer & Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    TimerHeroSection(
                        activeFast = activeFast,
                        isEatingWindow = isEatingWindow,
                        eatingStartMs = eatingStart ?: 0L,
                        eatingHours = eatingHours,
                        currentTimeMs = currentTimeMs,
                        chosenMethod = settings.chosenMethod,
                        streak = streak,
                        s = s,
                        onOpenPlanSelector = { showPlanSelector = true },
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TimerActions(
                        activeFast = activeFast,
                        isEatingWindow = isEatingWindow,
                        s = s,
                        onStart = { store.startFast() },
                        onStop = { showFinishFastModal = true },
                        onEditStart = { showEditStartDialog = true },
                        onOpenHungerSos = { showHungerSosModal = true },
                    )
                }

                // Right Column: Details & Water Tracker
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    WaterTrackerCard(
                        waterMl = settings.waterIntakeMl,
                        s = s,
                        onAdd = { store.addWater(it) },
                    )

                    if (activeFast != null) {
                        val elapsedMs = (currentTimeMs - activeFast!!.startMs).coerceAtLeast(0L)
                        val stage = stageForElapsed(elapsedMs)
                        StageIndicator(stage = stage, elapsedMs = elapsedMs, s = s)
                    } else if (isEatingWindow) {
                        EatingWindowCard(
                            eatingStartMs = eatingStart ?: 0L,
                            eatingHours = eatingHours,
                            currentTimeMs = currentTimeMs,
                            s = s,
                        )
                    }

                    DailyCycleCard(
                        activeFast = activeFast,
                        chosenMethod = settings.chosenMethod,
                        customHours = settings.customFastingHours,
                        isEatingWindow = isEatingWindow,
                        eatingHours = eatingHours,
                        s = s,
                    )

                    MethodCard(
                        methodId = settings.chosenMethod,
                        customHours = settings.customFastingHours,
                        s = s,
                        onChangePlan = { showPlanSelector = true },
                    )
                }
            }
        } else {
            // Mobile: Single-column vertical scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                TimerHeroSection(
                    activeFast = activeFast,
                    isEatingWindow = isEatingWindow,
                    eatingStartMs = eatingStart ?: 0L,
                    eatingHours = eatingHours,
                    currentTimeMs = currentTimeMs,
                    chosenMethod = settings.chosenMethod,
                    streak = streak,
                    s = s,
                    onOpenPlanSelector = { showPlanSelector = true },
                )

                TimerActions(
                    activeFast = activeFast,
                    isEatingWindow = isEatingWindow,
                    s = s,
                    onStart = { store.startFast() },
                    onStop = { showFinishFastModal = true },
                    onEditStart = { showEditStartDialog = true },
                    onOpenHungerSos = { showHungerSosModal = true },
                )

                WaterTrackerCard(
                    waterMl = settings.waterIntakeMl,
                    s = s,
                    onAdd = { store.addWater(it) },
                )

                if (activeFast != null) {
                    val elapsedMs = (currentTimeMs - activeFast!!.startMs).coerceAtLeast(0L)
                    val stage = stageForElapsed(elapsedMs)
                    StageIndicator(stage = stage, elapsedMs = elapsedMs, s = s)
                } else if (isEatingWindow) {
                    EatingWindowCard(
                        eatingStartMs = eatingStart ?: 0L,
                        eatingHours = eatingHours,
                        currentTimeMs = currentTimeMs,
                        s = s,
                    )
                }

                DailyCycleCard(
                    activeFast = activeFast,
                    chosenMethod = settings.chosenMethod,
                    customHours = settings.customFastingHours,
                    isEatingWindow = isEatingWindow,
                    eatingHours = eatingHours,
                    s = s,
                )

                MethodCard(
                    methodId = settings.chosenMethod,
                    customHours = settings.customFastingHours,
                    s = s,
                    onChangePlan = { showPlanSelector = true },
                )
            }
        }

        stravaFeedbackMessage?.let { msg ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (msg.contains("Gagal") || msg.contains("Failed")) MaterialTheme.colorScheme.errorContainer else Color(0xFFFC4C02),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = HeulaIcons.Strava,
                        contentDescription = null,
                        tint = if (msg.contains("Gagal") || msg.contains("Failed")) MaterialTheme.colorScheme.error else Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = msg,
                        color = if (msg.contains("Gagal") || msg.contains("Failed")) MaterialTheme.colorScheme.onErrorContainer else Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    }

    if (showFinishFastModal && activeFast != null) {
        val elapsedMs = (currentTimeMs - activeFast!!.startMs).coerceAtLeast(0L)
        val targetMs = (activeFast!!.targetHours * 3600000L).toLong()
        FinishFastModal(
            durationMs = elapsedMs,
            targetHours = activeFast!!.targetHours,
            isCompleted = elapsedMs >= targetMs,
            stravaConfig = stravaConfig,
            s = s,
            onConfirm = { mood, note, shareToStrava ->
                val session = FastingSession(
                    planId = activeFast!!.planId,
                    planLabel = activeFast!!.planLabel,
                    targetHours = activeFast!!.targetHours,
                    startMs = activeFast!!.startMs,
                    endMs = currentTimeMs,
                    durationMs = elapsedMs,
                    completed = elapsedMs >= targetMs,
                    createdAt = currentTimeMs,
                    mood = mood,
                    note = note
                )
                store.stopFast(currentTimeMs, mood, note)
                if (shareToStrava && stravaConfig.isConnected) {
                    store.postSessionToStrava(session) { res ->
                        res.onSuccess {
                            stravaFeedbackMessage = s.stravaPostSuccess
                        }.onFailure { err ->
                            stravaFeedbackMessage = "${s.stravaPostFailed}: ${err.message}"
                        }
                    }
                }
                showFinishFastModal = false
                sessionToShare = session
                playZenGongSound()
                showCelebration = true
            },
            onDismiss = { showFinishFastModal = false },
        )
    }

    if (sessionToShare != null) {
        FastingShareModal(
            session = sessionToShare!!,
            onDismiss = { sessionToShare = null },
            onExport = { config, onResult ->
                store.exportShareImage(sessionToShare!!, config, onResult)
            },
            onGeneratePreview = { config ->
                store.generateSharePreview(sessionToShare!!, config)
            }
        )
    }

    if (showPlanSelector) {
        PlanSelectorModal(
            currentMethod = settings.chosenMethod,
            onSelect = { methodId ->
                if (methodId == MethodId.CUSTOM) {
                    showCustomHoursDialog = true
                } else {
                    store.setMethod(methodId)
                }
                showPlanSelector = false
            },
            onDismiss = { showPlanSelector = false },
        )
    }

    if (showCustomHoursDialog) {
        CustomHoursModal(
            initialHours = settings.customFastingHours,
            onSave = { h ->
                store.setCustomFastingHours(h)
                store.setMethod(MethodId.CUSTOM)
                showCustomHoursDialog = false
            },
            onDismiss = { showCustomHoursDialog = false },
        )
    }

    if (showEditStartDialog && activeFast != null) {
        EditStartTimeModal(
            currentStartMs = activeFast!!.startMs,
            targetHours = activeFast!!.targetHours,
            isEnglish = settings.language == id.heula.domain.model.AppLanguage.ENGLISH,
            onSave = { newStartMs ->
                store.updateStartTime(newStartMs)
                showEditStartDialog = false
            },
            onDismiss = { showEditStartDialog = false },
        )
    }

    if (showHungerSosModal) {
        id.heula.presentation.component.hungersos.HungerSosModal(
            activeFast = activeFast,
            currentTimeMs = currentTimeMs,
            s = s,
            onDismiss = { showHungerSosModal = false },
        )
    }

    CelebrationOverlay(
        show = showCelebration,
        onFinished = { showCelebration = false },
    )
}

@Composable
private fun TimerHeroSection(
    activeFast: ActiveFast?,
    isEatingWindow: Boolean,
    eatingStartMs: Long,
    eatingHours: Double,
    currentTimeMs: Long,
    chosenMethod: MethodId,
    streak: Int,
    s: id.heula.presentation.util.Strings,
    onOpenPlanSelector: () -> Unit,
) {
    val method = METHODS[chosenMethod] ?: METHODS.values.first()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Heula",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.0).sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = when {
                    activeFast != null -> s.currentlyFasting
                    isEatingWindow -> s.eatingWindowActive
                    else -> s.fastingCycleReady
                },
                fontSize = 13.sp,
                fontWeight = if (isEatingWindow && activeFast == null) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isEatingWindow && activeFast == null) Color(0xFFFF9500) else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (streak > 0) {
                HeulaBadge(
                    text = "$streak ${s.streakLabel}",
                    backgroundColor = Color(0xFFFF9500).copy(alpha = 0.15f),
                    contentColor = Color(0xFFFF9500),
                )
            }
            HeulaBadge(
                text = "Metode ${activeFast?.planLabel ?: method.label}",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onOpenPlanSelector),
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val (progress, elapsedStr, targetStr, gradient) = when {
        activeFast != null -> {
            val elapsedMs = (currentTimeMs - activeFast.startMs).coerceAtLeast(0L)
            val targetMs = (activeFast.targetHours * 3600000).toLong()
            val ratio = if (targetMs > 0) (elapsedMs.toFloat() / targetMs.toFloat()) else 0f
            val hours = elapsedMs / 3600000
            val mins = (elapsedMs % 3600000) / 60000
            val secs = (elapsedMs % 60000) / 1000
            val elapsedFormatted = "${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
            val targetFormatted = "Target: ${activeFast.targetHours.toInt()} ${s.fastingHoursLabel.lowercase()}"
            val greenGradient = listOf(BrandPrimary, Color(0xFF30D158), BrandPrimary)
            Quadruple(ratio, elapsedFormatted, targetFormatted, greenGradient)
        }
        isEatingWindow -> {
            val elapsedWindowMs = (currentTimeMs - eatingStartMs).coerceAtLeast(0L)
            val totalWindowMs = (eatingHours * 3600000L).toLong()
            val remainingMs = (totalWindowMs - elapsedWindowMs).coerceAtLeast(0L)
            val ratio = if (totalWindowMs > 0) (1f - (remainingMs.toFloat() / totalWindowMs.toFloat())) else 1f
            val hours = remainingMs / 3600000
            val mins = (remainingMs % 3600000) / 60000
            val secs = (remainingMs % 60000) / 1000
            val remainingFormatted = "${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
            val targetFormatted = "${s.eatingHoursLabel} (${eatingHours.toInt()} jam)"
            val warmGradient = listOf(Color(0xFFFF9500), Color(0xFFFFB340), Color(0xFFFF9500))
            Quadruple(ratio, remainingFormatted, targetFormatted, warmGradient)
        }
        else -> {
            val grayGradient = listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline)
            Quadruple(0f, "00:00:00", "Target: ${method.fastingHours.toInt()} ${s.fastingHoursLabel.lowercase()}", grayGradient)
        }
    }

    ProgressRing(
        progress = progress,
        elapsedText = elapsedStr,
        targetText = targetStr,
        active = activeFast != null || isEatingWindow,
        gradientColors = gradient,
        size = 260.dp,
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun TimerActions(
    activeFast: ActiveFast?,
    isEatingWindow: Boolean,
    s: id.heula.presentation.util.Strings,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onEditStart: () -> Unit,
    onOpenHungerSos: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (activeFast == null) {
            HeulaButton(
                text = if (isEatingWindow) s.startFastFromEating else s.startFasting,
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            HeulaButton(
                text = s.endFastAndOpenEating,
                onClick = onStop,
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
            HeulaButton(
                text = s.editStartTime,
                onClick = onEditStart,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
            Surface(
                onClick = onOpenHungerSos,
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFF9500).copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9500).copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = id.heula.presentation.component.icon.HeulaIcons.Lifebuoy,
                        contentDescription = null,
                        tint = Color(0xFFFF9500),
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = s.hungerSosButton,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9500),
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterTrackerCard(
    waterMl: Int,
    targetMl: Int = 2000,
    s: id.heula.presentation.util.Strings,
    onAdd: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = (waterMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1f)

    HeulaCard(modifier = modifier) {
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
                    imageVector = HeulaIcons.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = s.waterTrackerTitle,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
            Text(
                text = "$waterMl / $targetMl ml",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (waterMl >= targetMl) BrandPrimary else Color(0xFF007AFF),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = Color(0xFF007AFF),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { onAdd(250) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF).copy(alpha = 0.12f),
                    contentColor = Color(0xFF007AFF),
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier.weight(1f).height(38.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("250 ml", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = { onAdd(500) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF).copy(alpha = 0.12f),
                    contentColor = Color(0xFF007AFF),
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier.weight(1f).height(38.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("500 ml", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            if (waterMl > 0) {
                Button(
                    onClick = { onAdd(-250) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(0.7f).height(38.dp),
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text("250", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun EatingWindowCard(
    eatingStartMs: Long,
    eatingHours: Double,
    currentTimeMs: Long,
    s: id.heula.presentation.util.Strings,
    modifier: Modifier = Modifier,
) {
    val endMs = eatingStartMs + (eatingHours * 3600000L).toLong()
    val endLdt = Instant.fromEpochMilliseconds(endMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val endHourStr = endLdt.hour.toString().padStart(2, '0')
    val endMinStr = endLdt.minute.toString().padStart(2, '0')

    HeulaCard(
        modifier = modifier,
        borderColor = Color(0xFFFF9500).copy(alpha = 0.4f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = HeulaIcons.Restaurant,
                contentDescription = null,
                tint = Color(0xFFFF9500),
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = s.eatingWindowActive,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFF9500),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${s.eatingWindowOpenUntil} $endHourStr:$endMinStr.",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "${s.mealGuidanceTitle} (${eatingHours.toInt()} Jam):",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MealGuidelineItem(
                badge = "1",
                title = s.meal1Title,
                desc = s.meal1Desc,
            )
            MealGuidelineItem(
                badge = "•",
                title = s.mealSnackTitle,
                desc = s.mealSnackDesc,
            )
            MealGuidelineItem(
                badge = "2",
                title = s.meal2Title,
                desc = s.meal2Desc,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = s.mealWindowTip,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun MealGuidelineItem(badge: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(BrandPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(badge, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(
                desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp,
            )
        }
    }
}

@Composable
private fun DailyCycleCard(
    activeFast: ActiveFast?,
    chosenMethod: MethodId,
    customHours: Double,
    isEatingWindow: Boolean,
    eatingHours: Double,
    s: id.heula.presentation.util.Strings,
    modifier: Modifier = Modifier,
) {
    val method = METHODS[chosenMethod] ?: METHODS.values.first()
    val fastH = if (activeFast != null) activeFast.targetHours else if (chosenMethod == MethodId.CUSTOM) customHours else method.fastingHours
    val eatH = if (isEatingWindow && eatingHours > 0) eatingHours else if (chosenMethod == MethodId.CUSTOM) (24.0 - customHours).coerceAtLeast(0.0) else method.eatingHours

    val fastRatio = (fastH / 24.0).toFloat().coerceIn(0.1f, 0.95f)
    val eatRatio = (1f - fastRatio).coerceAtLeast(0.05f)

    HeulaCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = s.dailyCycleTitle,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            HeulaBadge(text = s.total24Hours)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dual Segment Bar (Fasting vs Eating)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(99.dp)),
        ) {
            Box(
                modifier = Modifier
                    .weight(fastRatio)
                    .fillMaxHeight()
                    .background(BrandPrimary),
            )
            if (eatH > 0) {
                Box(
                    modifier = Modifier
                        .weight(eatRatio)
                        .fillMaxHeight()
                        .background(Color(0xFFFF9500)),
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrandPrimary))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${s.fastingHoursLabel}: ${fastH.toInt()} Jam",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            if (eatH > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF9500)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${s.eatingHoursLabel}: ${eatH.toInt()} Jam",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = s.dailyCycleExplanation,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun MethodCard(
    methodId: MethodId,
    customHours: Double,
    s: id.heula.presentation.util.Strings,
    onChangePlan: () -> Unit,
) {
    val method = METHODS[methodId] ?: METHODS.values.first()
    val displayLabel = if (methodId == MethodId.CUSTOM) "Custom (${customHours.toInt()}:${(24 - customHours).toInt()})" else method.label

    HeulaCard(onClick = onChangePlan) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${s.activeMethod}: $displayLabel",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                Text(
                    text = method.tagline,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = s.changeMethod,
                fontSize = 13.sp,
                color = BrandPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun PlanSelectorModal(
    currentMethod: MethodId,
    onSelect: (MethodId) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Metode Puasa", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                        text = "Prinsip 24 Jam: Angka pertama adalah jam berpuasa (termasuk jam tidur malam), dan angka kedua adalah jendela waktu kamu boleh makan. Bukan berpuasa 2 kali!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp,
                    )
                }

                METHOD_ORDER.forEach { id ->
                    val m = METHODS[id] ?: return@forEach
                    val isSelected = id == currentMethod
                    HeulaCard(
                        onClick = { onSelect(id) },
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
                                    contentDescription = "Dipilih",
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
            TextButton(onClick = onDismiss) { Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
private fun CustomHoursModal(
    initialHours: Double,
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var hours by remember { mutableStateOf(initialHours.toInt().coerceIn(12, 23)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Jam Puasa Custom", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Tentukan durasi puasa harian yang kamu inginkan:",
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
                        Icon(Icons.Default.Remove, contentDescription = "Kurang")
                    }
                    Text(
                        text = "$hours Jam",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    IconButton(
                        onClick = { if (hours < 23) hours++ },
                        enabled = hours < 23,
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }

                HeulaCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(
                        text = "Siklus Harian: ${hours} jam puasa • ${24 - hours} jam makan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(hours.toDouble()) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Terapkan", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
private fun FinishFastModal(
    durationMs: Long,
    targetHours: Double,
    isCompleted: Boolean,
    stravaConfig: StravaConfig,
    s: id.heula.presentation.util.Strings,
    onConfirm: (Mood?, String?, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var noteText by remember { mutableStateOf("") }
    var shareToStrava by remember { mutableStateOf(stravaConfig.isConnected && stravaConfig.autoPostOnFinish) }

    val totalHours = durationMs / 3600000
    val totalMins = (durationMs % 3600000) / 60000

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isCompleted) {
                    Icon(HeulaIcons.Trophy, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                    Text(s.congratsFastingFinished, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                } else {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                    Text(s.finishFastTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HeulaCard(
                    backgroundColor = if (isCompleted) Color(0xFFF59E0B).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    borderColor = if (isCompleted) Color(0xFFF59E0B).copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                ) {
                    Text(
                        text = "Total Durasi: $totalHours jam $totalMins menit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = if (isCompleted) "Target puasa ${targetHours.toInt()} jam berhasil dituntaskan!" else "Target belum penuh (${targetHours.toInt()} jam), namun tetap tercatat sebagai progresmu.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text("Bagaimana energimu saat ini?", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Mood.entries.forEach { mood ->
                        val visual = getMoodVisual(mood)
                        val isSelected = selectedMood == mood
                        Surface(
                            onClick = { selectedMood = if (isSelected) null else mood },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 2.dp, vertical = 6.dp),
                            ) {
                                Icon(
                                    imageVector = visual.icon,
                                    contentDescription = visual.label,
                                    tint = if (isSelected) BrandPrimary else visual.color,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = visual.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Menu buka puasa atau catatan tubuhmu...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )

                // Strava Share Checkbox (Active when Strava is connected)
                if (stravaConfig.isConnected) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFC4C02).copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFC4C02).copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { shareToStrava = !shareToStrava },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFC4C02).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = HeulaIcons.Strava,
                                        contentDescription = "Strava",
                                        tint = Color(0xFFFC4C02),
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Column {
                                    Text(
                                        text = s.stravaShareCheckbox,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = s.stravaShareCheckboxDesc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp,
                                    )
                                }
                            }
                            Checkbox(
                                checked = shareToStrava,
                                onCheckedChange = { shareToStrava = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFFC4C02),
                                    checkmarkColor = Color.White,
                                ),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMood, noteText.ifBlank { null }, shareToStrava) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            ) {
                Text("Simpan & Buka Jendela Makan", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal / Lanjut Puasa", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
private fun EditStartTimeModal(
    currentStartMs: Long,
    targetHours: Double,
    isEnglish: Boolean,
    onSave: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val tz = remember { TimeZone.currentSystemDefault() }
    var selectedStartMs by remember { mutableStateOf(currentStartMs) }

    val nowMs = Clock.System.now().toEpochMilliseconds()
    val isFuture = selectedStartMs > (nowMs + 60_000L) // 1 minute grace buffer
    val elapsedMs = (nowMs - selectedStartMs).coerceAtLeast(0L)
    val elapsedHours = elapsedMs / 3600000L
    val elapsedMins = (elapsedMs % 3600000L) / 60000L

    val selectedLdt = Instant.fromEpochMilliseconds(selectedStartMs).toLocalDateTime(tz)
    val nowLdt = Instant.fromEpochMilliseconds(nowMs).toLocalDateTime(tz)

    val todayDate = nowLdt.date
    val yesterdayDate = Instant.fromEpochMilliseconds(nowMs - 86_400_000L).toLocalDateTime(tz).date
    val isToday = selectedLdt.date == todayDate
    val isYesterday = selectedLdt.date == yesterdayDate

    fun updateTime(hour: Int? = null, minute: Int? = null, setYesterday: Boolean? = null) {
        val targetDate = when (setYesterday) {
            true -> yesterdayDate
            false -> todayDate
            null -> selectedLdt.date
        }
        val h = (hour ?: selectedLdt.hour).coerceIn(0, 23)
        val m = (minute ?: selectedLdt.minute).coerceIn(0, 59)
        val newLdt = LocalDateTime(targetDate.year, targetDate.month, targetDate.dayOfMonth, h, m, 0, 0)
        selectedStartMs = newLdt.toInstant(tz).toEpochMilliseconds()
    }

    val hourStr = selectedLdt.hour.toString().padStart(2, '0')
    val minuteStr = selectedLdt.minute.toString().padStart(2, '0')

    val targetEndMs = selectedStartMs + (targetHours * 3600000L).toLong()
    val targetLdt = Instant.fromEpochMilliseconds(targetEndMs).toLocalDateTime(tz)
    val targetHourStr = "${targetLdt.hour.toString().padStart(2, '0')}:${targetLdt.minute.toString().padStart(2, '0')}"
    val targetDayLabel = when (targetLdt.date) {
        todayDate -> if (isEnglish) "Today" else "Hari ini"
        Instant.fromEpochMilliseconds(nowMs + 86_400_000L).toLocalDateTime(tz).date -> if (isEnglish) "Tomorrow" else "Besok"
        else -> "${targetLdt.dayOfMonth} ${targetLdt.month.name.take(3)}"
    }

    val dayLabel = when {
        isToday -> if (isEnglish) "Today" else "Hari ini"
        isYesterday -> if (isEnglish) "Yesterday" else "Kemarin"
        else -> "${selectedLdt.dayOfMonth} ${selectedLdt.month.name.take(3)}"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= 600.dp

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
                contentAlignment = if (isWide) Alignment.Center else Alignment.BottomCenter,
            ) {
                Surface(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        )
                        .then(
                            if (isWide) {
                                Modifier.width(460.dp).padding(vertical = 24.dp)
                            } else {
                                Modifier.fillMaxWidth()
                            }
                        ),
                    shape = if (isWide) RoundedCornerShape(26.dp) else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Grabber bar (mobile bottom sheet)
                        if (!isWide) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 6.dp)
                                    .size(width = 38.dp, height = 5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                            )
                        }

                        // Header with Title and Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEnglish) "Adjust Start Time" else "Atur Jam Mulai Puasa",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = if (isEnglish) "Fine-tune when your fast actually started." else "Sesuaikan jika kamu sudah mulai lebih awal atau lupa menyalakan timer.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(32.dp),
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

                        // Day Selector Segmented Control (Kemarin vs Hari Ini)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                // Tab Kemarin
                                Surface(
                                    onClick = { updateTime(setYesterday = true) },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (isYesterday) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    shadowElevation = if (isYesterday) 2.dp else 0.dp,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isEnglish) "Yesterday" else "Kemarin",
                                            fontSize = 13.sp,
                                            fontWeight = if (isYesterday) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isYesterday) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }

                                // Tab Hari Ini
                                Surface(
                                    onClick = { updateTime(setYesterday = false) },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (isToday) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    shadowElevation = if (isToday) 2.dp else 0.dp,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isEnglish) "Today" else "Hari Ini",
                                            fontSize = 13.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isToday) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }

                        // Time Dial / Stepper Card (Apple HIG Focus)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // Hour Stepper Column
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Surface(
                                            onClick = { updateTime(hour = (selectedLdt.hour + 1) % 24) },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                            modifier = Modifier.size(36.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, contentDescription = "Tambah Jam", modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f)),
                                            modifier = Modifier.width(72.dp).height(56.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = hourStr,
                                                    fontSize = 32.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandPrimary,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Surface(
                                            onClick = { updateTime(hour = (selectedLdt.hour + 23) % 24) },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                            modifier = Modifier.size(36.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Remove, contentDescription = "Kurang Jam", modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isEnglish) "Hours" else "Jam (0-23)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }

                                    Text(
                                        text = ":",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 24.dp),
                                    )

                                    // Minute Stepper Column
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Surface(
                                            onClick = { updateTime(minute = ((selectedLdt.minute / 5 * 5) + 5) % 60) },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                            modifier = Modifier.size(36.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, contentDescription = "Tambah Menit", modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f)),
                                            modifier = Modifier.width(72.dp).height(56.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = minuteStr,
                                                    fontSize = 32.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandPrimary,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Surface(
                                            onClick = { updateTime(minute = ((selectedLdt.minute / 5 * 5) + 55) % 60) },
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                            modifier = Modifier.size(36.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Remove, contentDescription = "Kurang Menit", modifier = Modifier.size(18.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isEnglish) "Mins" else "Menit (0-59)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }

                        // Quick Adjust Steppers (-1h, -30m, +30m, +1h)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            listOf(
                                "-1j" to -3600000L,
                                "-30m" to -1800000L,
                                "+30m" to 1800000L,
                                "+1j" to 3600000L,
                            ).forEach { (label, deltaMs) ->
                                Surface(
                                    onClick = { selectedStartMs += deltaMs },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // Quick Presets Row
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isEnglish) "Common Presets:" else "Pilihan Cepat:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Surface(
                                    onClick = { updateTime(hour = 20, minute = 0, setYesterday = true) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("Kmrn 20:00", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Surface(
                                    onClick = { updateTime(hour = 21, minute = 0, setYesterday = true) },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("Kmrn 21:00", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Surface(
                                    onClick = { selectedStartMs = nowMs },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(if (isEnglish) "Now" else "Sekarang", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // Live Calculation / Summary Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isFuture) DangerRed.copy(alpha = 0.08f) else BrandPrimary.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, if (isFuture) DangerRed.copy(alpha = 0.4f) else BrandPrimary.copy(alpha = 0.25f)),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                if (isFuture) {
                                    Text(
                                        text = if (isEnglish) "⚠️ Start time cannot be in the future." else "⚠️ Waktu mulai tidak boleh melebihi waktu saat ini.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DangerRed,
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = if (isEnglish) "New Start Time:" else "Waktu Mulai Baru:",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = "$dayLabel, $hourStr:$minuteStr",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Elapsed Fasting Time:" else "Durasi Puasa Berjalan:",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = "${elapsedHours} jam ${elapsedMins} menit",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandPrimary,
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Projected Goal End:" else "Target Buka Puasa:",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = "$targetDayLabel, pukul $targetHourStr",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Actions (Batal & Terapkan)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Text(if (isEnglish) "Cancel" else "Batal")
                            }

                            Button(
                                onClick = { onSave(selectedStartMs) },
                                enabled = !isFuture,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            ) {
                                Text(
                                    text = if (isEnglish) "Apply" else "Terapkan",
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
