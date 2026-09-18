package com.example.ui.components

import java.util.Locale
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Exercise Video Data Models
 */
data class VideoTimestampCue(
    val second: Int,
    val phaseTitle: String,
    val cueDescription: String
)

data class ExerciseVideoDetails(
    val exerciseName: String,
    val category: String,
    val primaryVideoUrl: String,
    val availableAngles: List<String> = listOf("Side Angle (Sagittal)", "Front View", "45° Oblique Angle"),
    val timestamps: List<VideoTimestampCue>,
    val jointSafetyCheckpoints: List<String>,
    val commonMistakesToAvoid: List<String>,
    val readinessChecklistItems: List<String>,
    val muscleWikiExercise: com.example.data.musclewiki.MuscleWikiExercise? = null
)

/**
 * Video Repository providing rich form guides, MuscleWiki video streams,
 * multi-angle feeds, and step-by-step timestamp cues.
 */
object ExerciseVideoRepository {

    fun getVideoDetailsForExercise(exerciseName: String): ExerciseVideoDetails {
        val lower = exerciseName.lowercase()
        val directMuscleWiki = com.example.data.musclewiki.MuscleWikiRepository.getVideoUrlForExercise(exerciseName)
        val availableVideos = com.example.data.musclewiki.MuscleWikiRepository.getAvailableVideos(exerciseName)
        val angles = if (availableVideos.isNotEmpty()) {
            availableVideos.map { it.angle ?: "Perspective" }
        } else {
            listOf("Side Angle (Sagittal)", "Front View", "45° Oblique Angle")
        }

        return when {
            lower.contains("squat") || lower.contains("leg press") -> ExerciseVideoDetails(
                exerciseName = exerciseName,
                category = "Squat • Knee & Hip Mechanics",
                primaryVideoUrl = directMuscleWiki,
                availableAngles = angles,
                timestamps = listOf(
                    VideoTimestampCue(0, "Setup & Stance", "Feet shoulder-width apart, 15-30° toe flare. 360° belly brace."),
                    VideoTimestampCue(3, "Eccentric Descent", "Hips and knees unlock together. Push knees outward over toes."),
                    VideoTimestampCue(7, "Bottom Parallel Depth", "Hip crease breaks parallel. Barbell sits balanced over mid-foot."),
                    VideoTimestampCue(10, "Drive & Lockout", "Drive floor away through tripod foot. Exhale at apex.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Vertical Bar Path: Keeps weight centered over mid-foot to protect lumbar discs.",
                    "Active Knee Tracking: Prevents knee valgus (inward collapse) to safeguard patellar tendons.",
                    "Rigid Core Cylinder: Neutral spine alignment safely transmits load into hips and femur matrix."
                ),
                commonMistakesToAvoid = listOf(
                    "Good-morning squat: Hips shooting up before chest during ascent.",
                    "Heels rising off floor due to tight ankles.",
                    "Knees caving inward during turnaround."
                ),
                readinessChecklistItems = listOf(
                    "Feet firmly planted in tripod position (heel, big toe, pinky toe).",
                    "Deep intra-abdominal breath taken & core braced 360°.",
                    "Upper back tight with barbell resting securely on traps.",
                    "Knees pushed outward tracking with toes."
                )
            )

            lower.contains("deadlift") || lower.contains("rdl") || lower.contains("hinge") -> ExerciseVideoDetails(
                exerciseName = exerciseName,
                category = "Hinge • Posterior Chain Mechanics",
                primaryVideoUrl = directMuscleWiki,
                availableAngles = angles,
                timestamps = listOf(
                    VideoTimestampCue(0, "Wedge & Lat Lock", "Shins 1 inch from bar. Hinge hips back, lock lats into back pockets."),
                    VideoTimestampCue(3, "Pull Slack Out", "Engage bar tension until plates click before floor drive."),
                    VideoTimestampCue(6, "Leg Drive to Knee", "Push floor away holding torso angle static until bar passes knees."),
                    VideoTimestampCue(9, "Glute Lockout", "Snap hips forward to neutral lockout without lower back hyperextension.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Anti-Flexion Back Brace: Maintains natural lumbar curve under high shear forces.",
                    "Lat Activation: Secures upper scapula to eliminate spinal rounding.",
                    "Glute Drive: Maximizes hip bone mineral density osteogenic remodeling."
                ),
                commonMistakesToAvoid = listOf(
                    "Rounding lumbar spine when pulling off floor.",
                    "Jerking slack out of bar abruptly.",
                    "Hyperextending lower back at top lockout."
                ),
                readinessChecklistItems = listOf(
                    "Bar over midfoot with shins 1 inch away.",
                    "Shoulder blades squeezed & lats locked into back pockets.",
                    "Slack pulled completely out of the barbell before push.",
                    "Neutral neck position maintaining spine line."
                )
            )

            lower.contains("press") || lower.contains("bench") || lower.contains("dip") -> ExerciseVideoDetails(
                exerciseName = exerciseName,
                category = "Pressing • Scapular Stability & Upper Body",
                primaryVideoUrl = directMuscleWiki,
                availableAngles = angles,
                timestamps = listOf(
                    VideoTimestampCue(0, "Scapular Retraction", "Pinch shoulder blades together and depress down into bench/seat."),
                    VideoTimestampCue(3, "Controlled Lowering", "Lower bar under control to mid-sternum. Forearms vertical."),
                    VideoTimestampCue(6, "Turnaround Pause", "Touch chest lightly without bouncing. Maintain leg drive."),
                    VideoTimestampCue(9, "Pressing Arc", "Drive up and back slightly over shoulder joint center.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Stacked Forearms: Eliminates rotational torque on wrists and elbow ligaments.",
                    "Depressed Scapulae: Creates subacromial clearance preventing shoulder impingement.",
                    "Glute & Leg Drive: Stabilizes lumbar arch during heavy pressing loads."
                ),
                commonMistakesToAvoid = listOf(
                    "Flaring elbows out to 90° (keep at 45-60° angle).",
                    "Lifting hips off bench during heavy reps.",
                    "Bouncing weight off sternum."
                ),
                readinessChecklistItems = listOf(
                    "Shoulder blades retracted and driven into bench.",
                    "Wrists straight with knuckles pointing to ceiling.",
                    "Elbows tucked to safe 45-60 degree angle.",
                    "Feet flat driving firmly into floor for stability."
                )
            )

            else -> ExerciseVideoDetails(
                exerciseName = exerciseName,
                category = "Compound Lift • Structural Alignment",
                primaryVideoUrl = directMuscleWiki,
                availableAngles = angles,
                timestamps = listOf(
                    VideoTimestampCue(0, "Stance & Grip", "Set solid base of support and brace core cylinder."),
                    VideoTimestampCue(3, "Controlled Motion", "Execute full range of motion under controlled tempo."),
                    VideoTimestampCue(7, "Peak Contraction", "Hold top contraction with balanced joint alignment."),
                    VideoTimestampCue(10, "Controlled Return", "Lower with eccentric control without dropping weight.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Neutral Spine: Protects vertebrae throughout the active movement.",
                    "Controlled Tempo: Minimizes joint and tendon momentum shock.",
                    "Full Range of Motion: Optimizes osteogenic bone loading."
                ),
                commonMistakesToAvoid = listOf(
                    "Using momentum or body swing to cheat weight.",
                    "Cutting range of motion short.",
                    "Holding breath without bracing."
                ),
                readinessChecklistItems = listOf(
                    "Proper joint alignment established before initiating rep.",
                    "Core braced and breathing rhythm controlled.",
                    "Full range of motion planned without cutting depth."
                )
            )
        }
    }
}

/**
 * Modern Exercise Video Player with Multi-Angle HD Video,
 * Step-by-Step Video Timestamps, Bone Safety Checkpoints, and Interactive Readiness Checklist.
 */
@Composable
fun ExerciseVideoPlayerBox(
    exerciseName: String,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val videoDetails = remember(exerciseName) { ExerciseVideoRepository.getVideoDetailsForExercise(exerciseName) }

    var selectedAngleIndex by remember { mutableIntStateOf(0) }
    var selectedPlaybackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isPlaying by remember { mutableStateOf(true) }
    var isLooping by remember { mutableStateOf(true) }
    var isDataSaverMode by remember { mutableStateOf(false) }
    var dataSaverStartedPlayback by remember { mutableStateOf(false) }

    // Collapsible Accordion States (UX Heuristics Optimization)
    var isTimestampsExpanded by remember { mutableStateOf(true) }
    var isJointSafetyExpanded by remember { mutableStateOf(false) }
    var isPitfallsExpanded by remember { mutableStateOf(false) }
    var isChecklistExpanded by remember { mutableStateOf(true) }

    var currentPositionSeconds by remember { mutableFloatStateOf(0f) }
    val totalDurationSeconds = 12f

    // Quick-seek feedback animation states
    var seekRewindFeedback by remember { mutableStateOf(false) }
    var seekForwardFeedback by remember { mutableStateOf(false) }

    // Interactive readiness checklist tracking
    val checklistState = remember(exerciseName) {
        mutableStateMapOf<Int, Boolean>().apply {
            videoDetails.readinessChecklistItems.indices.forEach { put(it, false) }
        }
    }
    val allChecked = checklistState.values.all { it }
    val checkedCount = checklistState.values.count { it }

    // Active timestamp tracking based on current playback second
    val activeTimestampIndex = remember(currentPositionSeconds, videoDetails.timestamps) {
        val currentInt = currentPositionSeconds.toInt()
        val idx = videoDetails.timestamps.indexOfLast { currentInt >= it.second }
        if (idx >= 0) idx else 0
    }

    // Precise 60fps Playback Engine
    LaunchedEffect(isPlaying, selectedPlaybackSpeed, isLooping) {
        val frameDelayMs = 33L // ~30-60 FPS smooth animation
        while (true) {
            if (isPlaying) {
                val delta = (frameDelayMs / 1000f) * selectedPlaybackSpeed
                val nextPos = currentPositionSeconds + delta
                if (nextPos >= totalDurationSeconds) {
                    if (isLooping) {
                        currentPositionSeconds = 0f
                    } else {
                        currentPositionSeconds = totalDurationSeconds
                        isPlaying = false
                    }
                } else {
                    currentPositionSeconds = nextPos
                }
            }
            delay(frameDelayMs)
        }
    }

    // Lifecycle cleanup
    DisposableEffect(Unit) {
        onDispose {
            isPlaying = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exercise_video_player_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Exercise Title, Category, Video Badge, Close Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = videoDetails.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartDisplay,
                                    contentDescription = "HD Motion",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "HD Motion Guide",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Video",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-Angle Camera & Video Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Data Saver Mode Toggle Chip
                FilterChip(
                    selected = isDataSaverMode,
                    onClick = {
                        isDataSaverMode = !isDataSaverMode
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isDataSaverMode) Icons.Default.DataSaverOn else Icons.Default.DataSaverOff,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isDataSaverMode) "Data Saver: ON" else "Data Saver: OFF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isDataSaverMode) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )

                videoDetails.availableAngles.forEachIndexed { idx, angleName ->
                    val isSelected = selectedAngleIndex == idx
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedAngleIndex = idx
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = {
                            Text(
                                text = angleName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Motion Video Viewport Box with MuscleWiki Stream
            val availableVideos = com.example.data.musclewiki.MuscleWikiRepository.getAvailableVideos(exerciseName)
            val targetVideoUrl = if (availableVideos.isNotEmpty()) {
                availableVideos.getOrNull(selectedAngleIndex)?.url ?: videoDetails.primaryVideoUrl
            } else {
                videoDetails.primaryVideoUrl
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                if (isDataSaverMode && !dataSaverStartedPlayback) {
                    // High-contrast data saver placeholder with manual click-to-load
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))))
                            .clickable {
                                dataSaverStartedPlayback = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start Stream",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Data Saver Active",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to stream HD muscle form video (Cellular save)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                } else {
                    MuscleWikiVideoPlayer(
                        videoUrl = targetVideoUrl,
                        modifier = Modifier.fillMaxSize(),
                        autoPlay = true,
                        isLooping = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PERSISTENT HIGH-CONTRAST MOVEMENT PHASE BADGE (UX Expert Recommendation)
            val currentPhase = videoDetails.timestamps.getOrNull(activeTimestampIndex)
            if (currentPhase != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ACTIVE MOVEMENT PHASE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = currentPhase.phaseTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%02d:%02d", currentPhase.second / 60, currentPhase.second % 60),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isTimestampsExpanded = !isTimestampsExpanded
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Movement Chapters & Cues",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${videoDetails.timestamps.size} Steps",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isTimestampsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isTimestampsExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(visible = isTimestampsExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            videoDetails.timestamps.forEachIndexed { idx, stamp ->
                                val isActive = activeTimestampIndex == idx
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            currentPositionSeconds = stamp.second.toFloat()
                                            isPlaying = true
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = String.format(Locale.getDefault(), "%02d:%02d", stamp.second / 60, stamp.second % 60),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = stamp.phaseTitle,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stamp.cueDescription,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 17.sp
                                            )
                                        }

                                        if (isActive) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Active timestamp",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Accordion: Bone & Joint Safety Checkpoints
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isJointSafetyExpanded = !isJointSafetyExpanded
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Joint & Bone Safety Checkpoints",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${videoDetails.jointSafetyCheckpoints.size} Cues",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isJointSafetyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isJointSafetyExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(visible = isJointSafetyExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            videoDetails.jointSafetyCheckpoints.forEach { point ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Joint check",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = point,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Accordion: Common Technique Pitfalls
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isPitfallsExpanded = !isPitfallsExpanded
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Technique Pitfalls to Avoid",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "${videoDetails.commonMistakesToAvoid.size} Warnings",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isPitfallsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isPitfallsExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(visible = isPitfallsExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            videoDetails.commonMistakesToAvoid.forEach { mistake ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Pitfall to avoid",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = mistake,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Accordion: Interactive Form Readiness Checklist
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (allChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isChecklistExpanded = !isChecklistExpanded
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (allChecked) Icons.Default.CheckCircle else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pre-Set Form Checklist",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = if (allChecked) "Ready ($checkedCount/${videoDetails.readinessChecklistItems.size})" else "$checkedCount/${videoDetails.readinessChecklistItems.size} Checked",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (allChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isChecklistExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isChecklistExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(visible = isChecklistExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            videoDetails.readinessChecklistItems.forEachIndexed { idx, item ->
                                val isChecked = checklistState[idx] ?: false
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            checklistState[idx] = !isChecked
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            checklistState[idx] = it
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        videoDetails.readinessChecklistItems.indices.forEach { checklistState[it] = true }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                ) {
                    Text("Check All Ready", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = {
                        isPlaying = false
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDismiss?.invoke()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(if (allChecked) "Start Working Set" else "Done Watching", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
