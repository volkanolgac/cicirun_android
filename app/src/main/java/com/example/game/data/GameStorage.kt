package com.example.game.data

import android.content.Context
import android.content.SharedPreferences

class GameStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cicirun_game_prefs", Context.MODE_PRIVATE)

    var highScore: Int
        get() = prefs.getInt(KEY_HIGH_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_HIGH_SCORE, value).apply()

    var bestDistance: Int
        get() = prefs.getInt(KEY_BEST_DISTANCE, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_DISTANCE, value).apply()

    var totalCoins: Int
        get() = prefs.getInt(KEY_TOTAL_COINS, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_COINS, value).apply()

    var selectedSkinId: String
        get() = prefs.getString(KEY_SELECTED_SKIN, "classic_cici") ?: "classic_cici"
        set(value) = prefs.edit().putString(KEY_SELECTED_SKIN, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    fun isSkinUnlocked(skinId: String): Boolean {
        if (skinId == "classic_cici") return true
        return prefs.getBoolean(KEY_UNLOCKED_PREFIX + skinId, false)
    }

    fun unlockSkin(skinId: String) {
        prefs.edit().putBoolean(KEY_UNLOCKED_PREFIX + skinId, true).apply()
    }

    fun addCoins(coinsEarned: Int) {
        totalCoins += coinsEarned
    }

    fun updateScores(currentScore: Int, currentDistance: Int): Boolean {
        var isNewHighScore = false
        if (currentScore > highScore) {
            highScore = currentScore
            isNewHighScore = true
        }
        if (currentDistance > bestDistance) {
            bestDistance = currentDistance
        }
        return isNewHighScore
    }

    companion object {
        private const val KEY_HIGH_SCORE = "key_high_score"
        private const val KEY_BEST_DISTANCE = "key_best_distance"
        private const val KEY_TOTAL_COINS = "key_total_coins"
        private const val KEY_SELECTED_SKIN = "key_selected_skin"
        private const val KEY_SOUND = "key_sound_enabled"
        private const val KEY_HAPTICS = "key_haptics_enabled"
        private const val KEY_UNLOCKED_PREFIX = "skin_unlocked_"
    }
}
