package com.example.domain.video

import com.example.data.model.AudioTrackItem
import com.example.data.model.AudioTrackType
import com.example.data.model.ChunkProcessingConfig
import com.example.data.model.LongVideoProcessingProgress
import com.example.data.model.Project
import com.example.data.model.SubtitleCue
import com.example.data.model.SupportedLanguage
import com.example.data.model.VideoChunk
import com.example.domain.ai.DubbingVoice
import com.example.domain.ai.TextToSpeechDubbingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VideoChunkProcessor(
    private val ttsDubbingService: TextToSpeechDubbingService
) {

    /**
     * Splits a total duration (up to 8 hours / 480 minutes) into sequential chunks.
     * Default chunk duration is 15 minutes.
     */
    fun createChunksForDuration(
        totalDurationMs: Long,
        config: ChunkProcessingConfig = ChunkProcessingConfig()
    ): List<VideoChunk> {
        val chunkDuration = config.chunkDurationMs
        val clampedTotalMs = totalDurationMs.coerceIn(1000L, config.maxDuration.maxMs)
        val chunkCount = ((clampedTotalMs + chunkDuration - 1) / chunkDuration).toInt().coerceAtLeast(1)

        val chunks = mutableListOf<VideoChunk>()
        for (i in 0 until chunkCount) {
            val startMs = i * chunkDuration
            val endMs = ((i + 1) * chunkDuration).coerceAtMost(clampedTotalMs)
            chunks.add(
                VideoChunk(
                    index = i,
                    totalChunks = chunkCount,
                    startMs = startMs,
                    endMs = endMs,
                    isCompleted = false
                )
            )
        }
        return chunks
    }

    /**
     * Filters subtitle cues that fall within a specific chunk's time window.
     */
    fun getCuesForChunk(cues: List<SubtitleCue>, chunk: VideoChunk): List<SubtitleCue> {
        return cues.filter { cue ->
            cue.startMs >= chunk.startMs && cue.startMs < chunk.endMs
        }
    }

    /**
     * Processes AI dubbing for a specific chunk.
     * Keeps memory low by releasing intermediate buffers and explicitly calling GC hints.
     */
    suspend fun processDubbingChunk(
        chunk: VideoChunk,
        allCues: List<SubtitleCue>,
        language: SupportedLanguage,
        voiceId: String?,
        pitch: Float,
        speechRate: Float,
        onProgress: (cueIndex: Int, totalCuesInChunk: Int) -> Unit
    ): List<AudioTrackItem> = withContext(Dispatchers.IO) {
        val chunkCues = getCuesForChunk(allCues, chunk)
        if (chunkCues.isEmpty()) return@withContext emptyList()

        val audioItems = mutableListOf<AudioTrackItem>()
        chunkCues.forEachIndexed { idx, cue ->
            onProgress(idx + 1, chunkCues.size)
            val audioItem = ttsDubbingService.synthesizeSubtitleAudio(
                cue = cue,
                language = language,
                voiceId = voiceId,
                pitch = pitch,
                speechRate = speechRate
            )
            if (audioItem != null) {
                audioItems.add(audioItem)
            }
        }

        // Memory cleanup between chunks to prevent RAM bloat on long videos
        System.gc()

        audioItems
    }
}
