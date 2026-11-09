package id.heula.presentation.component.hungersos

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.heula.domain.model.ActiveFast
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.util.Strings
import id.heula.presentation.util.playZenGongSound
import kotlinx.coroutines.delay

@Composable
fun HungerSosModal(
    activeFast: ActiveFast?,
    currentTimeMs: Long,
    s: Strings,
    onDismiss: () -> Unit,
) {
    // 5-minute distraction countdown timer (300 seconds)
    var isTimerRunning by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(300) }

    LaunchedEffect(isTimerRunning, secondsRemaining) {
        if (isTimerRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        } else if (secondsRemaining == 0) {
            isTimerRunning = false
        }
    }

    // Breathing phase: 4s inhale, 7s hold, 8s exhale (19s cycle)
    val cycleSecond = (300 - secondsRemaining) % 19
    val (breathPhaseText, breathColor) = when {
        !isTimerRunning -> Pair("Klik mulai untuk latihan napas 4-7-8", BrandPrimary)
        cycleSecond < 4 -> Pair("Tarik napas dalam... (${4 - cycleSecond}s)", Color(0xFF007AFF))
        cycleSecond < 11 -> Pair("Tahan napas rileks... (${11 - cycleSecond}s)", Color(0xFFFF9500))
        else -> Pair("Buang napas perlahan... (${19 - cycleSecond}s)", Color(0xFF34C759))
    }

    // Gentle pulse animation for the breathing sphere
    val transition = rememberInfiniteTransition(label = "breathePulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        ) {
            val isWide = maxWidth >= 640.dp

            Box(
                modifier = Modifier.fillMaxSize(),
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
                                Modifier
                                    .width(560.dp)
                                    .fillMaxHeight(0.88f)
                                    .padding(vertical = 24.dp)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.92f)
                            }
                        ),
                    shape = if (isWide) RoundedCornerShape(26.dp) else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 18.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                    ) {
                        // Grabber bar on mobile
                        if (!isWide) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 12.dp)
                                    .size(width = 38.dp, height = 5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                            )
                        }

                        // Header Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(Color(0xFFFF9500).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = HeulaIcons.Lifebuoy,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9500),
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                Column {
                                    Text(
                                        text = s.hungerSosTitle,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = s.hungerSosSubtitle,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp,
                                    )
                                }
                            }

                            // Close Button
                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(30.dp),
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // Scrollable Body Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            // Section 1: 5-Minute Distraction & 4-7-8 Breathing Card
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, breathColor.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = s.hungerSosBreatheTitle,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = s.hungerSosBreatheDesc,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        lineHeight = 16.sp,
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Animated Breathing Circle with Timer
                                    val timerMin = secondsRemaining / 60
                                    val timerSec = secondsRemaining % 60
                                    val timeFormatted = "${timerMin.toString().padStart(2, '0')}:${timerSec.toString().padStart(2, '0')}"

                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .scale(if (isTimerRunning) pulseScale else 1f)
                                            .clip(CircleShape)
                                            .background(breathColor.copy(alpha = 0.12f))
                                            .border(2.dp, breathColor.copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = timeFormatted,
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = breathColor,
                                                letterSpacing = (-0.5).sp,
                                            )
                                            Text(
                                                text = if (isTimerRunning) "Tahan" else "Siap",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = breathPhaseText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = breathColor,
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Controls: Start/Pause and Reset
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = { isTimerRunning = !isTimerRunning },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = breathColor,
                                                contentColor = Color.White,
                                            ),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                                        ) {
                                            Icon(
                                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isTimerRunning) "Jeda" else "Mulai 5 Menit",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        if (secondsRemaining < 300) {
                                            Surface(
                                                onClick = {
                                                    isTimerRunning = false
                                                    secondsRemaining = 300
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = "Reset",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(15.dp),
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Reset",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Section 2: 3 Immediate Science-Backed Action Items
                            Text(
                                text = "3 Trik Instan Redakan Lapar Palsu:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            HungerTipItem(
                                icon = HeulaIcons.WaterDrop,
                                iconColor = Color(0xFF007AFF),
                                title = s.hungerSosTip1Title,
                                desc = s.hungerSosTip1Desc,
                            )

                            HungerTipItem(
                                icon = HeulaIcons.Bolt,
                                iconColor = Color(0xFFFF9500),
                                title = s.hungerSosTip2Title,
                                desc = s.hungerSosTip2Desc,
                            )

                            HungerTipItem(
                                icon = HeulaIcons.Flame,
                                iconColor = Color(0xFFFF2D55),
                                title = s.hungerSosTip3Title,
                                desc = s.hungerSosTip3Desc,
                            )

                            // Section 3: Personal Fasting Progress Affirmation
                            if (activeFast != null) {
                                val elapsedMs = (currentTimeMs - activeFast.startMs).coerceAtLeast(0L)
                                val elapsedHours = elapsedMs / 3600000L
                                val elapsedMins = (elapsedMs % 3600000L) / 60000L

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = BrandPrimary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimary),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = HeulaIcons.Sparkles,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp),
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "Sudah berjalan ${elapsedHours} jam ${elapsedMins} menit!",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandPrimary,
                                            )
                                            Text(
                                                text = "Cadangan lemak perutmu lagi dibakar aktif sekarang. Sayang banget kalau batal karena rasa lapar 15 menit!",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 16.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Affirmation Button
                        Button(
                            onClick = {
                                playZenGongSound()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPrimary,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        ) {
                            Icon(
                                imageVector = HeulaIcons.Sparkles,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = s.hungerSosStayStrong,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HungerTipItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    desc: String,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}
