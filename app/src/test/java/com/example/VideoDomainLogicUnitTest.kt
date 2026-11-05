package com.example

import com.example.data.model.MaxVideoDuration
import com.example.data.model.SubtitleCue
import com.example.domain.ai.MultilingualTranslationService
import com.example.domain.video.SrtParser
import com.example.domain.video.VideoChunkProcessor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Fast deterministic JVM unit tests for video editing domain logic,
 * language detection, SRT parser, and long video chunking.
 */
class VideoDomainLogicUnitTest {

    @Test
    fun testLanguageDetection() {
        val service = MultilingualTranslationService()

        val hindi = service.detectLanguage("नमस्ते दोस्तों, भाषा स्टूडियो में आपका स्वागत है")
        assertEquals("hi", hindi.code)

        val english = service.detectLanguage("Welcome to Bhasha Studio video dubbing pro")
        assertEquals("en", english.code)

        val bengali = service.detectLanguage("সবাইকে স্বাগতম")
        assertEquals("bn", bengali.code)
    }

    @Test
    fun testSrtExportAndParse() {
        val cues = listOf(
            SubtitleCue(
                startMs = 1500L,
                endMs = 4000L,
                text = "Welcome to Bhasha Studio",
                languageCode = "en"
            ),
            SubtitleCue(
                startMs = 5000L,
                endMs = 8500L,
                text = "Dubbing and subtitles in Hindi",
                languageCode = "en"
            )
        )

        val srtOutput = SrtParser.exportToSrt(cues)
        assertTrue(srtOutput.contains("00:00:01,500 --> 00:00:04,000"))
        assertTrue(srtOutput.contains("Welcome to Bhasha Studio"))

        val parsed = SrtParser.parseFromSrt(srtOutput, "en")
        assertEquals(2, parsed.size)
        assertEquals(1500L, parsed[0].startMs)
        assertEquals(4000L, parsed[0].endMs)
        assertEquals("Welcome to Bhasha Studio", parsed[0].text)
    }

    @Test
    fun testLongVideoChunkPartitioning() {
        // Test 4-hour video partitioned into 15-minute chunks
        val fourHoursMs = 4 * 3600 * 1000L
        val config15m = com.example.data.model.ChunkProcessingConfig(
            maxDuration = MaxVideoDuration.FOUR_HOURS,
            chunkDurationMinutes = 15
        )
        val chunks = VideoChunkProcessor.createChunksForDuration(fourHoursMs, config15m)

        // 4 hours = 240 minutes / 15 minutes = 16 chunks
        assertEquals(16, chunks.size)
        assertEquals(0L, chunks[0].startMs)
        assertEquals(15 * 60 * 1000L, chunks[0].endMs)
        assertEquals(fourHoursMs, chunks.last().endMs)

        // Test 8-hour video partitioned into 15-minute chunks
        val eightHoursMs = MaxVideoDuration.EIGHT_HOURS.maxMs
        val config8h = com.example.data.model.ChunkProcessingConfig(
            maxDuration = MaxVideoDuration.EIGHT_HOURS,
            chunkDurationMinutes = 15
        )
        val eightHourChunks = VideoChunkProcessor.createChunksForDuration(eightHoursMs, config8h)
        assertEquals(32, eightHourChunks.size)
        assertEquals(eightHoursMs, eightHourChunks.last().endMs)
    }

    @Test
    fun testMaxVideoDurationValues() {
        assertEquals(60 * 60 * 1000L, MaxVideoDuration.ONE_HOUR.maxMs)
        assertEquals(120 * 60 * 1000L, MaxVideoDuration.TWO_HOURS.maxMs)
        assertEquals(240 * 60 * 1000L, MaxVideoDuration.FOUR_HOURS.maxMs)
        assertEquals(480 * 60 * 1000L, MaxVideoDuration.EIGHT_HOURS.maxMs)
    }
}
