package com.sleep_paralysis_monitor.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class HeartRateMonitorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeartRateMonitorScreen()
        }
    }
}

@Composable
fun HeartRateMonitorScreen() {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    if (heartRateSensor == null) {
        Text("Heart rate sensor not found", modifier = Modifier.padding(16.dp))
        return
    }

    var heartRate by remember { mutableFloatStateOf(-1f) }
    var isMonitoring by remember { mutableStateOf(false) }

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
                heartRate = event.values[0]
                isMonitoring = false
                sensorManager.unregisterListener(this)

                // Store BPM in Firestore
                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()

                auth.currentUser?.let { user ->
                    val data = hashMapOf(
                        "heartRate" to heartRate,
                        "timestamp" to Calendar.getInstance().time
                    )
                    firestore.collection("users")
                        .document(user.uid)
                        .collection("heartRateMeasurements")
                        .add(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Heart rate saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Failed to save heart rate: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }


    LaunchedEffect(heartRateSensor, sensorManager) {
        isMonitoring = true
        sensorManager.registerListener(sensorEventListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }



    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isMonitoring) {
                CircularProgressIndicator()
                Text(
                    text = "Measuring Heart Rate...",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else if (heartRate != -1f) {
                Text(
                    text = "Heart Rate: $heartRate bpm",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

fun onDispose(function: () -> Unit) {
    function.invoke()
}

