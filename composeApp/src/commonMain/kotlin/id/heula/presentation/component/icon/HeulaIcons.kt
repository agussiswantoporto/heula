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

    // Apple Health Heart Icon (iOS HIG)
    val AppleHealth: ImageVector by lazy {
        ImageVector.Builder(
            name = "AppleHealth",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFFFF2D55))) {
                moveTo(12.0f, 21.35f)
                lineToRelative(-1.45f, -1.32f)
                curveTo(5.4f, 15.36f, 2.0f, 12.28f, 2.0f, 8.5f)
                curveTo(2.0f, 5.42f, 4.42f, 3.0f, 7.5f, 3.0f)
                curveToRelative(1.74f, 0.0f, 3.41f, 0.81f, 4.5f, 2.09f)
                curveTo(13.09f, 3.81f, 14.76f, 3.0f, 16.5f, 3.0f)
                curveTo(19.58f, 3.0f, 22.0f, 5.42f, 22.0f, 8.5f)
                curveToRelative(0.0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
                lineTo(12.0f, 21.35f)
                close()
            }
        }.build()
    }

    // Google Health Connect Icon
    val HealthConnect: ImageVector by lazy {
        ImageVector.Builder(
            name = "HealthConnect",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF00897B)),
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.0f, 21.0f)
                lineToRelative(-1.45f, -1.32f)
                curveTo(5.4f, 15.36f, 2.0f, 12.28f, 2.0f, 8.5f)
                curveTo(2.0f, 5.42f, 4.42f, 3.0f, 7.5f, 3.0f)
                curveToRelative(1.74f, 0.0f, 3.41f, 0.81f, 4.5f, 2.09f)
                curveTo(13.09f, 3.81f, 14.76f, 3.0f, 16.5f, 3.0f)
                curveTo(19.58f, 3.0f, 22.0f, 5.42f, 22.0f, 8.5f)
                curveToRelative(0.0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
                lineTo(12.0f, 21.0f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF00897B)),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8.5f, 12.0f)
                horizontalLineToRelative(2.0f)
                lineToRelative(1.5f, -3.0f)
                lineToRelative(1.5f, 6.0f)
                lineToRelative(1.5f, -3.0f)
                horizontalLineToRelative(1.0f)
            }
        }.build()
    }

    // Flame / Fat Burning Icon (iOS Apple HIG style)
    val Flame: ImageVector by lazy {
        ImageVector.Builder(
            name = "Flame",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.0f, 2.0f)
                curveToRelative(-0.25f, 2.25f, -1.5f, 4.5f, -3.25f, 6.25f)
                curveTo(7.0f, 10.0f, 6.0f, 12.0f, 6.0f, 14.5f)
                curveToRelative(0.0f, 3.31f, 2.69f, 6.0f, 6.0f, 6.0f)
                reflectiveCurveToRelative(6.0f, -2.69f, 6.0f, -6.0f)
                curveToRelative(0.0f, -3.0f, -2.0f, -5.5f, -3.5f, -7.0f)
                curveToRelative(-0.5f, 1.5f, -1.5f, 2.5f, -2.5f, 3.0f)
                curveToRelative(0.0f, -3.0f, 0.0f, -6.0f, 0.0f, -8.5f)
                close()
            }
        }.build()
    }

    // Moon / Night Sleep Circadian Icon
    val Moon: ImageVector by lazy {
        ImageVector.Builder(
            name = "Moon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.3f, 2.0f)
                curveToRelative(-0.43f, 0.0f, -0.7f, 0.47f, -0.47f, 0.83f)
                curveTo(13.44f, 5.41f, 13.5f, 8.78f, 11.66f, 11.66f)
                curveTo(9.82f, 14.54f, 6.57f, 15.63f, 3.48f, 14.52f)
                curveToRelative(-0.41f, -0.15f, -0.81f, 0.22f, -0.73f, 0.65f)
                curveTo(3.72f, 19.46f, 7.54f, 22.5f, 12.08f, 22.5f)
                curveToRelative(5.77f, 0.0f, 10.42f, -4.68f, 10.42f, -10.42f)
                curveTo(22.5f, 6.84f, 18.06f, 2.3f, 12.3f, 2.0f)
                close()
            }
        }.build()
    }

    // Sun / Daytime Circadian Icon
    val Sun: ImageVector by lazy {
        ImageVector.Builder(
            name = "Sun",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.0f, 7.0f)
                curveToRelative(-2.76f, 0.0f, -5.0f, 2.24f, -5.0f, 5.0f)
                reflectiveCurveToRelative(2.24f, 5.0f, 5.0f, 5.0f)
                reflectiveCurveToRelative(5.0f, -2.24f, 5.0f, -5.0f)
                reflectiveCurveToRelative(-2.24f, -5.0f, -5.0f, -5.0f)
                close()
                moveTo(12.0f, 2.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
                verticalLineToRelative(2.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
                verticalLineTo(3.0f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                close()
                moveTo(12.0f, 18.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
                verticalLineToRelative(2.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                reflectiveCurveToRelative(1.0f, -0.45f, 1.0f, -1.0f)
                verticalLineToRelative(-2.0f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                close()
                moveTo(5.64f, 4.22f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                reflectiveCurveToRelative(-0.39f, 1.02f, 0.0f, 1.41f)
                lineToRelative(1.41f, 1.41f)
                curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
                reflectiveCurveToRelative(0.39f, -1.02f, 0.0f, -1.41f)
                lineTo(5.64f, 4.22f)
                close()
                moveTo(18.36f, 16.95f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                reflectiveCurveToRelative(-0.39f, 1.02f, 0.0f, 1.41f)
                lineToRelative(1.41f, 1.41f)
                curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
                reflectiveCurveToRelative(0.39f, -1.02f, 0.0f, -1.41f)
                lineToRelative(-1.41f, -1.41f)
                close()
                moveTo(2.0f, 12.0f)
                curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
                horizontalLineToRelative(2.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
                reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
                horizontalLineTo(3.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
                close()
                moveTo(18.0f, 12.0f)
                curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
                horizontalLineToRelative(2.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
                reflectiveCurveToRelative(-0.45f, 1.0f, -1.0f, 1.0f)
                horizontalLineToRelative(-2.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
                close()
                moveTo(6.05f, 16.95f)
                lineToRelative(-1.41f, 1.41f)
                curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0.0f, 1.41f)
                curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
                lineToRelative(1.41f, -1.41f)
                curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                close()
                moveTo(18.36f, 5.64f)
                curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                lineToRelative(-1.41f, 1.41f)
                curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0.0f, 1.41f)
                curveToRelative(0.39f, 0.39f, 1.02f, 0.39f, 1.41f, 0.0f)
                lineToRelative(1.41f, -1.41f)
                close()
            }
        }.build()
    }

    // Bolt / Lightning Fasting Energy Icon
    val Bolt: ImageVector by lazy {
        ImageVector.Builder(
            name = "Bolt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(11.0f, 21.0f)
                curveToRelative(-0.45f, 0.0f, -0.83f, -0.3f, -0.95f, -0.74f)
                lineToRelative(-1.5f, -5.51f)
                horizontalLineTo(5.5f)
                curveToRelative(-0.72f, 0.0f, -1.18f, -0.75f, -0.86f, -1.39f)
                lineToRelative(7.0f, -11.0f)
                curveToRelative(0.36f, -0.56f, 1.19f, -0.5f, 1.46f, 0.12f)
                curveToRelative(0.12f, 0.28f, 0.1f, 0.6f, -0.05f, 0.86f)
                lineTo(11.5f, 8.75f)
                horizontalLineToRelative(4.0f)
                curveToRelative(0.72f, 0.0f, 1.18f, 0.75f, 0.86f, 1.39f)
                lineToRelative(-4.5f, 10.5f)
                curveToRelative(-0.19f, 0.23f, -0.49f, 0.36f, -0.86f, 0.36f)
                close()
            }
        }.build()
    }

    // Coffee / Tea Drinks Icon
    val Coffee: ImageVector by lazy {
        ImageVector.Builder(
            name = "Coffee",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(2.0f, 19.0f)
                horizontalLineToRelative(18.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(2.0f)
                close()
                moveTo(20.0f, 3.0f)
                horizontalLineTo(4.0f)
                verticalLineToRelative(10.0f)
                curveToRelative(0.0f, 2.21f, 1.79f, 4.0f, 4.0f, 4.0f)
                horizontalLineToRelative(6.0f)
                curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
                verticalLineToRelative(-3.0f)
                horizontalLineToRelative(2.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(5.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(18.0f, 8.0f)
                verticalLineTo(5.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(3.0f)
                horizontalLineToRelative(-2.0f)
                close()
            }
        }.build()
    }

    // Lifebuoy / Hunger SOS Icon
    val Lifebuoy: ImageVector by lazy {
        ImageVector.Builder(
            name = "Lifebuoy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(12.0f, 16.5f)
                curveToRelative(-2.49f, 0.0f, -4.5f, -2.01f, -4.5f, -4.5f)
                reflectiveCurveToRelative(2.01f, -4.5f, 4.5f, -4.5f)
                reflectiveCurveToRelative(4.5f, 2.01f, 4.5f, 4.5f)
                reflectiveCurveToRelative(-2.01f, 4.5f, -4.5f, 4.5f)
                close()
                moveTo(17.72f, 7.69f)
                lineToRelative(1.88f, -1.88f)
                curveToRelative(1.15f, 1.48f, 1.89f, 3.29f, 2.06f, 5.27f)
                horizontalLineToRelative(-2.67f)
                curveToRelative(-0.17f, -1.26f, -0.63f, -2.42f, -1.27f, -3.39f)
                close()
                moveTo(6.28f, 7.69f)
                curveTo(5.64f, 8.66f, 5.18f, 9.82f, 5.01f, 11.08f)
                horizontalLineTo(2.34f)
                curveToRelative(0.17f, -1.98f, 0.91f, -3.79f, 2.06f, -5.27f)
                lineToRelative(1.88f, 1.88f)
                close()
                moveTo(6.28f, 16.31f)
                lineToRelative(-1.88f, 1.88f)
                curveToRelative(-1.15f, -1.48f, -1.89f, -3.29f, -2.06f, -5.27f)
                horizontalLineToRelative(2.67f)
                curveToRelative(0.17f, 1.26f, 0.63f, 2.42f, 1.27f, 3.39f)
                close()
                moveTo(17.72f, 16.31f)
                curveToRelative(0.64f, -0.97f, 1.1f, -2.13f, 1.27f, -3.39f)
                horizontalLineToRelative(2.67f)
                curveToRelative(-0.17f, 1.98f, -0.91f, 3.79f, -2.06f, 5.27f)
                lineToRelative(-1.88f, -1.88f)
                close()
            }
        }.build()
    }

    // TrendingUp Icon
    val TrendingUp: ImageVector by lazy {
        ImageVector.Builder(
            name = "TrendingUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(16.0f, 6.0f)
                lineToRelative(2.29f, 2.29f)
                lineToRelative(-4.88f, 4.88f)
                lineToRelative(-4.0f, -4.0f)
                lineTo(2.0f, 16.59f)
                lineTo(3.41f, 18.0f)
                lineToRelative(6.0f, -6.0f)
                lineToRelative(4.0f, 4.0f)
                lineToRelative(6.3f, -6.29f)
                lineTo(22.0f, 12.0f)
                verticalLineTo(6.0f)
                horizontalLineToRelative(-6.0f)
                close()
            }
        }.build()
    }

    // Sparkles / Magic / Cellular Icon
    val Sparkles: ImageVector by lazy {
        ImageVector.Builder(
            name = "Sparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12.0f, 2.0f)
                lineToRelative(2.0f, 5.0f)
                lineToRelative(5.0f, 2.0f)
                lineToRelative(-5.0f, 2.0f)
                lineToRelative(-2.0f, 5.0f)
                lineToRelative(-2.0f, -5.0f)
                lineToRelative(-5.0f, -2.0f)
                lineToRelative(5.0f, -2.0f)
                close()
                moveTo(19.0f, 15.0f)
                lineToRelative(1.25f, 2.5f)
                lineToRelative(2.5f, 1.25f)
                lineToRelative(-2.5f, 1.25f)
                lineToRelative(-1.25f, 2.5f)
                lineToRelative(-1.25f, -2.5f)
                lineToRelative(-2.5f, -1.25f)
                lineToRelative(2.5f, -1.25f)
                close()
            }
        }.build()
    }
}
