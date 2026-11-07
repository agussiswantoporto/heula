package id.heula.presentation.component.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import id.heula.domain.model.Mood

data class MoodVisual(
    val mood: Mood,
    val label: String,
    val icon: ImageVector,
    val color: Color,
)

/**
 * Returns pure vector icon and iOS system palette for user's fasting energy/mood.
 * Ensures consistent rendering across Web (Wasm/Canvas) and Mobile without relying on system emoji fonts.
 */
fun getMoodVisual(mood: Mood): MoodVisual {
    return when (mood) {
        Mood.ENERGIK -> MoodVisual(
            mood = Mood.ENERGIK,
            label = "Energik",
            icon = Icons.Default.Bolt,
            color = Color(0xFFFF9500), // iOS System Orange
        )
        Mood.BIASA -> MoodVisual(
            mood = Mood.BIASA,
            label = "Biasa",
            icon = Icons.Default.SentimentSatisfied,
            color = Color(0xFF34C759), // iOS System Green
        )
        Mood.LEMES -> MoodVisual(
            mood = Mood.LEMES,
            label = "Lemas",
            icon = Icons.Default.BatteryAlert,
            color = Color(0xFF8E8E93), // iOS System Gray
        )
        Mood.LAPAR -> MoodVisual(
            mood = Mood.LAPAR,
            label = "Lapar",
            icon = Icons.Default.Restaurant,
            color = Color(0xFFFF3B30), // iOS System Red
        )
    }
}
