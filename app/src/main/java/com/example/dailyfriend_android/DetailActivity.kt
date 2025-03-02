package com.example.dailyfriend_android

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.random.Random

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voiceOption: VoiceOption? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("voice_option", VoiceOption::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("voice_option")
        }

        setContent {
            voiceOption?.let {
                DetailScreen(voiceOption = voiceOption) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }
}

@Composable
fun DetailScreen(voiceOption: VoiceOption, onBackPressed: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("Loading...") }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(Unit) {
        repeat(3) { count ->
            val randomInt = Random.nextInt(1, 21)
            val randomAudioUrl = String.format(voiceOption.audioStringUrl, randomInt)
            val randomTranscriptionUrl = String.format(voiceOption.transcriptionStringUrl, randomInt)
            isPlaying = true
            text = fetchTextFromUrl(randomTranscriptionUrl)
            playAudioFromUrl(randomAudioUrl)
            if (count == 2) {
                isPlaying = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    IconButton(
        onClick = { onBackPressed() }
    ) {
        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        LottieFromUrl(
            url = "https://static.dailyfriend.ai/images/mascot-animation.json",
            isPlaying = isPlaying
        )

        Text(
            text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

suspend fun playAudioFromUrl(url: String) {
    return suspendCancellableCoroutine { continuation ->
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()

            setOnPreparedListener { start() }
            setOnCompletionListener {
                release()
                continuation.resume(Unit)
            }
        }

        // If coroutine is cancelled, release MediaPlayer
        continuation.invokeOnCancellation {
            mediaPlayer.release()
        }
    }
}

suspend fun fetchTextFromUrl(urlString: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error fetching text"
        }
    }
}

