package com.sleep_paralysis_monitor.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    if (auth.currentUser != null) {
        val intent = Intent(context, DashActivity::class.java)
        context.startActivity(intent)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        bottom = 34.dp,
                        start = 10.dp,
                        end = 10.dp
                    )
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Sleep Paralysis Monitor",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp, start = 10.dp, end = 10.dp)
                )

                // Login Btn.
                Button(
                    onClick = {
                        if (auth.currentUser != null) {
                            val intent = Intent(context, DashActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .height(30.dp)
                ) {
                    Text(text = "Login", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp)) // Add space between buttons

                // Signup Btn.
                Button(
                    onClick = {
                        if (auth.currentUser != null) {
                            val intent = Intent(context, DashActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, SignUpActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .height(30.dp)
                ) {
                    Text(text = "Signup", color = Color.Black)
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:shape=Round,width=411,height=891,unit=dp,dpi=420",
    widthDp = 300,
    heightDp = 300
)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
