package com.example.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.data.GameStrings
import com.example.ui.theme.CiciCardBg
import com.example.ui.theme.CiciCoinGold
import com.example.ui.theme.CiciDark
import com.example.ui.theme.CiciSkyLight
import com.example.ui.theme.CiciYellow

@Composable
fun TutorialOverlay(
    lang: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = lang != "en"

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(Color.Black.copy(alpha = 0.88f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CiciCardBg,
            shadowElevation = 16.dp,
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .testTag("tutorial_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Title and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = GameStrings.tutorialTitle(lang),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = CiciYellow
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("tutorial_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Section 1: Controls
                Text(
                    text = GameStrings.tutorialControls(lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CiciSkyLight,
                    modifier = Modifier.fillMaxWidth()
                )

                TutorialItemCard(
                    icon = Icons.Default.KeyboardArrowUp,
                    iconBg = Color(0xFF38BDF8),
                    title = if (isTr) "Zıpla (Yukarı Kaydır / Buton)" else "Jump (Swipe Up / Button)",
                    description = if (isTr) "Kayalar, kaktüsler ve dikenli çalıların üzerinden atla! Havada tekrar basarak çift zıpla." else "Leap over rocks, cacti and spike bushes! Tap again in mid-air to double jump."
                )

                TutorialItemCard(
                    icon = Icons.Default.KeyboardArrowDown,
                    iconBg = Color(0xFFF97316),
                    title = if (isTr) "Kay / Çömel (Aşağı Kaydır / Buton)" else "Slide / Duck (Swipe Down / Button)",
                    description = if (isTr) "Yüksek asılı tabelaların ve bariyerlerin altından kayarak geç." else "Slide safely underneath high hanging signs and overhead wooden barriers."
                )

                TutorialItemCard(
                    icon = Icons.Default.ScreenRotation,
                    iconBg = Color(0xFF8B5CF6),
                    title = if (isTr) "Yatay & Dikey Mod" else "Landscape & Portrait Mode",
                    description = if (isTr) "Telefonu veya tableti yan çevirerek geniş ekranda daha rahat ve keyifli oyna!" else "Rotate your device sideways to enjoy a wide landscape running adventure on phones and tablets!"
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Section 2: Items & Power-ups
                Text(
                    text = GameStrings.tutorialItems(lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CiciYellow,
                    modifier = Modifier.fillMaxWidth()
                )

                TutorialItemCard(
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFFF59E0B),
                    title = if (isTr) "Altın Yıldız (Star Coin)" else "Star Coin",
                    description = if (isTr) "Puanını artırır ve toplam yıldız birikimine eklenir." else "Boosts your score and adds to your total coin balance."
                )

                TutorialItemCard(
                    icon = Icons.Default.Shield,
                    iconBg = Color(0xFF06B6D4),
                    title = if (isTr) "Koruma Kalkanı (Shield)" else "Protection Shield",
                    description = if (isTr) "1 engele çarpmayı tamamen engeller, Cici'yi korur!" else "Absorbs 1 obstacle collision and protects Cici!"
                )

                TutorialItemCard(
                    icon = Icons.Default.Navigation,
                    iconBg = Color(0xFFA855F7),
                    title = if (isTr) "Yıldız Mıknatısı (Magnet)" else "Star Magnet",
                    description = if (isTr) "Yakındaki tüm altın yıldızları otomatik olarak Cici'ye doğru çeker." else "Pulls all nearby gold coins directly towards Cici automatically."
                )

                TutorialItemCard(
                    icon = Icons.Default.FlashOn,
                    iconBg = Color(0xFFEF4444),
                    title = if (isTr) "Hız Roketi (Speed Boost)" else "Speed Boost",
                    description = if (isTr) "Süper hızlandırır ve geçici dokunulmazlık sağlar!" else "Grants blazing supersonic speed and temporary invincibility!"
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Section 3: Dynamic World & Obstacle Morphing
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isTr) "🌈 Dinamik Dünya & 1000m Dönüşümü" else "🌈 Dynamic World & 1000m Evolution",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4ADE80)
                        )
                        Text(
                            text = if (isTr) "Her 1000 metrede veya 1000 puanda engeller (kaktüsler, şeker çubukları, neon pilonlar) ve dünyalar farklı renk ve şekillere dönüşür!" else "Every 1000 meters or 1000 points, obstacles (cacti, candy canes, neon pylons) and environments transform into new colors and shapes!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 17.sp
                        )
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("tutorial_got_it_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CiciYellow,
                        contentColor = CiciDark
                    )
                ) {
                    Text(
                        text = GameStrings.tutorialClose(lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialItemCard(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
