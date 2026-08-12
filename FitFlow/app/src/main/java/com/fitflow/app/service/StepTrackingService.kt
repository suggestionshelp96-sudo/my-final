package com.fitflow.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fitflow.app.R

class StepTrackingService : Service(), SensorEventListener {
    private lateinit var sensors: SensorManager
    override fun onCreate() { super.onCreate(); createChannel(); sensors = getSystemService(SENSOR_SERVICE) as SensorManager; sensors.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let { sensors.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }; startForeground(7, NotificationCompat.Builder(this, "steps").setSmallIcon(R.mipmap.ic_launcher).setContentTitle("FitFlow is tracking steps").setContentText("Your daily activity stays private on this device.").setOngoing(true).build()) }
    override fun onSensorChanged(event: SensorEvent) { getSharedPreferences("steps", MODE_PRIVATE).edit().putInt("sensor_total", event.values[0].toInt()).apply() }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { sensors.unregisterListener(this); super.onDestroy() }
    private fun createChannel() { (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel("steps", "Step tracking", NotificationManager.IMPORTANCE_LOW)) }
}
