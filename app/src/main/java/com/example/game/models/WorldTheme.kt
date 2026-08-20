package com.example.game.models

import androidx.compose.ui.graphics.Color

data class WorldTheme(
    val index: Int,
    val name: String,
    val subtitle: String,
    val emoji: String,
    val skyColors: List<Color>,
    val mountainColor: Color,
    val hillColor: Color,
    val grassTopColors: List<Color>,
    val grassBladeColor: Color,
    val soilColors: List<Color>,
    val sunColor: Color,
    val sunHaloColor: Color,
    val isNight: Boolean = false
)

val GAME_WORLDS = listOf(
    // 0 - 999 Pts: Sunny Meadow / Günışığı Çayırı
    WorldTheme(
        index = 0,
        name = "GÜNEŞLİ ÇAYIR",
        subtitle = "Taptaze Yeşil Doğa",
        emoji = "🌿",
        skyColors = listOf(Color(0xFF38BDF8), Color(0xFF7DD3FC), Color(0xFFE0F2FE)),
        mountainColor = Color(0xFF94A3B8).copy(alpha = 0.55f),
        hillColor = Color(0xFF4ADE80).copy(alpha = 0.7f),
        grassTopColors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A)),
        grassBladeColor = Color(0xFF86EFAC),
        soilColors = listOf(Color(0xFF854D0E), Color(0xFF582F0E), Color(0xFF381D0B)),
        sunColor = Color(0xFFFBBF24),
        sunHaloColor = Color(0xFFFFFBEB),
        isNight = false
    ),
    // 1000 - 1999 Pts: Sunset Canyon / Gün Batımı Vadisi
    WorldTheme(
        index = 1,
        name = "GÜN BATIMI VADİSİ",
        subtitle = "Kızıl Gökyüzü & Sıcak Kumlar",
        emoji = "🌅",
        skyColors = listOf(Color(0xFF7C2D12), Color(0xFFDC2626), Color(0xFFF97316), Color(0xFFFDE047)),
        mountainColor = Color(0xFF881337).copy(alpha = 0.65f),
        hillColor = Color(0xFFD97706).copy(alpha = 0.75f),
        grassTopColors = listOf(Color(0xFFF97316), Color(0xFFEA580C)),
        grassBladeColor = Color(0xFFFED7AA),
        soilColors = listOf(Color(0xFF7C2D12), Color(0xFF451A03), Color(0xFF290E02)),
        sunColor = Color(0xFFEF4444),
        sunHaloColor = Color(0xFFFEF08A),
        isNight = false
    ),
    // 2000 - 2999 Pts: Mystic Starry Night / Büyülü Gece
    WorldTheme(
        index = 2,
        name = "BÜYÜLÜ GECE",
        subtitle = "Parıldayan Yıldızlar & Ay Işığı",
        emoji = "🌌",
        skyColors = listOf(Color(0xFF090D16), Color(0xFF1E1B4B), Color(0xFF3B0764)),
        mountainColor = Color(0xFF2E1065).copy(alpha = 0.85f),
        hillColor = Color(0xFF0284C7).copy(alpha = 0.65f),
        grassTopColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
        grassBladeColor = Color(0xFFA5F3FC),
        soilColors = listOf(Color(0xFF0F172A), Color(0xFF020617), Color(0xFF000000)),
        sunColor = Color(0xFFF8FAFC),
        sunHaloColor = Color(0xFFE2E8F0),
        isNight = true
    ),
    // 3000 - 3999 Pts: Candy Land / Şeker Dünyası
    WorldTheme(
        index = 3,
        name = "ŞEKER KRALLIĞI",
        subtitle = "Pamuk Şeker Bulutları & Çilekli Çimenler",
        emoji = "🍬",
        skyColors = listOf(Color(0xFFFF70A6), Color(0xFFFF9770), Color(0xFFFFD670)),
        mountainColor = Color(0xFFC084FC).copy(alpha = 0.6f),
        hillColor = Color(0xFF6EE7B7).copy(alpha = 0.7f),
        grassTopColors = listOf(Color(0xFFF472B6), Color(0xFFDB2777)),
        grassBladeColor = Color(0xFFFCE7F3),
        soilColors = listOf(Color(0xFF713F12), Color(0xFF451A03), Color(0xFF3B1F0B)),
        sunColor = Color(0xFFFFD670),
        sunHaloColor = Color(0xFFFFF0F5),
        isNight = false
    ),
    // 4000 - 4999 Pts: Cyber Neon / Siber Şehir
    WorldTheme(
        index = 4,
        name = "SİBER NEON DÜNYA",
        subtitle = "Geleceğin Işıklı Yolu",
        emoji = "⚡",
        skyColors = listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF0466C8)),
        mountainColor = Color(0xFF7C3AED).copy(alpha = 0.8f),
        hillColor = Color(0xFF00B4D8).copy(alpha = 0.7f),
        grassTopColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
        grassBladeColor = Color(0xFF6EE7B7),
        soilColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617)),
        sunColor = Color(0xFF38BDF8),
        sunHaloColor = Color(0xFFE0F2FE),
        isNight = true
    ),
    // 5000+ Pts: Golden Paradise / Altın Cennet
    WorldTheme(
        index = 5,
        name = "ALTIN EFSANE",
        subtitle = "Görkemli Şampiyonlar Diyarı",
        emoji = "👑",
        skyColors = listOf(Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFF67E8F9)),
        mountainColor = Color(0xFFBE123C).copy(alpha = 0.65f),
        hillColor = Color(0xFFFACC15).copy(alpha = 0.75f),
        grassTopColors = listOf(Color(0xFF059669), Color(0xFF047857)),
        grassBladeColor = Color(0xFFFEF08A),
        soilColors = listOf(Color(0xFF78350F), Color(0xFF451A03), Color(0xFF1E1B4B)),
        sunColor = Color(0xFFFFFBEB),
        sunHaloColor = Color(0xFFFEF08A),
        isNight = false
    )
)

fun getWorldForScore(score: Int): WorldTheme {
    val worldIndex = (score / 1000).coerceAtLeast(0)
    return GAME_WORLDS[worldIndex % GAME_WORLDS.size]
}
