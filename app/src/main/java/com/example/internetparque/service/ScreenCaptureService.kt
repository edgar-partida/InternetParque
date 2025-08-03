package com.example.internetparque.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.internetparque.R
import com.example.internetparque.util.Constants.SCREEN_CAPTURE_SERVICE_LOG

class ScreenCaptureService : Service() {

    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler
    private lateinit var toastRunnable: Runnable

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Starting onCreate")
        super.onCreate()
        startForegroundService()
        startRepeatingToast()
    }

    private fun startForegroundService() {
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Starting startForegroundService method")
        val channelId = "my_service_channel"
        val channelName = "Background Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chan)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servicio activo")
            .setContentText("La app está ejecutándose en segundo plano")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Starting foreground service")
        startForeground(1, notification)
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Service foreground started")
    }

    private fun startRepeatingToast() {
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Starting Toast repeating")
        handlerThread = HandlerThread("ToastThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        toastRunnable = object : Runnable {
            override fun run() {
                Handler(mainLooper).post {
                    Log.d(SCREEN_CAPTURE_SERVICE_LOG, "Ejecutando el toast")
                    Toast.makeText(applicationContext, "Servicio activo", Toast.LENGTH_SHORT).show()
                }
                handler.postDelayed(this, 10_000) // 10 segundos
            }
        }
        handler.post(toastRunnable)
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Finished Toast")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(SCREEN_CAPTURE_SERVICE_LOG,"Service destroyed")
        handler.removeCallbacks(toastRunnable)
        handlerThread.quitSafely()
        super.onDestroy()
    }
}
