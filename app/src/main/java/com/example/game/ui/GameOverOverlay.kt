package com.example.game.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.CiciOrange
import com.example.ui.theme.CiciYellow

@Composable
fun GameOverOverlay(
    engine: GameEngine,
    storage: GameStorage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "game_over_trophy")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    fun shareOnPlatform(platform: String) {
        val shareText = "CiCi Run oyununda ${engine.score} Puan ve ${engine.distance.toInt()}m Mesafe rekoru kırdım! ${engine.selectedSkin.emoji} Sen de bana katıl ve oyna! #CiCiRun #Game"
        val encodedText = Uri.encode(shareText)

        try {
            when (platform) {
                "twitter" -> {
                    val tweetUrl = "https://twitter.com/intent/tweet?text=$encodedText"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))
                    context.startActivity(intent)
                }
                "facebook" -> {
                    val fbUrl = "https://www.facebook.com/sharer/sharer.php?quote=$encodedText&u=https://cicirun.app"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl))
                    context.startActivity(intent)
                }
                "instagram" -> {
                    // Instagram direct share intent or general share sheet
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        setPackage("com.instagram.android")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val igUrl = "https://www.instagram.com/"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(igUrl)))
                    }
                }
                "tiktok" -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        setPackage("com.zhiliaoapp.musically")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val tiktokUrl = "https://www.tiktok.com/"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tiktokUrl)))
                    }
                }
                else -> {
                    // General share intent fallback
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Skorunu Paylaş")
                    context.startActivity(shareIntent)
                }
            }
        } catch (_: Exception) {
            // Safe fallback
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Skorunu Paylaş"))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A).copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = CiciCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Game Over Title
                    Text(
                        text = "OYUN BİTTİ",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = 1.5.sp
                    )

                    // New Best Record Badge
                    if (engine.isNewBest) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CiciYellow,
                            modifier = Modifier.scale(trophyScale)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Kupa",
                                    tint = CiciDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "YENİ REKOR!",
                                    color = CiciDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Final Score Display
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = CiciDark.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TOPLAM SKOR",
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${engine.score}",
                                color = CiciYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 42.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid (Distance, Coins, Best Score)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Distance Stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CiciDark.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Mesafe",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${engine.distance.toInt()}m",
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // Stars Collected Stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CiciDark.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Toplanan",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = CiciCoinGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "+${engine.coinsThisRun}",
                                        color = CiciCoinGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Best Record Stat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CiciDark.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "En İyi",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${storage.highScore}",
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Social Media Share Section: "Skorumu Paylaş" with Facebook, Instagram, X, TikTok
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CiciDark.copy(alpha = 0.65f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Paylaş",
                                    tint = CiciYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Skorumu Paylaş",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }

                            // 4 Social Platform Buttons (Facebook, Instagram, X, TikTok)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Facebook Button
                                SocialIconButton(
                                    name = "Facebook",
                                    tag = "share_facebook_button",
                                    backgroundColor = Color(0xFF1877F2),
                                    onClick = { shareOnPlatform("facebook") }
                                ) {
                                    // Custom vector "f"
                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        val p = Path().apply {
                                            moveTo(size.width * 0.62f, size.height * 0.88f)
                                            lineTo(size.width * 0.44f, size.height * 0.88f)
                                            lineTo(size.width * 0.44f, size.height * 0.52f)
                                            lineTo(size.width * 0.32f, size.height * 0.52f)
                                            lineTo(size.width * 0.32f, size.height * 0.38f)
                                            lineTo(size.width * 0.44f, size.height * 0.38f)
                                            lineTo(size.width * 0.44f, size.height * 0.26f)
                                            quadraticTo(size.width * 0.44f, size.height * 0.12f, size.width * 0.62f, size.height * 0.12f)
                                            lineTo(size.width * 0.74f, size.height * 0.12f)
                                            lineTo(size.width * 0.74f, size.height * 0.25f)
                                            lineTo(size.width * 0.62f, size.height * 0.25f)
                                            quadraticTo(size.width * 0.56f, size.height * 0.25f, size.width * 0.56f, size.height * 0.32f)
                                            lineTo(size.width * 0.56f, size.height * 0.38f)
                                            lineTo(size.width * 0.72f, size.height * 0.38f)
                                            lineTo(size.width * 0.70f, size.height * 0.52f)
                                            lineTo(size.width * 0.56f, size.height * 0.52f)
                                            lineTo(size.width * 0.56f, size.height * 0.88f)
                                            close()
                                        }
                                        drawPath(p, color = Color.White)
                                    }
                                }

                                // Instagram Button
                                SocialIconButton(
                                    name = "Instagram",
                                    tag = "share_instagram_button",
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                                    ),
                                    onClick = { shareOnPlatform("instagram") }
                                ) {
                                    // Custom camera icon
                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        drawRoundRect(
                                            color = Color.White,
                                            topLeft = Offset(3f, 3f),
                                            size = Size(size.width - 6f, size.height - 6f),
                                            cornerRadius = CornerRadius(6f, 6f),
                                            style = Stroke(width = 2.5f)
                                        )
                                        drawCircle(
                                            color = Color.White,
                                            radius = 4.5f,
                                            center = Offset(size.width / 2, size.height / 2),
                                            style = Stroke(width = 2.5f)
                                        )
                                        drawCircle(
                                            color = Color.White,
                                            radius = 1.5f,
                                            center = Offset(size.width - 7f, 7f)
                                        )
                                    }
                                }

                                // X (Twitter) Button
                                SocialIconButton(
                                    name = "X",
                                    tag = "share_x_button",
                                    backgroundColor = Color(0xFF000000),
                                    onClick = { shareOnPlatform("twitter") }
                                ) {
                                    // Custom stylized X
                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        drawLine(
                                            color = Color.White,
                                            start = Offset(4f, 4f),
                                            end = Offset(size.width - 4f, size.height - 4f),
                                            strokeWidth = 3f
                                        )
                                        drawLine(
                                            color = Color.White,
                                            start = Offset(size.width - 4f, 4f),
                                            end = Offset(4f, size.height - 4f),
                                            strokeWidth = 3f
                                        )
                                    }
                                }

                                // TikTok Button
                                SocialIconButton(
                                    name = "TikTok",
                                    tag = "share_tiktok_button",
                                    backgroundColor = Color(0xFF010101),
                                    onClick = { shareOnPlatform("tiktok") }
                                ) {
                                    // Musical Note Icon
                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        // Note Stem & flag
                                        val path = Path().apply {
                                            moveTo(size.width * 0.55f, size.height * 0.2f)
                                            lineTo(size.width * 0.55f, size.height * 0.65f)
                                        }
                                        drawPath(path, color = Color(0xFF00F2FE), style = Stroke(width = 3f))
                                        drawPath(path, color = Color(0xFFFE2C55), style = Stroke(width = 2f))
                                        drawCircle(
                                            color = Color.White,
                                            radius = 4f,
                                            center = Offset(size.width * 0.42f, size.height * 0.68f)
                                        )
                                        val flag = Path().apply {
                                            moveTo(size.width * 0.55f, size.height * 0.2f)
                                            quadraticTo(size.width * 0.75f, size.height * 0.2f, size.width * 0.85f, size.height * 0.38f)
                                        }
                                        drawPath(flag, color = Color.White, style = Stroke(width = 2.5f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Play Again Button (Large & vibrant)
                    Button(
                        onClick = { engine.startGame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("play_again_button"),
                        shape = RoundedCornerShape(28.dp),
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
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Tekrar Oyna",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "TEKRAR OYNA",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Main Menu Button
                    OutlinedButton(
                        onClick = { engine.returnToMenu() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("game_over_menu_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Ana Menü",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Ana Menü",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialIconButton(
    name: String,
    tag: String,
    backgroundColor: Color = Color.Transparent,
    brush: Brush? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (brush == null) backgroundColor else Color.Transparent,
        modifier = Modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .then(
                if (brush != null) Modifier.background(brush) else Modifier
            )
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
