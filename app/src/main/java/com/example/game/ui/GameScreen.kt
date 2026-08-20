package com.example.game.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.game.data.GameStorage
import com.example.game.engine.GameAudio
import com.example.game.engine.GameEngine
import com.example.game.models.GameStatus

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val storage = remember { GameStorage(context) }
    val audio = remember {
        GameAudio(context).apply {
            soundEnabled = storage.isSoundEnabled
            hapticsEnabled = storage.isHapticsEnabled
        }
    }
    val engine = remember { GameEngine(storage, audio) }

    // Real-time 60fps Game Loop with precise delta-time
    LaunchedEffect(engine.status) {
        if (engine.status == GameStatus.PLAYING) {
            var lastFrameTime = withFrameNanos { it }
            while (true) {
                withFrameNanos { currentFrameTime ->
                    val dt = ((currentFrameTime - lastFrameTime) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    lastFrameTime = currentFrameTime
                    engine.update(dt)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Base Game Canvas (Always active so scene is rendered underneath overlays)
        GameCanvas(engine = engine)

        // Overlay states
        when (engine.status) {
            GameStatus.MENU -> {
                StartScreen(engine = engine, storage = storage)
            }
            GameStatus.PLAYING -> {
                GameHud(engine = engine)
            }
            GameStatus.PAUSED -> {
                GameHud(engine = engine)
                PauseOverlay(engine = engine)
            }
            GameStatus.GAME_OVER -> {
                GameOverOverlay(engine = engine, storage = storage)
            }
        }
    }
}
