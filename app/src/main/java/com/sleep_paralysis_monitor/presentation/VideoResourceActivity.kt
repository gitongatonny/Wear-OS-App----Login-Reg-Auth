package com.sleep_paralysis_monitor.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class VideoResourceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoResourceScreen()
        }
    }
}

@Composable
fun VideoResourceScreen() {
    val copingTechniques = listOf(
        CopingTechnique("Deep Breathing", "Practice deep breathing exercises to help calm your mind and body.", "[https://www.youtube.com/watch?v=ZZoy_sCLPZ0&pp=ygUcRGVhbGluZyB3aXRoIFNsZWVwIFBhcmFseXNpcw%3D%3D]"),
        CopingTechnique("Bedtime Routine", "Create a bedtime routine to establish a sense of security and relaxation before sleep.", "[https://www.youtube.com/watch?v=ZZoy_sCLPZ0&pp=ygUcRGVhbGluZyB3aXRoIFNsZWVwIFBhcmFseXNpcw%3D%3D]"),
        CopingTechnique("Visualization", "Visualize a safe and peaceful place to reduce anxiety during sleep paralysis episodes.", "[https://www.youtube.com/watch?v=ZZoy_sCLPZ0&pp=ygUcRGVhbGluZyB3aXRoIFNsZWVwIFBhcmFseXNpcw%3D%3D]"),
        CopingTechnique("Focus on Movement", "Focus on moving a small part of your body, such as your fingers or toes, to break the paralysis.", "[https://www.youtube.com/watch?v=ZZoy_sCLPZ0&pp=ygUcRGVhbGluZyB3aXRoIFNsZWVwIFBhcmFseXNpcw%3D%3D]"),
        CopingTechnique("Seek Help", "Seek professional help and guidance from a sleep specialist or therapist for personalized coping strategies.", "[https://www.youtube.com/watch?v=ZZoy_sCLPZ0&pp=ygUcRGVhbGluZyB3aXRoIFNsZWVwIFBhcmFseXNpcw%3D%3D]")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(copingTechniques) { technique ->
            CopingTechniqueItem(technique = technique)
        }
    }
}

@Composable
fun CopingTechniqueItem(technique: CopingTechnique) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = technique.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(text = technique.description)
        Text(text = technique.youtubeUrl)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVideoResourceScreen() {
    VideoResourceScreen()
}


data class CopingTechnique(
    val title: String,
    val description: String,
    val youtubeUrl: String
)