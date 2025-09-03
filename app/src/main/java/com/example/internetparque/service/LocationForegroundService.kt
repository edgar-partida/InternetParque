package com.example.internetparque.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.internetparque.util.Constants.LOCATION_SERVICE_LOG
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationForegroundService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager

    override fun onCreate() {
        super.onCreate()
        Log.d(LOCATION_SERVICE_LOG, "onCreate: Servicio de ubicación creado")
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Servicio de ubicación")
            .setContentText("Guardando ubicación cada minuto")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
        startForeground(1, notification)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOCATION_SERVICE_LOG, "onStartCommand: Servicio iniciado")
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60_000L, // 1 minuto
                0f,
                this,
                Looper.getMainLooper()
            )
            Log.d(LOCATION_SERVICE_LOG, "Solicitadas actualizaciones de ubicación")
        } catch (e: SecurityException) {
            Log.e(LOCATION_SERVICE_LOG, "Error de permisos al solicitar ubicación: ${e.message}")
            stopSelf()
        }
        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = sdf.format(Date(now))

        val json = JSONObject().apply {
            put("timestamp", now)
            put("datetime", formattedDate)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
        }

        val file = File(getExternalFilesDir(null), "locations.txt")
        Log.d(LOCATION_SERVICE_LOG, "onLocationChanged: Guardando ubicación: $json en ${file.absolutePath}")
        try {
            FileWriter(file, true).use { it.write(json.toString() + "\n") }
            Log.d(LOCATION_SERVICE_LOG, "Ubicación guardada correctamente")
        } catch (e: Exception) {
            Log.e(LOCATION_SERVICE_LOG, "Error al guardar ubicación: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(
            LOCATION_SERVICE_LOG,
            "onDestroy: Servicio destruido, removiendo actualizaciones de ubicación"
        )
        try {
            locationManager.removeUpdates(this)
        } catch (e: Exception) {
            Log.e(LOCATION_SERVICE_LOG, "Error al remover actualizaciones: ${e.message}")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Ubicación",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d(LOCATION_SERVICE_LOG, "Canal de notificación creado")
        }
    }
}