package com.example.dailyfriend_android

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AudioStreamingScreen()
        }
    }
}

@Composable
fun AudioStreamingScreen() {
    val voiceSamples = listOf(
        "Meadow haha" to "https://static.dailyfriend.ai/conversations/samples/1/1/audio.mp3",
        "Cypress" to "https://static.dailyfriend.ai/conversations/samples/2/1/audio.mp3",
        "Iris" to "https://static.dailyfriend.ai/conversations/samples/3/1/audio.mp3",
        "Hawke" to "https://static.dailyfriend.ai/conversations/samples/4/1/audio.mp3",
        "Seren" to "https://static.dailyfriend.ai/conversations/samples/5/1/audio.mp3",
        "Stone" to "https://static.dailyfriend.ai/conversations/samples/6/1/audio.mp3"
    )
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select a Voice", style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(voiceSamples) { voice ->
                Button(
                    onClick = {
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(voice.second)
                            prepare()
                            start()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(voice.first)
                }
            }
        }
        Button(
            onClick = {
                val randomVoice = voiceSamples.random().second
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(randomVoice)
                    prepare()
                    start()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Next")
        }
    }
}
