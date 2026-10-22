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
}
