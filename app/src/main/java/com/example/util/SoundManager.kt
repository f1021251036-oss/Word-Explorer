package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SoundManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("word_explorer_audio", Context.MODE_PRIVATE)
    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_effects_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val soundPool: SoundPool
    private var correctSoundId: Int = 0
    private var incorrectSoundId: Int = 0
    private var isLoadedCorrect: Boolean = false
    private var isLoadedIncorrect: Boolean = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                if (sampleId == correctSoundId) isLoadedCorrect = true
                if (sampleId == incorrectSoundId) isLoadedIncorrect = true
            }
        }

        try {
            correctSoundId = soundPool.load(context, R.raw.sound_correct, 1)
            incorrectSoundId = soundPool.load(context, R.raw.sound_incorrect, 1)
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to load audio cues", e)
        }
    }

    fun toggleSound(): Boolean {
        val newState = !_isSoundEnabled.value
        setSoundEnabled(newState)
        return newState
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        prefs.edit().putBoolean("sound_effects_enabled", enabled).apply()
    }

    fun playCorrect() {
        if (!_isSoundEnabled.value) return
        if (correctSoundId != 0) {
            soundPool.play(correctSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun playIncorrect() {
        if (!_isSoundEnabled.value) return
        if (incorrectSoundId != 0) {
            soundPool.play(incorrectSoundId, 0.85f, 0.85f, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
