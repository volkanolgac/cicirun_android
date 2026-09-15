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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
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
import com.example.game.data.GameStrings
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
    var showTutorial by remember { mutableStateOf(false) }

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

    BoxWithConstraints(
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
        val isLandscape = maxWidth > maxHeight || maxHeight < 520.dp

        if (isLandscape) {
            StartScreenLandscape(
                engine = engine,
                storage = storage,
                bounceScale = bounceScale,
                onOpenCharacterSelect = { showCharacterSelect = true },
                onOpenTutorial = { showTutorial = true }
            )
        } else {
            StartScreenPortrait(
                engine = engine,
                storage = storage,
                bounceScale = bounceScale,
                onOpenCharacterSelect = { showCharacterSelect = true },
                onOpenTutorial = { showTutorial = true }
            )
        }

        // Character Select Modal Sheet
        if (showCharacterSelect) {
            CharacterSelectOverlay(
                engine = engine,
                storage = storage,
                onDismiss = { showCharacterSelect = false }
            )
        }

        // Tutorial Modal Overlay
        if (showTutorial) {
            TutorialOverlay(
                lang = storage.language,
                onDismiss = { showTutorial = false }
            )
        }
    }
}

@Composable
private fun StartScreenPortrait(
    engine: GameEngine,
    storage: GameStorage,
    bounceScale: Float,
    onOpenCharacterSelect: () -> Unit,
    onOpenTutorial: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Stars Balance + Language Switcher + Tutorial + Sound & Haptics
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
                        contentDescription = "Stars",
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

            // Controls: Language (TR/EN) + Tutorial (?) + Audio & Haptics
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher Button (🇹🇷 TR / 🇬🇧 EN)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CiciDark.copy(alpha = 0.8f),
                    modifier = Modifier.clickable {
                        storage.language = if (storage.language == "en") "tr" else "en"
                    }.testTag("language_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (storage.language == "en") "🇬🇧 EN" else "🇹🇷 TR",
                            color = CiciYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Tutorial Guide Button
                Surface(
                    shape = CircleShape,
                    color = CiciDark.copy(alpha = 0.8f)
                ) {
                    IconButton(
                        onClick = onOpenTutorial,
                        modifier = Modifier.size(44.dp).testTag("tutorial_open_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Tutorial",
                            tint = CiciSkyLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Sound Toggle
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

                // Haptics Toggle
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

        Spacer(modifier = Modifier.height(8.dp))

        // Center Content: Title, Character Display, High Score
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Game Title Logo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CiCi Run",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = CiciYellow,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .scale(bounceScale)
                        .testTag("game_title")
                )
                Text(
                    text = GameStrings.subTitle(storage.language),
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
                    .size(105.dp)
                    .clickable { onOpenCharacterSelect() }
                    .testTag("character_preview")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = Color(engine.selectedSkin.bodyColor),
                        modifier = Modifier.size(76.dp),
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = engine.selectedSkin.emoji,
                                fontSize = 40.sp
                            )
                        }
                    }
                }
            }

            Text(
                text = engine.selectedSkin.localizedName(storage.language),
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
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
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
                                text = GameStrings.highScore(storage.language),
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
                            text = GameStrings.bestDistance(storage.language),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Actions: Big Play Button & Secondary Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Play Button
            Button(
                onClick = { engine.startGame() },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(60.dp)
                    .shadow(12.dp, RoundedCornerShape(30.dp))
                    .testTag("play_button"),
                shape = RoundedCornerShape(30.dp),
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
                        contentDescription = "Play",
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = GameStrings.play(storage.language),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Row of Secondary Buttons: Characters & Tutorial
            Row(
                modifier = Modifier.fillMaxWidth(0.88f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Character Select Button
                OutlinedButton(
                    onClick = onOpenCharacterSelect,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("character_select_button"),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Karakter",
                            tint = CiciSkyLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = GameStrings.characters(storage.language),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                // How to Play Tutorial Button
                OutlinedButton(
                    onClick = onOpenTutorial,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("how_to_play_button"),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Tutorial",
                            tint = CiciYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = GameStrings.howToPlay(storage.language),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            // How to play hint
            Text(
                text = GameStrings.hintJumpSlide(storage.language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun StartScreenLandscape(
    engine: GameEngine,
    storage: GameStorage,
    bounceScale: Float,
    onOpenCharacterSelect: () -> Unit,
    onOpenTutorial: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Stars on left, Controls on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total Stars Bank
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CiciDark.copy(alpha = 0.8f),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = CiciCoinGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${storage.totalCoins}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Controls: Language (TR/EN) + Tutorial (?) + Audio & Haptics
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher Button (🇹🇷 TR / 🇬🇧 EN)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CiciDark.copy(alpha = 0.8f),
                    modifier = Modifier.clickable {
                        storage.language = if (storage.language == "en") "tr" else "en"
                    }.testTag("language_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (storage.language == "en") "🇬🇧 EN" else "🇹🇷 TR",
                            color = CiciYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Tutorial Guide Button
                Surface(
                    shape = CircleShape,
                    color = CiciDark.copy(alpha = 0.8f)
                ) {
                    IconButton(
                        onClick = onOpenTutorial,
                        modifier = Modifier.size(36.dp).testTag("tutorial_open_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Tutorial",
                            tint = CiciSkyLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Sound Toggle
                Surface(
                    shape = CircleShape,
                    color = CiciDark.copy(alpha = 0.8f)
                ) {
                    IconButton(
                        onClick = {
                            storage.isSoundEnabled = !storage.isSoundEnabled
                            engine.audio.soundEnabled = storage.isSoundEnabled
                        },
                        modifier = Modifier.size(36.dp).testTag("sound_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (storage.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Ses",
                            tint = if (storage.isSoundEnabled) CiciYellow else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Haptics Toggle
                Surface(
                    shape = CircleShape,
                    color = CiciDark.copy(alpha = 0.8f)
                ) {
                    IconButton(
                        onClick = {
                            storage.isHapticsEnabled = !storage.isHapticsEnabled
                            engine.audio.hapticsEnabled = storage.isHapticsEnabled
                        },
                        modifier = Modifier.size(36.dp).testTag("haptics_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Titreşim",
                            tint = if (storage.isHapticsEnabled) CiciYellow else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main 2-Column Split: Left (Title + Mascot + High Scores), Right (Play + Secondary Buttons + Hint)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT COLUMN: Title, Mascot, High Score Card
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CiCi Run",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = CiciYellow,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .scale(bounceScale)
                            .testTag("game_title")
                    )
                    Text(
                        text = GameStrings.subTitle(storage.language),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CiciSkyLight,
                        letterSpacing = 1.5.sp
                    )
                }

                // Mascot Avatar Preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clickable { onOpenCharacterSelect() }
                        .padding(vertical = 2.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(engine.selectedSkin.bodyColor).copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("character_preview")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Surface(
                                shape = CircleShape,
                                color = Color(engine.selectedSkin.bodyColor),
                                modifier = Modifier.size(40.dp),
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = engine.selectedSkin.emoji,
                                        fontSize = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text(
                            text = engine.selectedSkin.localizedName(storage.language),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = GameStrings.changeCharacter(storage.language),
                            color = CiciSkyLight,
                            fontSize = 10.sp
                        )
                    }
                }

                // High Score Card (compact)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CiciCardBg.copy(alpha = 0.9f),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth(0.95f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Kupa",
                                    tint = CiciCoinGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = GameStrings.highScore(storage.language),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "${storage.highScore}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CiciYellow
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(Color.White.copy(alpha = 0.15f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = GameStrings.bestDistance(storage.language),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${storage.bestDistance}m",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CiciSkyLight
                            )
                        }
                    }
                }
            }

            // RIGHT COLUMN: Big Play Button + Secondary Buttons + Hint
            Column(
                modifier = Modifier.weight(1.1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play Button
                Button(
                    onClick = { engine.startGame() },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(26.dp))
                        .testTag("play_button"),
                    shape = RoundedCornerShape(26.dp),
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
                            contentDescription = "Play",
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = GameStrings.play(storage.language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // Row of Secondary Buttons: Characters & Tutorial
                Row(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Character Select Button
                    OutlinedButton(
                        onClick = onOpenCharacterSelect,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("character_select_button"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Karakter",
                                tint = CiciSkyLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = GameStrings.characters(storage.language),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }

                    // How to Play Tutorial Button
                    OutlinedButton(
                        onClick = onOpenTutorial,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("how_to_play_button"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Tutorial",
                                tint = CiciYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = GameStrings.howToPlay(storage.language),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // How to play hint
                Text(
                    text = GameStrings.hintJumpSlide(storage.language),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

