package com.example.game.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.game.data.GameStorage
import com.example.game.models.AVAILABLE_SKINS
import com.example.game.models.CharacterSkin
import com.example.game.models.Collectible
import com.example.game.models.CollectibleType
import com.example.game.models.GameStatus
import com.example.game.models.Obstacle
import com.example.game.models.ObstacleType
import com.example.game.models.Particle
import com.example.game.models.Player
import com.example.game.models.PowerUpActive
import com.example.game.models.WorldTheme
import com.example.game.models.getWorldForScore
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class GameEngine(
    private val storage: GameStorage,
    val audio: GameAudio
) {
    var status by mutableStateOf(GameStatus.MENU)
        private set

    var score by mutableIntStateOf(0)
        private set

    var distance by mutableFloatStateOf(0f)
        private set

    var coinsThisRun by mutableIntStateOf(0)
        private set

    var isNewBest by mutableStateOf(false)
        private set

    var playTimeSeconds by mutableFloatStateOf(0f)
        private set

    var elapsedMinutes by mutableIntStateOf(0)
        private set

    var currentWorld by mutableStateOf(getWorldForScore(0))
        private set

    var worldBannerText by mutableStateOf<String?>(null)
        private set

    var worldBannerEmoji by mutableStateOf<String?>(null)
        private set

    var worldBannerTimer by mutableFloatStateOf(0f)
        private set

    var difficultyBannerText by mutableStateOf<String?>(null)
        private set

    var difficultyBannerTimer by mutableFloatStateOf(0f)
        private set

    var selectedSkin by mutableStateOf(
        AVAILABLE_SKINS.find { it.id == storage.selectedSkinId } ?: AVAILABLE_SKINS.first()
    )

    val player = Player(
        width = 155f,
        height = 155f
    )

    val obstacles = mutableStateListOf<Obstacle>()
    val collectibles = mutableStateListOf<Collectible>()
    val particles = mutableStateListOf<Particle>()
    val activePowerUps = mutableStateListOf<PowerUpActive>()

    var groundY by mutableFloatStateOf(0f)
    var worldWidth by mutableFloatStateOf(1000f)
    var worldHeight by mutableFloatStateOf(800f)

    var currentSpeed by mutableFloatStateOf(340f)
    var scrollOffset by mutableFloatStateOf(0f)

    private var obstacleTimer = 0f
    private var nextObstacleInterval = 2.0f
    private var collectibleTimer = 0f
    private var nextCollectibleInterval = 1.4f
    private var dustTimer = 0f
    private var nextId = 1L

    private var lastRecordedWorldIndex = 0
    private var lastRecordedSpeedInterval = 0
    var speedSurgeTimer by mutableFloatStateOf(0f)

    // Scaled physics for enlarged player character (155x155) - tuned for balanced agile gameplay
    private val gravity = 3000f
    private val jumpVelocity = -1180f
    private val doubleJumpVelocity = -1080f
    private val tripleJumpVelocity = -1000f

    val hasShield: Boolean
        get() = activePowerUps.any { it.type == CollectibleType.SHIELD }

    val hasMagnet: Boolean
        get() = activePowerUps.any { it.type == CollectibleType.MAGNET }

    val hasSpeedBoost: Boolean
        get() = activePowerUps.any { it.type == CollectibleType.SPEED_BOOST }

    fun updateScreenDimensions(width: Float, height: Float) {
        worldWidth = width
        worldHeight = height
        groundY = height * 0.74f
        if (player.isGrounded) {
            player.y = groundY - player.height
        }
    }

    fun selectSkin(skin: CharacterSkin) {
        if (storage.isSkinUnlocked(skin.id)) {
            selectedSkin = skin
            storage.selectedSkinId = skin.id
            audio.playButtonSound()
        } else if (storage.totalCoins >= skin.unlockCoins) {
            storage.totalCoins -= skin.unlockCoins
            storage.unlockSkin(skin.id)
            selectedSkin = skin
            storage.selectedSkinId = skin.id
            audio.playPowerUpSound()
        }
    }

    fun startGame() {
        status = GameStatus.PLAYING
        score = 0
        distance = 0f
        coinsThisRun = 0
        isNewBest = false
        playTimeSeconds = 0f
        elapsedMinutes = 0
        lastRecordedSpeedInterval = 0
        lastRecordedWorldIndex = 0
        currentWorld = getWorldForScore(0)
        worldBannerText = null
        worldBannerEmoji = null
        worldBannerTimer = 0f
        difficultyBannerText = null
        difficultyBannerTimer = 0f
        speedSurgeTimer = 0f

        currentSpeed = 340f
        scrollOffset = 0f
        obstacleTimer = 0.6f
        nextObstacleInterval = 2.0f
        collectibleTimer = 0.8f
        nextCollectibleInterval = 1.4f
        dustTimer = 0f

        player.apply {
            x = 100f
            width = 155f
            height = 155f
            y = groundY - 155f
            vy = 0f
            isGrounded = true
            isDucking = false
            duckTimer = 0f
            jumpCount = 0
            rotation = 0f
            animFrame = 0f
        }

        obstacles.clear()
        collectibles.clear()
        particles.clear()
        activePowerUps.clear()

        audio.playButtonSound()
    }

    fun pauseGame() {
        if (status == GameStatus.PLAYING) {
            status = GameStatus.PAUSED
            audio.playButtonSound()
        }
    }

    fun resumeGame() {
        if (status == GameStatus.PAUSED) {
            status = GameStatus.PLAYING
            audio.playButtonSound()
        }
    }

    fun returnToMenu() {
        status = GameStatus.MENU
        audio.playButtonSound()
    }

    fun onJumpPressed() {
        if (status != GameStatus.PLAYING) return

        if (player.isDucking) {
            // Cancel duck on jump
            player.isDucking = false
            player.width = 155f
            player.height = 155f
            player.y = groundY - 155f
        }

        if (player.isGrounded) {
            player.vy = jumpVelocity
            player.isGrounded = false
            player.jumpCount = 1
            audio.playJumpSound()
            spawnJumpPuff(player.x + player.width / 2, groundY)
        } else if (player.jumpCount == 1) {
            player.vy = doubleJumpVelocity
            player.jumpCount = 2
            audio.playDoubleJumpSound()
            spawnJumpPuff(player.x + player.width / 2, player.y + player.height)
        } else if (player.jumpCount == 2) {
            player.vy = tripleJumpVelocity
            player.jumpCount = 3
            audio.playTripleJumpSound()
            spawnJumpPuff(player.x + player.width / 2, player.y + player.height)
        }
    }

    fun onDuckPressed(isDown: Boolean) {
        if (status != GameStatus.PLAYING) return

        if (isDown) {
            player.isDucking = true
            player.duckTimer = 0.85f
            player.height = 80f
            player.width = 180f
            if (player.isGrounded) {
                player.y = groundY - 80f
            } else {
                // Quick dive if airborne
                player.vy += 950f
            }
        } else {
            player.isDucking = false
            player.height = 155f
            player.width = 155f
            if (player.isGrounded) {
                player.y = groundY - 155f
            }
        }
    }

    fun update(dt: Float) {
        if (status != GameStatus.PLAYING) return

        val safeDt = dt.coerceIn(0f, 0.05f)
        playTimeSeconds += safeDt

        // Check 30-second difficulty escalation: display "⚡ HIZLANIYOR!" banner & trigger 3s 0.3x surge
        val currentInterval = (playTimeSeconds / 30f).toInt()
        if (currentInterval > lastRecordedSpeedInterval) {
            lastRecordedSpeedInterval = currentInterval
            difficultyBannerText = "⚡ HIZLANIYOR!"
            difficultyBannerTimer = 3.0f
            speedSurgeTimer = 3.0f
            audio.playSpeedUpSound()
        }

        if (difficultyBannerTimer > 0f) {
            difficultyBannerTimer -= safeDt
            if (difficultyBannerTimer <= 0f) {
                difficultyBannerText = null
            }
        }

        if (speedSurgeTimer > 0f) {
            speedSurgeTimer -= safeDt
            if (speedSurgeTimer <= 0f) {
                speedSurgeTimer = 0f
            }
        }

        // Check 1000-point world progression
        val currentWorldIndex = (score / 1000)
        if (currentWorldIndex != lastRecordedWorldIndex) {
            lastRecordedWorldIndex = currentWorldIndex
            currentWorld = getWorldForScore(score)
            worldBannerText = "DÜNYA ${currentWorldIndex + 1}: ${currentWorld.name}"
            worldBannerEmoji = currentWorld.emoji
            worldBannerTimer = 3.2f
            audio.playWorldChangeSound()
            spawnWorldTransitionSparkles()
        }

        if (worldBannerTimer > 0f) {
            worldBannerTimer -= safeDt
            if (worldBannerTimer <= 0f) {
                worldBannerText = null
                worldBannerEmoji = null
            }
        }

        // Speed calculation: increases every 30s and with distance (tuned ~0.1x slower + 3s 0.3x surge)
        val speedIntervalBonus = lastRecordedSpeedInterval * 35f
        val distanceSpeedBonus = (distance * 0.075f).coerceAtMost(360f)
        val baseSpeed = 760f + speedIntervalBonus + distanceSpeedBonus
        val surgeMultiplier = if (speedSurgeTimer > 0f) 1.30f else 1.0f
        val speedMultiplier = (if (hasSpeedBoost) 1.35f else 1.0f) * surgeMultiplier
        currentSpeed = baseSpeed * speedMultiplier

        val moveStep = currentSpeed * safeDt
        scrollOffset += moveStep
        distance += (moveStep / 50f)

        val scoreIncrement = ((moveStep / 10f) * if (hasSpeedBoost) 2f else 1f).toInt()
        score += scoreIncrement

        // Update active powerups
        val expired = mutableListOf<PowerUpActive>()
        for (powerUp in activePowerUps) {
            powerUp.remainingTimeSec -= safeDt
            if (powerUp.remainingTimeSec <= 0f) {
                expired.add(powerUp)
            }
        }
        activePowerUps.removeAll(expired)

        // Update Player Physics
        updatePlayer(safeDt)

        // Spawn & Update Obstacles
        updateObstacles(safeDt, moveStep)

        // Spawn & Update Collectibles
        updateCollectibles(safeDt, moveStep)

        // Update Particles
        updateParticles(safeDt)

        // Check Collisions
        checkCollisions()
    }

    private fun updatePlayer(dt: Float) {
        player.animFrame += dt * 12f * (currentSpeed / 300f)

        if (player.isDucking) {
            player.duckTimer -= dt
            if (player.duckTimer <= 0f && !player.isGrounded) {
                player.isDucking = false
                player.height = 155f
                player.width = 155f
            }
        }

        if (!player.isGrounded) {
            player.vy += gravity * dt
            player.y += player.vy * dt

            // Jump tilt
            player.rotation = (player.vy * 0.035f).coerceIn(-25f, 35f)

            if (player.y >= groundY - player.height) {
                player.y = groundY - player.height
                player.vy = 0f
                player.isGrounded = true
                player.jumpCount = 0
                player.rotation = 0f
                spawnJumpPuff(player.x + player.width / 2, groundY)
            }
        } else {
            player.rotation = 0f
            dustTimer += dt
            if (dustTimer >= 0.11f) {
                dustTimer = 0f
                spawnRunDust(player.x + 10f, groundY - 4f)
            }
        }
    }

    private fun updateObstacles(dt: Float, moveStep: Float) {
        obstacleTimer += dt
        // Obstacle interval balances with gameplay speed
        val speedReduction = (lastRecordedSpeedInterval * 0.035f + distance * 0.00015f).coerceAtMost(0.45f)
        val targetIntervalBase = (1.35f - speedReduction).coerceAtLeast(0.85f)

        if (obstacleTimer >= nextObstacleInterval) {
            obstacleTimer = 0f
            nextObstacleInterval = Random.nextFloat() * 0.7f + targetIntervalBase
            spawnRandomObstacle()
        }

        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obs = iterator.next()
            obs.x -= moveStep

            // Score bonus on successfully passing obstacle
            if (!obs.scored && obs.x + obs.width < player.x) {
                obs.scored = true
                score += 20
            }

            if (obs.x + obs.width < -60f) {
                iterator.remove()
            }
        }
    }

    private fun spawnRandomObstacle() {
        val spawnX = worldWidth + 50f
        val rand = Random.nextFloat()

        val obstacle: Obstacle = when {
            rand < 0.32f -> {
                // Low Rock (Jump over - clean and visible, 50% bigger)
                Obstacle(
                    id = nextId++,
                    x = spawnX,
                    y = groundY - 90f,
                    width = 120f,
                    height = 90f,
                    type = ObstacleType.LOW_ROCK
                )
            }
            rand < 0.58f -> {
                // Spike Bush (Jump over, 50% bigger)
                Obstacle(
                    id = nextId++,
                    x = spawnX,
                    y = groundY - 120f,
                    width = 135f,
                    height = 120f,
                    type = ObstacleType.SPIKE_BUSH
                )
            }
            rand < 0.76f -> {
                // Flying Bee / Bird (High lane - can easily slide under!)
                // Bottom at groundY - 120f (ducking player is 80f, plenty of 40f clearance)
                Obstacle(
                    id = nextId++,
                    x = spawnX,
                    y = groundY - 215f,
                    width = 125f,
                    height = 95f,
                    type = ObstacleType.FLYING_BEE
                )
            }
            rand < 0.90f -> {
                // Overhead Hanging Barrier / Sign (High barrier - MUST duck/slide under!)
                // Top at groundY - 255f, height 140f -> bottom at groundY - 115f.
                // Standing player height is 155f (hits it), Ducking player height is 80f (35f clear tunnel to slide under!)
                Obstacle(
                    id = nextId++,
                    x = spawnX,
                    y = groundY - 255f,
                    width = 165f,
                    height = 140f,
                    type = ObstacleType.OVERHEAD_BARRIER
                )
            }
            else -> {
                // Tall Tree Stump (Requires high / double jump, 50% bigger)
                Obstacle(
                    id = nextId++,
                    x = spawnX,
                    y = groundY - 180f,
                    width = 105f,
                    height = 180f,
                    type = ObstacleType.TALL_TREE
                )
            }
        }
        obstacles.add(obstacle)
    }

    private fun updateCollectibles(dt: Float, moveStep: Float) {
        collectibleTimer += dt
        if (collectibleTimer >= nextCollectibleInterval) {
            collectibleTimer = 0f
            nextCollectibleInterval = Random.nextFloat() * 1.6f + 1.2f
            spawnCollectiblesPattern()
        }

        val iterator = collectibles.iterator()
        val isMagnetActive = hasMagnet

        while (iterator.hasNext()) {
            val col = iterator.next()
            col.bobOffset += dt * 5f

            if (isMagnetActive && col.type == CollectibleType.STAR_COIN) {
                // Attract star coin to player
                val dx = (player.x + player.width / 2) - (col.x + col.size / 2)
                val dy = (player.y + player.height / 2) - (col.y + col.size / 2)
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < 480f && dist > 1f) {
                    val pullSpeed = 700f * dt
                    col.x += (dx / dist) * pullSpeed
                    col.y += (dy / dist) * pullSpeed
                }
            }

            col.x -= moveStep

            if (col.x < -80f || col.isCollected) {
                iterator.remove()
            }
        }
    }

    private fun spawnCollectiblesPattern() {
        val spawnX = worldWidth + 50f
        val rand = Random.nextFloat()

        if (rand < 0.68f) {
            // Arc or Line of large 3-4 golden stars (50% bigger: 78px)
            val count = Random.nextInt(3, 5)
            val isArc = Random.nextBoolean()
            for (i in 0 until count) {
                val cx = spawnX + i * 95f
                val cy = if (isArc) {
                    val arcHeight = sin((i.toFloat() / (count - 1)) * Math.PI.toFloat()) * 140f
                    groundY - 120f - arcHeight
                } else {
                    groundY - 110f
                }
                collectibles.add(
                    Collectible(
                        id = nextId++,
                        x = cx,
                        y = cy,
                        size = 78f,
                        type = CollectibleType.STAR_COIN
                    )
                )
            }
        } else {
            // Power-up spawn (50% bigger: 84px)
            val pType = when (Random.nextInt(3)) {
                0 -> CollectibleType.SHIELD
                1 -> CollectibleType.MAGNET
                else -> CollectibleType.SPEED_BOOST
            }
            collectibles.add(
                Collectible(
                    id = nextId++,
                    x = spawnX,
                    y = groundY - 155f,
                    size = 84f,
                    type = pType
                )
            )
        }
    }

    private fun updateParticles(dt: Float) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.life -= dt
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
            if (p.life <= 0f) {
                iterator.remove()
            }
        }
    }

    private fun checkCollisions() {
        // Player hit box with generous inset for kid-friendly, responsive fairness
        val px = player.x + 10f
        val py = player.y + 8f
        val pw = player.width - 20f
        val ph = player.height - 12f

        // Check collectible overlaps
        for (col in collectibles) {
            if (col.isCollected) continue
            val cx = col.x
            val cy = col.y + sin(col.bobOffset) * 6f
            val cSize = col.size

            if (px < cx + cSize && px + pw > cx && py < cy + cSize && py + ph > cy) {
                col.isCollected = true
                onItemCollected(col)
            }
        }

        // Check obstacle collisions
        for (obs in obstacles) {
            // Precise hitbox for obstacle
            val ox = obs.x + 8f
            val oy = obs.y + 6f
            val ow = obs.width - 16f
            val oh = obs.height - 10f

            if (px < ox + ow && px + pw > ox && py < oy + oh && py + ph > oy) {
                onPlayerHit(obs)
                break
            }
        }
    }

    private fun onItemCollected(col: Collectible) {
        when (col.type) {
            CollectibleType.STAR_COIN -> {
                val value = if (hasSpeedBoost) 20 else 10
                score += value
                coinsThisRun += 1
                audio.playCoinSound()
                spawnCoinSparkles(col.x + col.size / 2, col.y + col.size / 2)
            }
            CollectibleType.SHIELD -> {
                activePowerUps.removeAll { it.type == CollectibleType.SHIELD }
                activePowerUps.add(PowerUpActive(CollectibleType.SHIELD, 10f, 10f))
                audio.playPowerUpSound()
                spawnPowerUpBurst(col.x + col.size / 2, col.y + col.size / 2, 0xFF00B4D8)
            }
            CollectibleType.MAGNET -> {
                activePowerUps.removeAll { it.type == CollectibleType.MAGNET }
                activePowerUps.add(PowerUpActive(CollectibleType.MAGNET, 8f, 8f))
                audio.playPowerUpSound()
                spawnPowerUpBurst(col.x + col.size / 2, col.y + col.size / 2, 0xFF9D4EDD)
            }
            CollectibleType.SPEED_BOOST -> {
                activePowerUps.removeAll { it.type == CollectibleType.SPEED_BOOST }
                activePowerUps.add(PowerUpActive(CollectibleType.SPEED_BOOST, 6f, 6f))
                audio.playPowerUpSound()
                spawnPowerUpBurst(col.x + col.size / 2, col.y + col.size / 2, 0xFFFF5400)
            }
        }
    }

    private fun onPlayerHit(obstacle: Obstacle) {
        if (hasSpeedBoost) {
            // Destroy obstacle during speed rush
            obstacles.remove(obstacle)
            score += 50
            spawnExplosionStars(obstacle.x + obstacle.width / 2, obstacle.y + obstacle.height / 2)
            audio.playJumpSound()
            return
        }

        if (hasShield) {
            // Shield absorbs damage
            activePowerUps.removeAll { it.type == CollectibleType.SHIELD }
            obstacles.remove(obstacle)
            spawnShieldBreak(player.x + player.width / 2, player.y + player.height / 2)
            audio.playCrashSound()
            return
        }

        // Game Over!
        status = GameStatus.GAME_OVER
        audio.playCrashSound()
        spawnExplosionStars(player.x + player.width / 2, player.y + player.height / 2)

        storage.addCoins(coinsThisRun)
        isNewBest = storage.updateScores(score, distance.toInt())
    }

    private fun spawnJumpPuff(cx: Float, cy: Float) {
        for (i in 0..8) {
            val angle = Random.nextFloat() * Math.PI.toFloat()
            val speed = Random.nextFloat() * 100f + 40f
            particles.add(
                Particle(
                    x = cx + (Random.nextFloat() - 0.5f) * 24f,
                    y = cy - 2f,
                    vx = cos(angle) * speed,
                    vy = -sin(angle) * (speed * 0.4f),
                    size = Random.nextFloat() * 8f + 5f,
                    color = 0xFFEDE8E1,
                    maxLife = 0.35f,
                    life = 0.35f
                )
            )
        }
    }

    private fun spawnRunDust(cx: Float, cy: Float) {
        particles.add(
            Particle(
                x = cx,
                y = cy,
                vx = -(Random.nextFloat() * 80f + 30f),
                vy = -(Random.nextFloat() * 25f + 5f),
                size = Random.nextFloat() * 7f + 4f,
                color = 0xFFD8C7B5,
                maxLife = 0.25f,
                life = 0.25f
            )
        )
    }

    private fun spawnCoinSparkles(cx: Float, cy: Float) {
        for (i in 0..10) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 140f + 50f
            particles.add(
                Particle(
                    x = cx,
                    y = cy,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    size = Random.nextFloat() * 7f + 4f,
                    color = 0xFFFFD129,
                    maxLife = 0.45f,
                    life = 0.45f
                )
            )
        }
    }

    private fun spawnPowerUpBurst(cx: Float, cy: Float, color: Long) {
        for (i in 0..16) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 180f + 70f
            particles.add(
                Particle(
                    x = cx,
                    y = cy,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    size = Random.nextFloat() * 9f + 5f,
                    color = color,
                    maxLife = 0.55f,
                    life = 0.55f
                )
            )
        }
    }

    private fun spawnShieldBreak(cx: Float, cy: Float) {
        for (i in 0..14) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 160f + 60f
            particles.add(
                Particle(
                    x = cx,
                    y = cy,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    size = Random.nextFloat() * 10f + 4f,
                    color = 0xFF00B4D8,
                    maxLife = 0.45f,
                    life = 0.45f
                )
            )
        }
    }

    private fun spawnExplosionStars(cx: Float, cy: Float) {
        for (i in 0..24) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 220f + 90f
            val colors = listOf(0xFFFFD129, 0xFFFF5400, 0xFFEF233C, 0xFFFFFFFF, 0xFF38BDF8)
            particles.add(
                Particle(
                    x = cx,
                    y = cy,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    size = Random.nextFloat() * 10f + 6f,
                    color = colors[Random.nextInt(colors.size)],
                    maxLife = 0.65f,
                    life = 0.65f
                )
            )
        }
    }

    private fun spawnWorldTransitionSparkles() {
        val colors = listOf(0xFFFFD129, 0xFF38BDF8, 0xFFFF70A6, 0xFF4ADE80, 0xFFFFFFFF)
        for (i in 0..30) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * worldWidth,
                    y = Random.nextFloat() * groundY,
                    vx = (Random.nextFloat() - 0.5f) * 120f,
                    vy = -(Random.nextFloat() * 140f + 40f),
                    size = Random.nextFloat() * 9f + 5f,
                    color = colors[Random.nextInt(colors.size)],
                    maxLife = 0.9f,
                    life = 0.9f
                )
            )
        }
    }
}
