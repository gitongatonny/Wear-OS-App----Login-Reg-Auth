package com.sleep_paralysis_monitor.presentation

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.text.InputType
import android.widget.EditText
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sleep_paralysis_monitor.R
import java.util.*


class AnomalyAlertActivity : Service() {

    private val notificationChannelId = "anomaly_alert_service_channel"
    private val NOTIFICATION_ID = 1
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var monitoringDurationHours = 6

    private lateinit var sensorManager: SensorManager
    private lateinit var heartRateSensor: Sensor
    private var baselineHeartRate = 0
    private var calibrationMode = true
    private val CALIBRATION_WINDOW = 60 * 5
    private var calibrationStartTime = 0L
    private val recentRRIntervals = mutableListOf<Int>()
    private var previousHeartRate = 0
    private var previousHeartRateTimestamp = 0L

    private var hrv: Float = 0.0f
    private var baselineHrv: Float = 0.0f
    private var baselineRrInterval: Float = 0.0f
    private var heartRate = 0




    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Monitoring for anomalies..."))
        showDurationInputDialog()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!
        calibrationStartTime = System.currentTimeMillis()
    }



    override fun onDestroy() {
        sensorManager.unregisterListener(heartRateListener)
        super.onDestroy()
    }

    private fun showDurationInputDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        AlertDialog.Builder(this)
            .setTitle("Enter Monitoring Duration (Hours)")
            .setView(input)
            .setPositiveButton("OK") { dialog, _ ->
                val durationInput = input.text.toString()
                val durationHours = durationInput.toIntOrNull() ?: return@setPositiveButton

                if (durationHours in 1..12) {
                    monitoringDurationHours = durationHours
                    startMonitoring(durationHours)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Invalid input. Please enter a number between 1 and 12.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun startMonitoring(durationHours: Int) {
        val monitoringDurationMillis = durationHours * 60 * 60 * 1000L
        val monitoringEndTime = System.currentTimeMillis() + monitoringDurationMillis


        // Monitoring
        while (System.currentTimeMillis() < monitoringEndTime) {

            Log.d("AnomalyAlertActivity", "Monitoring: ${System.currentTimeMillis()}")

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        stopSelf() 
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager.registerListener(heartRateListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Monitoring duration
        val durationHours = intent?.getIntExtra("durationHours", 6) ?: 6
        startMonitoring(durationHours)

        return START_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Anomaly Alert Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for sleep paralysis alerts"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        val intent = Intent(this, DashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Sleep Paralysis Monitor")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private val heartRateListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            heartRate = event.values[0].toInt()
            val heartRate = event.values[0].toInt()
            val currentTime = System.currentTimeMillis()

            if (calibrationMode) {
                // Update baselineHeartRate using averaging
                if (currentTime - calibrationStartTime > CALIBRATION_WINDOW) {
                    calibrationMode = false
                }
            } else {
                val rrInterval = (currentTime - previousHeartRateTimestamp).toInt()
                calculateSimpleHrv(rrInterval)
                previousHeartRate = heartRate
                previousHeartRateTimestamp = currentTime

                // Anomaly Detection
                if (heartRate > baselineHeartRate + 25 || hrv < baselineHrv - 10) {
                    triggerAnomalyAlert()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }



    private fun calculateSimpleHrv(rrInterval: Int) {
        recentRRIntervals.add(rrInterval)
        if (recentRRIntervals.size > 5) {
            recentRRIntervals.removeAt(0)
        }

        if (rrInterval < baselineRrInterval - 20) {
        }
    }

    private fun triggerAnomalyAlert() {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return
        val anomalyRecord = hashMapOf(
            "heartRate" to heartRate,
            "hrv" to hrv,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("users").document(currentUserId)
            .update("anomalyData", FieldValue.arrayUnion(anomalyRecord))
            .addOnSuccessListener { /* success listener */ }
            .addOnFailureListener { /* failure listener */ }

        val notification = createNotification("Anomaly Detected!")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(10000)
    }


}
