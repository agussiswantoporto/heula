package id.heula.presentation.component.icon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

/**
 * Clean vector flag for Indonesia.
 * Renders identically across Web (Wasm/Canvas), Android, and iOS without relying on system emoji fonts.
 */
@Composable
fun IndonesiaFlag(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 20.dp, height = 14.dp)
            .clip(RoundedCornerShape(3.dp))
            .border(
                width = 0.5.dp,
                color = Color.Black.copy(alpha = 0.15f),
                shape = RoundedCornerShape(3.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFE70011)) // Standard Indonesian Red
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
            )
        }
    }
}

/**
 * Clean vector flag for English / United Kingdom (Union Jack).
 * Renders identically across Web (Wasm/Canvas), Android, and iOS without relying on system emoji fonts.
 */
@Composable
fun UkFlag(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 20.dp, height = 14.dp)
            .clip(RoundedCornerShape(3.dp))
            .border(
                width = 0.5.dp,
                color = Color.Black.copy(alpha = 0.15f),
                shape = RoundedCornerShape(3.dp)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Blue field
            drawRect(color = Color(0xFF012169))

            // 2. White diagonals
            val diagStroke = w * 0.14f
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = diagStroke,
                cap = StrokeCap.Square
            )
            drawLine(
                color = Color.White,
                start = Offset(w, 0f),
                end = Offset(0f, h),
                strokeWidth = diagStroke,
                cap = StrokeCap.Square
            )

            // 3. Red diagonals
            val redDiagStroke = w * 0.05f
            drawLine(
                color = Color(0xFFC8102E),
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = redDiagStroke,
                cap = StrokeCap.Square
            )
            drawLine(
                color = Color(0xFFC8102E),
                start = Offset(w, 0f),
                end = Offset(0f, h),
                strokeWidth = redDiagStroke,
                cap = StrokeCap.Square
            )

            // 4. White central cross
            val whiteCrossW = w * 0.22f
            val whiteCrossH = h * 0.32f
            drawRect(
                color = Color.White,
                topLeft = Offset((w - whiteCrossW) / 2f, 0f),
                size = Size(whiteCrossW, h)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(0f, (h - whiteCrossH) / 2f),
                size = Size(w, whiteCrossH)
            )

            // 5. Red central cross
            val redCrossW = w * 0.13f
            val redCrossH = h * 0.19f
            drawRect(
                color = Color(0xFFC8102E),
                topLeft = Offset((w - redCrossW) / 2f, 0f),
                size = Size(redCrossW, h)
            )
            drawRect(
                color = Color(0xFFC8102E),
                topLeft = Offset(0f, (h - redCrossH) / 2f),
                size = Size(w, redCrossH)
            )
        }
    }
}
