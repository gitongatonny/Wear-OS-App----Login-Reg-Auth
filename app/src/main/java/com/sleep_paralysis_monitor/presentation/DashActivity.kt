package com.sleep_paralysis_monitor.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.google.firebase.auth.FirebaseAuth

class DashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashScreen()
        }
    }
}


@Composable
fun DashScreen() {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    Scaffold(
        timeText = {
            if (!lazyListState.isScrollInProgress) {
                TimeText()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(8.dp)
        ) {
            item {
                Text(
                    text = "Dashboard",
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                SmallButton(
                    label = "Measure Heart Rate",
                    onClick = {
                        val intent = Intent(context, HeartRateMonitorActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            item {
                SmallButton(
                    label = "Sleep Paralysis Detector",
                    onClick = {
                        val intent = Intent(context, AnomalyAlertActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            item {
                SmallButton(
                    label = "Coping Techniques",
                    onClick = {
                        val intent = Intent(context, VideoResourceActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            item {
                SignOutButton(
                    label = "Sign Out",
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SmallButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(150.dp)
            .height(40.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = label,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SignOutButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(50.dp)
            .height(30.dp)
            .fillMaxWidth(.5f),
        ) {
        Text(text = label, color = Color.Black)
    }
}
