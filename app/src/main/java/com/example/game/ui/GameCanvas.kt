package com.example.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.game.engine.GameEngine
import com.example.game.models.CharacterSkin
import com.example.game.models.CharacterSpecies
import com.example.game.models.Collectible
import com.example.game.models.CollectibleType
import com.example.game.models.Obstacle
import com.example.game.models.ObstacleType
import com.example.game.models.Particle
import com.example.game.models.Player
import com.example.game.models.WorldTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameCanvas(
    engine: GameEngine,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        engine.updateScreenDimensions(size.width, size.height)

        // 1. Draw Parallax Background with Current World Theme (Every 100 points new biome)
        drawWorldEnvironment(
            world = engine.currentWorld,
            scroll = engine.scrollOffset,
            distance = engine.distance,
            groundY = engine.groundY,
            width = size.width,
            height = size.height
        )

        // 2. Draw Obstacles (with clear overhead clearance for sliding)
        for (obs in engine.obstacles) {
            drawObstacle(obs)
        }

        // 3. Draw Large Collectibles (Stars & Power-ups)
        for (col in engine.collectibles) {
            drawCollectible(col)
        }

        // 4. Draw Particles
        for (p in engine.particles) {
            drawParticle(p)
        }

        // 5. Draw Big Cici Player (Kid-friendly size, vibrant and cute)
        drawCiciPlayer(
            player = engine.player,
            skin = engine.selectedSkin,
            hasShield = engine.hasShield,
            hasMagnet = engine.hasMagnet,
            hasSpeedBoost = engine.hasSpeedBoost
        )

        // 6. Draw Foreground Ground & Grass Layer styled for active World
        drawWorldGroundLayer(
            world = engine.currentWorld,
            scroll = engine.scrollOffset,
            groundY = engine.groundY,
            width = size.width,
            height = size.height
        )
    }
}

private fun DrawScope.drawWorldEnvironment(
    world: WorldTheme,
    scroll: Float,
    distance: Float,
    groundY: Float,
    width: Float,
    height: Float
) {
    // Dynamic Sky Gradient based on current world
    drawRect(
        brush = Brush.verticalGradient(
            colors = world.skyColors,
            startY = 0f,
            endY = groundY
        ),
        size = Size(width, groundY + 10f)
    )

    // Celestial Object (Sun / Moon / Galactic Orb)
    val celestialY = 90f
    val celestialX = (width * 0.82f - (scroll * 0.02f) % (width + 120f))

    if (!world.isNight) {
        // Glowing Radiant Sun
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(world.sunHaloColor, world.sunColor, Color.Transparent),
                center = Offset(celestialX, celestialY),
                radius = 65f
            ),
            radius = 65f,
            center = Offset(celestialX, celestialY)
        )
        drawCircle(
            color = world.sunColor,
            radius = 36f,
            center = Offset(celestialX, celestialY)
        )
    } else {
        // Twinkling Night Stars
        for (i in 0..18) {
            val starX = ((i * 73f + 30f) - (scroll * 0.04f)) % (width + 40f)
            val starY = 30f + (i * 37f) % (groundY * 0.45f)
            val starSize = 2f + (i % 3) * 1.5f
            drawCircle(
                color = Color.White.copy(alpha = 0.7f + (i % 3) * 0.15f),
                radius = starSize,
                center = Offset(if (starX < 0) starX + width + 40f else starX, starY)
            )
        }

        // Crescent Moon
        drawCircle(
            color = world.sunColor,
            radius = 38f,
            center = Offset(celestialX, celestialY)
        )
        drawCircle(
            color = world.skyColors.first(),
            radius = 32f,
            center = Offset(celestialX + 12f, celestialY - 8f)
        )
    }

    // Distant Clouds (Parallax 0.12x)
    val cloudScroll = scroll * 0.12f
    val cloudSpacing = 320f
    val cloudTint = if (world.isNight) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.85f)
    for (i in -1..((width / cloudSpacing).toInt() + 2)) {
        val cx = (i * cloudSpacing - (cloudScroll % cloudSpacing))
        val cy = 65f + (i % 3) * 40f
        drawCloud(cx, cy, 1.1f + (i % 3) * 0.25f, cloudTint)
    }

    // Distant Mountains / Pyramids / Cyber Peaks (Parallax 0.28x)
    val mountainScroll = scroll * 0.28f
    val mountainPath = Path().apply {
        moveTo(0f, groundY)
        val step = 160f
        for (x in -160..width.toInt() + 160 step 160) {
            val adjustedX = x - (mountainScroll % 320f)
            val peakY = groundY - 140f - ((x / 160) % 3) * 55f
            lineTo(adjustedX + 80f, peakY)
            lineTo(adjustedX + 160f, groundY)
        }
        lineTo(width, groundY)
        close()
    }
    drawPath(mountainPath, color = world.mountainColor)

    // Rolling Hills (Parallax 0.52x)
    val hillScroll = scroll * 0.52f
    val hillPath = Path().apply {
        moveTo(0f, groundY)
        for (x in -120..width.toInt() + 120 step 120) {
            val adjustedX = x - (hillScroll % 240f)
            val hY = groundY - 60f - sin((adjustedX + 60f) * 0.018f) * 38f
            lineTo(adjustedX + 60f, hY)
            lineTo(adjustedX + 120f, groundY)
        }
        close()
    }
    drawPath(hillPath, color = world.hillColor)
}

private fun DrawScope.drawCloud(cx: Float, cy: Float, scaleFactor: Float, tint: Color) {
    val r = 26f * scaleFactor
    drawCircle(tint, r, Offset(cx, cy))
    drawCircle(tint, r * 1.3f, Offset(cx + r * 1.1f, cy - r * 0.25f))
    drawCircle(tint, r * 1.15f, Offset(cx + r * 2.3f, cy))
    drawCircle(tint, r * 0.85f, Offset(cx + r * 0.6f, cy + r * 0.35f))
    drawCircle(tint, r * 0.95f, Offset(cx + r * 1.8f, cy + r * 0.35f))
}

private fun DrawScope.drawWorldGroundLayer(
    world: WorldTheme,
    scroll: Float,
    groundY: Float,
    width: Float,
    height: Float
) {
    // Soil base gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = world.soilColors,
            startY = groundY,
            endY = height
        ),
        topLeft = Offset(0f, groundY),
        size = Size(width, height - groundY)
    )

    // Lush Surface Ground Top Layer
    drawRect(
        brush = Brush.verticalGradient(
            colors = world.grassTopColors,
            startY = groundY - 10f,
            endY = groundY + 20f
        ),
        topLeft = Offset(0f, groundY - 10f),
        size = Size(width, 28f)
    )

    // Grass Tufts (scrolling)
    val grassStep = 28f
    for (x in -28..width.toInt() + 28 step 28) {
        val gx = x - (scroll % grassStep)
        val path = Path().apply {
            moveTo(gx, groundY - 10f)
            lineTo(gx + 7f, groundY - 24f)
            lineTo(gx + 14f, groundY - 10f)
            close()
        }
        drawPath(path, color = world.grassBladeColor)
    }

    // Soil Pebbles & Strata
    val pebbleStep = 70f
    for (x in -70..width.toInt() + 70 step 70) {
        val px = x - (scroll % pebbleStep)
        drawCircle(
            color = Color.Black.copy(alpha = 0.25f),
            radius = 5f,
            center = Offset(px + 20f, groundY + 34f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = 4f,
            center = Offset(px + 52f, groundY + 62f)
        )
    }
}

private fun DrawScope.drawCiciPlayer(
    player: Player,
    skin: CharacterSkin,
    hasShield: Boolean,
    hasMagnet: Boolean,
    hasSpeedBoost: Boolean
) {
    val bodyColor = Color(skin.bodyColor)
    val secColor = Color(skin.secondaryColor)
    val detailColor = Color(skin.detailColor)
    val accentColor = Color(skin.accentColor)

    val cx = player.x + player.width / 2
    val cy = player.y + player.height / 2

    // Speed boost trail flame
    if (hasSpeedBoost) {
        for (i in 1..3) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF5400).copy(alpha = 0.7f / i), Color.Transparent),
                    center = Offset(cx - i * 48f, cy),
                    radius = 70f
                ),
                radius = 70f,
                center = Offset(cx - i * 48f, cy)
            )
        }
    }

    rotate(degrees = player.rotation, pivot = Offset(cx, cy)) {
        when (skin.species) {
            CharacterSpecies.CHICK -> drawChick(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.BUNNY -> drawBunny(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.CAT -> drawCat(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.PANDA -> drawPanda(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.DOG -> drawDog(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.FOX -> drawFox(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.FROG -> drawFrog(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.BEAR -> drawBear(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.PENGUIN -> drawPenguin(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.DRAGON -> drawDragon(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
            CharacterSpecies.UNICORN -> drawUnicorn(player, bodyColor, secColor, detailColor, accentColor, cx, cy)
        }
    }

    // Shield Bubble Aura
    if (hasShield) {
        val shieldRadius = player.width * 0.75f
        drawCircle(
            color = Color(0xFF00B4D8).copy(alpha = 0.35f),
            radius = shieldRadius,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color(0xFF90E0EF),
            radius = shieldRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 5f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = 12f,
            center = Offset(cx - shieldRadius * 0.6f, cy - shieldRadius * 0.6f)
        )
    }

    // Magnet Aura Sparks
    if (hasMagnet) {
        val magnetPulse = sin(player.animFrame * 3f) * 6f
        drawCircle(
            color = Color(0xFF8338EC).copy(alpha = 0.25f),
            radius = player.width * 0.68f + magnetPulse,
            center = Offset(cx, cy),
            style = Stroke(width = 3.5f)
        )
    }
}

// 🐥 CIVCIV / KUŞ
private fun DrawScope.drawChick(
    player: Player,
    bodyColor: Color,
    wingColor: Color,
    beakColor: Color,
    headbandColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(
            color = bodyColor,
            topLeft = Offset(player.x - 12f, player.y),
            size = Size(player.width + 36f, player.height)
        )
        drawOval(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(player.x + 14f, player.y + 18f),
            size = Size(player.width * 0.6f, player.height * 0.6f)
        )
        drawRoundRect(
            color = headbandColor,
            topLeft = Offset(player.x + 60f, player.y + 10f),
            size = Size(player.width - 40f, 18f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        val beakPath = Path().apply {
            moveTo(player.x + player.width + 16f, player.y + 26f)
            lineTo(player.x + player.width + 46f, player.y + 38f)
            lineTo(player.x + player.width + 16f, player.y + 50f)
            close()
        }
        drawPath(beakPath, color = beakColor)
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 20f else -10f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 20f else 10f
        drawLine(color = beakColor, start = Offset(cx - 20f, cy + 40f), end = Offset(cx - 20f + leg1Offset, player.y + player.height + 10f), strokeWidth = 10f)
        drawLine(color = beakColor, start = Offset(cx + 20f, cy + 40f), end = Offset(cx + 20f + leg2Offset, player.y + player.height + 10f), strokeWidth = 10f)

        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = Color.White.copy(alpha = 0.38f), topLeft = Offset(player.x + 22f, player.y + 46f), size = Size(player.width * 0.55f, player.height * 0.48f))
        drawCircle(color = Color(0xFFFF5400).copy(alpha = 0.4f), radius = 13f, center = Offset(player.x + player.width - 42f, player.y + 88f))

        drawRoundRect(color = headbandColor, topLeft = Offset(player.x + 20f, player.y + 24f), size = Size(player.width - 14f, 20f), cornerRadius = CornerRadius(9f, 9f))
        val ribbonFlutter = sin(player.animFrame * 2f) * 11f
        val ribbonPath = Path().apply {
            moveTo(player.x + 20f, player.y + 34f)
            lineTo(player.x - 26f, player.y + 26f + ribbonFlutter)
            lineTo(player.x - 38f, player.y + 45f + ribbonFlutter)
            lineTo(player.x + 20f, player.y + 44f)
            close()
        }
        drawPath(ribbonPath, color = headbandColor)

        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        val beakPath = Path().apply {
            moveTo(player.x + player.width - 10f, player.y + 64f)
            lineTo(player.x + player.width + 32f, player.y + 74f)
            lineTo(player.x + player.width - 10f, player.y + 88f)
            close()
        }
        drawPath(beakPath, color = beakColor)

        val wingFlap = if (player.isGrounded) sin(player.animFrame) * 11f else -35f
        val wingPath = Path().apply {
            moveTo(player.x + 24f, cy)
            quadraticTo(player.x - 14f, cy + wingFlap - 20f, player.x + 54f, cy + wingFlap + 30f)
            close()
        }
        drawPath(wingPath, color = wingColor)
    }
}

// 🐰 TAVŞAN
private fun DrawScope.drawBunny(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        // Flattened bunny loaf with ears blown backwards
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = Color.White.copy(alpha = 0.5f), topLeft = Offset(player.x + 14f, player.y + 18f), size = Size(player.width * 0.6f, player.height * 0.6f))
        // Ears flat
        drawRoundRect(color = bodyColor, topLeft = Offset(player.x - 20f, player.y + 4f), size = Size(90f, 18f), cornerRadius = CornerRadius(9f, 9f))
        drawRoundRect(color = secColor, topLeft = Offset(player.x - 14f, player.y + 7f), size = Size(74f, 11f), cornerRadius = CornerRadius(6f, 6f))
        // Nose & Eye
        drawCircle(color = detailColor, radius = 6f, center = Offset(player.x + player.width + 20f, player.y + 36f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        // Paws
        drawCircle(color = bodyColor, radius = 14f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = bodyColor, radius = 14f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Fluffy Cottontail on back
        val tailBob = sin(player.animFrame * 2f) * 5f
        drawCircle(color = Color.White, radius = 18f, center = Offset(player.x - 4f, cy + 20f + tailBob))
        drawCircle(color = secColor.copy(alpha = 0.3f), radius = 12f, center = Offset(player.x - 4f, cy + 20f + tailBob))

        // Long Upright Bunny Ears with pink inner
        val earWiggle = sin(player.animFrame * 1.8f) * 6f
        // Left Ear
        drawRoundRect(color = bodyColor, topLeft = Offset(player.x + 32f, player.y - 50f + earWiggle), size = Size(26f, 70f), cornerRadius = CornerRadius(13f, 13f))
        drawRoundRect(color = secColor, topLeft = Offset(player.x + 36f, player.y - 44f + earWiggle), size = Size(18f, 54f), cornerRadius = CornerRadius(9f, 9f))
        // Right Ear
        drawRoundRect(color = bodyColor, topLeft = Offset(player.x + 72f, player.y - 46f - earWiggle), size = Size(26f, 66f), cornerRadius = CornerRadius(13f, 13f))
        drawRoundRect(color = secColor, topLeft = Offset(player.x + 76f, player.y - 40f - earWiggle), size = Size(18f, 50f), cornerRadius = CornerRadius(9f, 9f))

        // Chubby Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = Color.White.copy(alpha = 0.55f), topLeft = Offset(player.x + 22f, player.y + 46f), size = Size(player.width * 0.55f, player.height * 0.48f))
        drawCircle(color = secColor.copy(alpha = 0.4f), radius = 13f, center = Offset(player.x + player.width - 42f, player.y + 88f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Cute Pink Nose & Whiskers
        val noseX = player.x + player.width - 8f
        val noseY = player.y + 76f
        val nosePath = Path().apply {
            moveTo(noseX, noseY)
            lineTo(noseX + 16f, noseY + 6f)
            lineTo(noseX, noseY + 12f)
            close()
        }
        drawPath(nosePath, color = detailColor)

        // Cute Whiskers
        drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(noseX - 6f, noseY + 2f), end = Offset(noseX + 32f, noseY - 4f), strokeWidth = 3f)
        drawLine(color = Color.White.copy(alpha = 0.9f), start = Offset(noseX - 6f, noseY + 8f), end = Offset(noseX + 32f, noseY + 12f), strokeWidth = 3f)
    }
}

// 🐱 KEDİ
private fun DrawScope.drawCat(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = Color.White.copy(alpha = 0.35f), topLeft = Offset(player.x + 14f, player.y + 18f), size = Size(player.width * 0.6f, player.height * 0.6f))
        // Cat Ears flattened
        val earPath = Path().apply {
            moveTo(player.x + 40f, player.y + 8f)
            lineTo(player.x - 10f, player.y + 16f)
            lineTo(player.x + 30f, player.y + 24f)
            close()
        }
        drawPath(earPath, color = bodyColor)
        drawCircle(color = detailColor, radius = 6f, center = Offset(player.x + player.width + 18f, player.y + 36f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        drawCircle(color = bodyColor, radius = 13f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = bodyColor, radius = 13f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Swaying Cat Tail
        val tailSway = sin(player.animFrame * 1.5f) * 14f
        val tailPath = Path().apply {
            moveTo(player.x + 10f, cy + 20f)
            cubicTo(player.x - 30f, cy + tailSway, player.x - 45f, cy - 20f + tailSway, player.x - 25f, cy - 35f + tailSway)
        }
        drawPath(tailPath, color = bodyColor, style = Stroke(width = 12f))

        // Pointy Triangle Cat Ears
        val ear1 = Path().apply {
            moveTo(player.x + 28f, player.y + 20f)
            lineTo(player.x + 45f, player.y - 28f)
            lineTo(player.x + 65f, player.y + 14f)
            close()
        }
        drawPath(ear1, color = bodyColor)
        val ear1Inner = Path().apply {
            moveTo(player.x + 34f, player.y + 18f)
            lineTo(player.x + 46f, player.y - 18f)
            lineTo(player.x + 58f, player.y + 14f)
            close()
        }
        drawPath(ear1Inner, color = secColor)

        val ear2 = Path().apply {
            moveTo(player.x + 75f, player.y + 12f)
            lineTo(player.x + 98f, player.y - 24f)
            lineTo(player.x + 115f, player.y + 20f)
            close()
        }
        drawPath(ear2, color = bodyColor)

        // Chubby Cat Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = Color.White.copy(alpha = 0.4f), topLeft = Offset(player.x + 22f, player.y + 46f), size = Size(player.width * 0.55f, player.height * 0.48f))
        drawCircle(color = Color(0xFFFF5400).copy(alpha = 0.35f), radius = 13f, center = Offset(player.x + player.width - 42f, player.y + 88f))

        // Collar with Bell
        drawRoundRect(color = accentColor, topLeft = Offset(player.x + 35f, player.y + 90f), size = Size(player.width - 50f, 12f), cornerRadius = CornerRadius(6f, 6f))
        drawCircle(color = Color(0xFFFFD129), radius = 7f, center = Offset(player.x + player.width - 32f, player.y + 96f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Nose & Whiskers
        val noseX = player.x + player.width - 12f
        val noseY = player.y + 72f
        drawCircle(color = detailColor, radius = 6f, center = Offset(noseX + 6f, noseY + 6f))
        drawLine(color = Color.White, start = Offset(noseX - 4f, noseY + 2f), end = Offset(noseX + 32f, noseY - 2f), strokeWidth = 3f)
        drawLine(color = Color.White, start = Offset(noseX - 4f, noseY + 9f), end = Offset(noseX + 32f, noseY + 12f), strokeWidth = 3f)
    }
}

// 🐼 PANDA
private fun DrawScope.drawPanda(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawCircle(color = secColor, radius = 16f, center = Offset(player.x + 20f, player.y + 12f))
        drawOval(color = secColor, topLeft = Offset(player.x + player.width - 18f, player.y + 14f), size = Size(36f, 26f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f, color = Color.White)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        // Black panda paws
        drawCircle(color = secColor, radius = 14f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = secColor, radius = 14f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Black Panda Ears
        drawCircle(color = secColor, radius = 22f, center = Offset(player.x + 35f, player.y + 10f))
        drawCircle(color = secColor, radius = 22f, center = Offset(player.x + 105f, player.y + 10f))

        // Chubby White Panda Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))

        // Black Panda Vest / Arms
        drawOval(color = secColor, topLeft = Offset(player.x + 10f, cy + 5f), size = Size(50f, 50f))

        // Iconic Black Eye Patch
        drawOval(color = secColor, topLeft = Offset(player.x + player.width - 62f, player.y + 34f), size = Size(46f, 52f))

        // Rosy Cheek
        drawCircle(color = Color(0xFFFF70A6).copy(alpha = 0.4f), radius = 12f, center = Offset(player.x + player.width - 32f, player.y + 92f))

        // Anime Eye Inside Patch
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Cute Black Panda Nose & Smile
        drawOval(color = detailColor, topLeft = Offset(player.x + player.width - 16f, player.y + 72f), size = Size(16f, 12f))

        // Green Bamboo Leaf Accessory
        val leafPath = Path().apply {
            moveTo(player.x + 24f, player.y + 40f)
            quadraticTo(player.x - 8f, player.y + 26f, player.x - 14f, player.y + 48f)
            close()
        }
        drawPath(leafPath, color = accentColor)
    }
}

// 🐶 KÖPEK
private fun DrawScope.drawDog(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = secColor, topLeft = Offset(player.x + 10f, player.y + 10f), size = Size(50f, 22f))
        drawCircle(color = detailColor, radius = 7f, center = Offset(player.x + player.width + 20f, player.y + 36f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        drawCircle(color = secColor, radius = 13f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = secColor, radius = 13f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Wagging Puppy Tail
        val tailWag = sin(player.animFrame * 3f) * 15f
        val tailPath = Path().apply {
            moveTo(player.x + 8f, cy + 18f)
            quadraticTo(player.x - 25f, cy + tailWag, player.x - 36f, cy - 8f + tailWag)
        }
        drawPath(tailPath, color = bodyColor, style = Stroke(width = 12f))

        // Chubby Puppy Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = Color.White.copy(alpha = 0.45f), topLeft = Offset(player.x + 22f, player.y + 46f), size = Size(player.width * 0.55f, player.height * 0.48f))

        // Floppy Dog Ears (Curved hanging down)
        val ear1 = Path().apply {
            moveTo(player.x + 30f, player.y + 15f)
            cubicTo(player.x + 10f, player.y + 20f, player.x + 8f, player.y + 70f, player.x + 28f, player.y + 75f)
            close()
        }
        drawPath(ear1, color = secColor)

        val ear2 = Path().apply {
            moveTo(player.x + 85f, player.y + 12f)
            cubicTo(player.x + 65f, player.y + 20f, player.x + 65f, player.y + 65f, player.x + 85f, player.y + 70f)
            close()
        }
        drawPath(ear2, color = secColor)

        // Red Hero Collar
        drawRoundRect(color = accentColor, topLeft = Offset(player.x + 28f, player.y + 92f), size = Size(player.width - 40f, 13f), cornerRadius = CornerRadius(6f, 6f))
        drawCircle(color = Color(0xFFFFD129), radius = 7f, center = Offset(player.x + player.width - 34f, player.y + 98f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Dog Snout & Nose
        drawOval(color = Color.White.copy(alpha = 0.7f), topLeft = Offset(player.x + player.width - 24f, player.y + 64f), size = Size(36f, 30f))
        drawOval(color = detailColor, topLeft = Offset(player.x + player.width - 10f, player.y + 68f), size = Size(18f, 14f))
    }
}

// 🦊 TİLKİ
private fun DrawScope.drawFox(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = secColor, topLeft = Offset(player.x + 14f, player.y + 18f), size = Size(player.width * 0.6f, player.height * 0.6f))
        // Pointy ears back
        val ear = Path().apply {
            moveTo(player.x + 40f, player.y + 6f)
            lineTo(player.x - 20f, player.y + 12f)
            lineTo(player.x + 30f, player.y + 24f)
            close()
        }
        drawPath(ear, color = bodyColor)
        drawCircle(color = detailColor, radius = 6f, center = Offset(player.x + player.width + 20f, player.y + 36f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        drawCircle(color = detailColor, radius = 12f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = detailColor, radius = 12f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Big Bushy Fox Tail with White Tip
        val tailSway = sin(player.animFrame * 1.8f) * 14f
        val tailPath = Path().apply {
            moveTo(player.x + 12f, cy + 18f)
            cubicTo(player.x - 30f, cy + 10f + tailSway, player.x - 65f, cy - 10f + tailSway, player.x - 45f, cy - 35f + tailSway)
            cubicTo(player.x - 25f, cy - 45f + tailSway, player.x - 10f, cy - 15f + tailSway, player.x + 12f, cy + 18f)
            close()
        }
        drawPath(tailPath, color = bodyColor)
        // White Tail Tip
        val tailTip = Path().apply {
            moveTo(player.x - 45f, cy - 35f + tailSway)
            cubicTo(player.x - 55f, cy - 25f + tailSway, player.x - 40f, cy - 15f + tailSway, player.x - 32f, cy - 32f + tailSway)
            close()
        }
        drawPath(tailTip, color = Color.White)

        // Pointy Fox Ears with White Inner
        val ear1 = Path().apply {
            moveTo(player.x + 28f, player.y + 20f)
            lineTo(player.x + 48f, player.y - 34f)
            lineTo(player.x + 70f, player.y + 14f)
            close()
        }
        drawPath(ear1, color = bodyColor)
        val ear1In = Path().apply {
            moveTo(player.x + 36f, player.y + 18f)
            lineTo(player.x + 48f, player.y - 24f)
            lineTo(player.x + 60f, player.y + 14f)
            close()
        }
        drawPath(ear1In, color = Color.White)

        val ear2 = Path().apply {
            moveTo(player.x + 78f, player.y + 12f)
            lineTo(player.x + 102f, player.y - 30f)
            lineTo(player.x + 118f, player.y + 20f)
            close()
        }
        drawPath(ear2, color = bodyColor)

        // Fox Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))

        // White Fluffy Chest & Cheek
        drawOval(color = Color.White, topLeft = Offset(player.x + 30f, player.y + 54f), size = Size(player.width * 0.5f, player.height * 0.45f))
        val cheekFluff = Path().apply {
            moveTo(player.x + player.width - 30f, player.y + 60f)
            lineTo(player.x + player.width + 12f, player.y + 80f)
            lineTo(player.x + player.width - 25f, player.y + 95f)
            close()
        }
        drawPath(cheekFluff, color = Color.White)

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Black Fox Nose
        drawCircle(color = detailColor, radius = 7f, center = Offset(player.x + player.width + 6f, player.y + 78f))
    }
}

// 🐸 KURBAĞA
private fun DrawScope.drawFrog(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = secColor, topLeft = Offset(player.x + 14f, player.y + 22f), size = Size(player.width * 0.6f, player.height * 0.5f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        // Webbed Feet
        drawOval(color = detailColor, topLeft = Offset(cx - 28f + leg1Offset, player.y + player.height), size = Size(28f, 14f))
        drawOval(color = detailColor, topLeft = Offset(cx + 12f + leg2Offset, player.y + player.height), size = Size(28f, 14f))

        // Elevated Bulging Frog Eyes on top
        drawCircle(color = bodyColor, radius = 24f, center = Offset(player.x + 48f, player.y + 16f))
        drawCircle(color = bodyColor, radius = 24f, center = Offset(player.x + 106f, player.y + 18f))

        // Frog Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y + 12f), size = Size(player.width, player.height - 12f))
        // Lime Belly
        drawOval(color = secColor, topLeft = Offset(player.x + 24f, player.y + 54f), size = Size(player.width * 0.55f, player.height * 0.44f))

        // Big Anime Eye on right bulb
        drawAnimeEye(player.x + player.width - 52f, player.y + 16f)

        // Left eye sparkle
        drawCircle(color = Color.White, radius = 10f, center = Offset(player.x + 48f, player.y + 16f))
        drawCircle(color = Color(0xFF1E1E24), radius = 6f, center = Offset(player.x + 48f, player.y + 16f))

        // Blushing Cheek
        drawCircle(color = Color(0xFFFF70A6).copy(alpha = 0.45f), radius = 14f, center = Offset(player.x + player.width - 36f, player.y + 78f))

        // Wide Happy Frog Smile
        drawArc(
            color = detailColor,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(player.x + player.width - 44f, player.y + 68f),
            size = Size(42f, 26f),
            style = Stroke(width = 4.5f)
        )
    }
}

// 🐻 AYICIK
private fun DrawScope.drawBear(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawCircle(color = bodyColor, radius = 16f, center = Offset(player.x + 20f, player.y + 10f))
        drawCircle(color = secColor, radius = 10f, center = Offset(player.x + 20f, player.y + 10f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        drawCircle(color = bodyColor, radius = 14f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = bodyColor, radius = 14f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Round Teddy Ears with Beige Inner
        drawCircle(color = bodyColor, radius = 22f, center = Offset(player.x + 36f, player.y + 12f))
        drawCircle(color = secColor, radius = 13f, center = Offset(player.x + 36f, player.y + 12f))

        drawCircle(color = bodyColor, radius = 22f, center = Offset(player.x + 108f, player.y + 12f))
        drawCircle(color = secColor, radius = 13f, center = Offset(player.x + 108f, player.y + 12f))

        // Bear Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = secColor.copy(alpha = 0.5f), topLeft = Offset(player.x + 22f, player.y + 48f), size = Size(player.width * 0.55f, player.height * 0.46f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Snout & Button Nose
        drawOval(color = secColor, topLeft = Offset(player.x + player.width - 28f, player.y + 64f), size = Size(38f, 32f))
        drawOval(color = detailColor, topLeft = Offset(player.x + player.width - 14f, player.y + 68f), size = Size(16f, 12f))
    }
}

// 🐧 PENGUEN
private fun DrawScope.drawPenguin(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        // Belly sliding penguin!
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = secColor, topLeft = Offset(player.x + 14f, player.y + 24f), size = Size(player.width * 0.6f, player.height * 0.5f))
        val beakPath = Path().apply {
            moveTo(player.x + player.width + 16f, player.y + 28f)
            lineTo(player.x + player.width + 42f, player.y + 38f)
            lineTo(player.x + player.width + 16f, player.y + 48f)
            close()
        }
        drawPath(beakPath, color = detailColor)
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f, color = Color.White)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        // Orange feet
        drawOval(color = detailColor, topLeft = Offset(cx - 26f + leg1Offset, player.y + player.height), size = Size(26f, 14f))
        drawOval(color = detailColor, topLeft = Offset(cx + 12f + leg2Offset, player.y + player.height), size = Size(26f, 14f))

        // Tuxedo Navy Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))

        // Bright White Heart/Oval Belly
        drawOval(color = secColor, topLeft = Offset(player.x + 26f, player.y + 36f), size = Size(player.width * 0.58f, player.height * 0.6f))

        // Yellow Crest Tuft
        drawCircle(color = accentColor, radius = 9f, center = Offset(player.x + 65f, player.y + 8f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Penguin Beak
        val beakPath = Path().apply {
            moveTo(player.x + player.width - 8f, player.y + 68f)
            lineTo(player.x + player.width + 26f, player.y + 76f)
            lineTo(player.x + player.width - 8f, player.y + 86f)
            close()
        }
        drawPath(beakPath, color = detailColor)

        // Penguin Flippers
        val flipperFlap = if (player.isGrounded) sin(player.animFrame * 2f) * 12f else -25f
        val flipperPath = Path().apply {
            moveTo(player.x + 22f, cy + 10f)
            quadraticTo(player.x - 12f, cy + flipperFlap - 10f, player.x + 40f, cy + flipperFlap + 35f)
            close()
        }
        drawPath(flipperPath, color = bodyColor)
    }
}

// 🐲 MİNİK EJDERHA
private fun DrawScope.drawDragon(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = secColor, topLeft = Offset(player.x + 14f, player.y + 22f), size = Size(player.width * 0.6f, player.height * 0.5f))
        // Horns back
        drawRoundRect(color = secColor, topLeft = Offset(player.x + 10f, player.y + 4f), size = Size(40f, 12f), cornerRadius = CornerRadius(6f, 6f))
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        drawCircle(color = detailColor, radius = 13f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = detailColor, radius = 13f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Tiny Dragon Horns
        val horn1 = Path().apply {
            moveTo(player.x + 36f, player.y + 16f)
            lineTo(player.x + 45f, player.y - 20f)
            lineTo(player.x + 56f, player.y + 10f)
            close()
        }
        drawPath(horn1, color = secColor)

        val horn2 = Path().apply {
            moveTo(player.x + 85f, player.y + 12f)
            lineTo(player.x + 96f, player.y - 18f)
            lineTo(player.x + 106f, player.y + 14f)
            close()
        }
        drawPath(horn2, color = secColor)

        // Dragon Spines on Back
        for (i in 0..2) {
            val spinePath = Path().apply {
                moveTo(player.x + 8f + i * 20f, player.y + 18f + i * 14f)
                lineTo(player.x - 8f + i * 20f, player.y + 4f + i * 14f)
                lineTo(player.x + 18f + i * 20f, player.y + 26f + i * 14f)
                close()
            }
            drawPath(spinePath, color = accentColor)
        }

        // Dragon Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = secColor.copy(alpha = 0.6f), topLeft = Offset(player.x + 22f, player.y + 48f), size = Size(player.width * 0.55f, player.height * 0.46f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)

        // Dragon Wings
        val wingFlap = if (player.isGrounded) sin(player.animFrame * 2f) * 14f else -30f
        val wingPath = Path().apply {
            moveTo(player.x + 26f, cy + 10f)
            lineTo(player.x - 20f, cy + wingFlap - 20f)
            lineTo(player.x + 10f, cy + wingFlap - 4f)
            lineTo(player.x - 12f, cy + wingFlap + 12f)
            lineTo(player.x + 40f, cy + 28f)
            close()
        }
        drawPath(wingPath, color = secColor)
    }
}

// 🦄 UNICORN
private fun DrawScope.drawUnicorn(
    player: Player,
    bodyColor: Color,
    secColor: Color,
    detailColor: Color,
    accentColor: Color,
    cx: Float,
    cy: Float
) {
    if (player.isDucking) {
        drawOval(color = bodyColor, topLeft = Offset(player.x - 12f, player.y), size = Size(player.width + 36f, player.height))
        drawOval(color = secColor, topLeft = Offset(player.x + 14f, player.y + 18f), size = Size(player.width * 0.6f, player.height * 0.6f))
        // Horn pointing forward
        val hornPath = Path().apply {
            moveTo(player.x + player.width + 10f, player.y + 10f)
            lineTo(player.x + player.width + 50f, player.y + 4f)
            lineTo(player.x + player.width + 18f, player.y + 24f)
            close()
        }
        drawPath(hornPath, color = detailColor)
        drawDuckingEye(player.x + player.width - 4f, player.y + 18f)
        drawSlideWindLines(player.x, player.y)
    } else {
        val leg1Offset = if (player.isGrounded) sin(player.animFrame * 1.5f) * 18f else -8f
        val leg2Offset = if (player.isGrounded) -sin(player.animFrame * 1.5f) * 18f else 8f
        // Golden hooves
        drawCircle(color = detailColor, radius = 13f, center = Offset(cx - 20f + leg1Offset, player.y + player.height + 4f))
        drawCircle(color = detailColor, radius = 13f, center = Offset(cx + 20f + leg2Offset, player.y + player.height + 4f))

        // Rainbow Mane along the back
        val maneFlutter = sin(player.animFrame * 2f) * 8f
        drawCircle(color = Color(0xFFFF70A6), radius = 18f, center = Offset(player.x + 20f, player.y + 24f + maneFlutter))
        drawCircle(color = Color(0xFF70D6FF), radius = 18f, center = Offset(player.x + 10f, player.y + 46f + maneFlutter))
        drawCircle(color = Color(0xFFFFD670), radius = 18f, center = Offset(player.x + 8f, player.y + 68f + maneFlutter))

        // Unicorn Body
        drawOval(color = bodyColor, topLeft = Offset(player.x, player.y), size = Size(player.width, player.height))
        drawOval(color = secColor.copy(alpha = 0.55f), topLeft = Offset(player.x + 22f, player.y + 46f), size = Size(player.width * 0.55f, player.height * 0.48f))

        // Golden Shining Spiral Horn on Forehead
        val hornPath = Path().apply {
            moveTo(player.x + 82f, player.y + 14f)
            lineTo(player.x + 118f, player.y - 36f)
            lineTo(player.x + 102f, player.y + 22f)
            close()
        }
        drawPath(hornPath, color = detailColor)
        // Horn sparkles
        drawCircle(color = Color.White, radius = 5f, center = Offset(player.x + 118f, player.y - 36f))

        // Star Mark on Cheek
        drawCircle(color = accentColor, radius = 10f, center = Offset(player.x + player.width - 32f, player.y + 88f))

        // Anime Eye
        drawAnimeEye(player.x + player.width - 50f, player.y + 42f)
    }
}

// Reusable Anime Eye Drawing
private fun DrawScope.drawAnimeEye(eyeX: Float, eyeY: Float) {
    drawOval(color = Color.White, topLeft = Offset(eyeX, eyeY), size = Size(40f, 44f))
    drawOval(color = Color(0xFF1E1E24), topLeft = Offset(eyeX + 11f, eyeY + 6f), size = Size(27f, 35f))
    drawCircle(color = Color.White, radius = 7f, center = Offset(eyeX + 16f, eyeY + 14f))
    drawCircle(color = Color.White, radius = 3.5f, center = Offset(eyeX + 26f, eyeY + 25f))
}

private fun DrawScope.drawDuckingEye(eyeX: Float, eyeY: Float, color: Color = Color.Black) {
    drawArc(
        color = color,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(eyeX, eyeY),
        size = Size(28f, 22f),
        style = Stroke(width = 5.5f)
    )
}

private fun DrawScope.drawSlideWindLines(px: Float, py: Float) {
    drawLine(
        color = Color.White.copy(alpha = 0.75f),
        start = Offset(px - 24f, py + 24f),
        end = Offset(px - 65f, py + 24f),
        strokeWidth = 4.5f
    )
    drawLine(
        color = Color.White.copy(alpha = 0.75f),
        start = Offset(px - 14f, py + 48f),
        end = Offset(px - 48f, py + 48f),
        strokeWidth = 4.5f
    )
}

private fun DrawScope.drawObstacle(obs: Obstacle) {
    when (obs.type) {
        ObstacleType.LOW_ROCK -> {
            // Low Rock with moss (120x90)
            val rockPath = Path().apply {
                moveTo(obs.x, obs.y + obs.height)
                lineTo(obs.x + 12f, obs.y + 20f)
                lineTo(obs.x + obs.width * 0.5f, obs.y)
                lineTo(obs.x + obs.width - 15f, obs.y + 18f)
                lineTo(obs.x + obs.width, obs.y + obs.height)
                close()
            }
            drawPath(rockPath, color = Color(0xFF64748B))

            // Rock Highlight & Moss
            drawOval(
                color = Color(0xFF4ADE80),
                topLeft = Offset(obs.x + 15f, obs.y + 4f),
                size = Size(obs.width * 0.5f, 20f)
            )
        }
        ObstacleType.SPIKE_BUSH -> {
            // Sharp Thorny Bush with spikes (135x120)
            drawRoundRect(
                color = Color(0xFF15803D),
                topLeft = Offset(obs.x + 6f, obs.y + 24f),
                size = Size(obs.width - 12f, obs.height - 24f),
                cornerRadius = CornerRadius(14f, 14f)
            )
            // Red warning spikes
            val spikeCount = 4
            val step = obs.width / (spikeCount + 1)
            for (i in 1..spikeCount) {
                val sx = obs.x + i * step
                val spikePath = Path().apply {
                    moveTo(sx - 14f, obs.y + 26f)
                    lineTo(sx, obs.y)
                    lineTo(sx + 14f, obs.y + 26f)
                    close()
                }
                drawPath(spikePath, color = Color(0xFFDC2626))
            }
        }
        ObstacleType.TALL_TREE -> {
            // Tree Stump (105x180)
            drawRoundRect(
                color = Color(0xFF78350F),
                topLeft = Offset(obs.x + 12f, obs.y + 32f),
                size = Size(obs.width - 24f, obs.height - 32f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            // Foliage Crown
            drawOval(
                color = Color(0xFF16A34A),
                topLeft = Offset(obs.x, obs.y),
                size = Size(obs.width, 68f)
            )
        }
        ObstacleType.FLYING_BEE -> {
            // Flying Bee (125x95)
            val bx = obs.x
            val by = obs.y

            // Flapping Wings
            drawOval(
                color = Color.White.copy(alpha = 0.85f),
                topLeft = Offset(bx + 24f, by - 20f),
                size = Size(36f, 26f)
            )
            // Yellow Body
            drawOval(
                color = Color(0xFFFBBF24),
                topLeft = Offset(bx, by),
                size = Size(obs.width, obs.height)
            )
            // Black Stripes
            drawRect(
                color = Color(0xFF1E1E24),
                topLeft = Offset(bx + 28f, by + 4f),
                size = Size(12f, obs.height - 8f)
            )
            drawRect(
                color = Color(0xFF1E1E24),
                topLeft = Offset(bx + 52f, by + 4f),
                size = Size(12f, obs.height - 8f)
            )
            // Stinger
            val stingerPath = Path().apply {
                moveTo(bx + 3f, by + obs.height * 0.5f - 9f)
                lineTo(bx - 15f, by + obs.height * 0.5f)
                lineTo(bx + 3f, by + obs.height * 0.5f + 9f)
                close()
            }
            drawPath(stingerPath, color = Color(0xFF1E1E24))

            // Eyes
            drawCircle(
                color = Color.White,
                radius = 9f,
                center = Offset(bx + obs.width - 20f, by + 24f)
            )
            drawCircle(
                color = Color.Black,
                radius = 4.5f,
                center = Offset(bx + obs.width - 17f, by + 24f)
            )
        }
        ObstacleType.HIGH_CLOUD -> {
            // Floating Thunder Cloud
            drawOval(
                color = Color(0xFF475569),
                topLeft = Offset(obs.x, obs.y),
                size = Size(obs.width, obs.height)
            )
        }
        ObstacleType.OVERHEAD_BARRIER -> {
            // Overhead Hanging Wooden Barrier (165x140 - Must duck/slide under!)
            // Hanging Support Chains from Screen Top
            drawLine(
                color = Color(0xFF4B5563),
                start = Offset(obs.x + 20f, 0f),
                end = Offset(obs.x + 20f, obs.y),
                strokeWidth = 6f
            )
            drawLine(
                color = Color(0xFF4B5563),
                start = Offset(obs.x + obs.width - 20f, 0f),
                end = Offset(obs.x + obs.width - 20f, obs.y),
                strokeWidth = 6f
            )

            // Wooden Sign Box
            drawRoundRect(
                color = Color(0xFF713F12),
                topLeft = Offset(obs.x, obs.y),
                size = Size(obs.width, obs.height),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Red Warning Hazard Stripes
            for (i in 0..5) {
                drawLine(
                    color = Color(0xFFEF4444),
                    start = Offset(obs.x + 12f + i * 24f, obs.y + 12f),
                    end = Offset(obs.x + 32f + i * 24f, obs.y + obs.height - 12f),
                    strokeWidth = 9f
                )
            }

            // Duck Arrow Indicator (Showing kid players they can slide under!)
            val arrowPath = Path().apply {
                val acx = obs.x + obs.width / 2
                val acy = obs.y + obs.height + 8f
                moveTo(acx - 12f, acy)
                lineTo(acx + 12f, acy)
                lineTo(acx, acy + 15f)
                close()
            }
            drawPath(arrowPath, color = Color(0xFFFBBF24))
        }
    }
}

private fun DrawScope.drawCollectible(col: Collectible) {
    val cy = col.y + sin(col.bobOffset) * 6f
    val cx = col.x
    val size = col.size

    when (col.type) {
        CollectibleType.STAR_COIN -> {
            // Outer golden aura glow
            drawCircle(
                color = Color(0xFFFEF08A).copy(alpha = 0.6f),
                radius = size * 0.8f,
                center = Offset(cx + size / 2, cy + size / 2)
            )
            // Golden Coin Disk
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF07C), Color(0xFFF59E0B), Color(0xFFD97706)),
                    center = Offset(cx + size / 2, cy + size / 2),
                    radius = size / 2
                ),
                radius = size / 2,
                center = Offset(cx + size / 2, cy + size / 2)
            )
            // Inner 5-Point Star
            val starPath = Path()
            val starRadius = size * 0.35f
            val starCenter = Offset(cx + size / 2, cy + size / 2)
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) starRadius else starRadius * 0.45f
                val angle = (i * 36 - 90) * (PI / 180f)
                val x = starCenter.x + (r * cos(angle)).toFloat()
                val y = starCenter.y + (r * sin(angle)).toFloat()
                if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
            }
            starPath.close()
            drawPath(starPath, color = Color.White)
        }
        CollectibleType.SHIELD -> {
            // Cyan Bubble Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF90E0EF), Color(0xFF00B4D8), Color(0xFF0077B6)),
                    center = Offset(cx + size / 2, cy + size / 2),
                    radius = size * 0.65f
                ),
                radius = size * 0.65f,
                center = Offset(cx + size / 2, cy + size / 2)
            )
            // Shield Emblem
            val shieldPath = Path().apply {
                moveTo(cx + size / 2, cy + 6f)
                lineTo(cx + size - 6f, cy + 14f)
                lineTo(cx + size - 6f, cy + size * 0.65f)
                quadraticTo(cx + size / 2, cy + size + 2f, cx + size / 2, cy + size + 2f)
                quadraticTo(cx + 6f, cy + size * 0.65f, cx + 6f, cy + 14f)
                close()
            }
            drawPath(shieldPath, color = Color.White)
        }
        CollectibleType.MAGNET -> {
            // Purple Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE0AAFF), Color(0xFF9D4EDD), Color(0xFF5A189A)),
                    center = Offset(cx + size / 2, cy + size / 2),
                    radius = size * 0.65f
                ),
                radius = size * 0.65f,
                center = Offset(cx + size / 2, cy + size / 2)
            )
            // Horseshoe Magnet Icon
            drawArc(
                color = Color.White,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx + 8f, cy + 8f),
                size = Size(size - 16f, size - 12f),
                style = Stroke(width = 6f)
            )
        }
        CollectibleType.SPEED_BOOST -> {
            // Orange Flame Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF9E00), Color(0xFFFF5400), Color(0xFFDC2F02)),
                    center = Offset(cx + size / 2, cy + size / 2),
                    radius = size * 0.65f
                ),
                radius = size * 0.65f,
                center = Offset(cx + size / 2, cy + size / 2)
            )
            // Lightning Bolt Icon
            val boltPath = Path().apply {
                moveTo(cx + size * 0.55f, cy + 4f)
                lineTo(cx + 8f, cy + size * 0.52f)
                lineTo(cx + size * 0.48f, cy + size * 0.52f)
                lineTo(cx + size * 0.42f, cy + size - 3f)
                lineTo(cx + size - 6f, cy + size * 0.45f)
                lineTo(cx + size * 0.52f, cy + size * 0.45f)
                close()
            }
            drawPath(boltPath, color = Color.White)
        }
    }
}

private fun DrawScope.drawParticle(p: Particle) {
    drawCircle(
        color = Color(p.color).copy(alpha = p.alpha),
        radius = p.size * p.alpha,
        center = Offset(p.x, p.y)
    )
}
