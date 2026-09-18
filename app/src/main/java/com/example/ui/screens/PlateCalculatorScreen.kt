package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomFlowRow
import com.example.ui.theme.PlateTokens
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.TelemetryNumeralStyle
import kotlin.math.max

enum class EquipmentCategory(val label: String) {
    OLYMPIC_BARBELL("Olympic 2\""),
    STANDARD_BARBELL("Home 1\" Bar"),
    DUMBBELLS("Dumbbell Pair"),
    MACHINE_STACK("Pin Machine")
}

data class EquipmentOption(
    val name: String,
    val tareWeightLbs: Double,
    val category: EquipmentCategory,
    val description: String
)

data class PlateCount(
    val plateWeight: Double,
    val count: Int,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlateCalculatorScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var selectedCategory by remember { mutableStateOf(EquipmentCategory.OLYMPIC_BARBELL) }
    var targetWeightText by remember { mutableStateOf("135") }
    val targetWeight = targetWeightText.toDoubleOrNull() ?: 135.0

    val equipmentList = remember {
        listOf(
            // Olympic
            EquipmentOption("Standard Olympic Bar (45 lbs / 20.4 kg)", 45.0, EquipmentCategory.OLYMPIC_BARBELL, "Full-size 7ft Olympic sleeve bar"),
            EquipmentOption("Women's Olympic Bar (33 lbs / 15 kg)", 33.0, EquipmentCategory.OLYMPIC_BARBELL, "25mm grip diameter competition bar"),
            EquipmentOption("Aluminum Training Bar (25 lbs)", 25.0, EquipmentCategory.OLYMPIC_BARBELL, "Lightweight technique progression bar"),
            EquipmentOption("Rehab / Technique Bar (15 lbs)", 15.0, EquipmentCategory.OLYMPIC_BARBELL, "Ultra-light bone loading entry bar"),
            EquipmentOption("EZ Curl Bar (25 lbs)", 25.0, EquipmentCategory.OLYMPIC_BARBELL, "Cambered joint-friendly arm bar"),

            // Standard 1-inch Home
            EquipmentOption("Standard 1-Inch Bar (15 lbs)", 15.0, EquipmentCategory.STANDARD_BARBELL, "Common home gym threaded or collar bar"),
            EquipmentOption("Short 1-Inch Home Bar (10 lbs)", 10.0, EquipmentCategory.STANDARD_BARBELL, "5ft standard home barbell"),

            // Dumbbells
            EquipmentOption("Dumbbell Pair (Load per Hand / 0 lb Tare)", 0.0, EquipmentCategory.DUMBBELLS, "Calculates single or paired dumbbell load"),
            EquipmentOption("Adjustable DB Handles (5 lbs each)", 10.0, EquipmentCategory.DUMBBELLS, "Threaded metal dumbbell handles (pair)"),

            // Machine / Stack
            EquipmentOption("Selectorized Pin Stack (0 lb Tare)", 0.0, EquipmentCategory.MACHINE_STACK, "Cable & leg press weight stack increments")
        )
    }

    val filteredEquipment = remember(selectedCategory) {
        equipmentList.filter { it.category == selectedCategory }
    }

    var selectedEquipment by remember { mutableStateOf(equipmentList[0]) }

    // Synchronize selected equipment when category tab changes
    LaunchedEffect(selectedCategory) {
        if (selectedEquipment.category != selectedCategory) {
            selectedEquipment = filteredEquipment.firstOrNull() ?: equipmentList[0]
            if (selectedCategory == EquipmentCategory.DUMBBELLS && targetWeightText == "135") {
                targetWeightText = "35" // Friendly default for dumbbells
            } else if (selectedCategory == EquipmentCategory.OLYMPIC_BARBELL && targetWeightText == "35") {
                targetWeightText = "135"
            }
        }
    }

    // Available Plates based on Equipment Category
    val availablePlates = remember(selectedCategory) {
        when (selectedCategory) {
            EquipmentCategory.OLYMPIC_BARBELL -> listOf(
                Triple(45.0, "45 lbs", PlateTokens.PlateRed),
                Triple(35.0, "35 lbs", PlateTokens.PlateYellow),
                Triple(25.0, "25 lbs", PlateTokens.PlateGreen),
                Triple(10.0, "10 lbs", PlateTokens.PlateBlue),
                Triple(5.0, "5 lbs", PlateTokens.PlatePurple),
                Triple(2.5, "2.5 lbs", PlateTokens.PlateGray),
                Triple(1.25, "1.25 lbs", PlateTokens.PlateDarkGray)
            )
            EquipmentCategory.STANDARD_BARBELL -> listOf(
                Triple(25.0, "25 lbs", PlateTokens.PlateGreen),
                Triple(10.0, "10 lbs", PlateTokens.PlateBlue),
                Triple(5.0, "5 lbs", PlateTokens.PlatePurple),
                Triple(2.5, "2.5 lbs", PlateTokens.PlateGray),
                Triple(1.25, "1.25 lbs", PlateTokens.PlateDarkGray)
            )
            EquipmentCategory.DUMBBELLS -> listOf(
                Triple(10.0, "10 lbs", PlateTokens.PlateBlue),
                Triple(5.0, "5 lbs", PlateTokens.PlatePurple),
                Triple(2.5, "2.5 lbs", PlateTokens.PlateGray),
                Triple(1.25, "1.25 lbs", PlateTokens.PlateDarkGray)
            )
            EquipmentCategory.MACHINE_STACK -> listOf(
                Triple(20.0, "20 lbs Stack Plate", PlateTokens.PlateBlue),
                Triple(10.0, "10 lbs Stack Plate", PlateTokens.PlateGreen),
                Triple(5.0, "5 lbs Add-on Weight", PlateTokens.PlatePurple),
                Triple(2.5, "2.5 lbs Micro-pin", PlateTokens.PlateYellow)
            )
        }
    }

    val netWeight = max(0.0, targetWeight - selectedEquipment.tareWeightLbs)
    val weightPerSide = if (selectedCategory == EquipmentCategory.MACHINE_STACK) netWeight else netWeight / 2.0

    val plateBreakdown = remember(weightPerSide, availablePlates) {
        val result = mutableListOf<PlateCount>()
        var remaining = weightPerSide
        for ((plateWeight, _, color) in availablePlates) {
            val count = (remaining / plateWeight).toInt()
            if (count > 0) {
                result.add(PlateCount(plateWeight, count, color))
                remaining -= count * plateWeight
                remaining = (remaining * 100).toInt() / 100.0
            }
        }
        result
    }

    val unallocatedRemainder = remember(weightPerSide, plateBreakdown) {
        var accounted = 0.0
        for (pc in plateBreakdown) {
            accounted += pc.plateWeight * pc.count
        }
        val diff = weightPerSide - accounted
        (diff * 100).toInt() / 100.0
    }

    val haptic = LocalHapticFeedback.current

    val presets = remember(selectedCategory) {
        when (selectedCategory) {
            EquipmentCategory.DUMBBELLS -> listOf(15.0, 20.0, 25.0, 30.0, 35.0, 45.0, 50.0)
            EquipmentCategory.MACHINE_STACK -> listOf(30.0, 50.0, 70.0, 90.0, 110.0, 130.0, 150.0)
            EquipmentCategory.STANDARD_BARBELL -> listOf(45.0, 65.0, 85.0, 95.0, 115.0, 135.0)
            EquipmentCategory.OLYMPIC_BARBELL -> listOf(65.0, 95.0, 115.0, 135.0, 155.0, 185.0, 225.0)
        }
    }

    // Composable block for Target Weight & Controls
    @Composable
    fun TargetWeightInputSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == EquipmentCategory.DUMBBELLS) "Target Load Per Hand (lbs)" else "Target Total Weight (lbs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${if (selectedEquipment.tareWeightLbs % 1.0 == 0.0) selectedEquipment.tareWeightLbs.toInt() else selectedEquipment.tareWeightLbs} lb Tare",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            val current = targetWeightText.toDoubleOrNull() ?: 135.0
                            val step = if (selectedCategory == EquipmentCategory.DUMBBELLS) 2.5 else 5.0
                            targetWeightText = max(0.0, current - step).let {
                                if (it % 1.0 == 0.0) "${it.toInt()}" else "$it"
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(64.dp)
                            .testTag("decrease_weight_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease Weight")
                    }

                    OutlinedTextField(
                        value = targetWeightText,
                        onValueChange = { input ->
                            val sanitized = input.filter { it.isDigit() || it == '.' }
                            if (sanitized.count { it == '.' } <= 1) {
                                targetWeightText = sanitized
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("target_weight_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TelemetryNumeralStyle.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilledTonalButton(
                        onClick = {
                            val current = targetWeightText.toDoubleOrNull() ?: 135.0
                            val step = if (selectedCategory == EquipmentCategory.DUMBBELLS) 2.5 else 5.0
                            val next = current + step
                            targetWeightText = if (next % 1.0 == 0.0) "${next.toInt()}" else "$next"
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(64.dp)
                            .testTag("increase_weight_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase Weight")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Equipment Options Picker within Selected Category
                Text(
                    text = "Selected Implement / Bar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                CustomFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 6.dp,
                    verticalSpacing = 6.dp
                ) {
                    filteredEquipment.forEach { opt ->
                        val isSelected = selectedEquipment == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedEquipment = opt },
                            label = {
                                Text(
                                    text = opt.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Weight Presets based on Category
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                CustomFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 6.dp,
                    verticalSpacing = 6.dp
                ) {
                    presets.forEach { preset ->
                        val isSelected = targetWeight == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                targetWeightText = if (preset % 1.0 == 0.0) "${preset.toInt()}" else "$preset"
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            label = {
                                Text(
                                    text = "${if (preset % 1.0 == 0.0) preset.toInt() else preset} lbs",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("preset_${preset.toInt()}")
                        )
                    }
                }
            }
        }
    }

    // Composable block for Calculation Results & Visual Barbell
    @Composable
    fun CalculationResultsSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = when (selectedCategory) {
                        EquipmentCategory.DUMBBELLS -> "Plates Per Dumbbell Handle"
                        EquipmentCategory.MACHINE_STACK -> "Pin Stack & Add-on Weight"
                        else -> "Plates Needed Per Side"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (targetWeight < selectedEquipment.tareWeightLbs) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Target load is less than empty implement weight (${selectedEquipment.tareWeightLbs} lbs).",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Suggestions:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val suggestion = if (selectedCategory == EquipmentCategory.OLYMPIC_BARBELL) {
                                equipmentList.find { it.category == EquipmentCategory.OLYMPIC_BARBELL && it.tareWeightLbs <= targetWeight }
                            } else null

                            if (suggestion != null) {
                                FilterChip(
                                    selected = false,
                                    onClick = { selectedEquipment = suggestion },
                                    label = { Text("Use ${suggestion.name}") },
                                    leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                                )
                            }
                            
                            FilterChip(
                                selected = false,
                                onClick = { selectedCategory = EquipmentCategory.DUMBBELLS },
                                label = { Text("Switch to Dumbbells") },
                                leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                            )
                        }
                    }
                } else if (netWeight == 0.0) {
                    Text(
                        text = "Empty Implement (${selectedEquipment.tareWeightLbs} lbs). No plates needed!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    val summarySubtext = when (selectedCategory) {
                        EquipmentCategory.DUMBBELLS -> "Handle (${selectedEquipment.tareWeightLbs} lbs) + ${weightPerSide} lbs plates per hand"
                        EquipmentCategory.MACHINE_STACK -> "Total Stack Resistance: $targetWeight lbs"
                        else -> "Barbell (${selectedEquipment.tareWeightLbs} lbs) + 2 × (${weightPerSide} lbs per side) = $targetWeight lbs total"
                    }
                    Text(
                        text = summarySubtext,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Physical Visual Barbell Sleeve Representation
                    VisualBarbell(
                        plateBreakdown = plateBreakdown,
                        barWeightLbs = selectedEquipment.tareWeightLbs,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Explicit text list of plates needed with total weight contribution
                    plateBreakdown.forEach { pc ->
                        val countLabel = if (selectedCategory == EquipmentCategory.MACHINE_STACK) "total" else "per side"
                        val perSideContrib = pc.plateWeight * pc.count
                        val totalContrib = if (selectedCategory == EquipmentCategory.MACHINE_STACK) perSideContrib else perSideContrib * 2.0
                        val chipTextColor = PlateTokens.textColorForPlate(pc.plateWeight, pc.color)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(pc.color, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (pc.plateWeight % 1.0 == 0.0) "${pc.plateWeight.toInt()}" else "${pc.plateWeight}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = chipTextColor
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "${if (pc.plateWeight % 1.0 == 0.0) pc.plateWeight.toInt() else pc.plateWeight} lbs Plate",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (selectedCategory != EquipmentCategory.MACHINE_STACK) {
                                            Text(
                                                text = "${perSideContrib} lbs/side • ${totalContrib.toInt()} lbs total",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "× ${pc.count} $countLabel",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (unallocatedRemainder > 0.0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Note: $unallocatedRemainder lbs unallocated per side. Micro-plates or 1.25 lb fractional collars recommended.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 840.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (isWide) 1100.dp else 680.dp)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card with Back Navigation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plate_calculator_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Navigation",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(
                                text = "Equipment & Plate Calculator",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Load calculation for Olympic, home 1\" bars, dumbbells & stacks",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Equipment Category Tab Selector
            ScrollableTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                EquipmentCategory.values().forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat.label,
                                fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (selectedCategory == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isWide) {
                // Responsive Two-Pane Layout on Tablets & Expanded Screens
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TargetWeightInputSection()
                    }
                    Box(modifier = Modifier.weight(1.1f)) {
                        CalculationResultsSection()
                    }
                }
            } else {
                // Standard Single-Column Stack for Compact Screens
                TargetWeightInputSection()
                Spacer(modifier = Modifier.height(16.dp))
                CalculationResultsSection()
            }

            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

/**
 * VisualBarbell renders an authentic Olympic barbell sleeve, inner collar flange,
 * steel sleeve loading pin, stacked weight plates with IPF/Olympic color coding,
 * fractional plate accuracy, and outside quick-release locking collar clamp.
 */
@Composable
fun VisualBarbell(
    plateBreakdown: List<PlateCount>,
    modifier: Modifier = Modifier,
    barWeightLbs: Double = 45.0
) {
    val barHeight = 16.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 1. Center Shaft (Machined steel with subtle knurling tone)
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(barHeight)
                    .background(PlateTokens.BarbellShaftDark, RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
            )

            // 2. Inner Collar Stop Flange (Thicker disk that stops plates from sliding inward)
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(38.dp)
                    .background(PlateTokens.CollarFlangeSteel, RoundedCornerShape(2.dp))
                    .border(1.dp, PlateTokens.BarbellShaftDark.copy(alpha = 0.5f), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (barWeightLbs % 1.0 == 0.0) "${barWeightLbs.toInt()}" else "$barWeightLbs",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SpaceGrotesk
                )
            }

            // 3. Loading Sleeve Area with Stacked Plates
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                // Steel loading pin cylinder
                val totalPlatesCount = plateBreakdown.sumOf { it.count }
                val minSleeveWidth = 140.dp
                val dynamicSleeveWidth = maxOf(minSleeveWidth, (totalPlatesCount * 22 + 40).dp)

                Box(
                    modifier = Modifier
                        .width(dynamicSleeveWidth)
                        .height(20.dp)
                        .background(PlateTokens.SleeveChrome, RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                        .border(1.dp, PlateTokens.CollarFlangeSteel, RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                )

                // Plates stacked from inside collar outward
                Row(
                    modifier = Modifier.padding(start = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    plateBreakdown.forEach { plateCount ->
                        repeat(plateCount.count) {
                            val plateHeight = when {
                                plateCount.plateWeight >= 45.0 -> 96.dp
                                plateCount.plateWeight >= 35.0 -> 84.dp
                                plateCount.plateWeight >= 25.0 -> 72.dp
                                plateCount.plateWeight >= 10.0 -> 58.dp
                                plateCount.plateWeight >= 5.0 -> 46.dp
                                plateCount.plateWeight >= 2.5 -> 36.dp
                                else -> 28.dp
                            }
                            val plateWidth = when {
                                plateCount.plateWeight >= 45.0 -> 18.dp
                                plateCount.plateWeight >= 35.0 -> 16.dp
                                plateCount.plateWeight >= 25.0 -> 14.dp
                                plateCount.plateWeight >= 10.0 -> 12.dp
                                else -> 10.dp
                            }

                            val textColor = PlateTokens.textColorForPlate(plateCount.plateWeight, plateCount.color)

                            val labelText = if (plateCount.plateWeight % 1.0 == 0.0) {
                                "${plateCount.plateWeight.toInt()}"
                            } else {
                                "${plateCount.plateWeight}"
                            }

                            Box(
                                modifier = Modifier
                                    .width(plateWidth)
                                    .height(plateHeight)
                                    .background(plateCount.color, RoundedCornerShape(3.dp))
                                    .border(1.dp, Color.Black.copy(alpha = 0.25f), RoundedCornerShape(3.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = labelText,
                                    color = textColor,
                                    fontSize = if (labelText.length > 3) 7.sp else 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = SpaceGrotesk,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Outside Quick-Release Collar Clamp (locks plates on bar)
                    if (plateBreakdown.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .width(10.dp)
                                .height(28.dp)
                                .background(Color(0xFF263238), RoundedCornerShape(2.dp))
                                .border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(2.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(Color(0xFFD32F2F), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
