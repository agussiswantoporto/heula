package id.heula.presentation.component.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object HeulaIcons {
    // Restaurant / Eating Window Icon (Knife & Fork)
    val Restaurant: ImageVector by lazy {
        ImageVector.Builder(
            name = "Restaurant",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(11.0f, 9.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(7.0f)
                verticalLineToRelative(7.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(3.0f)
                verticalLineToRelative(7.0f)
                curveToRelative(0.0f, 2.12f, 1.66f, 3.84f, 3.75f, 3.97f)
                verticalLineTo(22.0f)
                horizontalLineToRelative(2.5f)
                verticalLineToRelative(-9.03f)
                curveTo(11.34f, 12.84f, 13.0f, 11.12f, 13.0f, 9.0f)
                verticalLineTo(2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(7.0f)
                close()
                moveTo(16.0f, 6.0f)
                verticalLineToRelative(14.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(2.0f)
                curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
                verticalLineToRelative(7.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(6.0f)
                close()
            }
        }.build()
    }

    // Trophy / Review Puasa & Achievement Icon
    val Trophy: ImageVector by lazy {
        ImageVector.Builder(
            name = "Trophy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19.0f, 5.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(7.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(1.0f)
                curveToRelative(0.0f, 2.55f, 1.92f, 4.63f, 4.39f, 4.94f)
                curveToRelative(0.63f, 1.5f, 1.98f, 2.63f, 3.61f, 2.96f)
                verticalLineTo(19.0f)
                horizontalLineTo(7.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(10.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(-3.1f)
                curveToRelative(1.63f, -0.33f, 2.98f, -1.46f, 3.61f, -2.96f)
                curveToRelative(2.47f, -0.31f, 4.39f, -2.39f, 4.39f, -4.94f)
                verticalLineTo(7.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(5.0f, 8.0f)
                verticalLineTo(7.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(3.82f)
                curveTo(5.84f, 10.4f, 5.0f, 9.3f, 5.0f, 8.0f)
                close()
                moveTo(19.0f, 8.0f)
                curveToRelative(0.0f, 1.3f, -0.84f, 2.4f, -2.0f, 2.82f)
                verticalLineTo(7.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(1.0f)
                close()
            }
        }.build()
    }

    // Water Drop Icon
    val WaterDrop: ImageVector by lazy {
        ImageVector.Builder(
            name = "WaterDrop",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.0f, 2.69f)
                lineTo(6.36f, 9.47f)
                curveTo(4.24f, 12.01f, 4.54f, 15.77f, 7.08f, 17.89f)
                curveTo(8.52f, 19.1f, 10.26f, 19.74f, 12.0f, 19.74f)
                curveToRelative(1.74f, 0.0f, 3.48f, -0.64f, 4.92f, -1.85f)
                curveToRelative(2.54f, -2.12f, 2.84f, -5.88f, 0.72f, -8.42f)
                lineTo(12.0f, 2.69f)
                close()
            }
        }.build()
    }

    // Translate / Language Icon
    val Language: ImageVector by lazy {
        ImageVector.Builder(
            name = "Language",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.87f, 15.07f)
                lineToRelative(-2.54f, -2.51f)
                curveToRelative(1.17f, -1.32f, 1.99f, -2.84f, 2.44f, -4.56f)
                horizontalLineTo(15.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(7.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(1.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(9.74f)
                curveToRelative(-0.41f, 1.32f, -1.09f, 2.51f, -2.0f, 3.54f)
                curveToRelative(-0.62f, -0.69f, -1.13f, -1.46f, -1.52f, -2.29f)
                horizontalLineTo(5.17f)
                curveToRelative(0.48f, 1.25f, 1.2f, 2.39f, 2.11f, 3.39f)
                lineTo(2.76f, 17.2f)
                lineTo(4.17f, 18.61f)
                lineToRelative(4.55f, -4.55f)
                lineToRelative(3.11f, 3.11f)
                lineToRelative(1.04f, -2.1f)
                close()
                moveTo(18.5f, 10.0f)
                horizontalLineToRelative(-2.0f)
                lineTo(12.0f, 22.0f)
                horizontalLineToRelative(2.0f)
                lineToRelative(1.12f, -3.0f)
                horizontalLineToRelative(4.75f)
                lineTo(21.0f, 22.0f)
                horizontalLineToRelative(2.0f)
                lineToRelative(-4.5f, -12.0f)
                close()
                moveTo(15.88f, 17.0f)
                lineToRelative(1.62f, -4.33f)
                lineTo(19.12f, 17.0f)
                horizontalLineToRelative(-3.24f)
                close()
            }
        }.build()
    }

    // Official 4-color Google "G" vector mark
    val Google: ImageVector by lazy {
        ImageVector.Builder(
            name = "Google",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF4285F4))) {
                moveTo(22.56f, 12.25f)
                curveToRelative(0.0f, -0.78f, -0.07f, -1.53f, -0.2f, -2.25f)
                horizontalLineTo(12.0f)
                verticalLineToRelative(4.26f)
                horizontalLineToRelative(5.92f)
                curveToRelative(-0.26f, 1.37f, -1.04f, 2.53f, -2.21f, 3.31f)
                verticalLineToRelative(2.77f)
                horizontalLineToRelative(3.57f)
                curveToRelative(2.08f, -1.92f, 3.28f, -4.74f, 3.28f, -8.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFF34A853))) {
                moveTo(12.0f, 23.0f)
                curveToRelative(2.97f, 0.0f, 5.46f, -0.98f, 7.28f, -2.66f)
                lineToRelative(-3.57f, -2.77f)
                curveToRelative(-0.98f, 0.66f, -2.23f, 1.06f, -3.71f, 1.06f)
                curveToRelative(-2.86f, 0.0f, -5.29f, -1.93f, -6.16f, -4.53f)
                horizontalLineTo(2.18f)
                verticalLineToRelative(2.84f)
                curveTo(3.99f, 20.53f, 7.7f, 23.0f, 12.0f, 23.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBBC05))) {
                moveTo(5.84f, 14.09f)
                curveToRelative(-0.22f, -0.66f, -0.35f, -1.36f, -0.35f, -2.09f)
                reflectiveCurveToRelative(0.13f, -1.43f, 0.35f, -2.09f)
                verticalLineTo(7.06f)
                horizontalLineTo(2.18f)
                curveTo(1.43f, 8.55f, 1.0f, 10.22f, 1.0f, 12.0f)
                reflectiveCurveToRelative(0.43f, 3.45f, 1.18f, 4.94f)
                lineToRelative(2.85f, -2.22f)
                lineToRelative(0.81f, -0.63f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEA4335))) {
                moveTo(12.0f, 5.38f)
                curveToRelative(1.62f, 0.0f, 3.06f, 0.56f, 4.21f, 1.64f)
                lineToRelative(3.15f, -3.15f)
                curveTo(17.45f, 2.09f, 14.97f, 1.0f, 12.0f, 1.0f)
                curveTo(7.7f, 1.0f, 3.99f, 3.47f, 2.18f, 7.06f)
                lineToRelative(3.66f, 2.84f)
                curveToRelative(0.87f, -2.6f, 3.3f, -4.52f, 6.16f, -4.52f)
                close()
            }
        }.build()
    }
}
