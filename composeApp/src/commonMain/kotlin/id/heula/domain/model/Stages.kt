package id.heula.domain.model

data class Stage(
    val key: String,
    val label: String,
    val description: String,
    val whatHappens: String,
    val tip: String,
    val minHours: Double,
    val iconKey: String,
)

val STAGES = listOf(
    Stage(
        key = "pencernaan",
        label = "Pencernaan",
        description = "Tubuh mencerna makanan terakhir",
        whatHappens = "Insulin tinggi, tubuh memproses glukosa dari makanan yang baru dimakan.",
        tip = "Minum air putih yang cukup untuk membantu proses pencernaan.",
        minHours = 0.0,
        iconKey = "stage_pencernaan",
    ),
    Stage(
        key = "bakar_lemak",
        label = "Bakar Lemak",
        description = "Mulai membakar cadangan lemak",
        whatHappens = "Glukosa darah turun, tubuh beralih ke pembakaran lemak sebagai sumber energi.",
        tip = "Ini waktu yang bagus untuk olahraga ringan agar pembakaran lemak makin optimal.",
        minHours = 4.0,
        iconKey = "stage_bakar_lemak",
    ),
    Stage(
        key = "ketosis",
        label = "Ketosis Awal",
        description = "Keton mulai diproduksi",
        whatHappens = "Hati mengubah asam lemak menjadi keton sebagai bahan bakar otak dan tubuh.",
        tip = "Jaga hidrasi dan elektrolit agar tidak merasa lemas.",
        minHours = 12.0,
        iconKey = "stage_ketosis",
    ),
    Stage(
        key = "autophagy",
        label = "Autophagy",
        description = "Sel mulai membersihkan diri",
        whatHappens = "Tubuh mulai mendaur ulang sel-sel rusak, proses detoksifikasi alami dimulai.",
        tip = "Hindari kafein berlebihan, fokus pada istirahat yang cukup.",
        minHours = 16.0,
        iconKey = "stage_autophagy",
    ),
    Stage(
        key = "deep_fast",
        label = "Deep Fast",
        description = "Puasa dalam, manfaat maksimal",
        whatHappens = "Autophagy berjalan penuh, regenerasi sel meningkat, sensitivitas insulin sangat membaik.",
        tip = "Jaga tekanan darah dan konsultasikan dengan dokter jika ini pertama kalinya.",
        minHours = 24.0,
        iconKey = "stage_deep_fast",
    ),
)

fun stageForElapsed(elapsedMs: Long): Stage {
    val hours = elapsedMs / 3_600_000.0
    return STAGES.lastOrNull { hours >= it.minHours } ?: STAGES.first()
}

enum class TimelineState { PASSED, CURRENT, UPCOMING }

data class TimelineEntry(val stage: Stage, val state: TimelineState)

fun timelineForElapsed(elapsedMs: Long): List<TimelineEntry> {
    val hours = elapsedMs / 3_600_000.0
    return STAGES.map { stage ->
        val state = when {
            hours >= (STAGES.getOrNull(STAGES.indexOf(stage) + 1)?.minHours ?: Double.MAX_VALUE) -> TimelineState.PASSED
            hours >= stage.minHours -> TimelineState.CURRENT
            else -> TimelineState.UPCOMING
        }
        TimelineEntry(stage, state)
    }
}
