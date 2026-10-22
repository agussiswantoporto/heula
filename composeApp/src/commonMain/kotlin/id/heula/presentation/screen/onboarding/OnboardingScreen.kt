package id.heula.presentation.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.heula.domain.model.*
import id.heula.presentation.component.ui.ButtonVariant
import id.heula.presentation.component.ui.HeulaBadge
import id.heula.presentation.component.ui.HeulaButton
import id.heula.presentation.component.ui.HeulaCard
import id.heula.presentation.store.AppStore
import id.heula.presentation.theme.BrandPrimary

@Composable
fun OnboardingScreen(
    store: AppStore,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableStateOf(1) }

    var selectedGoal by remember { mutableStateOf(Goal.LOSE_WEIGHT) }
    var exercises by remember { mutableStateOf(true) }
    var intensity by remember { mutableStateOf(Intensity.MODERATE) }
    var heightCmStr by remember { mutableStateOf("170") }
    var weightKgStr by remember { mutableStateOf("68") }
    var targetWeightKgStr by remember { mutableStateOf("62") }
    var selectedMethod by remember { mutableStateOf(MethodId.SIXTEEN_EIGHT) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth >= 768.dp
        val horizontalPadding = if (isWide) 120.dp else 24.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header / Step progress
                Text(
                    text = "Langkah $step dari 4",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimary,
                )
                Spacer(modifier = Modifier.height(16.dp))

                when (step) {
                    1 -> {
                        Text(
                            text = "Apa tujuan puasa kamu?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Heula akan menyesuaikan rekomendasi dengan tujuanmu.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        val goals = listOf(
                            Goal.LOSE_WEIGHT to "Turunkan Berat Badan",
                            Goal.MAINTAIN to "Jaga Berat Badan Stabil",
                            Goal.FEEL_BETTER to "Tubuh Lebih Segar & Berenergi",
                            Goal.METABOLIC to "Tingkatkan Kesehatan Metabolik",
                        )

                        goals.forEach { (g, label) ->
                            val isSelected = selectedGoal == g
                            HeulaCard(
                                onClick = { selectedGoal = g },
                                borderColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(vertical = 4.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Dipilih",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        Text(
                            text = "Aktivitas Olahraga",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Apakah kamu rutin berolahraga?",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Button(
                                onClick = { exercises = true },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (exercises) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (exercises) Color.White else MaterialTheme.colorScheme.onSurface,
                                ),
                            ) {
                                Text("Ya, Olahraga")
                            }
                            Button(
                                onClick = { exercises = false },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!exercises) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (!exercises) Color.White else MaterialTheme.colorScheme.onSurface,
                                ),
                            ) {
                                Text("Jarang / Tidak")
                            }
                        }

                        if (exercises) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("Tingkat Intensitas:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            listOf(
                                Intensity.LIGHT to "Ringan (Jalan kaki, yoga santai)",
                                Intensity.MODERATE to "Sedang (Jogging, gym rutin 3-4x)",
                                Intensity.HEAVY to "Berat (Lari maraton, angkat beban intensif)",
                            ).forEach { (inte, label) ->
                                val isSelected = intensity == inte
                                HeulaCard(
                                    onClick = { intensity = inte },
                                    borderColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        Text(
                            text = "Tinggi, Berat & Target",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hitung BMI dan tentukan tujuan berat badan impianmu.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = heightCmStr,
                            onValueChange = { heightCmStr = it },
                            label = { Text("Tinggi Badan (cm)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = weightKgStr,
                            onValueChange = { weightKgStr = it },
                            label = { Text("Berat Badan Saat Ini (kg)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = targetWeightKgStr,
                            onValueChange = { targetWeightKgStr = it },
                            label = { Text("Target Berat Badan (kg)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        val h = heightCmStr.toDoubleOrNull() ?: 170.0
                        val w = weightKgStr.toDoubleOrNull() ?: 68.0
                        val targetW = targetWeightKgStr.toDoubleOrNull() ?: w
                        val bmi = w / ((h / 100.0) * (h / 100.0))
                        val deltaW = w - targetW

                        Spacer(modifier = Modifier.height(16.dp))
                        HeulaCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "BMI: ${(bmi * 10).toInt() / 10.0}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                                if (deltaW > 0) {
                                    Text(
                                        text = "Target: -${(deltaW * 10).toInt() / 10.0} kg",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = BrandPrimary,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when {
                                    bmi < 18.5 -> "Di bawah ideal. Disarankan metode fleksibel 14:10."
                                    bmi < 25.0 -> "Ideal dan seimbang. Sangat cocok untuk metode 16:8."
                                    else -> "Sedikit di atas ideal. Puasa berkala akan mempercepat metabolisme."
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    4 -> {
                        val hVal = heightCmStr.toDoubleOrNull() ?: 170.0
                        val wVal = weightKgStr.toDoubleOrNull() ?: 68.0
                        val bmiVal = wVal / ((hVal / 100.0) * (hVal / 100.0))
                        val recommendedMethod = when {
                            bmiVal < 18.5 || selectedGoal == Goal.FEEL_BETTER || !exercises -> MethodId.FOURTEEN_TEN
                            exercises && intensity == Intensity.HEAVY -> MethodId.EIGHTEEN_SIX
                            else -> MethodId.SIXTEEN_EIGHT
                        }

                        Text(
                            text = "Rekomendasi Metode",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Berdasarkan profil BMI dan rutinitasmu, ini rekomendasi terbaik:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        METHOD_ORDER.forEach { methodId ->
                            val m = METHODS[methodId] ?: return@forEach
                            val isSelected = selectedMethod == methodId
                            val isRecommended = methodId == recommendedMethod

                            HeulaCard(
                                onClick = { selectedMethod = methodId },
                                borderColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(vertical = 4.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = m.label,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface,
                                            )
                                            if (isRecommended) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                HeulaBadge(text = "Rekomendasi Profil")
                                            }
                                        }
                                        Text(
                                            text = m.tagline,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Dipilih",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (step > 1) {
                    HeulaButton(
                        text = "Kembali",
                        onClick = { step-- },
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f),
                    )
                }
                HeulaButton(
                    text = if (step == 4) "Mulai Sekarang" else "Lanjut",
                    onClick = {
                        if (step < 4) {
                            if (step == 3) {
                                val hVal = heightCmStr.toDoubleOrNull() ?: 170.0
                                val wVal = weightKgStr.toDoubleOrNull() ?: 68.0
                                val bmiVal = wVal / ((hVal / 100.0) * (hVal / 100.0))
                                selectedMethod = when {
                                    bmiVal < 18.5 || selectedGoal == Goal.FEEL_BETTER || !exercises -> MethodId.FOURTEEN_TEN
                                    exercises && intensity == Intensity.HEAVY -> MethodId.EIGHTEEN_SIX
                                    else -> MethodId.SIXTEEN_EIGHT
                                }
                            }
                            step++
                        } else {
                            val answers = ProfilingAnswers(
                                goal = selectedGoal,
                                exercises = exercises,
                                intensity = if (exercises) intensity else null,
                                heightCm = heightCmStr.toDoubleOrNull() ?: 170.0,
                                weightKg = weightKgStr.toDoubleOrNull() ?: 68.0,
                                targetWeightKg = targetWeightKgStr.toDoubleOrNull(),
                            )
                            store.completeOnboarding(answers, selectedMethod)
                            onFinish()
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
