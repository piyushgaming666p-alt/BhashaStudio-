package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChunkProcessingConfig
import com.example.data.model.MaxVideoDuration
import com.example.ui.theme.AudioDubColor
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun LongVideoSettingsDialog(
    currentDuration: MaxVideoDuration,
    chunkConfig: ChunkProcessingConfig,
    onSelectDuration: (MaxVideoDuration) -> Unit,
    onToggleChunking: (Boolean) -> Unit,
    onSelectChunkMinutes: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("long_video_settings_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AvTimer,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Long Video Settings",
                                color = StudioTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "4-Hour Current / 8-Hour Future Architecture",
                                color = StudioTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StudioTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Architecture Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AudioDubColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("8-Hour Ready Architecture Active", color = AudioDubColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Supports continuous 4-hour dubbing today with modular architecture ready for up to 8 hours (480 min).",
                            color = StudioTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Max Duration Presets
                Text("Max Video Duration Limit", color = StudioTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MaxVideoDuration.values().forEach { duration ->
                        val isSelected = currentDuration == duration
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF281E3B) else StudioSurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) PurplePrimary else StudioBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectDuration(duration) }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = duration.label,
                                            color = if (isSelected) Color.White else StudioTextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (duration.isFutureExtended) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(PurplePrimary)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("FUTURE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${duration.maxMinutes} min timeline limit (${duration.maxMs / 3600000}h)",
                                        color = StudioTextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chunk-Based Processing Settings
                Text("RAM & Background Safeguards", color = StudioTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Chunk-Based Processing", color = StudioTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Splits long audio into safe chunks (prevents OOM on 4-6 GB RAM)", color = StudioTextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = chunkConfig.isChunkingEnabled,
                                onCheckedChange = onToggleChunking,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PurplePrimary)
                            )
                        }

                        if (chunkConfig.isChunkingEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Chunk Duration", color = StudioTextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(10 to "10 min", 15 to "15 min (Std)", 30 to "30 min").forEach { (mins, label) ->
                                    val isChunkSelected = chunkConfig.chunkDurationMinutes == mins
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isChunkSelected) PurplePrimary else Color(0xFF1E162F))
                                            .border(1.dp, if (isChunkSelected) Color.White else StudioBorder, RoundedCornerShape(8.dp))
                                            .clickable { onSelectChunkMinutes(mins) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isChunkSelected) Color.White else StudioTextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = AudioDubColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Auto-Save Intermediate Progress", color = StudioTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Saves synthesized dubbed cues to Room DB after each chunk", color = StudioTextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save & Apply Settings", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
