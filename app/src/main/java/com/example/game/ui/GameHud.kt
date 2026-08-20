package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.GameEngine
import com.example.game.models.CollectibleType
import com.example.ui.theme.CiciCoinGold
import com.example.ui.theme.CiciDark
import com.example.ui.theme.CiciMagnetPurple
import com.example.ui.theme.CiciOrange
import com.example.ui.theme.CiciShieldBlue
import com.example.ui.theme.CiciSpeedOrange
import com.example.ui.theme.CiciYellow

@Composable
fun GameHud(
    engine: GameEngine,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val bannerPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "banner_pulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Full screen gesture overlay (tap right half to jump, tap left half to slide)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            val isRightSide = offset.x > size.width * 0.45f
                            if (isRightSide) {
                                engine.onJumpPressed()
                            } else {
                                engine.onDuckPressed(true)
                                tryAwaitRelease()
                                engine.onDuckPressed(false)
                            }
                        }
                    )
                }
        )

        // Top Status HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score & Distance Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Badge (Large & readable)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CiciDark.copy(alpha = 0.82f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${engine.score}",
                            color = CiciYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "PUAN",
                            color = Color.White.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Coins Collected Badge
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CiciDark.copy(alpha = 0.82f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Yıldız",
                            tint = CiciCoinGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${engine.coinsThisRun}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                // Distance Badge
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CiciDark.copy(alpha = 0.82f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "${engine.distance.toInt()}m",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Pause Button
            Surface(
                shape = CircleShape,
                color = CiciDark.copy(alpha = 0.82f),
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = { engine.pauseGame() },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Durdur",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Active Power-Ups Indicators
        if (engine.activePowerUps.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 68.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (powerUp in engine.activePowerUps) {
                    val (color, name, icon) = when (powerUp.type) {
                        CollectibleType.SHIELD -> Triple(CiciShieldBlue, "KALKAN", Icons.Default.Shield)
                        CollectibleType.MAGNET -> Triple(CiciMagnetPurple, "MIKNATIS", Icons.Default.Star)
                        CollectibleType.SPEED_BOOST -> Triple(CiciSpeedOrange, "HIZ GAZI 2X", Icons.Default.Bolt)
                        else -> Triple(CiciYellow, "GÜÇ", Icons.Default.Star)
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CiciDark.copy(alpha = 0.88f),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = name,
                                color = color,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            LinearProgressIndicator(
                                progress = { (powerUp.remainingTimeSec / powerUp.totalDurationSec).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = color,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }

        // Center World Notification Banner (Every 100 Points Milestone)
        AnimatedVisibility(
            visible = engine.worldBannerText != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 90.dp)
        ) {
            engine.worldBannerText?.let { bannerText ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CiciDark.copy(alpha = 0.92f),
                    shadowElevation = 12.dp,
                    modifier = Modifier.scale(bannerPulse)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = engine.worldBannerEmoji ?: "🌍",
                                fontSize = 24.sp
                            )
                            Text(
                                text = bannerText,
                                color = CiciYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = engine.currentWorld.subtitle,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Center Minute Difficulty Banner (Every 1 minute played)
        AnimatedVisibility(
            visible = engine.difficultyBannerText != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 100.dp)
        ) {
            engine.difficultyBannerText?.let { diffText ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFDC2626).copy(alpha = 0.92f),
                    shadowElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Hız",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = diffText,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Bottom Action Control Touch Pads (Large, dynamic & eye-catching for players)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DYNAMIC & EYE-CATCHING SLIDE / DUCK BUTTON
            val isSliding = engine.player.isDucking
            val slideBgBrush = if (isSliding) {
                Brush.linearGradient(
                    listOf(Color(0xFFFF0055), Color(0xFFFF5400), Color(0xFFFF7A00))
                )
            } else {
                Brush.linearGradient(
                    listOf(Color(0xFF00E5FF), Color(0xFF0091EA), Color(0xFF2979FF))
                )
            }
            val slideBorderColor = if (isSliding) Color(0xFFFFD166) else Color(0xFFE0F7FA)

            Box(
                modifier = Modifier
                    .size(width = 148.dp, height = 86.dp)
                    .shadow(
                        elevation = if (isSliding) 18.dp else 12.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = if (isSliding) Color(0xFFFF0055) else Color(0xFF00E5FF),
                        spotColor = if (isSliding) Color(0xFFFF5400) else Color(0xFF2979FF)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(slideBgBrush)
                    .border(
                        width = 2.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(slideBorderColor, slideBorderColor.copy(alpha = 0.4f))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                engine.onDuckPressed(true)
                                tryAwaitRelease()
                                engine.onDuckPressed(false)
                            }
                        )
                    }
                    .testTag("slide_button"),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle diagonal shine lines
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dynamic Icon Badge
                    Surface(
                        shape = CircleShape,
                        color = if (isSliding) Color.White.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.22f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Kay",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "KAY!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 0.8.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFFFFE600),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isSliding) "⚡ KAYIYOR!" else "ALTINDAN GEÇ",
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // DYNAMIC JUMP BUTTON
            Box(
                modifier = Modifier
                    .size(width = 148.dp, height = 86.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = CiciOrange,
                        spotColor = CiciYellow
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFEA00), Color(0xFFFF9100), Color(0xFFFF5400))
                        )
                    )
                    .border(
                        width = 2.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.25f))
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                engine.onJumpPressed()
                            }
                        )
                    }
                    .testTag("jump_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Jump Icon Badge
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.12f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Zıpla",
                                tint = CiciDark,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ZIPLA!",
                            color = CiciDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = when (engine.player.jumpCount) {
                                1 -> "⚡ 2. ZIPLA!"
                                2 -> "🔥 3. ZIPLA!"
                                else -> "3X ZIPLAMA"
                            },
                            color = Color(0xFF78350F),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
