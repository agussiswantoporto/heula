package id.heula.presentation.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.util.Strings

@Composable
fun CircadianPlannerCard(
    sleepStartHour: Int,
    sleepEndHour: Int,
    targetFastingHours: Double,
    s: Strings,
    onUpdateSleepSchedule: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Calculate sleep duration in hours (handles wrapping over midnight)
    val sleepHours = if (sleepEndHour >= sleepStartHour) {
        sleepEndHour - sleepStartHour
    } else {
        (24 - sleepStartHour) + sleepEndHour
    }.coerceIn(4, 12)

    val sleepRatio = (sleepHours.toFloat() / 24f).coerceIn(0.15f, 0.5f)
    val awakeFastingHours = (targetFastingHours - sleepHours).coerceAtLeast(0.0)
    val awakeFastingRatio = (awakeFastingHours.toFloat() / 24f).coerceIn(0.1f, 0.45f)
    val eatingRatio = (1f - sleepRatio - awakeFastingRatio).coerceAtLeast(0.15f)

    // Recommended schedule based on 16:8 and user's sleep window
    val idealEatingEndHour = (sleepStartHour - 3 + 24) % 24
    val idealEatingStartHour = (idealEatingEndHour - (24 - targetFastingHours).toInt() + 24) % 24

    HeulaCard(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFF5856D6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = HeulaIcons.Moon,
                        contentDescription = null,
                        tint = Color(0xFF5856D6),
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column {
                    Text(
                        text = s.circadianTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${sleepHours} Jam Puasa Sambil Tidur 💤",
                        fontSize = 12.sp,
                        color = Color(0xFF5856D6),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                HeulaBadge(
                    text = "${sleepStartHour.toString().padStart(2, '0')}:00 - ${sleepEndHour.toString().padStart(2, '0')}:00",
                    backgroundColor = Color(0xFF5856D6).copy(alpha = 0.12f),
                    contentColor = Color(0xFF5856D6),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Visual 24-Hour Tri-Segment Bar (Eating vs Fasting Sleep vs Fasting Awake)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(99.dp)),
        ) {
            // Eating Window Segment (Warm Orange)
            Box(
                modifier = Modifier
                    .weight(eatingRatio)
                    .fillMaxHeight()
                    .background(Color(0xFFFF9500)),
            )
            // Asleep Fasting Segment (Indigo / Deep Purple)
            Box(
                modifier = Modifier
                    .weight(sleepRatio)
                    .fillMaxHeight()
                    .background(Color(0xFF5856D6)),
            )
            // Awake Fasting Segment (Brand Primary Green)
            if (awakeFastingHours > 0) {
                Box(
                    modifier = Modifier
                        .weight(awakeFastingRatio)
                        .fillMaxHeight()
                        .background(BrandPrimary),
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF9500)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Makan (${(24 - targetFastingHours).toInt()}j)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF5856D6)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tidur (${sleepHours}j)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (awakeFastingHours > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrandPrimary))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Puasa Bangun (${awakeFastingHours.toInt()}j)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = s.circadianSubtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
        )

        // Expandable Settings: Adjust Sleep & Wake Up Times
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Atur Jam Tidur & Bangunmu:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Sleep Start Control
                            Column {
                                Text("Mulai Tidur", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateSleepSchedule((sleepStartHour - 1 + 24) % 24, sleepEndHour) },
                                        modifier = Modifier.size(28.dp),
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = "${sleepStartHour.toString().padStart(2, '0')}:00",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    IconButton(
                                        onClick = { onUpdateSleepSchedule((sleepStartHour + 1) % 24, sleepEndHour) },
                                        modifier = Modifier.size(28.dp),
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            // Wake Up Control
                            Column {
                                Text("Bangun Tidur", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateSleepSchedule(sleepStartHour, (sleepEndHour - 1 + 24) % 24) },
                                        modifier = Modifier.size(28.dp),
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = "${sleepEndHour.toString().padStart(2, '0')}:00",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    IconButton(
                                        onClick = { onUpdateSleepSchedule(sleepStartHour, (sleepEndHour + 1) % 24) },
                                        modifier = Modifier.size(28.dp),
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Optimal Fasting Window Tip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandPrimary.copy(alpha = 0.10f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = HeulaIcons.Sparkles,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Jadwal Ideal: Selesai makan malam jam ${idealEatingEndHour.toString().padStart(2, '0')}:00, lalu buka puasa besok jam ${idealEatingStartHour.toString().padStart(2, '0')}:00. Enteng banget!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp,
                        )
                    }
                }
            }
        }
    }
}
