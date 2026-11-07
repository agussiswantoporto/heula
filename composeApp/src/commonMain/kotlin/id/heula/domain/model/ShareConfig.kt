package id.heula.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ShareTemplate(val displayName: String, val description: String) {
    MINIMALIST("Minimalist", "Tipografi besar, garis bersih & minimalis"),
    MODERN_GRADIENT("Modern Gradient", "Aksen gradasi oranye Strava & sunset enerjik"),
    DARK_MODE("Dark Mode", "Elemen gelap kontras tinggi untuk foto cerah"),
    GLASS_STYLE("Glass Style", "Pill transparan dengan efek frosted glass"),
    TYPOGRAPHY("Typography", "Fokus ekstrim pada angka durasi jam puasa"),
    EDITORIAL("Editorial", "Desain majalah kesehatan modern dan berkelas"),
    NEON("Neon", "Aksen warna cyber lime & neon oranye berani"),
    CLEAN_WHITE("Clean White", "Teks putih murni dengan bayangan halus terbaca di foto apa pun"),
}

@Serializable
enum class ShareAspectRatio(val label: String, val width: Int, val height: Int, val ratio: Float) {
    STORY("Story 9:16", 1080, 1920, 9f / 16f),
    SQUARE("Feed 1:1", 1080, 1080, 1f),
}

@Serializable
enum class ShareTextColor(val label: String, val hex: Long) {
    WHITE("Putih", 0xFFFFFFFF),
    DARK("Hitam / Gelap", 0xFF1C1C1E),
    STRAVA_ORANGE("Oranye Strava", 0xFFFC5200),
    EMERALD("Emerald Green", 0xFF10B981),
    CYBER_LIME("Cyber Lime", 0xFFCCFF00),
}

@Serializable
data class ShareCustomization(
    val template: ShareTemplate = ShareTemplate.MODERN_GRADIENT,
    val aspectRatio: ShareAspectRatio = ShareAspectRatio.STORY,
    val textColor: ShareTextColor = ShareTextColor.WHITE,
    val showDate: Boolean = true,
    val showTarget: Boolean = true,
    val showTimes: Boolean = true,
    val showBranding: Boolean = true,
)
