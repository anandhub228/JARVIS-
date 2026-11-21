
package com.jarvis.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var speechResult by mutableStateOf("")

    private val speechLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )?.firstOrNull()

                speechResult = spokenText ?: ""
            }
        }

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                startSpeechRecognition()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JarvisScreen(
                onMicClick = { requestMicrophone() },
                spokenText = speechResult,
                onSpokenTextUsed = { speechResult = "" }
            )
        }
    }

    private fun requestMicrophone() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            permissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak to JARVIS"
            )
        }

        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            speechResult = "Speech recognition is unavailable"
        }
    }
}

@Composable
fun JarvisScreen(
    onMicClick: () -> Unit,
    spokenText: String,
    onSpokenTextUsed: () -> Unit
) {
    val background = Color(0xFF07111F)
    val cyan = Color(0xFF00E5FF)

    var message by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf("JARVIS: Hello! How can I assist you?")
        )
    }

    LaunchedEffect(spokenText) {
        if (spokenText.isNotBlank()) {
            message = spokenText
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(16.dp)
    ) {
        Text(
            text = "J.A.R.V.I.S.",
            color = cyan,
            fontSize = 28.sp
        )

        Text(
            text = "PERSONAL AI ASSISTANT",
            color = Color.LightGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    Color(0xFF102033),
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            messages.forEach { item ->
                Text(
                    text = item,
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Ask JARVIS...")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(onClick = onMicClick) {
                Text("🎙️", fontSize = 24.sp)
            }

            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        messages = messages + "YOU: $message"
                        message = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = cyan
                ),
                shape = CircleShape
            ) {
                Text("Send", color = Color.Black)
            }
        }
    }
}
