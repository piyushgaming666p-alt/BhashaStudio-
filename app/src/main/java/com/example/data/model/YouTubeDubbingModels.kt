package com.example.data.model

data class YouTubeVideoMetadata(
    val videoId: String,
    val originalUrl: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val durationSeconds: Long,
    val durationFormatted: String,
    val descriptionSnippet: String = "",
    val detectedLanguage: String = "en",
    val detectedLanguageLabel: String = "English (Auto-detected)"
)

enum class YouTubeDubbingStage(val labelEn: String, val labelHi: String) {
    IDLE("Idle", "तैयार"),
    FETCHING_METADATA("Fetching YouTube Metadata...", "यूट्यूब विवरण लोड हो रहा है..."),
    SCANNING_IMAGE("Extracting URL via OCR...", "तस्वीर से लिंक निकाला जा रहा है..."),
    PREPARING_AUDIO("Extracting Audio Stream...", "ऑडियो स्ट्रीम तैयार हो रही है..."),
    TRANSCRIBING("Recognizing Speech...", "ध्वनि से संवाद पहचाने जा रहे हैं..."),
    TRANSLATING("Translating to Conversational Hindi...", "सहज हिंदी में अनुवाद जारी है..."),
    VOICE_GENERATING("Synthesizing AI Dubbing Voice...", "एआई वॉयस डबिंग तैयार हो रही है..."),
    SYNCING("Synchronizing Audio & Subtitles...", "ऑडियो और सबटाइटल तालमेल..."),
    RENDERING_VIDEO("Rendering MP4 Video / MP3...", "वीडियो / ऑडियो रेंडर हो रहा है..."),
    COMPLETED("Dubbing Completed Successfully!", "डबिंग सफलतापूर्वक पूरी हुई!"),
    FAILED("Operation Failed", "प्रक्रिया असफल")
}

data class YouTubeDubbingJobState(
    val stage: YouTubeDubbingStage = YouTubeDubbingStage.IDLE,
    val progress: Float = 0f,
    val statusMessageEn: String = "",
    val statusMessageHi: String = "",
    val errorMessage: String? = null,
    val currentChunkIndex: Int = 0,
    val totalChunks: Int = 1,
    val outputMp4Path: String? = null,
    val outputMp3Path: String? = null,
    val outputSrtPath: String? = null
)

enum class YouTubeExportFormat(val label: String, val extension: String) {
    MP4_VIDEO("MP4 Video (Dubbed + Subtitles)", "mp4"),
    MP3_AUDIO("MP3 Audio Only (Hindi Dub)", "mp3"),
    SRT_SUBTITLES("SRT Subtitles File", "srt")
}
