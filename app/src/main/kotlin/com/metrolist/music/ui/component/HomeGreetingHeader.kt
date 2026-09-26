package com.metrolist.music.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun HomeGreetingHeader(userName: String?) {
    val greeting = remember(userName) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greetings = when (hour) {
            in 5..11 -> if (userName != null) {
                listOf(
                    "Good morning, $userName! ☀️",
                    "Glad you're awake, $userName. 🌅",
                    "Wakey wakey, $userName! 🎧",
                    "Hey, morning check, $userName! ⚡",
                    "Ready today, $userName? ☀️",
                    "Welcome back, $userName! 🌅",
                    "Morning vibe check, $userName! ✨"
                )
            } else {
                listOf(
                    "Good morning! ☀️",
                    "Glad you're awake. 🌅",
                    "Wakey wakey! 🎧",
                    "Morning check! ⚡",
                    "Ready today? ☀️",
                    "Welcome back! 🌅",
                    "Morning vibe check! ✨"
                )
            }
            in 12..16 -> if (userName != null) {
                listOf(
                    "Hey, how's your day, $userName? ☀️",
                    "Need a break, $userName? 💆",
                    "Glad to see you, $userName. 🍕",
                    "Listening under the sun, $userName? ☀️",
                    "Hope it's going well, $userName. 🌟",
                    "Hey, what's playing, $userName? 🎧",
                    "Midday vibe check, $userName! ⚡"
                )
            } else {
                listOf(
                    "Hey, how's your day? ☀️",
                    "Need a break? 💆",
                    "Glad to see you. 🍕",
                    "Listening under the sun? ☀️",
                    "Hope it's going well. 🌟",
                    "Hey, what's playing? 🎧",
                    "Midday vibe check! ⚡"
                )
            }
            in 17..21 -> if (userName != null) {
                listOf(
                    "Welcome home, $userName! 🏡",
                    "Unwinding, $userName? 🛋️",
                    "Glad you made it, $userName. 💛",
                    "Time to chill, $userName. 🌃",
                    "Sunset listening, $userName. 🌇",
                    "How was your day, $userName? ✨"
                )
            } else {
                listOf(
                    "Welcome home! 🏡",
                    "Unwinding? 🛋️",
                    "Glad you made it. 💛",
                    "Time to chill. 🌃",
                    "Sunset listening. 🌇",
                    "How was your day? ✨"
                )
            }
            else -> if (userName != null) {
                listOf(
                    "Under the stars, $userName 🌌",
                    "Up late, $userName? 🌙",
                    "Quiet hours, $userName. 🕯️",
                    "Rest easy, $userName. 💤",
                    "Midnight thoughts, $userName? 💭",
                    "Soft music now, $userName. 🎧"
                )
            } else {
                listOf(
                    "Under the stars 🌌",
                    "Up late? 🌙",
                    "Quiet hours. 🕯️",
                    "Rest easy. 💤",
                    "Midnight thoughts? 💭",
                    "Soft music now. 🎧"
                )
            }
        }
        greetings.random()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = greeting,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
