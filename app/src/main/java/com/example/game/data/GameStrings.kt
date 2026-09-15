package com.example.game.data

object GameStrings {

    fun appSubtitle(lang: String) = if (lang == "en") "ENDLESS RUNNER ADVENTURE" else "SONSUZ KOŞU MACERASI"
    fun subTitle(lang: String) = appSubtitle(lang)
    fun play(lang: String) = if (lang == "en") "PLAY" else "OYNA"
    fun changeCharacter(lang: String) = if (lang == "en") "Change Character" else "Karakter Değiştir"
    fun characters(lang: String) = if (lang == "en") "Characters" else "Karakterler"
    fun howToPlay(lang: String) = if (lang == "en") "How to Play" else "Nasıl Oynanır?"
    fun highScore(lang: String) = if (lang == "en") "BEST SCORE" else "EN İYİ SKOR"
    fun bestDistance(lang: String) = if (lang == "en") "BEST DISTANCE" else "EN UZAK MESAFE"
    fun points(lang: String) = if (lang == "en") "PTS" else "PUAN"
    fun score(lang: String) = if (lang == "en") "SCORE" else "SKOR"
    fun meters(lang: String) = if (lang == "en") "m" else "m"
    fun star(lang: String) = if (lang == "en") "Star" else "Yıldız"
    fun stars(lang: String) = if (lang == "en") "Stars" else "Yıldızlar"
    fun pause(lang: String) = if (lang == "en") "Pause" else "Durdur"
    fun paused(lang: String) = if (lang == "en") "GAME PAUSED" else "OYUN DURDURULDU"
    fun gamePaused(lang: String) = paused(lang)
    fun resume(lang: String) = if (lang == "en") "RESUME" else "DEVAM ET"
    fun restart(lang: String) = if (lang == "en") "PLAY AGAIN" else "TEKRAR OYNA"
    fun playAgain(lang: String) = restart(lang)
    fun mainMenu(lang: String) = if (lang == "en") "MAIN MENU" else "ANA MENÜ"
    fun currentScore(lang: String) = if (lang == "en") "Current Score" else "Mevcut Skor"
    fun distance(lang: String) = if (lang == "en") "Distance" else "Mesafe"
    fun gameOver(lang: String) = if (lang == "en") "GAME OVER" else "OYUN BİTTİ"
    fun newRecord(lang: String) = if (lang == "en") "NEW RECORD!" else "YENİ REKOR!"
    fun speedSurge(lang: String) = if (lang == "en") "⚡ SPEEDING UP!" else "⚡ HIZLANIYOR!"
    fun worldLabel(lang: String, index: Int, name: String) =
        if (lang == "en") "WORLD ${index + 1}: $name" else "DÜNYA ${index + 1}: $name"

    // Controls
    fun jump(lang: String) = if (lang == "en") "JUMP!" else "ZIPLA!"
    fun slide(lang: String) = if (lang == "en") "SLIDE!" else "KAY!"
    fun doubleJump(lang: String) = if (lang == "en") "⚡ 2. JUMP!" else "⚡ 2. ZIPLA!"
    fun tripleJump(lang: String) = if (lang == "en") "🔥 3. JUMP!" else "🔥 3. ZIPLA!"
    fun tripleJumpCap(lang: String) = if (lang == "en") "3X JUMP" else "3X ZIPLAMA"
    fun slidingNow(lang: String) = if (lang == "en") "⚡ SLIDING!" else "⚡ KAYIYOR!"
    fun slideUnder(lang: String) = if (lang == "en") "SLIDE UNDER" else "ALTINDAN GEÇ"

    // Power-ups
    fun shield(lang: String) = if (lang == "en") "SHIELD" else "KALKAN"
    fun magnet(lang: String) = if (lang == "en") "MAGNET" else "MIKNATIS"
    fun speedBoost(lang: String) = if (lang == "en") "SPEED 2X" else "HIZ GAZI 2X"

    // Quick hint
    fun quickHint(lang: String) = if (lang == "en")
        "💡 Tap Right to Jump (3x Jump!) • Hold Left to Slide"
    else
        "💡 Zıplamak için Dokun (3x Zıplama!) • Kaymak için Basılı Tut"

    fun hintJumpSlide(lang: String) = quickHint(lang)

    // Character Select & Shop
    fun charactersTitle(lang: String) = if (lang == "en") "SELECT CHARACTER" else "KARAKTER SEÇ"
    fun availableStars(lang: String) = if (lang == "en") "Available Stars:" else "Kullanılabilir Yıldızlar:"
    fun characterUnlocked(lang: String) = if (lang == "en") "Selected" else "Seçildi"
    fun selected(lang: String) = if (lang == "en") "SELECTED" else "SEÇİLDİ"
    fun unlocked(lang: String) = if (lang == "en") "UNLOCKED" else "AÇIK"
    fun characterSelect(lang: String) = if (lang == "en") "Select" else "Seç"
    fun characterUnlockFor(lang: String, coins: Int) = if (lang == "en") "Unlock $coins ⭐" else "Aç $coins ⭐"
    fun buy(lang: String) = if (lang == "en") "BUY ✅" else "AL ✅"
    fun cancel(lang: String) = if (lang == "en") "CANCEL" else "İPTAL"
    fun insufficientStars(lang: String) = if (lang == "en") "Not enough ⭐" else "Yetersiz ⭐"
    fun needMoreCoins(lang: String, needed: Int) =
        if (lang == "en") "$needed more stars needed!" else "$needed yıldız daha lazım!"
    fun close(lang: String) = if (lang == "en") "Close" else "Kapat"
    fun done(lang: String) = if (lang == "en") "DONE" else "TAMAM"

    // Share Strings
    fun shareText(lang: String, score: Int, distance: Int, emoji: String) = if (lang == "en")
        "I scored $score Points and reached ${distance}m in CiCi Run! $emoji Can you beat my score? #CiCiRun"
    else
        "CiCi Run oyununda $score Puan ve ${distance}m Mesafe skoru yaptım! $emoji Sen de oyna! #CiCiRun"

    fun shareScreenshot(lang: String) = if (lang == "en") "Share Screenshot" else "Ekran Görüntüsünü Paylaş"
    fun shareScore(lang: String) = if (lang == "en") "Share Score" else "Skorunu Paylaş"
    fun screenshot(lang: String) = if (lang == "en") "Screenshot" else "Ekran Görüntüsü"
    fun shareBarLabel(lang: String) = if (lang == "en") "Share:" else "Paylaş:"
    fun share(lang: String) = shareBarLabel(lang)

    // How to Play Tutorial Details
    fun tutorialTitle(lang: String) = if (lang == "en") "HOW TO PLAY" else "NASIL OYNANIR?"
    fun tutorialSubtitle(lang: String) = if (lang == "en")
        "Learn controls, powers, and explore changing worlds!"
    else
        "Kontrolleri, güçlendiricileri ve değişen dünyaları öğren!"

    fun tutorialControls(lang: String) = if (lang == "en") "🎮 MOVEMENT & CONTROLS" else "🎮 HAREKET & KONTROLLER"
    fun sectionControls(lang: String) = tutorialControls(lang)
    fun jumpTitle(lang: String) = if (lang == "en") "Triple Jump (1x, 2x, 3x)" else "Üçlü Zıplama (1x, 2x, 3x)"
    fun jumpDesc(lang: String) = if (lang == "en")
        "Tap the JUMP button or the right side of the screen. Tap in mid-air up to 3 times to leap over tall trees and flying bees!"
    else
        "ZIPLA butonuna veya ekranın sağına dokun. Havada 3 defaya kadar tekrar zıplayarak yüksek ağaçların ve arıların üzerinden aş!"

    fun slideTitle(lang: String) = if (lang == "en") "Slide / Duck" else "Kayma & Eğilme"
    fun slideDesc(lang: String) = if (lang == "en")
        "Hold the SLIDE button or touch the left side of the screen. Slide safely underneath hanging barriers and flying obstacles!"
    else
        "KAY butonuna basılı tut veya ekranın soluna dokun. Askıdaki tabelaların ve uçan engellerin altından güvenle kayarak geç!"

    fun tutorialItems(lang: String) = if (lang == "en") "⭐ ITEMS & POWER-UPS" else "⭐ NESNELER VE GÜÇLER"
    fun sectionCollectibles(lang: String) = tutorialItems(lang)
    fun coinTitle(lang: String) = if (lang == "en") "Golden Star Coins" else "Altın Yıldızlar"
    fun coinDesc(lang: String) = if (lang == "en")
        "Collect stars along the track to boost your score and unlock 13 unique cute characters in the shop!"
    else
        "Koşu boyunca yıldızları topla! Skorunu katlar ve mağazada 13 sevimli karakter kostümünün kilidini açar."

    fun shieldTitle(lang: String) = if (lang == "en") "Cyan Protection Shield" else "Mavi Koruma Kalkanı"
    fun shieldDesc(lang: String) = if (lang == "en")
        "Grants complete immunity against 1 crash. When hit, the shield absorbs the impact so Cici keeps running uninterrupted!"
    else
        "Seni bir çarpmaya karşı tamamen korur! Bir engele çarptığında kalkan kırılır ama Cici koşmaya devam eder."

    fun magnetTitle(lang: String) = if (lang == "en") "Purple Magnet Power" else "Mor Mıknatıs Gücü"
    fun magnetDesc(lang: String) = if (lang == "en")
        "Creates a strong magnetic field around Cici, pulling in all nearby gold stars without needing to touch them!"
    else
        "Cici'nin etrafında güçlü çekim alanı oluşturur, yakındaki tüm yıldızları havada süzülerek otomatik toplar!"

    fun speedBoostTitle(lang: String) = if (lang == "en") "Orange Flame 2X Speed" else "Turuncu Hız Gazı 2X"
    fun speedBoostDesc(lang: String) = if (lang == "en")
        "Propels Cici into super velocity! While active, all score increments are doubled (2X Points) for massive records!"
    else
        "Cici'ye süper hız patlaması verir! Aktif olduğu süre boyunca kazanılan tüm puanlar ikiye (2X) katlanır!"

    fun sectionWorlds(lang: String) = if (lang == "en") "🌍 DYNAMIC EVOLVING WORLDS" else "🌍 HER 1000m'DE DEĞİŞEN DÜNYALAR"
    fun worldsDesc(lang: String) = if (lang == "en")
        "Every 1000 meters reached, the entire landscape changes! Obstacles transform into new shapes (Canyon Cacti, Amethyst Crystals, Candy Canes, Cyber Lasers), and collectibles adapt with unique world themes!"
    else
        "Her 1000 metrede bir oyun evrilir! Engeller yeni tematik şekillere bürünür (Kaktüsler, Kristaller, Şeker Bastonları, Siber Lazerler) ve nesneler dünyaların büyülü renklerine dönüşür!"

    fun gotIt(lang: String) = if (lang == "en") "LET'S RUN!" else "ANLADIM, BAŞLA!"
    fun tutorialClose(lang: String) = gotIt(lang)
}
