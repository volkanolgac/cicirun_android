package com.example.game.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.game.models.AVAILABLE_SKINS

class GameStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("cicirun_game_prefs", Context.MODE_PRIVATE)

    var highScore by mutableIntStateOf(prefs.getInt(KEY_HIGH_SCORE, 0))
        private set

    var bestDistance by mutableIntStateOf(prefs.getInt(KEY_BEST_DISTANCE, 0))
        private set

    var totalCoins by mutableIntStateOf(prefs.getInt(KEY_TOTAL_COINS, 0))
        private set

    var selectedSkinId by mutableStateOf(prefs.getString(KEY_SELECTED_SKIN, "classic_cici") ?: "classic_cici")
        private set

    private val _language = mutableStateOf(prefs.getString(KEY_LANGUAGE, "tr") ?: "tr")
    var language: String
        get() = _language.value
        set(value) {
            _language.value = value
            prefs.edit().putString(KEY_LANGUAGE, value).apply()
        }

    fun toggleLanguage() {
        language = if (language == "tr") "en" else "tr"
    }

    private val _isSoundEnabled = mutableStateOf(prefs.getBoolean(KEY_SOUND, true))
    var isSoundEnabled: Boolean
        get() = _isSoundEnabled.value
        set(value) {
            _isSoundEnabled.value = value
            prefs.edit().putBoolean(KEY_SOUND, value).apply()
        }

    private val _isHapticsEnabled = mutableStateOf(prefs.getBoolean(KEY_HAPTICS, true))
    var isHapticsEnabled: Boolean
        get() = _isHapticsEnabled.value
        set(value) {
            _isHapticsEnabled.value = value
            prefs.edit().putBoolean(KEY_HAPTICS, value).apply()
        }

    private val unlockedSkinsState = mutableStateMapOf<String, Boolean>().apply {
        put("classic_cici", true)
        AVAILABLE_SKINS.forEach { skin ->
            if (prefs.getBoolean(KEY_UNLOCKED_PREFIX + skin.id, false)) {
                put(skin.id, true)
            }
        }
    }

    fun isSkinUnlocked(skinId: String): Boolean {
        if (skinId == "classic_cici") return true
        return unlockedSkinsState[skinId] ?: prefs.getBoolean(KEY_UNLOCKED_PREFIX + skinId, false)
    }

    fun unlockSkin(skinId: String) {
        unlockedSkinsState[skinId] = true
        prefs.edit().putBoolean(KEY_UNLOCKED_PREFIX + skinId, true).apply()
    }

    fun selectSkin(skinId: String) {
        selectedSkinId = skinId
        prefs.edit().putString(KEY_SELECTED_SKIN, skinId).apply()
    }

    fun addCoins(coinsEarned: Int) {
        totalCoins += coinsEarned
        prefs.edit().putInt(KEY_TOTAL_COINS, totalCoins).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        if (totalCoins >= amount) {
            totalCoins -= amount
            prefs.edit().putInt(KEY_TOTAL_COINS, totalCoins).apply()
            return true
        }
        return false
    }

    fun updateScores(currentScore: Int, currentDistance: Int): Boolean {
        var isNewHighScore = false
        if (currentScore > highScore) {
            highScore = currentScore
            prefs.edit().putInt(KEY_HIGH_SCORE, highScore).apply()
            isNewHighScore = true
        }
        if (currentDistance > bestDistance) {
            bestDistance = currentDistance
            prefs.edit().putInt(KEY_BEST_DISTANCE, bestDistance).apply()
        }
        return isNewHighScore
    }

    companion object {
        private const val KEY_HIGH_SCORE = "key_high_score"
        private const val KEY_BEST_DISTANCE = "key_best_distance"
        private const val KEY_TOTAL_COINS = "key_total_coins"
        private const val KEY_SELECTED_SKIN = "key_selected_skin"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_SOUND = "key_sound_enabled"
        private const val KEY_HAPTICS = "key_haptics_enabled"
        private const val KEY_UNLOCKED_PREFIX = "skin_unlocked_"
    }
}
