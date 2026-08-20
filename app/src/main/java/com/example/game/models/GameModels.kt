package com.example.game.models

enum class GameStatus {
    MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}

enum class ObstacleType {
    LOW_ROCK,          // Jump over
    SPIKE_BUSH,        // Jump over
    TALL_TREE,         // Double jump over
    FLYING_BEE,        // Flying mid-air, jump or duck
    HIGH_CLOUD,        // High obstacle, duck under
    OVERHEAD_BARRIER   // Hanging vines/sign, must duck
}

enum class CollectibleType {
    STAR_COIN,
    MAGNET,
    SHIELD,
    SPEED_BOOST
}

enum class CharacterSpecies {
    CHICK,       // Civciv / Kuş
    BUNNY,       // Tavşan
    CAT,         // Kedi
    PANDA,       // Panda
    DOG,         // Köpek
    FOX,         // Tilki
    FROG,        // Kurbağa
    BEAR,        // Ayıcık
    PENGUIN,     // Penguen
    DRAGON,      // Minik Ejderha
    UNICORN      // Sevimli Unicorn
}

data class CharacterSkin(
    val id: String,
    val name: String,
    val emoji: String,
    val species: CharacterSpecies,
    val bodyColor: Long,
    val secondaryColor: Long, // wingColor or earColor or bellyColor
    val detailColor: Long,    // beakColor or noseColor or hornColor
    val accentColor: Long,    // headbandColor or collarColor or spineColor
    val unlockCoins: Int
)

val AVAILABLE_SKINS = listOf(
    CharacterSkin(
        id = "classic_cici",
        name = "Civciv Cici",
        emoji = "🐥",
        species = CharacterSpecies.CHICK,
        bodyColor = 0xFFFFD129,
        secondaryColor = 0xFFFF8C00,
        detailColor = 0xFFFF5400,
        accentColor = 0xFFEF233C,
        unlockCoins = 0
    ),
    CharacterSkin(
        id = "blue_robin",
        name = "Maviş Kuş",
        emoji = "🐦",
        species = CharacterSpecies.CHICK,
        bodyColor = 0xFF38BDF8,
        secondaryColor = 0xFF0284C7,
        detailColor = 0xFFF59E0B,
        accentColor = 0xFFFFFFFF,
        unlockCoins = 30
    ),
    CharacterSkin(
        id = "bunny_pamuk",
        name = "Tavşan Pamuk",
        emoji = "🐰",
        species = CharacterSpecies.BUNNY,
        bodyColor = 0xFFFFF1F2,
        secondaryColor = 0xFFF472B6,
        detailColor = 0xFFFB7185,
        accentColor = 0xFFC084FC,
        unlockCoins = 60
    ),
    CharacterSkin(
        id = "kitty_tekir",
        name = "Kedi Tekir",
        emoji = "🐱",
        species = CharacterSpecies.CAT,
        bodyColor = 0xFFFB923C,
        secondaryColor = 0xFFEA580C,
        detailColor = 0xFFF43F5E,
        accentColor = 0xFF38BDF8,
        unlockCoins = 100
    ),
    CharacterSkin(
        id = "panda_bambu",
        name = "Panda Bambu",
        emoji = "🐼",
        species = CharacterSpecies.PANDA,
        bodyColor = 0xFFF8FAFC,
        secondaryColor = 0xFF1E293B,
        detailColor = 0xFF0F172A,
        accentColor = 0xFF22C55E,
        unlockCoins = 150
    ),
    CharacterSkin(
        id = "puppy_karamel",
        name = "Köpek Karamel",
        emoji = "🐶",
        species = CharacterSpecies.DOG,
        bodyColor = 0xFFD97706,
        secondaryColor = 0xFF92400E,
        detailColor = 0xFF451A03,
        accentColor = 0xFFEF4444,
        unlockCoins = 200
    ),
    CharacterSkin(
        id = "fox_tilki",
        name = "Kızıl Tilki",
        emoji = "🦊",
        species = CharacterSpecies.FOX,
        bodyColor = 0xFFF97316,
        secondaryColor = 0xFFFFFBEB,
        detailColor = 0xFF1C1917,
        accentColor = 0xFF06B6D4,
        unlockCoins = 260
    ),
    CharacterSkin(
        id = "frog_vakvak",
        name = "Kurbağa Vakvak",
        emoji = "🐸",
        species = CharacterSpecies.FROG,
        bodyColor = 0xFF4ADE80,
        secondaryColor = 0xFF86EFAC,
        detailColor = 0xFF15803D,
        accentColor = 0xFFFACC15,
        unlockCoins = 320
    ),
    CharacterSkin(
        id = "bear_bal",
        name = "Bal Ayısı",
        emoji = "🐻",
        species = CharacterSpecies.BEAR,
        bodyColor = 0xFFB45309,
        secondaryColor = 0xFFFDE68A,
        detailColor = 0xFF78350F,
        accentColor = 0xFFEC4899,
        unlockCoins = 400
    ),
    CharacterSkin(
        id = "penguin_pingu",
        name = "Penguen Pingu",
        emoji = "🐧",
        species = CharacterSpecies.PENGUIN,
        bodyColor = 0xFF1E293B,
        secondaryColor = 0xFFF8FAFC,
        detailColor = 0xFFF59E0B,
        accentColor = 0xFFFACC15,
        unlockCoins = 500
    ),
    CharacterSkin(
        id = "dragon_ejder",
        name = "Minik Ejderha",
        emoji = "🐲",
        species = CharacterSpecies.DRAGON,
        bodyColor = 0xFF2DD4BF,
        secondaryColor = 0xFFFACC15,
        detailColor = 0xFF0D9488,
        accentColor = 0xFFF43F5E,
        unlockCoins = 650
    ),
    CharacterSkin(
        id = "unicorn_yildiz",
        name = "Sihirli Unicorn",
        emoji = "🦄",
        species = CharacterSpecies.UNICORN,
        bodyColor = 0xFFF0ABFC,
        secondaryColor = 0xFFFDF4FF,
        detailColor = 0xFFFBBF24,
        accentColor = 0xFF67E8F9,
        unlockCoins = 800
    ),
    CharacterSkin(
        id = "ninja_shadow",
        name = "Gölge Ninja",
        emoji = "🥷",
        species = CharacterSpecies.CHICK,
        bodyColor = 0xFF333333,
        secondaryColor = 0xFF1E1E24,
        detailColor = 0xFFE63946,
        accentColor = 0xFFEF233C,
        unlockCoins = 1000
    )
)

data class Player(
    var x: Float = 120f,
    var y: Float = 0f,
    var vy: Float = 0f,
    var isGrounded: Boolean = true,
    var isDucking: Boolean = false,
    var duckTimer: Float = 0f,
    var jumpCount: Int = 0,
    var width: Float = 155f,
    var height: Float = 155f,
    var rotation: Float = 0f,
    var animFrame: Float = 0f
)

data class Obstacle(
    val id: Long,
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val type: ObstacleType,
    var scored: Boolean = false
)

data class Collectible(
    val id: Long,
    var x: Float,
    var y: Float,
    val size: Float = 78f,
    val type: CollectibleType,
    var isCollected: Boolean = false,
    var bobOffset: Float = 0f
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    val color: Long,
    var alpha: Float = 1f,
    val maxLife: Float = 0.5f,
    var life: Float = 0.5f
)

data class PowerUpActive(
    val type: CollectibleType,
    var remainingTimeSec: Float,
    val totalDurationSec: Float
)
