package id.heula.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import heula.composeapp.generated.resources.Res
import heula.composeapp.generated.resources.ic_logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
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
import id.heula.domain.model.*
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.timer.ProgressRing
import id.heula.presentation.component.timer.StageIndicator
import id.heula.presentation.component.ui.ButtonVariant
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.store.AppStore
import id.heula.presentation.store.calculateStreak
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.util.getStrings
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeScreen(
    store: AppStore,
    modifier: Modifier = Modifier,
) {
    val settings by store.settings.collectAsState()
    val activeFast by store.activeFast.collectAsState()
    val sessions by store.sessions.collectAsState()
    val s = getStrings(settings.language)

    var currentTimeMs by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }

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
                        StageIndicator(stage = stage, elapsedMs = elapsedMs)
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
                )

                WaterTrackerCard(
                    waterMl = settings.waterIntakeMl,
                    s = s,
                    onAdd = { store.addWater(it) },
                )

                if (activeFast != null) {
                    val elapsedMs = (currentTimeMs - activeFast!!.startMs).coerceAtLeast(0L)
                    val stage = stageForElapsed(elapsedMs)
                    StageIndicator(stage = stage, elapsedMs = elapsedMs)
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
    }

    if (showFinishFastModal && activeFast != null) {
        val elapsedMs = (currentTimeMs - activeFast!!.startMs).coerceAtLeast(0L)
        val targetMs = (activeFast!!.targetHours * 3600000L).toLong()
        FinishFastModal(
            durationMs = elapsedMs,
            targetHours = activeFast!!.targetHours,
            isCompleted = elapsedMs >= targetMs,
            s = s,
            onConfirm = { mood, note ->
                store.stopFast(currentTimeMs, mood, note)
                showFinishFastModal = false
            },
            onDismiss = { showFinishFastModal = false },
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
            onSave = { newStartMs ->
                store.updateStartTime(newStartMs)
                showEditStartDialog = false
            },
            onDismiss = { showEditStartDialog = false },
        )
    }
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
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = "Heula",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 2.dp)
            )
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
    s: id.heula.presentation.util.Strings,
    onConfirm: (Mood?, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var noteText by remember { mutableStateOf("") }

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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf(
                        Mood.ENERGIK to "⚡ Energik",
                        Mood.BIASA to "🙂 Biasa",
                        Mood.LEMES to "🥱 Lemas",
                        Mood.LAPAR to "🤤 Lapar",
                    ).forEach { (mood, label) ->
                        val isSelected = selectedMood == mood
                        Button(
                            onClick = { selectedMood = if (isSelected) null else mood },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(label.split(" ")[0], fontSize = 15.sp)
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMood, noteText.ifBlank { null }) },
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
    onSave: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var hoursAgo by remember { mutableStateOf(1.0) }

    val newStartMs = (Clock.System.now().toEpochMilliseconds() - (hoursAgo * 3600000L).toLong())
    val ldt = Instant.fromEpochMilliseconds(newStartMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val timePreview = "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Waktu Mulai Puasa", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Pilih berapa jam yang lalu puasa ini sebenarnya dimulai:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf(0.5, 1.0, 2.0, 4.0, 8.0, 12.0).forEach { h ->
                        val isSelected = hoursAgo == h
                        Button(
                            onClick = { hoursAgo = h },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (h < 1.0) "-30m" else "-${h.toInt()}j", fontSize = 11.sp)
                        }
                    }
                }

                HeulaCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(
                        text = "Waktu mulai disesuaikan: Pukul $timePreview",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(newStartMs) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
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
