package id.heula.domain.model

val METHODS = mapOf(
    MethodId.FOURTEEN_TEN to FastingMethod(
        id = MethodId.FOURTEEN_TEN,
        label = "14:10",
        fastingHours = 14.0,
        eatingHours = 10.0,
        tagline = "14 Jam Puasa • 10 Jam Makan (Santai & Ramah Pemula)",
    ),
    MethodId.SIXTEEN_EIGHT to FastingMethod(
        id = MethodId.SIXTEEN_EIGHT,
        label = "16:8",
        fastingHours = 16.0,
        eatingHours = 8.0,
        tagline = "16 Jam Puasa • 8 Jam Makan (Paling Favorit & Efektif)",
    ),
    MethodId.EIGHTEEN_SIX to FastingMethod(
        id = MethodId.EIGHTEEN_SIX,
        label = "18:6",
        fastingHours = 18.0,
        eatingHours = 6.0,
        tagline = "18 Jam Puasa • 6 Jam Makan (Bakar Lemak Lebih Ngebut)",
    ),
    MethodId.TWENTY_FOUR to FastingMethod(
        id = MethodId.TWENTY_FOUR,
        label = "24 Jam",
        fastingHours = 24.0,
        eatingHours = 0.0,
        tagline = "24 Jam Seharian Penuh (Detoks Sel Mendalam)",
    ),
    MethodId.OMAD to FastingMethod(
        id = MethodId.OMAD,
        label = "OMAD (23:1)",
        fastingHours = 23.0,
        eatingHours = 1.0,
        tagline = "23 Jam Puasa • 1 Jam Makan (One Meal A Day)",
    ),
    MethodId.CUSTOM to FastingMethod(
        id = MethodId.CUSTOM,
        label = "Kustom",
        fastingHours = 16.0,
        eatingHours = 8.0,
        tagline = "Bebas Atur Jam Puasa & Jendela Makanmu Sendiri",
    ),
)

val METHOD_ORDER = listOf(
    MethodId.FOURTEEN_TEN,
    MethodId.SIXTEEN_EIGHT,
    MethodId.EIGHTEEN_SIX,
    MethodId.TWENTY_FOUR,
    MethodId.OMAD,
    MethodId.CUSTOM,
)
