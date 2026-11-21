
package com.jarvis.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "J.A.R.V.I.S.",
            color = cyan,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "JUST A RATHER VERY INTELLIGENT SYSTEM",
            color = Color.LightGray,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(70.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Color(0xFF102B40), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "J",
                color = cyan,
                fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "SYSTEM ONLINE",
            color = cyan,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Good evening. How can I help you?",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}
