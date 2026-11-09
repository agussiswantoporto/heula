package id.heula.presentation.component.metabolism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.heula.domain.model.STAGES
import id.heula.domain.model.Stage
import id.heula.domain.model.TimelineEntry
import id.heula.domain.model.TimelineState
import id.heula.domain.model.timelineForElapsed
import id.heula.presentation.component.icon.HeulaIcons
import id.heula.presentation.component.timer.getStageVisual
import id.heula.presentation.theme.BrandPrimary
import id.heula.presentation.util.Strings

data class DrinkItem(
    val name: String,
    val calories: String,
    val tag: String,
    val isAllowed: Boolean,
    val description: String,
    val icon: ImageVector,
)

val ALLOWED_DRINKS = listOf(
    DrinkItem(
        name = "Air Mineral (Dingin/Hangat)",
        calories = "0 Kalori",
        tag = "Wajib & Utama",
        isAllowed = true,
        description = "Bantu ginjal buang racun, jaga sirkulasi darah, dan redam lapar palsu secara instan.",
        icon = HeulaIcons.WaterDrop,
    ),
    DrinkItem(
        name = "Kopi Hitam Polos / Americano",
        calories = "0 Kalori (Tanpa Gula/Susu)",
        tag = "Aman & Dorong Autofagi",
        isAllowed = true,
        description = "Kafein murni meningkatkan metabolisme, menekan nafsu makan, dan membantu regenerasi sel tubuh.",
        icon = HeulaIcons.Coffee,
    ),
    DrinkItem(
        name = "Teh Hijau / Hitam / Chamomile",
        calories = "0 Kalori (Tanpa Gula)",
        tag = "Kaya Antioksidan",
        isAllowed = true,
        description = "Polifenol dan L-Theanine di teh menenangkan lambung dan bikin pikiran makin fokus selama berpuasa.",
        icon = HeulaIcons.Coffee,
    ),
    DrinkItem(
        name = "Air Garam / Elektrolit Tanpa Gula",
        calories = "0 Kalori",
        tag = "Anti Lemas & Pusing",
        isAllowed = true,
        description = "Sejumput garam dapur/himalaya di air hangat menjaga natrium darah agar tidak pusing saat tubuh masuk ketosis.",
        icon = HeulaIcons.Bolt,
    ),
    DrinkItem(
        name = "Air Infus Lemon Tipis",
        calories = "< 3 Kalori",
        tag = "Segar & Aman",
        isAllowed = true,
        description = "1-2 irisan tipis lemon dalam sebotol air tidak memicu respon insulin, bikin nafas segar.",
        icon = HeulaIcons.Sparkles,
    ),
)

val FORBIDDEN_DRINKS = listOf(
    DrinkItem(
        name = "Kopi Susu, Latte, Boba",
        calories = "150 - 450 Kalori",
        tag = "Batal Puasa",
        isAllowed = false,
        description = "Laktosa susu dan sirup gula memicu lonjakan insulin seketika dan menghentikan pembakaran lemak.",
        icon = HeulaIcons.Restaurant,
    ),
    DrinkItem(
        name = "Jus Buah Segar & Air Kelapa",
        calories = "90 - 200 Kalori",
        tag = "Batal Puasa",
        isAllowed = false,
        description = "Gula fruktosa alami langsung diproses di hati dan mematikan mode autofagi sel.",
        icon = HeulaIcons.Restaurant,
    ),
    DrinkItem(
        name = "Susu Kedelai / Oat / Almond Manis",
        calories = "100 - 180 Kalori",
        tag = "Batal Puasa",
        isAllowed = false,
        description = "Mengandung karbohidrat dan protein cair yang langsung dicerna oleh lambung.",
        icon = HeulaIcons.Restaurant,
    ),
)

@Composable
fun MetabolismJournalModal(
    currentStage: Stage,
    elapsedMs: Long,
    s: Strings,
    onDismiss: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Timeline, 1: Boleh Minum Apa?

    val timeline = remember(elapsedMs) { timelineForElapsed(elapsedMs) }
    val totalHours = elapsedMs / 3_600_000.0
    val totalH = totalHours.toInt()
    val totalM = ((totalHours - totalH) * 60).toInt()
    val currentVisual = remember(currentStage.key) { getStageVisual(currentStage.key) }

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
                                    .width(600.dp)
                                    .fillMaxHeight(0.90f)
                                    .padding(vertical = 24.dp)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.94f)
                            }
                        ),
                    shape = if (isWide) RoundedCornerShape(26.dp) else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 20.dp,
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

                        // Header with Close
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.metabolismJournalTitle,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.6).sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = s.metabolismJournalSubtitle,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp,
                                )
                            }

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

                        // Apple iOS Segmented Control Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(3.dp),
                        ) {
                            // Tab 0: Timeline Sel & Organ
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(
                                        if (selectedTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent
                                    )
                                    .clickable { selectedTab = 0 }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = HeulaIcons.Sparkles,
                                        contentDescription = null,
                                        tint = if (selectedTab == 0) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Text(
                                        text = s.metabolismTabTimeline,
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            // Tab 1: Boleh Minum Apa?
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(
                                        if (selectedTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent
                                    )
                                    .clickable { selectedTab = 1 }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = HeulaIcons.Coffee,
                                        contentDescription = null,
                                        tint = if (selectedTab == 1) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Text(
                                        text = s.metabolismTabDrinks,
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tab 0 Content: Cellular Stage Timeline
                        if (selectedTab == 0) {
                            // Current Active Stage Highlight Banner
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = currentVisual.containerColor,
                                border = BorderStroke(1.dp, currentVisual.primaryColor.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(currentVisual.primaryColor),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = currentVisual.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp),
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Waktu Puasa: ${totalH} jam ${totalM} menit",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Text(
                                            text = "Sedang di fase ${currentStage.label}. Tubuhmu lagi kerja hebat!",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                            ) {
                                itemsIndexed(timeline) { index, entry ->
                                    val isLast = index == timeline.lastIndex
                                    MetabolicTimelineNode(entry = entry, isLast = isLast)
                                }
                            }
                        } else {
                            // Tab 1 Content: Fasting Drinks Guide
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                item {
                                    Text(
                                        text = "✅ 100% Aman & Sangat Dianjurkan (Bebas Kalori)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34C759),
                                    )
                                }

                                items(ALLOWED_DRINKS.size) { idx ->
                                    DrinkCard(ALLOWED_DRINKS[idx])
                                }

                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "❌ Dilarang (Langsung Membatalkan Puasa)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF3B30),
                                    )
                                }

                                items(FORBIDDEN_DRINKS.size) { idx ->
                                    DrinkCard(FORBIDDEN_DRINKS[idx])
                                }

                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Icon(
                                                imageVector = HeulaIcons.Bolt,
                                                contentDescription = null,
                                                tint = Color(0xFFFF9500),
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Text(
                                                text = "Aturan Simpel: Selama minumanmu 0 kalori, tidak ada susu/gula, dan tidak memicu respon insulin—puasamu 100% aman!",
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

                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPrimary,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        ) {
                            Text(
                                text = "Keren, Makin Paham & Semangat Puasa!",
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
private fun DrinkCard(drink: DrinkItem) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            0.5.dp,
            if (drink.isAllowed) Color(0xFF34C759).copy(alpha = 0.35f) else Color(0xFFFF3B30).copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (drink.isAllowed) Color(0xFF34C759).copy(alpha = 0.12f) else Color(0xFFFF3B30).copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = drink.icon,
                    contentDescription = null,
                    tint = if (drink.isAllowed) Color(0xFF34C759) else Color(0xFFFF3B30),
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = drink.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (drink.isAllowed) Color(0xFF34C759).copy(alpha = 0.15f) else Color(0xFFFF3B30).copy(alpha = 0.15f),
                    ) {
                        Text(
                            text = drink.tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (drink.isAllowed) Color(0xFF34C759) else Color(0xFFFF3B30),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = drink.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun MetabolicTimelineNode(
    entry: TimelineEntry,
    isLast: Boolean,
) {
    val stage = entry.stage
    val isCurrent = entry.state == TimelineState.CURRENT
    val isPassed = entry.state == TimelineState.PASSED
    val visual = remember(stage.key) { getStageVisual(stage.key) }

    val lineColor = when {
        isPassed -> BrandPrimary.copy(alpha = 0.6f)
        isCurrent -> visual.primaryColor.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isPassed -> BrandPrimary
                            isCurrent -> visual.containerColor
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (isCurrent) 2.dp else 1.dp,
                        color = when {
                            isCurrent -> visual.primaryColor
                            isPassed -> Color.Transparent
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    isPassed -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selesai",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    isCurrent -> {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = stage.label,
                            tint = visual.primaryColor,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                        )
                    }
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 12.dp else 20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stage.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) visual.primaryColor else if (isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${stage.hourRange})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = visual.primaryColor.copy(alpha = 0.16f),
                    ) {
                        Text(
                            text = "Aktif Sekarang",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = visual.primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stage.whatHappens,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "💡 Tips:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                    )
                    Text(
                        text = stage.tip,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp,
                    )
                }
            }
        }
    }
}
