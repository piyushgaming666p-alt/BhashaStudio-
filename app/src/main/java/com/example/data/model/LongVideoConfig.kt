package com.example.data.model

enum class MaxVideoDuration(
    val label: String,
    val maxMinutes: Int,
    val maxMs: Long,
    val isFutureExtended: Boolean = false
) {
    ONE_HOUR("1 Hour (60 min)", 60, 60 * 60 * 1000L),
    TWO_HOURS("2 Hours (120 min)", 120, 120 * 60 * 1000L),
    FOUR_HOURS("4 Hours (240 min) - Current Standard", 240, 240 * 60 * 1000L),
    EIGHT_HOURS("8 Hours (480 min) - Extended Future Mode", 480, 480 * 60 * 1000L, isFutureExtended = true);

    companion object {
        val DEFAULT = FOUR_HOURS
    }
}

data class ChunkProcessingConfig(
    val maxDuration: MaxVideoDuration = MaxVideoDuration.FOUR_HOURS,
    val isChunkingEnabled: Boolean = true,
    val chunkDurationMinutes: Int = 15, // 15-minute chunks
    val autoSaveIntervalChunks: Int = 1, // Auto-save project after each chunk
    val lowRamOptimization: Boolean = true // Aggressive memory & bitmap recycling
) {
    val chunkDurationMs: Long get() = chunkDurationMinutes * 60 * 1000L
}

data class VideoChunk(
    val index: Int,
    val totalChunks: Int,
    val startMs: Long,
    val endMs: Long,
    val isCompleted: Boolean = false,
    val subtitleCuesCount: Int = 0,
    val dubbedAudioTrackId: String? = null
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(0L)
    val label: String get() = "Chunk ${index + 1}/$totalChunks (${startMs / 60000}m - ${endMs / 60000}m)"
}

data class LongVideoProcessingProgress(
    val isProcessing: Boolean = false,
    val currentChunkIndex: Int = 0,
    val totalChunks: Int = 0,
    val overallProgress: Float = 0f,
    val stageMessage: String = "",
    val lastSavedTimestamp: Long = 0L,
    val canResume: Boolean = false
)
