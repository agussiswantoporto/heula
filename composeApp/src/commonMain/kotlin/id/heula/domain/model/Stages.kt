package id.heula.domain.model

data class Stage(
    val key: String,
    val label: String,
    val hourRange: String,
    val description: String,
    val whatHappens: String,
    val tip: String,
    val minHours: Double,
    val maxHours: Double?,
    val iconKey: String = "",
)

val STAGES = listOf(
    Stage(
        key = "pencernaan",
        label = "Pencernaan",
        hourRange = "0 - 4 Jam",
        description = "Lagi nyerna makanan terakhir",
        whatHappens = "Insulin lagi naik buat ngolah glukosa dari makananmu. Energi tubuh masih fokus penuh di lambung dan usus.",
        tip = "Minum air putih secukupnya ya, biar lambungmu nyaman dan pencernaan lancar.",
        minHours = 0.0,
        maxHours = 4.0,
        iconKey = "stage_pencernaan",
    ),
    Stage(
        key = "bakar_lemak",
        label = "Bakar Lemak",
        hourRange = "4 - 12 Jam",
        description = "Mulai switch bakar cadangan lemak",
        whatHappens = "Gula darah mulai turun normal. Tubuhmu kehabisan glukosa instan dan mulai manggil cadangan lemak buat dijadiin energi harian!",
        tip = "Waktu pas banget buat jalan santai atau beraktivitas ringan, lemak bakal lumer lebih cepet!",
        minHours = 4.0,
        maxHours = 12.0,
        iconKey = "stage_bakar_lemak",
    ),
    Stage(
        key = "ketosis",
        label = "Ketosis Awal",
        hourRange = "12 - 16 Jam",
        description = "Pabrik keton mulai aktif ngebul",
        whatHappens = "Cadangan glikogen di hati hampir habis. Hati mulai memproduksi keton—bahan bakar super bersih buat otak dan bikin pikiran makin fokus.",
        tip = "Kalo kerasa agak lemes atau pusing, tambahin sejumput garam ke air minummu buat jaga elektrolit ya.",
        minHours = 12.0,
        maxHours = 16.0,
        iconKey = "stage_ketosis",
    ),
    Stage(
        key = "autophagy",
        label = "Autophagy",
        hourRange = "16 - 24 Jam",
        description = "Detoks & daur ulang sel otomatis",
        whatHappens = "Mode bersih-bersih sel aktif! Tubuh mendaur ulang sel-sel tua dan rusak jadi komponen baru. Ini rahasia awet muda dan perbaikan imun alami!",
        tip = "Hindari minuman manis berkalori ya. Tetap rileks, tidur cukup, biarin tubuhmu kerja magisnya.",
        minHours = 16.0,
        maxHours = 24.0,
        iconKey = "stage_autophagy",
    ),
    Stage(
        key = "deep_fast",
        label = "Deep Fast",
        hourRange = "24+ Jam",
        description = "Level puncak puasa, manfaat maksimal",
        whatHappens = "Autophagy berjalan di puncak tertinggi, regenerasi sel meningkat pesat, dan sensitivitas insulin ter-reset sempurna!",
        tip = "Dengerin sinyal tubuhmu ya. Kalo ngerasa nggak enak badan jangan dipaksa, buka puasa perlahan dengan sup atau makanan lembut.",
        minHours = 24.0,
        maxHours = null,
        iconKey = "stage_deep_fast",
    ),
)

fun stageForElapsed(elapsedMs: Long): Stage {
    val hours = elapsedMs / 3_600_000.0
    return STAGES.lastOrNull { hours >= it.minHours } ?: STAGES.first()
}

data class NextStageInfo(
    val stage: Stage,
    val remainingHours: Double,
    val remainingFormatted: String,
)

fun nextStageForElapsed(elapsedMs: Long): NextStageInfo? {
    val hours = elapsedMs / 3_600_000.0
    val currentIndex = STAGES.indexOfLast { hours >= it.minHours }.coerceAtLeast(0)
    val nextStage = STAGES.getOrNull(currentIndex + 1) ?: return null
    val remainingHours = (nextStage.minHours - hours).coerceAtLeast(0.0)
    val remMinutesTotal = (remainingHours * 60).toInt()
    val remH = remMinutesTotal / 60
    val remM = remMinutesTotal % 60
    val formatted = when {
        remH > 0 && remM > 0 -> "${remH}j ${remM}m"
        remH > 0 -> "${remH} jam"
        else -> "${remM} menit"
    }
    return NextStageInfo(
        stage = nextStage,
        remainingHours = remainingHours,
        remainingFormatted = formatted,
    )
}

enum class TimelineState { PASSED, CURRENT, UPCOMING }

data class TimelineEntry(
    val stage: Stage,
    val state: TimelineState,
    val progressInStage: Float = 0f,
)

fun timelineForElapsed(elapsedMs: Long): List<TimelineEntry> {
    val hours = elapsedMs / 3_600_000.0
    return STAGES.mapIndexed { index, stage ->
        val nextMinHours = STAGES.getOrNull(index + 1)?.minHours ?: Double.MAX_VALUE
        val state = when {
            hours >= nextMinHours -> TimelineState.PASSED
            hours >= stage.minHours -> TimelineState.CURRENT
            else -> TimelineState.UPCOMING
        }
        val progress = when (state) {
            TimelineState.PASSED -> 1f
            TimelineState.UPCOMING -> 0f
            TimelineState.CURRENT -> {
                val span = nextMinHours - stage.minHours
                if (span > 0 && nextMinHours != Double.MAX_VALUE) {
                    ((hours - stage.minHours) / span).toFloat().coerceIn(0f, 1f)
                } else {
                    1f
                }
            }
        }
        TimelineEntry(stage, state, progress)
    }
}
