
package com.jarvis.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JarvisScreen()
        }
    }
}

@Composable
fun JarvisScreen() {
    val background = Color(0xFF07111F)
    val cyan = Color(0xFF00E5FF)

    var message by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(listOf("JARVIS: Hello! How can I assist you?"))
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
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = cyan,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

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
