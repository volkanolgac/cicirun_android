package com.example.game.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.BiasAlignment
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.game.data.GameStorage
import com.example.game.data.GameStrings
import com.example.game.engine.GameEngine
import com.example.ui.theme.CiciCardBg
import com.example.ui.theme.CiciCoinGold
import com.example.ui.theme.CiciDark
import com.example.ui.theme.CiciYellow
import java.io.File
import java.io.FileOutputStream

@Composable
fun GameOverOverlay(
    engine: GameEngine,
    storage: GameStorage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val lang = storage.language
    val isTr = lang != "en"

    val infiniteTransition = rememberInfiniteTransition(label = "game_over_trophy")
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    fun captureAndShareScreenshot() {
        try {
            val activity = context as? Activity
            val window = activity?.window
            val view = window?.decorView?.rootView

            val bitmap: Bitmap = if (view != null && view.width > 0 && view.height > 0) {
                val b = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val c = android.graphics.Canvas(b)
                view.draw(c)
                b
            } else {
                val b = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888)
                val c = android.graphics.Canvas(b)
                c.drawColor(android.graphics.Color.parseColor("#0F172A"))
                val paint = Paint().apply {
                    color = android.graphics.Color.parseColor("#FFD166")
                    textSize = 52f
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }
                c.drawText("CiCi Run", 400f, 150f, paint)
                paint.color = android.graphics.Color.WHITE
                paint.textSize = 38f
                paint.isFakeBoldText = false
                c.drawText("Skor: ${engine.score}", 400f, 300f, paint)
                c.drawText("Mesafe: ${engine.distance.toInt()}m", 400f, 380f, paint)
                c.drawText("Karakter: ${engine.selectedSkin.name}", 400f, 460f, paint)
                b
            }

            val screenshotsDir = File(context.cacheDir, "screenshots").apply { mkdirs() }
            val imageFile = File(screenshotsDir, "cicirun_score_${System.currentTimeMillis()}.png")
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                imageFile
            )

            val shareText = if (isTr) {
                "CiCi Run oyununda ${engine.score} Puan ve ${engine.distance.toInt()}m Mesafe skoru yaptım! ${engine.selectedSkin.emoji} Sen de oyna! #CiCiRun"
            } else {
                "I scored ${engine.score} Points and ${engine.distance.toInt()}m in CiCi Run! ${engine.selectedSkin.emoji} Play now! #CiCiRun"
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, if (isTr) "Ekran Görüntüsünü Paylaş" else "Share Screenshot"))
        } catch (_: Exception) {
            val shareText = if (isTr) {
                "CiCi Run oyununda ${engine.score} Puan ve ${engine.distance.toInt()}m Mesafe skoru yaptım! ${engine.selectedSkin.emoji} Sen de oyna! #CiCiRun"
            } else {
                "I scored ${engine.score} Points and ${engine.distance.toInt()}m in CiCi Run! ${engine.selectedSkin.emoji} Play now! #CiCiRun"
            }
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Skorunu Paylaş"))
        }
    }

    fun shareOnPlatform(platform: String) {
        val shareText = "CiCi Run oyununda ${engine.score} Puan ve ${engine.distance.toInt()}m Mesafe skoru yaptım! ${engine.selectedSkin.emoji} Sen de oyna! #CiCiRun"
        val encodedText = Uri.encode(shareText)

        try {
            when (platform) {
                "twitter" -> {
                    val tweetUrl = "https://twitter.com/intent/tweet?text=$encodedText"
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl)))
                }
                "facebook" -> {
                    val fbUrl = "https://www.facebook.com/sharer/sharer.php?quote=$encodedText&u=https://cicirun.app"
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl)))
                }
                "instagram" -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        setPackage("com.instagram.android")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/")))
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
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/")))
                    }
                }
                else -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Skorunu Paylaş"))
                }
            }
        } catch (_: Exception) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Skorunu Paylaş"))
        }
    }

    val overlayAlignment = BiasAlignment(0f, -0.22f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 14.dp, vertical = 24.dp),
        contentAlignment = overlayAlignment
    ) {
        // Soft backdrop around the center card keeping the bottom character scene fully visible
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.60f)
                .align(overlayAlignment)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0F172A).copy(alpha = 0.55f),
                            Color(0xFF0F172A).copy(alpha = 0.55f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center Compact Card
        Card(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth(0.94f)
                .shadow(24.dp, RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CiciCardBg.copy(alpha = 0.96f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row: Game Over Title & Optional New Record Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = GameStrings.gameOver(lang),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )

                    if (engine.isNewBest) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CiciYellow,
                            modifier = Modifier.scale(trophyScale)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Kupa",
                                    tint = CiciDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = GameStrings.newRecord(lang),
                                    color = CiciDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Compact Score and Stats Grid
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CiciDark.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = GameStrings.score(lang),
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${engine.score}",
                                color = CiciYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = GameStrings.distance(lang), color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                Text(text = "${engine.distance.toInt()}m", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isTr) "Toplanan" else "Coins", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = CiciCoinGold, modifier = Modifier.size(12.dp))
                                    Text(text = "+${engine.coinsThisRun}", color = CiciCoinGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isTr) "En İyi" else "Best", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                Text(text = "${storage.highScore}", color = Color(0xFF4ADE80), fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons Row (TEKRAR OYNA + MENÜ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { engine.startGame() },
                        modifier = Modifier
                            .weight(1.15f)
                            .height(44.dp)
                            .testTag("play_again_button"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CiciYellow,
                            contentColor = CiciDark
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Play Again",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = GameStrings.playAgain(lang),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { engine.returnToMenu() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("game_over_menu_button"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Menu",
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = GameStrings.mainMenu(lang),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Social Media Share Bar (Compact with Screenshot button before Facebook)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = GameStrings.share(lang),
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Screenshot capture & share button (Placed BEFORE Facebook)
                        SocialIconButton(
                            name = if (isTr) "Ekran Görüntüsü" else "Screenshot",
                            tag = "share_screenshot_button",
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                            ),
                            onClick = { captureAndShareScreenshot() }
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
                                // Camera top flash bump
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(size.width * 0.34f, 1.5f),
                                    size = Size(size.width * 0.32f, 3.5f),
                                    cornerRadius = CornerRadius(1.5f, 1.5f)
                                )
                                // Camera body
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(1.5f, 4f),
                                    size = Size(size.width - 3f, size.height - 5.5f),
                                    cornerRadius = CornerRadius(4f, 4f),
                                    style = Stroke(width = 2f)
                                )
                                // Lens circle
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.6f,
                                    center = Offset(size.width / 2, size.height * 0.58f),
                                    style = Stroke(width = 2f)
                                )
                                // Small sensor dot
                                drawCircle(
                                    color = Color.White,
                                    radius = 1f,
                                    center = Offset(size.width - 4.5f, 7.2f)
                                )
                            }
                        }

                        // 2. Facebook
                        SocialIconButton(
                            name = "Facebook",
                            tag = "share_facebook_button",
                            backgroundColor = Color(0xFF1877F2),
                            onClick = { shareOnPlatform("facebook") }
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
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

                        // 3. Instagram
                        SocialIconButton(
                            name = "Instagram",
                            tag = "share_instagram_button",
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                            ),
                            onClick = { shareOnPlatform("instagram") }
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(2f, 2f),
                                    size = Size(size.width - 4f, size.height - 4f),
                                    cornerRadius = CornerRadius(4.5f, 4.5f),
                                    style = Stroke(width = 2f)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.5f,
                                    center = Offset(size.width / 2, size.height / 2),
                                    style = Stroke(width = 2f)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 1.2f,
                                    center = Offset(size.width - 5f, 5f)
                                )
                            }
                        }

                        // 4. X (Twitter)
                        SocialIconButton(
                            name = "X",
                            tag = "share_x_button",
                            backgroundColor = Color(0xFF000000),
                            onClick = { shareOnPlatform("twitter") }
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
                                drawLine(color = Color.White, start = Offset(3f, 3f), end = Offset(size.width - 3f, size.height - 3f), strokeWidth = 2.5f)
                                drawLine(color = Color.White, start = Offset(size.width - 3f, 3f), end = Offset(3f, size.height - 3f), strokeWidth = 2.5f)
                            }
                        }

                        // 5. TikTok
                        SocialIconButton(
                            name = "TikTok",
                            tag = "share_tiktok_button",
                            backgroundColor = Color(0xFF010101),
                            onClick = { shareOnPlatform("tiktok") }
                        ) {
                            Canvas(modifier = Modifier.size(18.dp)) {
                                val path = Path().apply {
                                    moveTo(size.width * 0.55f, size.height * 0.2f)
                                    lineTo(size.width * 0.55f, size.height * 0.65f)
                                }
                                drawPath(path, color = Color(0xFF00F2FE), style = Stroke(width = 2.5f))
                                drawPath(path, color = Color(0xFFFE2C55), style = Stroke(width = 1.8f))
                                drawCircle(color = Color.White, radius = 3.5f, center = Offset(size.width * 0.42f, size.height * 0.68f))
                                val flag = Path().apply {
                                    moveTo(size.width * 0.55f, size.height * 0.2f)
                                    quadraticTo(size.width * 0.75f, size.height * 0.2f, size.width * 0.85f, size.height * 0.38f)
                                }
                                drawPath(flag, color = Color.White, style = Stroke(width = 2f))
                            }
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
    icon: @Composable () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (brush == null) backgroundColor else Color.Transparent,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .then(if (brush != null) Modifier.background(brush) else Modifier)
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            icon()
        }
    }
}

