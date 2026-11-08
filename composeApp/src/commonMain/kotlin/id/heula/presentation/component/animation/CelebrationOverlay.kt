package id.heula.presentation.component.animation

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class MicroParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val size: Float,
    val isRibbon: Boolean,
    val color: Color,
    val angularSpeed: Float,
    var angle: Float = 0f,
    var flipSpeed: Float,
    var flipPhase: Float = 0f,
)

/**
 * Elegant, refined celebration burst following Apple HIG aesthetic.
 * Uses micro-particles, subtle flutter physics, and gentle organic decay.
 */
@Composable
fun CelebrationOverlay(
    show: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
) {
    if (!show) return

    val particles = remember {
        val palette = listOf(
            Color(0xFF34C759), // iOS Green
            Color(0xFF30D158), // Mint
            Color(0xFFFFD60A), // Champagne Gold
            Color(0xFFFFFFFF), // Crisp White
            Color(0xFFA7F3D0), // Soft Mint Tint
            Color(0xFF5856D6), // Subtle Apple Indigo
        )
        val rng = Random(ClockMillis())
        List(42) {
            val angle = rng.nextDouble(-2.4, -0.7).toFloat() // Upward cone (-135° to -40°)
            val speed = rng.nextDouble(450.0, 950.0).toFloat()
            val isRibbon = rng.nextBoolean()
            MicroParticle(
                x = 0f,
                y = 0f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                size = if (isRibbon) rng.nextDouble(7.0, 11.0).toFloat() else rng.nextDouble(3.5, 6.0).toFloat(),
                isRibbon = isRibbon,
                color = palette[rng.nextInt(palette.size)],
                angularSpeed = rng.nextDouble(-8.0, 8.0).toFloat(),
                angle = rng.nextDouble(0.0, 360.0).toFloat(),
                flipSpeed = rng.nextDouble(4.0, 10.0).toFloat(),
                flipPhase = rng.nextDouble(0.0, 6.28).toFloat(),
            )
        }
    }

    var elapsedSeconds by remember { mutableStateOf(0f) }
    var lastFrameTime by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val startTime = ClockMillis()
        while (elapsedSeconds < 2.2f) {
            withInfiniteAnimationFrameMillis { frameTime ->
                val dt = if (lastFrameTime == 0L) 0.016f else ((frameTime - lastFrameTime) / 1000f).coerceIn(0.001f, 0.05f)
                lastFrameTime = frameTime
                elapsedSeconds = ((frameTime - startTime) / 1000f)

                val gravity = 1050f // px/s^2
                particles.forEach { p ->
                    p.x += p.vx * dt
                    p.y += p.vy * dt
                    p.vx *= (1f - 0.65f * dt) // Air drag
                    p.vy += gravity * dt
                    p.angle += p.angularSpeed * 60f * dt
                    p.flipPhase += p.flipSpeed * dt
                }
            }
        }
        onFinished()
    }

    // Alpha decay: 1.0 for first 0.6s, then smooth exponential fade out to 0 at 2.2s
    val alpha = remember(elapsedSeconds) {
        if (elapsedSeconds < 0.6f) 1f
        else ((2.2f - elapsedSeconds) / 1.6f).coerceIn(0f, 1f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val originX = size.width / 2f
            val originY = size.height * 0.45f

            particles.forEach { p ->
                val drawX = originX + p.x
                val drawY = originY + p.y

                val particleColor = p.color.copy(alpha = (p.color.alpha * alpha))

                if (p.isRibbon) {
                    val scaleX = cos(p.flipPhase) // 3D tumbling paper effect
                    val w = (p.size * 0.4f * scaleX.let { if (it < 0) -it else it }).coerceAtLeast(1.5f)
                    val h = p.size

                    rotate(degrees = p.angle, pivot = Offset(drawX, drawY)) {
                        drawRoundRect(
                            color = particleColor,
                            topLeft = Offset(drawX - w / 2, drawY - h / 2),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(2f, 2f),
                        )
                    }
                } else {
                    drawCircle(
                        color = particleColor,
                        radius = p.size,
                        center = Offset(drawX, drawY),
                    )
                }
            }
        }
    }
}

private fun ClockMillis(): Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
