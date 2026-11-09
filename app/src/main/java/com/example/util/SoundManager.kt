package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import android.util.Log
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * High-performance, delightful synthesized adventure audio manager.
 * Implements low-latency SoundPool playback backed by programmatic royalty-free audio cues:
 * - Joyful glockenspiel arpeggio for correct answers
 * - Gentle organic wooden log thud for incorrect answers
 * - Crisp tactile wooden tile click for chip & card taps
 * - Shimmering gemstone chime for pair matches
 * - Triumphant 5-note major victory fanfare for completing levels
 * - Tactile compass click for navigation & buttons
 */
class SoundManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("word_explorer_audio", Context.MODE_PRIVATE)
    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_effects_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val soundPool: SoundPool
    private var soundIdCorrect: Int = 0
    private var soundIdIncorrect: Int = 0
    private var soundIdTileTap: Int = 0
    private var soundIdMatchPair: Int = 0
    private var soundIdVictoryFanfare: Int = 0
    private var soundIdClick: Int = 0

    private val loadedSoundIds = mutableSetOf<Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                synchronized(loadedSoundIds) {
                    loadedSoundIds.add(sampleId)
                }
            }
        }

        // Initialize audio cues in background to guarantee instant low-latency game playback
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Ensure raw resources are loaded first if present
                try {
                    soundIdCorrect = soundPool.load(context, R.raw.sound_correct, 1)
                    soundIdIncorrect = soundPool.load(context, R.raw.sound_incorrect, 1)
                } catch (e: Exception) {
                    Log.w("SoundManager", "Falling back to synthesized raw resources", e)
                }

                // Generate and load bespoke adventure audio cues into SoundPool
                soundIdTileTap = loadSynthesizedWav("sound_tile_tap.wav", generateTileTapPcm())
                soundIdMatchPair = loadSynthesizedWav("sound_match_pair.wav", generateMatchPairPcm())
                soundIdVictoryFanfare = loadSynthesizedWav("sound_victory.wav", generateVictoryFanfarePcm())
                soundIdClick = loadSynthesizedWav("sound_click.wav", generateClickPcm())
            } catch (e: Exception) {
                Log.e("SoundManager", "Error initializing audio engine", e)
            }
        }
    }

    private fun loadSynthesizedWav(fileName: String, pcmData: ShortArray): Int {
        return try {
            val file = File(context.cacheDir, fileName)
            createWavFile(file, pcmData, 44100)
            soundPool.load(file.absolutePath, 1)
        } catch (e: Exception) {
            Log.e("SoundManager", "Could not cache synthesized wav $fileName", e)
            0
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

    /**
     * Joyful, rewarding expedition arpeggio: C5 -> E5 -> G5 -> C6 glockenspiel chord
     */
    fun playCorrect() {
        if (!_isSoundEnabled.value) return
        if (soundIdCorrect != 0 && isLoaded(soundIdCorrect)) {
            soundPool.play(soundIdCorrect, 1.0f, 1.0f, 1, 0, 1.0f)
        } else {
            playDirectPcm(generateCorrectChimePcm())
        }
    }

    /**
     * Gentle, muffled tactile wooden log thud (organic, encouraging, non-punitive)
     */
    fun playIncorrect() {
        if (!_isSoundEnabled.value) return
        if (soundIdIncorrect != 0 && isLoaded(soundIdIncorrect)) {
            soundPool.play(soundIdIncorrect, 0.85f, 0.85f, 1, 0, 1.0f)
        } else {
            playDirectPcm(generateIncorrectThudPcm())
        }
    }

    /**
     * Crisp tactile wooden chip / tile tap for letters, chips, and grid cells
     */
    fun playTileTap() {
        if (!_isSoundEnabled.value) return
        if (soundIdTileTap != 0 && isLoaded(soundIdTileTap)) {
            soundPool.play(soundIdTileTap, 0.70f, 0.70f, 1, 0, 1.0f)
        } else {
            playDirectPcm(generateTileTapPcm())
        }
    }

    /**
     * Shimmering double bell chime when matching pairs or sorting items
     */
    fun playMatchPair() {
        if (!_isSoundEnabled.value) return
        if (soundIdMatchPair != 0 && isLoaded(soundIdMatchPair)) {
            soundPool.play(soundIdMatchPair, 0.85f, 0.85f, 1, 0, 1.0f)
        } else {
            playDirectPcm(generateMatchPairPcm())
        }
    }

    /**
     * Triumphant 5-note major victory fanfare (G4-C5-E5-G5-C6) for completing levels
     */
    fun playVictoryFanfare() {
        if (!_isSoundEnabled.value) return
        if (soundIdVictoryFanfare != 0 && isLoaded(soundIdVictoryFanfare)) {
            soundPool.play(soundIdVictoryFanfare, 1.0f, 1.0f, 2, 0, 1.0f)
        } else {
            playDirectPcm(generateVictoryFanfarePcm())
        }
    }

    /**
     * Subtle mechanical compass click for buttons and tabs
     */
    fun playClick() {
        if (!_isSoundEnabled.value) return
        if (soundIdClick != 0 && isLoaded(soundIdClick)) {
            soundPool.play(soundIdClick, 0.50f, 0.50f, 1, 0, 1.0f)
        } else {
            playDirectPcm(generateClickPcm())
        }
    }

    private fun isLoaded(soundId: Int): Boolean {
        synchronized(loadedSoundIds) {
            return loadedSoundIds.contains(soundId)
        }
    }

    // --- Audio Synthesis Engine ---

    private fun generateCorrectChimePcm(): ShortArray {
        return synthChime(
            frequencies = listOf(523.25f, 659.25f, 783.99f, 1046.50f),
            durationsMs = listOf(60, 60, 60, 220),
            decay = 3.5
        )
    }

    private fun generateIncorrectThudPcm(): ShortArray {
        val sampleRate = 44100
        val durMs = 160
        val totalSamples = sampleRate * durMs / 1000
        val buffer = ShortArray(totalSamples)
        for (s in 0 until totalSamples) {
            val t = s.toDouble() / sampleRate
            val progress = s.toDouble() / totalSamples
            val freq = 180.0 - 60.0 * progress
            val envelope = Math.exp(-8.5 * progress)
            // Soft wooden acoustic resonance with sub-harmonic body
            val sampleVal = (0.75 * Math.sin(2.0 * Math.PI * freq * t) + 0.25 * Math.sin(Math.PI * freq * t)) * envelope
            buffer[s] = (sampleVal * Short.MAX_VALUE * 0.55).toInt().toShort()
        }
        return buffer
    }

    private fun generateTileTapPcm(): ShortArray {
        val sampleRate = 44100
        val durMs = 35
        val totalSamples = sampleRate * durMs / 1000
        val buffer = ShortArray(totalSamples)
        for (s in 0 until totalSamples) {
            val t = s.toDouble() / sampleRate
            val progress = s.toDouble() / totalSamples
            val freq = 800.0 - 400.0 * progress
            val envelope = Math.exp(-9.0 * progress)
            val sampleVal = Math.sin(2.0 * Math.PI * freq * t) * envelope
            buffer[s] = (sampleVal * Short.MAX_VALUE * 0.50).toInt().toShort()
        }
        return buffer
    }

    private fun generateMatchPairPcm(): ShortArray {
        return synthChime(
            frequencies = listOf(880.00f, 1318.51f),
            durationsMs = listOf(80, 200),
            decay = 3.8
        )
    }

    private fun generateVictoryFanfarePcm(): ShortArray {
        return synthChime(
            frequencies = listOf(392.00f, 523.25f, 659.25f, 783.99f, 1046.50f),
            durationsMs = listOf(80, 80, 80, 100, 350),
            decay = 2.8
        )
    }

    private fun generateClickPcm(): ShortArray {
        val sampleRate = 44100
        val durMs = 25
        val totalSamples = sampleRate * durMs / 1000
        val buffer = ShortArray(totalSamples)
        for (s in 0 until totalSamples) {
            val t = s.toDouble() / sampleRate
            val progress = s.toDouble() / totalSamples
            val freq = 600.0 - 250.0 * progress
            val envelope = Math.exp(-12.0 * progress)
            val sampleVal = Math.sin(2.0 * Math.PI * freq * t) * envelope
            buffer[s] = (sampleVal * Short.MAX_VALUE * 0.40).toInt().toShort()
        }
        return buffer
    }

    private fun synthChime(frequencies: List<Float>, durationsMs: List<Int>, decay: Double): ShortArray {
        val sampleRate = 44100
        var totalSamples = 0
        durationsMs.forEach { totalSamples += (sampleRate * it / 1000) }
        val buffer = ShortArray(totalSamples)
        var bufferIdx = 0

        for (i in frequencies.indices) {
            val freq = frequencies[i]
            val durationSamples = (sampleRate * durationsMs[i] / 1000)
            for (s in 0 until durationSamples) {
                val t = s.toDouble() / sampleRate
                val progress = s.toDouble() / durationSamples
                val envelope = Math.exp(-decay * progress)
                // Glockenspiel harmonic recipe: fundamental + octave + fifth + double-octave
                val sampleVal = (
                    0.60 * Math.sin(2.0 * Math.PI * freq * t) +
                    0.25 * Math.sin(4.0 * Math.PI * freq * t) +
                    0.10 * Math.sin(6.0 * Math.PI * freq * t) +
                    0.05 * Math.sin(8.0 * Math.PI * freq * t)
                ) * envelope
                buffer[bufferIdx++] = (sampleVal * Short.MAX_VALUE * 0.65).toInt().toShort()
            }
        }
        return buffer
    }

    private fun playDirectPcm(buffer: ShortArray) {
        Thread {
            try {
                val sampleRate = 44100
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                val sleepTime = ((buffer.size.toDouble() / sampleRate) * 1000 + 40).toLong()
                Thread.sleep(sleepTime)
                audioTrack.release()
            } catch (e: Exception) {
                Log.e("SoundManager", "Error in direct PCM fallback", e)
            }
        }.start()
    }

    private fun createWavFile(file: File, pcmData: ShortArray, sampleRate: Int) {
        val totalAudioLen = pcmData.size * 2
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * 2
        val header = ByteArray(44)

        // "RIFF"
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        // "WAVE"
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        // "fmt "
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0
        header[22] = 1; header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2; header[33] = 0
        header[34] = 16; header[35] = 0
        // "data"
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        val out = FileOutputStream(file)
        out.write(header)
        val byteBuffer = ByteBuffer.allocate(pcmData.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in pcmData) {
            byteBuffer.putShort(sample)
        }
        out.write(byteBuffer.array())
        out.flush()
        out.close()
    }

    fun release() {
        soundPool.release()
    }
}
