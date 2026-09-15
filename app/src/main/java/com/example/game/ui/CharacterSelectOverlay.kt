package com.example.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.data.GameStorage
import com.example.game.data.GameStrings
import com.example.game.engine.GameEngine
import com.example.game.models.AVAILABLE_SKINS
import com.example.game.models.CharacterSkin
import com.example.ui.theme.CiciCardBg
import com.example.ui.theme.CiciCoinGold
import com.example.ui.theme.CiciDark
import com.example.ui.theme.CiciGreen
import com.example.ui.theme.CiciYellow

@Composable
fun CharacterSelectOverlay(
    engine: GameEngine,
    storage: GameStorage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmingSkin by remember { mutableStateOf<CharacterSkin?>(null) }

    val lang = storage.language
    val isTr = lang != "en"

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = maxWidth > maxHeight || maxHeight < 520.dp

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CiciCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .widthIn(max = if (isLandscape) 640.dp else 460.dp)
                .fillMaxWidth(0.92f)
                .fillMaxHeight(if (isLandscape) 0.95f else 0.88f)
                .padding(if (isLandscape) 6.dp else 16.dp)
                .clickable(enabled = false, onClick = {})
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isLandscape) 14.dp else 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 14.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = GameStrings.charactersTitle(lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp).testTag("close_characters_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = GameStrings.close(lang),
                            tint = Color.White
                        )
                    }
                }

                // Balance Info (Reactive Compose State)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = GameStrings.availableStars(lang),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = CiciCoinGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${storage.totalCoins}",
                        color = CiciCoinGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                // Grid of Characters
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isLandscape) 4 else 2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(AVAILABLE_SKINS) { skin ->
                        val isUnlocked = storage.isSkinUnlocked(skin.id)
                        val isSelected = engine.selectedSkin.id == skin.id
                        val canUnlock = storage.totalCoins >= skin.unlockCoins
                        val isConfirming = confirmingSkin?.id == skin.id

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isLandscape) 124.dp else 148.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isSelected) CiciYellow.copy(alpha = 0.2f) else CiciDark.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) CiciYellow else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .clickable {
                                        if (isUnlocked) {
                                            engine.selectSkin(skin)
                                            confirmingSkin = null
                                        } else {
                                            confirmingSkin = skin
                                        }
                                    }
                                    .testTag("skin_item_${skin.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Avatar circle
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(54.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(skin.bodyColor),
                                            modifier = Modifier.size(50.dp),
                                            shadowElevation = 4.dp
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = skin.emoji,
                                                    fontSize = 26.sp
                                                )
                                            }
                                        }

                                        if (!isUnlocked) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.55f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Kilitli",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = skin.localizedName(lang),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )

                                    when {
                                        isSelected -> {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = CiciGreen
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = GameStrings.selected(lang),
                                                        tint = CiciDark,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Text(
                                                        text = GameStrings.selected(lang),
                                                        color = CiciDark,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                        isUnlocked -> {
                                            Text(
                                                text = GameStrings.unlocked(lang),
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        else -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Star",
                                                    tint = if (canUnlock) CiciCoinGold else Color.Gray,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = "${skin.unlockCoins}",
                                                    color = if (canUnlock) CiciCoinGold else Color.Gray,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Confirmation Card Overlay on top of the selected tile
                            if (isConfirming && !isUnlocked) {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(0xFF0F172A).copy(alpha = 0.95f),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(2.dp, if (canUnlock) CiciYellow else Color(0xFFEF4444), RoundedCornerShape(18.dp))
                                        .clickable(enabled = false, onClick = {})
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        if (canUnlock) {
                                            Text(
                                                text = GameStrings.characterUnlockFor(lang, skin.unlockCoins),
                                                color = CiciYellow,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Button(
                                                    onClick = {
                                                        engine.purchaseAndSelectSkin(skin)
                                                        confirmingSkin = null
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = CiciGreen,
                                                        contentColor = CiciDark
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(32.dp)
                                                        .testTag("confirm_buy_${skin.id}")
                                                ) {
                                                    Text(
                                                        text = GameStrings.buy(lang),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                                Button(
                                                    onClick = { confirmingSkin = null },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF475569),
                                                        contentColor = Color.White
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(32.dp)
                                                        .testTag("cancel_buy_${skin.id}")
                                                ) {
                                                    Text(
                                                        text = GameStrings.cancel(lang),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = "${GameStrings.insufficientStars(lang)}\n${GameStrings.needMoreCoins(lang, skin.unlockCoins - storage.totalCoins)}",
                                                color = Color(0xFFEF4444),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Button(
                                                onClick = { confirmingSkin = null },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF475569),
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth(0.9f)
                                                    .height(30.dp)
                                                    .testTag("close_insufficient_coins_${skin.id}")
                                            ) {
                                                Text(
                                                    text = GameStrings.close(lang),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Done button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLandscape) 40.dp else 46.dp)
                        .testTag("done_characters_button"),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CiciYellow,
                        contentColor = CiciDark
                    )
                ) {
                    Text(
                        text = GameStrings.done(lang),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
