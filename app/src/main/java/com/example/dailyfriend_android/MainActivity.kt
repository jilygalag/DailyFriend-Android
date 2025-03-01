package com.example.dailyfriend_android

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickVoiceScreen()
        }
    }
}

@Composable
fun PickVoiceScreen() {
    var selectedVoice by remember { mutableStateOf("") }

    val voices = listOf(
        "Meadow " to "https://static.dailyfriend.ai/conversations/samples/1/1/audio.mp3",
        "Cypress" to "https://static.dailyfriend.ai/conversations/samples/2/1/audio.mp3",
        "Iris" to "https://static.dailyfriend.ai/conversations/samples/3/1/audio.mp3",
        "Hawke" to "https://static.dailyfriend.ai/conversations/samples/4/1/audio.mp3",
        "Seren" to "https://static.dailyfriend.ai/conversations/samples/5/1/audio.mp3",
        "Stone" to "https://static.dailyfriend.ai/conversations/samples/6/1/audio.mp3"
    )
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFFFF8F2)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Pick my voice",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Default.RadioButtonChecked,
            contentDescription = "Selected",
            tint = Color(0xFFFF7D67),
            modifier = Modifier.size(80.dp)
        )

        // Subtitle
        Text(
            text = "Find the voice that resonates with you",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(voices) { voice ->
                VoiceOption(
                    name = voice.first,
                    isSelected = selectedVoice == voice.first,
                    onSelect = {
                        selectedVoice = voice.first

                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(voice.second)
                            prepare()
                            start()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Button
        Button(
            onClick = { /* Handle Next */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF7D67) // Gradient not supported, using solid
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(text = "Next", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VoiceOption(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFFFE0D4) else Color.White)
            .border(1.dp, if (isSelected) Color(0xFFFF7D67) else Color.LightGray, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(16.dp)
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,  // Center children vertically
            horizontalAlignment = Alignment.CenterHorizontally  // Center children horizontally
        ) {
            // Placeholder for Image/Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Radio Button
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.RadioButtonChecked,
                contentDescription = "Selected",
                tint = Color(0xFFFF7D67),
                modifier = Modifier
                    .size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.RadioButtonUnchecked,
                contentDescription = "Unselected",
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}
