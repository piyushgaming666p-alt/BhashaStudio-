package com.example.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrackItem
import com.example.data.model.AudioTrackType
import com.example.data.model.Project
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoClip
import com.example.ui.theme.AudioBgmColor
import com.example.ui.theme.AudioDubColor
import com.example.ui.theme.PlayheadColor
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.SubtitleTrackColor
import com.example.ui.theme.VideoTrackColor

@Composable
fun TimelineView(
    project: Project,
    currentTimeMs: Long,
    selectedClipId: String?,
    selectedSubtitleId: String?,
    onSeek: (Long) -> Unit,
    onSelectClip: (String) -> Unit,
    onSelectSubtitle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDurationMs = project.totalDurationMs.coerceAtLeast(3000L)
    
    // Adaptive timeline scaling: Supports short clips up to 4 hours and 8 hours without RAM bloat
    val dpPerSecond = remember(totalDurationMs) { calculateAdaptiveDpPerSecond(totalDurationMs) }
    val stepSeconds = remember(totalDurationMs) { calculateAdaptiveStepSeconds(totalDurationMs) }
    
    val totalSeconds = (totalDurationMs / 1000f) + 2f // Extra breathing room
    val timelineWidthDp = (totalSeconds * dpPerSecond).dp.coerceAtLeast(300.dp)

    val scrollState = rememberScrollState()

    // Smoothly scroll to keep playhead in view during playback
    LaunchedEffect(currentTimeMs) {
        val playheadPx = ((currentTimeMs / 1000f) * dpPerSecond * 2.5f).toInt()
        val viewportWidth = scrollState.viewportSize
        if (viewportWidth > 0 && (playheadPx < scrollState.value || playheadPx > scrollState.value + viewportWidth - 100)) {
            scrollState.animateScrollTo((playheadPx - viewportWidth / 3).coerceAtLeast(0))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(StudioSurface)
            .border(1.dp, StudioBorder)
            .testTag("timeline_container")
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Track Header Column (Fixed on Left)
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .fillMaxHeight()
                    .background(StudioSurfaceVariant)
                    .border(1.dp, StudioBorder)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ruler space
                Box(modifier = Modifier.height(24.dp), contentAlignment = Alignment.Center) {
                    Text("TIME", color = StudioTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                TrackHeaderItem(icon = Icons.Default.Videocam, color = VideoTrackColor, label = "Video")
                Spacer(modifier = Modifier.height(4.dp))
                TrackHeaderItem(icon = Icons.Default.Subtitles, color = SubtitleTrackColor, label = "Subs")
                Spacer(modifier = Modifier.height(4.dp))
                TrackHeaderItem(icon = Icons.Default.GraphicEq, color = AudioDubColor, label = "Dub")
                Spacer(modifier = Modifier.height(4.dp))
                TrackHeaderItem(icon = Icons.Default.MusicNote, color = AudioBgmColor, label = "BGM")
            }

            // Scrollable Timeline Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
            ) {
                // Timeline Canvas
                Column(
                    modifier = Modifier
                        .width(timelineWidthDp)
                        .fillMaxHeight()
                        .pointerInput(totalDurationMs, dpPerSecond) {
                            detectTapGestures { offset ->
                                val clickedSec = offset.x / (dpPerSecond * density)
                                val seekMs = (clickedSec * 1000).toLong().coerceIn(0L, totalDurationMs)
                                onSeek(seekMs)
                            }
                        }
                ) {
                    // Time Ruler with virtualized/stepped markers
                    TimeRulerRow(
                        totalSeconds = totalSeconds.toInt(),
                        dpPerSecond = dpPerSecond,
                        stepSeconds = stepSeconds
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Video Track
                    VideoClipsTrack(
                        clips = project.clips,
                        selectedClipId = selectedClipId,
                        dpPerSecond = dpPerSecond,
                        onSelectClip = onSelectClip
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Subtitles Track
                    SubtitlesTrack(
                        subtitles = project.subtitles,
                        selectedSubtitleId = selectedSubtitleId,
                        dpPerSecond = dpPerSecond,
                        onSelectSubtitle = onSelectSubtitle
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dubbed Audio Track
                    val dubbingItems = project.audioTracks.filter { it.type == AudioTrackType.DUBBING }
                    AudioItemsTrack(
                        items = dubbingItems,
                        trackColor = AudioDubColor,
                        labelPrefix = "Dub",
                        dpPerSecond = dpPerSecond
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // BGM & SFX Track
                    val bgmItems = project.audioTracks.filter { it.type == AudioTrackType.BGM || it.type == AudioTrackType.VOICEOVER }
                    AudioItemsTrack(
                        items = bgmItems,
                        trackColor = AudioBgmColor,
                        labelPrefix = "BGM",
                        dpPerSecond = dpPerSecond
                    )
                }

                // Vertical Playhead Line
                val playheadOffsetDp = ((currentTimeMs / 1000f) * dpPerSecond).dp
                Box(
                    modifier = Modifier
                        .offset { IntOffset(playheadOffsetDp.roundToPx(), 0) }
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(PlayheadColor)
                        .testTag("timeline_playhead")
                ) {
                    // Playhead indicator top cap
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.TopCenter)
                            .clip(CircleShape)
                            .background(PlayheadColor)
                    )
                }
            }
        }
    }
}

@Composable
fun TrackHeaderItem(icon: ImageVector, color: Color, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = StudioTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
fun TimeRulerRow(
    totalSeconds: Int,
    dpPerSecond: Float,
    stepSeconds: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(StudioSurfaceCard)
    ) {
        val totalSecClamped = totalSeconds.coerceAtLeast(1)
        for (sec in 0..totalSecClamped step stepSeconds) {
            val stepWidth = (stepSeconds * dpPerSecond).dp
            Box(
                modifier = Modifier
                    .width(stepWidth)
                    .fillMaxHeight()
                    .padding(start = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val h = sec / 3600
                val m = (sec % 3600) / 60
                val s = sec % 60
                val timeLabel = if (totalSecClamped >= 3600) {
                    String.format("%02d:%02d:%02d", h, m, s)
                } else {
                    String.format("%02d:%02d", m, s)
                }
                Text(
                    text = timeLabel,
                    color = StudioTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun VideoClipsTrack(
    clips: List<VideoClip>,
    selectedClipId: String?,
    dpPerSecond: Float,
    onSelectClip: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        if (clips.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF231C38)),
                contentAlignment = Alignment.Center
            ) {
                Text("No Video Imported", color = StudioTextMuted, fontSize = 11.sp)
            }
        } else {
            clips.forEach { clip ->
                val clipWidthDp = ((clip.effectiveDurationMs / 1000f) * dpPerSecond).dp.coerceAtLeast(24.dp)
                val isSelected = clip.id == selectedClipId

                Box(
                    modifier = Modifier
                        .width(clipWidthDp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) PurplePrimary else VideoTrackColor)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color.White else StudioBorder,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectClip(clip.id) }
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = clip.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}

@Composable
fun SubtitlesTrack(
    subtitles: List<SubtitleCue>,
    selectedSubtitleId: String?,
    dpPerSecond: Float,
    onSelectSubtitle: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
    ) {
        subtitles.forEach { cue ->
            val startDp = ((cue.startMs / 1000f) * dpPerSecond).dp
            val widthDp = ((cue.durationMs / 1000f) * dpPerSecond).dp.coerceAtLeast(18.dp)
            val isSelected = cue.id == selectedSubtitleId

            Box(
                modifier = Modifier
                    .offset { IntOffset(startDp.roundToPx(), 0) }
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Color(0xFFF43F5E) else SubtitleTrackColor)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelectSubtitle(cue.id) }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = cue.text,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AudioItemsTrack(
    items: List<AudioTrackItem>,
    trackColor: Color,
    labelPrefix: String,
    dpPerSecond: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
    ) {
        items.forEach { item ->
            val startDp = ((item.startTimelineMs / 1000f) * dpPerSecond).dp
            val widthDp = ((item.durationMs / 1000f) * dpPerSecond).dp.coerceAtLeast(18.dp)

            Box(
                modifier = Modifier
                    .offset { IntOffset(startDp.roundToPx(), 0) }
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(trackColor)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "$labelPrefix: ${item.title}",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun calculateAdaptiveDpPerSecond(durationMs: Long): Float {
    return when {
        durationMs <= 180_000L -> 40f      // <= 3 minutes: 40 dp/sec
        durationMs <= 900_000L -> 15f      // <= 15 minutes: 15 dp/sec
        durationMs <= 3_600_000L -> 5f     // <= 1 hour: 5 dp/sec
        durationMs <= 14_400_000L -> 1.2f  // <= 4 hours: 1.2 dp/sec (~17,280 dp)
        else -> 0.6f                       // <= 8 hours: 0.6 dp/sec (~17,280 dp)
    }
}

private fun calculateAdaptiveStepSeconds(durationMs: Long): Int {
    return when {
        durationMs <= 60_000L -> 5         // Every 5s
        durationMs <= 300_000L -> 15       // Every 15s
        durationMs <= 900_000L -> 30       // Every 30s
        durationMs <= 3_600_000L -> 120    // Every 2 min
        durationMs <= 14_400_000L -> 600   // Every 10 min
        else -> 1800                       // Every 30 min for 8-hour mode
    }
}
