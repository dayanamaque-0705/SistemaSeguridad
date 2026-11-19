package org.example.project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bo.edu.ucb.segura.App
import bo.edu.ucb.segura.core.data.AppStateRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 1. Solicitar permisos de ubicación en tiempo real GPS (FINE & COARSE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1001
            )
        }

        // 2. Solicitar permisos de Notificaciones Push POST_NOTIFICATIONS (Android 13+ / API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1002
                )
            }
        }

        // 3. Inicializar Firebase (FCM, Firestore y Realtime Database)
        FirebaseSyncManager.initializeAndSyncTables(applicationContext)

        // 4. Procesar Intent si la app fue abierta desde una Notificación SOS
        procesarIntentNotificacion(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarIntentNotificacion(intent)
    }

    private fun procesarIntentNotificacion(intent: Intent?) {
        if (intent == null) return
        val openSos = intent.getBooleanExtra("EXTRA_OPEN_SOS", false)
        if (openSos) {
            val lat = intent.getDoubleExtra("EXTRA_LAT", -17.3714)
            val lng = intent.getDoubleExtra("EXTRA_LNG", -66.1432)
            Log.d("MainActivity", "Abierto desde Notificación SOS. Coordenadas: ($lat, $lng)")
            try {
                AppStateRepository.triggerPanicAlert(latitude = lat, longitude = lng)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error procesando toque de notificación SOS", e)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
