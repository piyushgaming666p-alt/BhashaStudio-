package com.example.data.model

data class YouTubeVoiceProfile(
    val id: String,
    val name: String,
    val hindiName: String,
    val description: String,
    val styleBadge: String,
    val isDefault: Boolean = false,
    val defaultPitch: Float = 1.0f,
    val defaultSpeed: Float = 1.02f,
    val sampleNarration: String
)

object YouTubeVoiceCatalog {
    /**
     * Voice 1: Default Reference Voice
     * Recreates the general narration characteristics of reference URL:
     * https://youtu.be/ZxbwxHHQqRE?si=j1ZFmMcPp2Gx8scW
     * Characteristics:
     * - Natural Indian Hindi narration mixed with commonly spoken English terms (Hinglish conversational flow).
     * - Balanced, neither overly formal Hindi nor excessive English.
     * - Smooth storytelling delivery with natural pauses and emotional expression.
     * - Consistent volume and engaging pacing.
     */
    val VOICE_1_REFERENCE = YouTubeVoiceProfile(
        id = "voice_reference_conversational",
        name = "Kavya / Rahul (Reference Storyteller)",
        hindiName = "संदर्भ कथावाचक (नेचुरल हिंदी + इंग्लिश)",
        description = "Balanced Hindi & English storytelling narration with natural conversational pauses, engaging pacing, and smooth expression inspired by modern educational creators.",
        styleBadge = "DEFAULT REFERENCE",
        isDefault = true,
        defaultPitch = 1.0f,
        defaultSpeed = 1.02f,
        sampleNarration = "दोस्तों आज के इस वीडियो में हम जानेंगे कि कैसे ये टेक्नोलॉजी पूरी दुनिया को बदल रही है, so watch till the end!"
    )

    /**
     * Voice 2: Natural Hindi (Clean, warm, everyday conversational Hindi)
     */
    val VOICE_2_NATURAL_HINDI = YouTubeVoiceProfile(
        id = "voice_natural_hindi",
        name = "Aarti / Vivek (Natural Hindi)",
        hindiName = "सहज प्राकृतिक हिंदी (सरल और स्पष्ट)",
        description = "Warm, everyday conversational Hindi. Clean articulation ideal for tutorials, vlogs, and explainers.",
        styleBadge = "NATURAL HINDI",
        isDefault = false,
        defaultPitch = 0.98f,
        defaultSpeed = 1.0f,
        sampleNarration = "नमस्ते दोस्तों, आज हम बहुत ही आसान तरीके से इस पूरी प्रक्रिया को स्टेप बाय स्टेप समझने वाले हैं।"
    )

    /**
     * Voice 3: Cinematic Storytelling (Dramatic, dynamic pacing, emotional depth)
     */
    val VOICE_3_CINEMATIC = YouTubeVoiceProfile(
        id = "voice_cinematic_storytelling",
        name = "Vikram (Cinematic Storyteller)",
        hindiName = "सिनेमैटिक स्टोरीटेलिंग (रोमांचक व गंभीर)",
        description = "Dramatic, rich, and dynamic narration style with subtle suspense pauses. Perfect for crime stories, case studies, and cinema recaps.",
        styleBadge = "CINEMATIC",
        isDefault = false,
        defaultPitch = 0.92f,
        defaultSpeed = 0.95f,
        sampleNarration = "साल 1995 की एक अंधेरी रात, जब अचानक एक ऐसी घटना घटी जिसने पूरे देश को हैरान कर दिया..."
    )

    /**
     * Voice 4: Deep Documentary (Authoritative, resonant, calm)
     */
    val VOICE_4_DOCUMENTARY = YouTubeVoiceProfile(
        id = "voice_deep_documentary",
        name = "Siddharth (Deep Documentary)",
        hindiName = "गंभीर डॉक्यूमेंट्री (ज्ञानवर्धक व प्रभावशाली)",
        description = "Authoritative, resonant, low-frequency baritone delivery. Ideal for science, history, wildlife, and geopolitics.",
        styleBadge = "DEEP DOCUMENTARY",
        isDefault = false,
        defaultPitch = 0.88f,
        defaultSpeed = 0.94f,
        sampleNarration = "ब्रह्मांड के इस विशाल विस्तार में, हमारे सौर मंडल का यह रहस्य सदियों से वैज्ञानिकों को आकर्षित करता रहा है।"
    )

    val ALL_VOICES: List<YouTubeVoiceProfile> = listOf(
        VOICE_1_REFERENCE,
        VOICE_2_NATURAL_HINDI,
        VOICE_3_CINEMATIC,
        VOICE_4_DOCUMENTARY
    )

    fun getById(id: String): YouTubeVoiceProfile {
        return ALL_VOICES.firstOrNull { it.id == id } ?: VOICE_1_REFERENCE
    }
}
