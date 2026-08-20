package com.example.game.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.data.GameStorage
import com.example.game.engine.GameEngine
import com.example.ui.theme.CiciCardBg
import com.example.ui.theme.CiciCoinGold
import com.example.ui.theme.CiciDark
import com.example.ui.theme.CiciGrassLight
import com.example.ui.theme.CiciOrange
import com.example.ui.theme.CiciSkyDark
import com.example.ui.theme.CiciSkyLight
import com.example.ui.theme.CiciYellow

@Composable
fun StartScreen(
    engine: GameEngine,
    storage: GameStorage,
    modifier: Modifier = Modifier
) {
    var showCharacterSelect by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "title_bounce")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A).copy(alpha = 0.88f),
                        Color(0xFF1E293B).copy(alpha = 0.94f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Sound, Haptics, Total Stars Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Stars Bank
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CiciDark.copy(alpha = 0.8f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Yıldız",
                            tint = CiciCoinGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${storage.totalCoins}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                // Audio & Haptic Controls
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = CiciDark.copy(alpha = 0.8f)
                    ) {
                        IconButton(
                            onClick = {
                                storage.isSoundEnabled = !storage.isSoundEnabled
                                engine.audio.soundEnabled = storage.isSoundEnabled
                            },
                            modifier = Modifier.size(44.dp).testTag("sound_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (storage.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Ses",
                                tint = if (storage.isSoundEnabled) CiciYellow else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = CiciDark.copy(alpha = 0.8f)
                    ) {
                        IconButton(
                            onClick = {
                                storage.isHapticsEnabled = !storage.isHapticsEnabled
                                engine.audio.hapticsEnabled = storage.isHapticsEnabled
                            },
                            modifier = Modifier.size(44.dp).testTag("haptics_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Titreşim",
                                tint = if (storage.isHapticsEnabled) CiciYellow else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Center Content: Title, Character Display, High Score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Game Title Logo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CiCi Run",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = CiciYellow,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .scale(bounceScale)
                            .testTag("game_title")
                    )
                    Text(
                        text = "SONSUZ KOŞU MACERASI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CiciSkyLight,
                        letterSpacing = 2.sp
                    )
                }

                // Mascot Preview Avatar
                Surface(
                    shape = CircleShape,
                    color = Color(engine.selectedSkin.bodyColor).copy(alpha = 0.25f),
                    modifier = Modifier
                        .size(110.dp)
                        .clickable { showCharacterSelect = true }
                        .testTag("character_preview")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = Color(engine.selectedSkin.bodyColor),
                            modifier = Modifier.size(80.dp),
                            shadowElevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = engine.selectedSkin.emoji,
                                    fontSize = 42.sp
                                )
                            }
                        }
                    }
                }

                Text(
                    text = engine.selectedSkin.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                // High Score Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CiciCardBg.copy(alpha = 0.9f),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Kupa",
                                    tint = CiciCoinGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "EN İYİ SKOR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "${storage.highScore}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = CiciYellow
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(Color.White.copy(alpha = 0.15f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "EN UZAK MESAFE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${storage.bestDistance}m",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = CiciSkyLight
                            )
                        }
                    }
                }
            }

            // Bottom Actions: Big Play Button & Character Select Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play Button
                Button(
                    onClick = { engine.startGame() },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(64.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp))
                        .testTag("play_button"),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CiciYellow,
                        contentColor = CiciDark
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Oyna",
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "OYNA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // Character Select Button
                OutlinedButton(
                    onClick = { showCharacterSelect = true },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(48.dp)
                        .testTag("character_select_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Karakter",
                            tint = CiciSkyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Karakter Değiştir",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // How to play hint
                Text(
                    text = "💡 Zıplamak için Dokun (3x Zıplama!) • Kaymak için Basılı Tut",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Character Select Modal Sheet
        if (showCharacterSelect) {
            CharacterSelectOverlay(
                engine = engine,
                storage = storage,
                onDismiss = { showCharacterSelect = false }
            )
        }
    }
}
