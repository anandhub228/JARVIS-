
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

import androidx.core.content.ContextCompat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.json.JSONObject

import java.net.HttpURLConnection
import java.net.URL
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


suspend fun askJarvisAI(userMessage: String): String {

    return withContext(Dispatchers.IO) {

        val url = URL(
            "https://jarvis-ai.anandhub228.workers.dev/"
        )

        val connection =
            url.openConnection() as HttpURLConnection

        try {

            connection.requestMethod = "POST"
            connection.connectTimeout = 15000
            connection.readTimeout = 60000
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            val json = JSONObject()
            json.put("message", userMessage)

            connection.outputStream.use {
                it.write(
                    json.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val responseCode = connection.responseCode

            val responseStream =
                if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                        ?: throw Exception("Server error: $responseCode")
                }

            val response = responseStream.bufferedReader().use {
                it.readText()
            }

            val result = JSONObject(response)

            if (responseCode in 200..299) {

                result.optString(
                    "reply",
                    "JARVIS received an empty response."
                )

            } else {

                result.optString(
                    "error",
                    "Server error: $responseCode"
                )
            }

        } finally {
            connection.disconnect()
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

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var message by remember {
        mutableStateOf("")
    }

    var messages by remember {
        mutableStateOf(
            listOf("JARVIS: Hello! How can I assist you?")
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }


    // Automatically scroll to the latest message

    LaunchedEffect(messages.size, isLoading) {

        val lastItemIndex =
            messages.lastIndex + if (isLoading) 1 else 0

        if (lastItemIndex >= 0) {
            listState.animateScrollToItem(lastItemIndex)
        }
    }


    // Handle voice input

    LaunchedEffect(spokenText) {

        if (spokenText.isNotBlank()) {

            val userMessage = spokenText.trim()

            messages = messages + "YOU: $userMessage"

            message = ""

            onSpokenTextUsed()

            if (!isLoading) {

                isLoading = true

                scope.launch {

                    try {

                        val reply = askJarvisAI(userMessage)

                        messages = messages + "JARVIS: $reply"

                    } catch (e: Exception) {

                        messages = messages +
                            "JARVIS: Connection error. Please try again."

                    } finally {

                        isLoading = false
                    }
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
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


        // Scrollable chat area

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    Color(0xFF102033),
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {

            items(messages) { item ->

                Text(
                    text = item,
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (isLoading) {

                item {

                    Text(
                        text = "JARVIS is thinking...",
                        color = cyan,
                        fontSize = 14.sp
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(12.dp))


        // Message input and buttons

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = message,

                onValueChange = {
                    message = it
                },

                modifier = Modifier.weight(1f),

                placeholder = {
                    Text(
                        "Ask JARVIS...",
                        color = Color.LightGray
                    )
                },

                textStyle = TextStyle(
                    color = Color.White
                ),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = cyan,
                    focusedBorderColor = cyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedPlaceholderColor = Color.LightGray,
                    unfocusedPlaceholderColor = Color.LightGray
                ),

                singleLine = true
            )


            Spacer(modifier = Modifier.width(6.dp))


            IconButton(
                onClick = onMicClick
            ) {
                Text(
                    "🎙️",
                    fontSize = 24.sp
                )
            }


            Button(
                onClick = {

                    if (message.isNotBlank() && !isLoading) {

                        val userMessage = message.trim()

                        messages = messages + "YOU: $userMessage"

                        message = ""

                        isLoading = true

                        scope.launch {

                            try {

                                val reply = askJarvisAI(userMessage)

                                messages = messages + "JARVIS: $reply"

                            } catch (e: Exception) {

                                messages = messages +
                                    "JARVIS: Connection error. Please try again."

                            } finally {

                                isLoading = false
                            }
                        }
                    }
                },

                enabled = !isLoading,

                colors = ButtonDefaults.buttonColors(
                    containerColor = cyan
                ),

                shape = CircleShape

            ) {

                Text(
                    "Send",
                    color = Color.Black
                )
            }
        }
    }
}
