package id.heula.presentation.component.liveactivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.ActiveFast
import id.heula.domain.model.ScreenTab
import id.heula.domain.model.nextStageForElapsed
import id.heula.domain.model.stageForElapsed
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.timer.getStageVisual
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.util.Strings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DynamicIslandPill(
    activeFast: ActiveFast,
    currentTimeMs: Long,
    currentTab: ScreenTab,
    s: Strings,
    onNavigateToHome: () -> Unit,
    onOpenHungerSos: () -> Unit,
    onOpenMetabolism: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val elapsedMs = (currentTimeMs - activeFast.startMs).coerceAtLeast(0L)
    val targetMs = (activeFast.targetHours * 3600000).toLong()
    val ratio = if (targetMs > 0) (elapsedMs.toFloat() / targetMs.toFloat()).coerceIn(0f, 1f) else 0f

    val hours = elapsedMs / 3600000L
    val mins = (elapsedMs % 3600000L) / 60000L
    val secs = (elapsedMs % 60000L) / 1000L
    val timeFormatted = "${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"

    val stage = stageForElapsed(elapsedMs)
    val visual = getStageVisual(stage.key)
    val nextInfo = nextStageForElapsed(elapsedMs)

    // Calculate end time
    val endMs = activeFast.startMs + targetMs
    val endLdt = Instant.fromEpochMilliseconds(endMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val endStr = "${endLdt.hour.toString().padStart(2, '0')}:${endLdt.minute.toString().padStart(2, '0')}"

    // Pulsing animation for the active stage dot
    val transition = rememberInfiniteTransition(label = "islandPulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .clip(RoundedCornerShape(if (isExpanded) 24.dp else 99.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { isExpanded = !isExpanded },
                )
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = 400f,
                    )
                ),
            shape = RoundedCornerShape(if (isExpanded) 24.dp else 99.dp),
            color = Color(0xFF121214), // Apple Deep Onyx / Dynamic Island True Black
            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.18f)),
            shadowElevation = 10.dp,
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = if (isExpanded) 18.dp else 14.dp,
                    vertical = if (isExpanded) 16.dp else 8.dp
                ),
            ) {
                // Collapsed Top Row: Always visible capsule header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Left: Pulsing Stage Dot & Stage Label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(visual.primaryColor)
                        )
                        Text(
                            text = stage.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = visual.primaryColor,
                            letterSpacing = (-0.2).sp,
                        )
                    }

                    // Right: Live Timer + Expand Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = timeFormatted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = (-0.3).sp,
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Tutup" else "Buka Detail",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                // Expanded Drawer: Rich iOS Live Activity Controls
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(tween(250)) + expandVertically(tween(250)),
                    exit = fadeOut(tween(180)) + shrinkVertically(tween(180)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                    ) {
                        // Metrics: Progress Bar & Milestone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Target: ${activeFast.targetHours.toInt()} jam (Selesai ~$endStr WIB)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.75f),
                            )
                            Text(
                                text = "${(ratio * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(CircleShape),
                            color = visual.primaryColor,
                            trackColor = Color.White.copy(alpha = 0.12f),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (nextInfo != null) {
                            Text(
                                text = "⏱️ ${nextInfo.remainingFormatted} lagi menuju fase ${nextInfo.stage.label}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f),
                            )
                        } else {
                            Text(
                                text = "🏆 Kamu sudah di fase puncak puasa!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimary,
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Action Buttons (HIG Horizontal Pills)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            // Button 1: Ke Timer Tab
                            Surface(
                                onClick = {
                                    isExpanded = false
                                    onNavigateToHome()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.10f),
                                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),
                                modifier = Modifier.weight(1f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = s.liveActivityOpenTimer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                }
                            }

                            // Button 2: Lapar SOS
                            Surface(
                                onClick = {
                                    isExpanded = false
                                    onOpenHungerSos()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFF9500).copy(alpha = 0.18f),
                                border = BorderStroke(0.5.dp, Color(0xFFFF9500).copy(alpha = 0.35f)),
                                modifier = Modifier.weight(1f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = HeulaIcons.Lifebuoy,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9500),
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = s.liveActivitySos,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9500),
                                    )
                                }
                            }

                            // Button 3: Metabolisme
                            Surface(
                                onClick = {
                                    isExpanded = false
                                    onOpenMetabolism()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF007AFF).copy(alpha = 0.18f),
                                border = BorderStroke(0.5.dp, Color(0xFF007AFF).copy(alpha = 0.35f)),
                                modifier = Modifier.weight(1f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = HeulaIcons.Sparkles,
                                        contentDescription = null,
                                        tint = Color(0xFF007AFF),
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = s.liveActivityMetabolism,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF007AFF),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
