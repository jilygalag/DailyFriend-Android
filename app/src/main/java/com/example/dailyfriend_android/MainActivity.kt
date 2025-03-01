package com.example.dailyfriend_android

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickVoiceScreen { selectedVoiceOption ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("voice_option", selectedVoiceOption)
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun PickVoiceScreen(onSelect: (VoiceOption) -> Unit) {
    var selectedVoice by remember { mutableStateOf<VoiceOption?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val voiceOptions = listOf(
        VoiceOption(
            "Meadow",
            "https://static.dailyfriend.ai/conversations/samples/1/%d/audio.mp3",
            "https://static.dailyfriend.ai/conversations/samples/1/%d/transcription.txt",
            "https://static.dailyfriend.ai/images/voices/meadow.svg"
        ),
        VoiceOption(
            "Cypress",
            "https://static.dailyfriend.ai/conversations/samples/2/%d/audio.mp3",
            "https://static.dailyfriend.ai/conversations/samples/2/%d/transcription.txt",
            "https://static.dailyfriend.ai/images/voices/cypress.svg"
        ),
        VoiceOption(
            "Iris",
            "https://static.dailyfriend.ai/conversations/samples/3/%d/audio.mp3",
            "https://static.dailyfriend.ai/conversations/samples/3/%d/transcription.txt",
            "https://static.dailyfriend.ai/images/voices/iris.svg"
        ),
        VoiceOption(
            "Hawke",
            "https://static.dailyfriend.ai/conversations/samples/4/%d/audio.mp3",
            "https://static.dailyfriend.ai/conversations/samples/4/%d/transcription.txt",
            "https://static.dailyfriend.ai/images/voices/hawke.svg"
        ),
        VoiceOption(
            "Seren",
            "https://static.dailyfriend.ai/conversations/samples/5/%d/audio.mp3",
            "https://static.dailyfriend.ai/conversations/samples/5/%d/transcription.txt",
            "https://static.dailyfriend.ai/images/voices/seren.svg"
        ),
        VoiceOption(
            "Stone",
            "https://static.dailyfriend.ai/conversations/samples/6/%d/audio.mp3",
            "https://static.dailyfriend.ai/conversations/samples/6/%d/transcription.txt",
            "https://static.dailyfriend.ai/images/voices/stone.svg"
        ),
    )
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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

        LottieFromUrl(
            url = "https://static.dailyfriend.ai/images/mascot-animation.json",
            isPlaying = isPlaying
        )

        // Subtitle
        Text(
            text = "Find the voice that resonates with you",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(voiceOptions) { voice ->
                VoiceOptionView(
                    voiceOption = voice,
                    isSelected = selectedVoice?.name == voice.name,
                    onSelect = {
                        selectedVoice = voice

                        mediaPlayer?.release()
                        mediaPlayer = playAudio(voice.audioStringUrl, completion = {
                            isPlaying = false
                        })
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Button
        Button(
            onClick = {
                selectedVoice?.let {
                    onSelect(it)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF7D67) // Gradient not supported, using solid
            ),
            enabled = selectedVoice != null,
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

fun playAudio(url: String, completion: () -> Unit) : MediaPlayer {
    val formattedUrl = String.format(url, Random.nextInt(1, 21))
    return MediaPlayer().apply {
        setDataSource(formattedUrl)
        prepareAsync()
        setOnPreparedListener { start() }
        setOnCompletionListener {
            release()
            completion()
        }
    }
}

@Composable
fun VoiceOptionView(voiceOption: VoiceOption, isSelected: Boolean, onSelect: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFFFE0D4) else Color.White)
            .border(
                1.dp,
                if (isSelected) Color(0xFFFF7D67) else Color.LightGray,
                RoundedCornerShape(16.dp)
            )
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

            SvgImage(
                url = voiceOption.imageStringUrl
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = voiceOption.name,
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

@Composable
fun SvgImage(url: String) {
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .decoderFactory(SvgDecoder.Factory())
                .build()
        ),
        contentDescription = "SVG Image",
        modifier = Modifier.size(100.dp)
    )
}

@Composable
fun LottieFromUrl(url: String, isPlaying: Boolean) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url(url)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(200.dp),
        alignment = Alignment.Center
    )
}

data class VoiceOption(
    val name: String,
    val audioStringUrl: String,
    val transcriptionStringUrl: String,
    val imageStringUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(audioStringUrl)
        parcel.writeString(transcriptionStringUrl)
        parcel.writeString(imageStringUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<VoiceOption> {
        override fun createFromParcel(parcel: Parcel): VoiceOption {
            return VoiceOption(parcel)
        }

        override fun newArray(size: Int): Array<VoiceOption?> {
            return arrayOfNulls(size)
        }
    }
}
