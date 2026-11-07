package com.example.ui.youtube

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.MaxVideoDuration
import com.example.data.model.YouTubeDubbingStage
import com.example.data.model.YouTubeExportFormat
import com.example.data.model.YouTubeVoiceCatalog
import com.example.data.model.YouTubeVoiceProfile
import com.example.ui.theme.GradientBlue
import com.example.ui.theme.GradientPink
import com.example.ui.theme.GradientPurple
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSecondary
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioError
import com.example.ui.theme.StudioSuccess
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.YouTubeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeAutoDubbingScreen(
    onNavigateBack: () -> Unit,
    viewModel: YouTubeDubbingViewModel = viewModel()
) {
    val context = LocalContext.current
    val urlInput by viewModel.urlInput.collectAsState()
    val videoMeta by viewModel.videoMetadata.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()
    val voicePitch by viewModel.voicePitch.collectAsState()
    val voiceVolume by viewModel.voiceVolume.collectAsState()
    val isVoiceSaved by viewModel.isVoiceSaved.collectAsState()
    val maxDuration by viewModel.maxDurationSetting.collectAsState()
    val jobState by viewModel.jobState.collectAsState()
    val scriptCues by viewModel.scriptCues.collectAsState()
    val preserveBgm by viewModel.preserveBgm.collectAsState()
    val exportFormat by viewModel.exportFormat.collectAsState()

    var isPreviewingVoice by remember { mutableStateOf(false) }

    // Image Picker for OCR Scanning YouTube Link
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.scanUrlFromImage(uri)
            Toast.makeText(context, "Scanning image for YouTube link...", Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        onNavigateBack()
    }

    val headerGradient = Brush.horizontalGradient(
        colors = listOf(GradientPurple, GradientPink, GradientBlue)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(YouTubeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "YouTube Auto Dubbing",
                                color = StudioTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "यूट्यूब ऑटो डबिंग (हिंदी स्टोरीटेलिंग)",
                                color = PurpleSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("yt_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StudioTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioBackground)
            )
        },
        containerColor = StudioBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Gradient Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(headerGradient)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Conversational Hindi Dubbing",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Reference storytelling voice with natural Indian cadence, balanced English terms, auto subtitles, and multi-hour processing support.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 2. YouTube URL Input & Image OCR Scanner Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. YouTube Video URL", color = StudioTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("लिंक दर्ज करें", color = StudioTextMuted, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { viewModel.updateUrlInput(it) },
                            placeholder = { Text("Paste YouTube link (https://youtu.be/...)", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("yt_url_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = StudioTextPrimary,
                                unfocusedTextColor = StudioTextPrimary,
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = StudioBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.fetchVideoMetadata(urlInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("fetch_metadata_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Load Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceCard),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("scan_picture_button")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PurpleSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Picture", color = StudioTextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. YouTube Video Metadata Preview Card
            item {
                videoMeta?.let { meta ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimary.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 120.dp, height = 75.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black)
                                ) {
                                    AsyncImage(
                                        model = meta.thumbnailUrl,
                                        contentDescription = meta.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Black.copy(alpha = 0.75f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(meta.durationFormatted, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = meta.title,
                                        color = StudioTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Channel: ${meta.author}",
                                        color = StudioTextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PurplePrimary.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(meta.detectedLanguageLabel, color = PurpleSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Voice System: Four Voices (Voice 1 = Reference Style Default)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = GradientPink, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. AI Dubbing Voice Selection", color = StudioTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Reference Style is the default voice. You can choose any of the 4 voices:",
                            color = StudioTextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        YouTubeVoiceCatalog.ALL_VOICES.forEach { voice ->
                            val isSelected = selectedVoice.id == voice.id
                            VoiceCardItem(
                                voice = voice,
                                isSelected = isSelected,
                                onSelect = { viewModel.selectVoice(voice) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Voice Controls (Speed, Pitch, Volume, Save)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Voice Tuning Controls", color = StudioTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Speed Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Speed (${String.format("%.2fx", voiceSpeed)})", color = StudioTextSecondary, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                            Slider(
                                value = voiceSpeed,
                                onValueChange = { viewModel.setVoiceSpeed(it) },
                                valueRange = 0.7f..1.5f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = PurplePrimary, activeTrackColor = PurplePrimary)
                            )
                        }

                        // Pitch Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Pitch (${String.format("%.2fx", voicePitch)})", color = StudioTextSecondary, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                            Slider(
                                value = voicePitch,
                                onValueChange = { viewModel.setVoicePitch(it) },
                                valueRange = 0.7f..1.4f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = GradientPink, activeTrackColor = GradientPink)
                            )
                        }

                        // Volume Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Volume (${(voiceVolume * 100).toInt()}%)", color = StudioTextSecondary, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                            Slider(
                                value = voiceVolume,
                                onValueChange = { viewModel.setVoiceVolume(it) },
                                valueRange = 0.1f..1.0f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = GradientBlue, activeTrackColor = GradientBlue)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (isPreviewingVoice) {
                                        viewModel.stopVoicePreview()
                                        isPreviewingVoice = false
                                    } else {
                                        viewModel.previewSelectedVoice()
                                        isPreviewingVoice = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceCard),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    if (isPreviewingVoice) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = PurpleSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isPreviewingVoice) "Stop" else "Preview Voice", color = StudioTextPrimary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { viewModel.saveVoicePreference() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isVoiceSaved) StudioSuccess else PurplePrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isVoiceSaved) "Saved!" else "Save Voice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 5. Long Video Processing Tier & Options (30m, 1h, 2h, 4h, 8h)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AvTimer, contentDescription = null, tint = GradientBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3. Processing Tier & Audio Settings", color = StudioTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Chunk processing architecture with automatic state saving:",
                            color = StudioTextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MaxVideoDuration.values().forEach { tier ->
                                val isSelected = maxDuration == tier
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setMaxDuration(tier) },
                                    label = { Text(tier.label.replace(" Video", ""), fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PurplePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Preserve Background Music Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = PurpleSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Preserve Background Music", color = StudioTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Keep original background score separated from dialogue", color = StudioTextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = preserveBgm,
                                onCheckedChange = { viewModel.setPreserveBgm(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PurplePrimary)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Export Format (MP4, MP3, SRT)
                        Text("Export Output Format:", color = StudioTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            YouTubeExportFormat.values().forEach { fmt ->
                                val isSel = exportFormat == fmt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PurplePrimary else StudioSurfaceCard)
                                        .border(1.dp, if (isSel) PurpleSecondary else StudioBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setExportFormat(fmt) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = fmt.name.split("_").first(),
                                        color = if (isSel) Color.White else StudioTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Action Button: Start Auto Dubbing
            item {
                val isProcessing = jobState.stage != YouTubeDubbingStage.IDLE &&
                        jobState.stage != YouTubeDubbingStage.COMPLETED &&
                        jobState.stage != YouTubeDubbingStage.FAILED

                Button(
                    onClick = {
                        if (isProcessing) {
                            viewModel.cancelJob()
                        } else {
                            viewModel.startAutoDubbing()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_dubbing_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isProcessing) StudioError else PurplePrimary
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Cancel Processing", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Auto Dubbing (ऑटो डबिंग शुरू करें)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 7. Real Processing Status & Progress Card
            item {
                if (jobState.stage != YouTubeDubbingStage.IDLE) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (jobState.stage == YouTubeDubbingStage.COMPLETED) StudioSuccess else PurplePrimary
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (jobState.stage == YouTubeDubbingStage.COMPLETED) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StudioSuccess, modifier = Modifier.size(20.dp))
                                } else if (jobState.stage == YouTubeDubbingStage.FAILED) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StudioError, modifier = Modifier.size(20.dp))
                                } else {
                                    CircularProgressIndicator(color = PurpleSecondary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = jobState.stage.labelEn,
                                    color = StudioTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${(jobState.progress * 100).toInt()}%",
                                    color = PurpleSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { jobState.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (jobState.stage == YouTubeDubbingStage.COMPLETED) StudioSuccess else PurplePrimary,
                                trackColor = StudioBorder
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = jobState.statusMessageEn,
                                color = StudioTextSecondary,
                                fontSize = 11.sp
                            )
                            if (jobState.statusMessageHi.isNotBlank()) {
                                Text(
                                    text = jobState.statusMessageHi,
                                    color = StudioTextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            // Error Message Display
                            jobState.errorMessage?.let { err ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioError.copy(alpha = 0.15f))
                                        .border(1.dp, StudioError.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(text = err, color = Color(0xFFFCA5A5), fontSize = 11.sp)
                                }
                            }

                            // Download / Output Links
                            if (jobState.stage == YouTubeDubbingStage.COMPLETED) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Ready Download Files:", color = StudioSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                jobState.outputMp4Path?.let { mp4 ->
                                    DownloadFilePill(label = "Download Dubbed MP4 Video", path = mp4)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                jobState.outputMp3Path?.let { mp3 ->
                                    DownloadFilePill(label = "Download Hindi Audio MP3/WAV", path = mp3)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                jobState.outputSrtPath?.let { srt ->
                                    DownloadFilePill(label = "Download SRT Subtitles", path = srt)
                                }
                            }
                        }
                    }
                }
            }

            // 8. Script Editing & Regeneration Section
            if (scriptCues.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = GradientPink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("4. Edit Translated Dialogue Script", color = StudioTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${scriptCues.size} segments", color = StudioTextMuted, fontSize = 11.sp)
                    }
                }

                items(scriptCues) { cue ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PurplePrimary.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(cue.speakerTag ?: "Speaker", color = PurpleSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${cue.startMs / 1000}s - ${cue.endMs / 1000}s",
                                    color = StudioTextMuted,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { viewModel.regenerateSingleCueVoice(cue.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play Dialogue", tint = PurpleSecondary, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Original English:", color = StudioTextMuted, fontSize = 10.sp)
                            Text(cue.originalText, color = StudioTextSecondary, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Hindi Conversational Dubbing (Editable):", color = PurpleSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = cue.text,
                                onValueChange = { viewModel.updateCueText(cue.id, it) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = StudioTextPrimary,
                                    unfocusedTextColor = StudioTextPrimary,
                                    focusedBorderColor = PurplePrimary,
                                    unfocusedBorderColor = StudioBorder
                                )
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun VoiceCardItem(
    voice: YouTubeVoiceProfile,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) StudioSurfaceVariant else StudioSurfaceCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) PurplePrimary else StudioBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, if (isSelected) PurplePrimary else StudioTextMuted, CircleShape)
                        .background(if (isSelected) PurplePrimary else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = voice.name,
                            color = StudioTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (voice.isDefault) GradientPink.copy(alpha = 0.2f) else PurplePrimary.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = voice.styleBadge,
                                color = if (voice.isDefault) GradientPink else PurpleSecondary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Text(text = voice.hindiName, color = StudioTextMuted, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = voice.description,
                color = StudioTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun DownloadFilePill(label: String, path: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StudioSurfaceCard)
            .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
            .clickable {
                Toast.makeText(context, "Saved at: $path", Toast.LENGTH_LONG).show()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Download, contentDescription = null, tint = StudioSuccess, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = StudioTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(path, color = StudioTextMuted, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
