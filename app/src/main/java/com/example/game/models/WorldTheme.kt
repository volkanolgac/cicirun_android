package com.example.game.models

import androidx.compose.ui.graphics.Color

enum class WorldStyle {
    MEADOW,     // 0-999m
    CANYON,     // 1000-1999m
    MYSTIC,     // 2000-2999m
    CANDY,      // 3000-3999m
    CYBER,      // 4000-4999m
    GOLDEN      // 5000m+
}

data class WorldTheme(
    val index: Int,
    val style: WorldStyle,
    val name: String,
    val subtitle: String,
    val enName: String,
    val enSubtitle: String,
    val emoji: String,
    val skyColors: List<Color>,
    val mountainColor: Color,
    val hillColor: Color,
    val grassTopColors: List<Color>,
    val grassBladeColor: Color,
    val soilColors: List<Color>,
    val sunColor: Color,
    val sunHaloColor: Color,
    val isNight: Boolean = false,

    // Dynamic Themed Obstacle Visuals (Shapes & Colors evolve every 1000m!)
    val rockColor: Color = Color(0xFF64748B),
    val rockAccentColor: Color = Color(0xFF4ADE80),
    val bushColor: Color = Color(0xFF15803D),
    val spikeColor: Color = Color(0xFFDC2626),
    val treeTrunkColor: Color = Color(0xFF78350F),
    val treeFoliageColor: Color = Color(0xFF16A34A),
    val flyerBodyColor: Color = Color(0xFFFBBF24),
    val flyerWingColor: Color = Color(0xFFFFFFFF),
    val barrierColor: Color = Color(0xFF713F12),
    val barrierStripeColor: Color = Color(0xFFEF4444),

    // Dynamic Themed Collectible Colors
    val coinGlowColor: Color = Color(0xFFFEF08A),
    val coinColors: List<Color> = listOf(Color(0xFFFFF07C), Color(0xFFF59E0B), Color(0xFFD97706)),
    val shieldColors: List<Color> = listOf(Color(0xFF90E0EF), Color(0xFF00B4D8), Color(0xFF0077B6)),
    val magnetColors: List<Color> = listOf(Color(0xFFE0AAFF), Color(0xFF9D4EDD), Color(0xFF5A189A)),
    val speedColors: List<Color> = listOf(Color(0xFFFF9E00), Color(0xFFFF5400), Color(0xFFDC2F02))
) {
    fun localizedName(lang: String): String = if (lang == "en") enName else name
    fun localizedSubtitle(lang: String): String = if (lang == "en") enSubtitle else subtitle
}

val GAME_WORLDS = listOf(
    // 0 - 999m / Pts: Sunny Meadow / Günışığı Çayırı
    WorldTheme(
        index = 0,
        style = WorldStyle.MEADOW,
        name = "GÜNEŞLİ ÇAYIR",
        subtitle = "Taptaze Yeşil Doğa",
        enName = "SUNNY MEADOW",
        enSubtitle = "Fresh Green Nature",
        emoji = "🌿",
        skyColors = listOf(Color(0xFF38BDF8), Color(0xFF7DD3FC), Color(0xFFE0F2FE)),
        mountainColor = Color(0xFF94A3B8).copy(alpha = 0.55f),
        hillColor = Color(0xFF4ADE80).copy(alpha = 0.7f),
        grassTopColors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A)),
        grassBladeColor = Color(0xFF86EFAC),
        soilColors = listOf(Color(0xFF854D0E), Color(0xFF582F0E), Color(0xFF381D0B)),
        sunColor = Color(0xFFFBBF24),
        sunHaloColor = Color(0xFFFFFBEB),
        isNight = false,
        rockColor = Color(0xFF64748B),
        rockAccentColor = Color(0xFF4ADE80),
        bushColor = Color(0xFF15803D),
        spikeColor = Color(0xFFDC2626),
        treeTrunkColor = Color(0xFF78350F),
        treeFoliageColor = Color(0xFF16A34A),
        flyerBodyColor = Color(0xFFFBBF24),
        flyerWingColor = Color.White,
        barrierColor = Color(0xFF713F12),
        barrierStripeColor = Color(0xFFEF4444)
    ),

    // 1000 - 1999m / Pts: Sunset Canyon & Desert / Gün Batımı Vadisi
    WorldTheme(
        index = 1,
        style = WorldStyle.CANYON,
        name = "GÜN BATIMI VADİSİ",
        subtitle = "Kızıl Kanyon & Çöl Kaktüsleri",
        enName = "SUNSET CANYON",
        enSubtitle = "Crimson Rocks & Desert Cacti",
        emoji = "🌅",
        skyColors = listOf(Color(0xFF7C2D12), Color(0xFFDC2626), Color(0xFFF97316), Color(0xFFFDE047)),
        mountainColor = Color(0xFF881337).copy(alpha = 0.65f),
        hillColor = Color(0xFFD97706).copy(alpha = 0.75f),
        grassTopColors = listOf(Color(0xFFF97316), Color(0xFFEA580C)),
        grassBladeColor = Color(0xFFFED7AA),
        soilColors = listOf(Color(0xFF7C2D12), Color(0xFF451A03), Color(0xFF290E02)),
        sunColor = Color(0xFFEF4444),
        sunHaloColor = Color(0xFFFEF08A),
        isNight = false,
        rockColor = Color(0xFFB45309),       // Terracotta Red Canyon Rock
        rockAccentColor = Color(0xFFFDE047), // Desert Sand highlights
        bushColor = Color(0xFF059669),       // Desert Saguaro Cactus Green
        spikeColor = Color(0xFFFACC15),      // Golden Sharp Cactus Needles
        treeTrunkColor = Color(0xFF9A3412),  // Sunbaked Red Wood Pillar
        treeFoliageColor = Color(0xFFD97706),// Autumn Desert Palm Foliage
        flyerBodyColor = Color(0xFFEA580C),  // Crimson Desert Hornet
        flyerWingColor = Color(0xFFFEF08A),  // Translucent Amber Wings
        barrierColor = Color(0xFF9A3412),
        barrierStripeColor = Color(0xFFFACC15),
        coinGlowColor = Color(0xFFFED7AA),
        coinColors = listOf(Color(0xFFFDE047), Color(0xFFF97316), Color(0xFFEA580C)),
        shieldColors = listOf(Color(0xFFFED7AA), Color(0xFFFB923C), Color(0xFFC2410C)),
        magnetColors = listOf(Color(0xFFF472B6), Color(0xFFDB2777), Color(0xFF9D174D)),
        speedColors = listOf(Color(0xFFFDE047), Color(0xFFEF4444), Color(0xFF7F1D1D))
    ),

    // 2000 - 2999m / Pts: Mystic Starry Night / Büyülü Kristal Gece
    WorldTheme(
        index = 2,
        style = WorldStyle.MYSTIC,
        name = "BÜYÜLÜ GECE",
        subtitle = "Ametist Kristaller & Ay Işığı",
        enName = "MYSTIC NIGHT",
        enSubtitle = "Amethyst Crystals & Moonlight",
        emoji = "🌌",
        skyColors = listOf(Color(0xFF090D16), Color(0xFF1E1B4B), Color(0xFF3B0764)),
        mountainColor = Color(0xFF2E1065).copy(alpha = 0.85f),
        hillColor = Color(0xFF0284C7).copy(alpha = 0.65f),
        grassTopColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
        grassBladeColor = Color(0xFFA5F3FC),
        soilColors = listOf(Color(0xFF0F172A), Color(0xFF020617), Color(0xFF000000)),
        sunColor = Color(0xFFF8FAFC),
        sunHaloColor = Color(0xFFE2E8F0),
        isNight = true,
        rockColor = Color(0xFF6B21A8),       // Glowing Purple Amethyst Shards
        rockAccentColor = Color(0xFFC084FC), // Crystal Light refraction
        bushColor = Color(0xFF0284C7),       // Glowing Ice/Crystal Spikes
        spikeColor = Color(0xFF67E8F9),      // Neon Cyan Ice Crystals
        treeTrunkColor = Color(0xFF3B0764),  // Mystic Obsidian Crystal Column
        treeFoliageColor = Color(0xFF8B5CF6),// Luminous Lavender Canopy
        flyerBodyColor = Color(0xFFA855F7),  // Bioluminescent Lunar Moth
        flyerWingColor = Color(0xFF67E8F9),  // Iridescent Cyan Wings
        barrierColor = Color(0xFF1E1B4B),
        barrierStripeColor = Color(0xFFC084FC),
        coinGlowColor = Color(0xFFA5F3FC),
        coinColors = listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8), Color(0xFF0284C7)),
        shieldColors = listOf(Color(0xFFC084FC), Color(0xFF9333EA), Color(0xFF581C87)),
        magnetColors = listOf(Color(0xFF67E8F9), Color(0xFF06B6D4), Color(0xFF0E7490)),
        speedColors = listOf(Color(0xFFA855F7), Color(0xFFEC4899), Color(0xFF9D174D))
    ),

    // 3000 - 3999m / Pts: Candy Land / Şeker Krallığı
    WorldTheme(
        index = 3,
        style = WorldStyle.CANDY,
        name = "ŞEKER KRALLIĞI",
        subtitle = "Lolipoplar & Çilekli Şekerlemeler",
        enName = "CANDY KINGDOM",
        enSubtitle = "Giant Lollipops & Sweet Treats",
        emoji = "🍬",
        skyColors = listOf(Color(0xFFFF70A6), Color(0xFFFF9770), Color(0xFFFFD670)),
        mountainColor = Color(0xFFC084FC).copy(alpha = 0.6f),
        hillColor = Color(0xFF6EE7B7).copy(alpha = 0.7f),
        grassTopColors = listOf(Color(0xFFF472B6), Color(0xFFDB2777)),
        grassBladeColor = Color(0xFFFCE7F3),
        soilColors = listOf(Color(0xFF713F12), Color(0xFF451A03), Color(0xFF3B1F0B)),
        sunColor = Color(0xFFFFD670),
        sunHaloColor = Color(0xFFFFF0F5),
        isNight = false,
        rockColor = Color(0xFFDB2777),       // Pink Frosted Sugar Donut Rock
        rockAccentColor = Color(0xFFFDF2F8), // White Sugar Sprinkles
        bushColor = Color(0xFFEC4899),       // Berry Gummy Spikes
        spikeColor = Color(0xFFFF0055),      // Cherry Candy Tips
        treeTrunkColor = Color(0xFFF43F5E),  // Peppermint Candy Cane Striped
        treeFoliageColor = Color(0xFFF472B6),// Fluffy Cotton Candy Cloud
        flyerBodyColor = Color(0xFFFF69B4),  // Flying Sweet Marshmallow Bug
        flyerWingColor = Color(0xFFFFF1F2),  // Sugar Glaze Wings
        barrierColor = Color(0xFFBE185D),
        barrierStripeColor = Color(0xFFFFFBEB),
        coinGlowColor = Color(0xFFFCE7F3),
        coinColors = listOf(Color(0xFFFFF0F5), Color(0xFFFF69B4), Color(0xFFDB2777)),
        shieldColors = listOf(Color(0xFFFBCFE8), Color(0xFFF472B6), Color(0xFFDB2777)),
        magnetColors = listOf(Color(0xFFFED7AA), Color(0xFFFB923C), Color(0xFFEA580C)),
        speedColors = listOf(Color(0xFFFDE047), Color(0xFFFF0055), Color(0xFF9F1239))
    ),

    // 4000 - 4999m / Pts: Cyber Neon / Siber Neon Şehir
    WorldTheme(
        index = 4,
        style = WorldStyle.CYBER,
        name = "SİBER NEON DÜNYA",
        subtitle = "Lazer Bariyerler & Siber Şebeke",
        enName = "CYBER NEON",
        enSubtitle = "Laser Grids & High-Tech Matrix",
        emoji = "⚡",
        skyColors = listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF0466C8)),
        mountainColor = Color(0xFF7C3AED).copy(alpha = 0.8f),
        hillColor = Color(0xFF00B4D8).copy(alpha = 0.7f),
        grassTopColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
        grassBladeColor = Color(0xFF6EE7B7),
        soilColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617)),
        sunColor = Color(0xFF38BDF8),
        sunHaloColor = Color(0xFFE0F2FE),
        isNight = true,
        rockColor = Color(0xFF0F172A),       // Matte Dark Tech Cube
        rockAccentColor = Color(0xFF10B981), // Glowing Emerald Data Bus
        bushColor = Color(0xFF064E3B),       // Electronic High-Voltage Pylon
        spikeColor = Color(0xFF00FFCC),      // Electric Neon Cyan Sparks
        treeTrunkColor = Color(0xFF1E293B),  // Steel Antenna Transmitter
        treeFoliageColor = Color(0xFF00E5FF),// Hologram Projection Matrix
        flyerBodyColor = Color(0xFF00F0FF),  // High-Speed Cyber Drone
        flyerWingColor = Color(0xFF7C3AED),  // Neon Plasma Thruster Glow
        barrierColor = Color(0xFF020617),
        barrierStripeColor = Color(0xFF00FFCC),
        coinGlowColor = Color(0xFF6EE7B7),
        coinColors = listOf(Color(0xFFA7F3D0), Color(0xFF10B981), Color(0xFF065F46)),
        shieldColors = listOf(Color(0xFFA5F3FC), Color(0xFF00E5FF), Color(0xFF0077B6)),
        magnetColors = listOf(Color(0xFFC084FC), Color(0xFF7C3AED), Color(0xFF4C1D95)),
        speedColors = listOf(Color(0xFF67E8F9), Color(0xFF3B82F6), Color(0xFF1D4ED8))
    ),

    // 5000+ m / Pts: Golden Paradise / Altın Efsane
    WorldTheme(
        index = 5,
        style = WorldStyle.GOLDEN,
        name = "ALTIN EFSANE",
        subtitle = "Görkemli Şampiyonlar Diyarı",
        enName = "GOLDEN MYTHIC",
        enSubtitle = "Glory of the Ultimate Champions",
        emoji = "👑",
        skyColors = listOf(Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFF67E8F9)),
        mountainColor = Color(0xFFBE123C).copy(alpha = 0.65f),
        hillColor = Color(0xFFFACC15).copy(alpha = 0.75f),
        grassTopColors = listOf(Color(0xFF059669), Color(0xFF047857)),
        grassBladeColor = Color(0xFFFEF08A),
        soilColors = listOf(Color(0xFF78350F), Color(0xFF451A03), Color(0xFF1E1B4B)),
        sunColor = Color(0xFFFFFBEB),
        sunHaloColor = Color(0xFFFEF08A),
        isNight = false,
        rockColor = Color(0xFFF59E0B),       // Solid Gold Ingot Rock
        rockAccentColor = Color(0xFFFFFBEB), // Brilliant Diamond Sparkle
        bushColor = Color(0xFF047857),       // Emerald Crown Shrub
        spikeColor = Color(0xFFFEF08A),      // Golden Crown Spikes
        treeTrunkColor = Color(0xFF92400E),  // Divine Golden Obelisk
        treeFoliageColor = Color(0xFFFDE047),// Gilded Sunlight Tree Crown
        flyerBodyColor = Color(0xFFFEF08A),  // Golden Phoenix Flyer
        flyerWingColor = Color(0xFFFFFBEB),  // Radiant Angelic Wings
        barrierColor = Color(0xFFB45309),
        barrierStripeColor = Color(0xFFFFFBEB),
        coinGlowColor = Color(0xFFFFFBEB),
        coinColors = listOf(Color(0xFFFFFFFF), Color(0xFFFEF08A), Color(0xFFF59E0B)),
        shieldColors = listOf(Color(0xFFFEF08A), Color(0xFFFBBF24), Color(0xFFD97706)),
        magnetColors = listOf(Color(0xFFFDE047), Color(0xFFE11D48), Color(0xFF881337)),
        speedColors = listOf(Color(0xFFFFFBEB), Color(0xFFF59E0B), Color(0xFFEA580C))
    )
)

fun getWorldForScoreOrDistance(score: Int, distance: Float): WorldTheme {
    // Both score & distance (every 1000m or 1000 pts) drive progression
    val distanceIndex = (distance / 1000f).toInt()
    val scoreIndex = (score / 1000)
    val chosenIndex = maxOf(distanceIndex, scoreIndex).coerceAtLeast(0)
    return GAME_WORLDS[chosenIndex % GAME_WORLDS.size]
}

fun getWorldForScore(score: Int): WorldTheme {
    val worldIndex = (score / 1000).coerceAtLeast(0)
    return GAME_WORLDS[worldIndex % GAME_WORLDS.size]
}
