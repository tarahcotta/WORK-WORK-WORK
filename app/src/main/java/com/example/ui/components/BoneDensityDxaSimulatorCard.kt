package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Interactive Clinical DXA & T-Score Bone Density Simulator
 * Built on LIFTMOR & OPTIMA-Ex clinical resistance training research data.
 */
@Composable
fun BoneDensityDxaSimulatorCard(
    modifier: Modifier = Modifier,
    initialTScore: Float = -1.8f
) {
    var weeksOfTraining by remember { mutableFloatStateOf(24f) }
    var trainingFrequencyPerWeek by remember { mutableIntStateOf(2) }
    var isSedentaryComparison by remember { mutableStateOf(false) }
    var showClinicalDetails by remember { mutableStateOf(false) }

    // Dynamic DXA calculation models
    val weeks = weeksOfTraining.toInt()
    
    // Baseline model: Postmenopausal natural bone loss is ~ -1.2% per year (~ -0.023% per week)
    // LIFTMOR trial: High-intensity axial resistance training produces ~ +2.9% Lumbar & +2.4% Femoral Neck BMD over 8 months (32 weeks)
    val weeklyResistanceGainPercent = when (trainingFrequencyPerWeek) {
        1 -> 0.055f // Minimal osteogenic adaptation
        2 -> 0.092f // Clinical sweet spot
        else -> 0.115f // Accelerated bone remodeling
    }

    val naturalDeclinePercent = (weeks * 0.025f)
    val interventionGainPercent = if (isSedentaryComparison) {
        -naturalDeclinePercent
    } else {
        (weeks * weeklyResistanceGainPercent)
    }

    // T-Score delta: 1 SD in DXA T-Score is approx 10-12% BMD change
    val tScoreDelta = (interventionGainPercent / 10.5f)
    val simulatedTScore = initialTScore + tScoreDelta

    val statusLabel = when {
        simulatedTScore >= -1.0f -> "Normal Bone Density"
        simulatedTScore >= -2.5f -> "Osteopenia (Low Mass)"
        else -> "Osteoporosis"
    }

    val statusColor = when {
        simulatedTScore >= -1.0f -> Color(0xFF00C853)
        simulatedTScore >= -2.5f -> Color(0xFFF57C00)
        else -> Color(0xFFD32F2F)
    }

    val dxaAccessibleSummary = remember(simulatedTScore, initialTScore, interventionGainPercent, weeks, trainingFrequencyPerWeek, isSedentaryComparison) {
        if (isSedentaryComparison) {
            "Simulated sedentary bone mineral density trend over $weeks weeks. Projected T-score is ${String.format(Locale.getDefault(), "%.2f", simulatedTScore)} from baseline ${String.format(Locale.getDefault(), "%.2f", initialTScore)}, representing a net decline of ${String.format(Locale.getDefault(), "%.1f", -interventionGainPercent)} percent due to natural age-related resorption."
        } else {
            "Simulated LIFTMOR high-intensity resistance training bone density projection over $weeks weeks with $trainingFrequencyPerWeek sessions per week. Projected T-score improves to ${String.format(Locale.getDefault(), "%.2f", simulatedTScore)} from baseline ${String.format(Locale.getDefault(), "%.2f", initialTScore)} ($statusLabel), with an estimated bone mineral density net increase of ${String.format(Locale.getDefault(), "%.1f", interventionGainPercent)} percent."
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                heading()
                contentDescription = dxaAccessibleSummary
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. HEADER & SCIENCE CONTEXT
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Clinical Science Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "DXA Bone Density Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "LIFTMOR Clinical Trial Remodeling Model",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = { showClinicalDetails = !showClinicalDetails },
                    modifier = Modifier
                        .size(36.dp)
                        .semantics {
                            contentDescription = if (showClinicalDetails) "Hide clinical trial details" else "Show clinical trial details"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expandable Clinical Details Box
            AnimatedVisibility(visible = showClinicalDetails) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Clinical Foundation:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Based on the landmark LIFTMOR trial (Watson et al., JBMR 2018), high-intensity resistance training at RPE 8+ reversed bone mineral density decline in postmenopausal women with osteopenia/osteoporosis, improving femoral neck & lumbar spine T-scores.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ==========================================
            // 2. HERO METRIC DISPLAY (Current Base -> Stimulus -> Projected)
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("t_score_hero_display"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Projected T-Score comparison. Current baseline is ${String.format(Locale.getDefault(), "%.2f", initialTScore)}. Estimated BMD net change is ${if (interventionGainPercent >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.1f", interventionGainPercent)} percent. Projected T-Score is ${String.format(Locale.getDefault(), "%.2f", simulatedTScore)}, status: $statusLabel."
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PROJECTED T-SCORE SIMULATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current Baseline Column
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Current Base",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.2f", initialTScore),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Net Change Badge (Center Pill)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (interventionGainPercent >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            border = BorderStroke(1.dp, if (interventionGainPercent >= 0) Color(0xFF4CAF50) else Color(0xFFEF5350))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (interventionGainPercent >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${if (interventionGainPercent >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.1f", interventionGainPercent)}% BMD",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        // Projected Score Column
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Projected Score",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.2f", simulatedTScore),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = statusColor
                            )
                        }
                    }

                    // Status Badge with High Contrast
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.5.dp, statusColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CLASSIFICATION",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 3. INTERACTIVE SIMULATION PARAMETERS
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulation_parameters_card"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Row with Tune Icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Simulation Parameters",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Projection horizon and weekly mechanical loading frequency",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Timeline Slider Section
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Training Duration",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "$weeks Weeks (${String.format(Locale.US, "%.1f", weeks / 4.33f)} Mos)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        // Smooth slider with high-visibility track
                        Slider(
                            value = weeksOfTraining,
                            onValueChange = { weeksOfTraining = it.roundToInt().toFloat() },
                            valueRange = 4f..104f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulation_duration_slider")
                                .semantics {
                                    contentDescription = "Simulated training duration slider"
                                    stateDescription = "$weeks weeks"
                                }
                        )

                        // Timeline Horizon Markers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "4 wks (1 mo)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "52 wks (1 yr)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "104 wks (2 yrs)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Frequency & Comparison Selectors
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Weekly Protocol Frequency",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Active Training Frequency Options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val frequencies = listOf(
                                Triple(1, "1x / wk", "Maintenance"),
                                Triple(2, "2x / wk", "Optimal ⭐"),
                                Triple(3, "3x / wk", "Accelerated")
                            )

                            frequencies.forEach { (freq, title, badge) ->
                                val isSelected = !isSedentaryComparison && trainingFrequencyPerWeek == freq
                                val containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                                val contentColor = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                                val borderColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            isSedentaryComparison = false
                                            trainingFrequencyPerWeek = freq
                                        }
                                        .testTag("freq_option_${freq}x")
                                        .semantics {
                                            stateDescription = if (isSelected) "$title selected ($badge)" else "Select $title"
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    color = containerColor,
                                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                color = contentColor,
                                                maxLines = 1,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = badge,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Dedicated Sedentary Control Baseline Toggle Card
                        val isSedentary = isSedentaryComparison
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    isSedentaryComparison = !isSedentaryComparison
                                }
                                .testTag("sedentary_control_card")
                                .semantics {
                                    stateDescription = if (isSedentary) "Sedentary comparison mode active" else "Switch to sedentary comparison mode"
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSedentary) {
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            },
                            border = BorderStroke(
                                if (isSedentary) 1.5.dp else 1.dp,
                                if (isSedentary) MaterialTheme.colorScheme.error.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSedentary) Icons.AutoMirrored.Filled.TrendingDown else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isSedentary) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Sedentary Control Baseline",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSedentary) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "0x / week • Natural age-related bone resorption model",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = isSedentary,
                                    onCheckedChange = { isSedentaryComparison = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onError,
                                        checkedTrackColor = MaterialTheme.colorScheme.error,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 4. CLINICAL TAKEAWAY SUMMARY
            // ==========================================
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSedentaryComparison) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                },
                border = BorderStroke(
                    1.dp,
                    if (isSedentaryComparison) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clinical_takeaway_summary_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSedentaryComparison) {
                            MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSedentaryComparison) Icons.AutoMirrored.Filled.TrendingDown else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isSedentaryComparison) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isSedentaryComparison) {
                            "Without axial loading stimulus, age-related bone resorption outpaces formation by ~1-2% annually."
                        } else {
                            "Consistent ${trainingFrequencyPerWeek}x/week training signals bone osteoblasts to mineralize bone matrix, offsetting age-related loss."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

