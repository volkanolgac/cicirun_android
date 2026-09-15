package com.example.game.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class GameAudio(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playJumpSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSweep(startFreq = 400.0, endFreq = 750.0, durationMs = 90)
        }
        triggerHaptic(30)
    }

    fun playDoubleJumpSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSweep(startFreq = 650.0, endFreq = 1100.0, durationMs = 110)
        }
        triggerHaptic(45)
    }

    fun playTripleJumpSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSweep(startFreq = 850.0, endFreq = 1450.0, durationMs = 125)
        }
        triggerHaptic(60)
    }

    fun playCoinSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(freq = 987.77, durationMs = 60) // B5
            playTone(freq = 1318.51, durationMs = 80) // E6
        }
        triggerHaptic(20)
    }

    fun playPowerUpSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(freq = 523.25, durationMs = 50) // C5
            playTone(freq = 659.25, durationMs = 50) // E5
            playTone(freq = 783.99, durationMs = 60) // G5
            playTone(freq = 1046.50, durationMs = 100) // C6
        }
        triggerHaptic(70)
    }

    fun playCrashSound() {
        if (!soundEnabled) return
        scope.launch {
            try {
                val sampleRate = 22050
                val durationMs = 1000 // Exactly 1 second crash audio
                val numSamples = (durationMs * sampleRate) / 1000
                val buffer = ShortArray(numSamples)
                var phase = 0.0
                val random = java.util.Random(1337)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate.toDouble()
                    val progress = i.toDouble() / numSamples.toDouble()

                    // Downward frequency sweep from 280 Hz down to 35 Hz
                    val currentFreq = 280.0 * (1.0 - progress * 0.85)
                    phase += 2.0 * Math.PI * currentFreq / sampleRate

                    val sine = sin(phase)
                    val noise = (random.nextDouble() * 2.0 - 1.0)

                    // Initial punchy attack + decaying rumble tail lasting 1 second
                    val attack = if (t < 0.015) (t / 0.015) else 1.0
                    val decay = Math.exp(-3.2 * t)
                    val noiseMix = if (t < 0.3) (1.0 - t / 0.3) * 0.5 else 0.08 * (1.0 - progress)

                    val sampleVal = ((sine * (1.0 - noiseMix) + noise * noiseMix) * attack * decay * 32767 * 0.85).toInt()
                    buffer[i] = sampleVal.coerceIn(-32768, 32767).toShort()
                }

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
                Thread.sleep(1050)
                audioTrack.release()
            } catch (_: Exception) {}
        }
        triggerHaptic(220)
    }

    fun playButtonSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(freq = 600.0, durationMs = 35)
        }
        triggerHaptic(15)
    }

    fun playWorldChangeSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(freq = 440.0, durationMs = 60) // A4
            playTone(freq = 554.37, durationMs = 60) // C#5
            playTone(freq = 659.25, durationMs = 80) // E5
            playTone(freq = 880.0, durationMs = 120) // A5
        }
        triggerHaptic(90)
    }

    fun playSpeedUpSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSweep(startFreq = 350.0, endFreq = 950.0, durationMs = 180)
        }
        triggerHaptic(60)
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!hapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        v.vibrate(durationMs)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun playTone(freq: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)
            val angleStep = 2.0 * Math.PI * freq / sampleRate

            for (i in 0 until numSamples) {
                // Envelope decay to prevent clicks
                val envelope = 1.0 - (i.toDouble() / numSamples.toDouble())
                val sample = (sin(i * angleStep) * 32767 * 0.4 * envelope).toInt()
                buffer[i] = sample.coerceIn(-32768, 32767).toShort()
            }

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
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.release()
        } catch (_: Exception) {}
    }

    private fun playToneSweep(startFreq: Double, endFreq: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            var phase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples.toDouble()
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                phase += 2.0 * Math.PI * currentFreq / sampleRate
                val envelope = if (progress < 0.1) progress / 0.1 else (1.0 - progress)
                val sample = (sin(phase) * 32767 * 0.45 * envelope).toInt()
                buffer[i] = sample.coerceIn(-32768, 32767).toShort()
            }

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
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.release()
        } catch (_: Exception) {}
    }
}
