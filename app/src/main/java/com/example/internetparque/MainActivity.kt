package com.example.internetparque

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.internetparque.service.LocationForegroundService
import com.example.internetparque.service.ScreenCaptureService
import com.example.internetparque.ui.theme.InternetParqueTheme
import com.example.internetparque.util.Constants.MAIN_ACTIVITY_LOG


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(MAIN_ACTIVITY_LOG,"Starting the application")
        enableEdgeToEdge()
        setContent {
            InternetParqueTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        Log.d(MAIN_ACTIVITY_LOG,"Starting foreground service")
        val serviceIntent = Intent(this, ScreenCaptureService::class.java)

        Log.d(MAIN_ACTIVITY_LOG, "Starting Foreground Service")
        startForegroundService(serviceIntent)
        Log.d(MAIN_ACTIVITY_LOG, "Finished Foreground Service")
        // Starting location service
        Log.d(MAIN_ACTIVITY_LOG,"Starting location service")
        val locationServiceIntent = Intent(this, LocationForegroundService::class.java)
        startForegroundService(locationServiceIntent)
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )
        requestPermissions(permissions.toTypedArray(), 100)
        Log.d(MAIN_ACTIVITY_LOG,"Location service started")
        Log.d(MAIN_ACTIVITY_LOG,"Content rendering completed!")
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InternetParqueTheme {
        Greeting("Android")
    }
}