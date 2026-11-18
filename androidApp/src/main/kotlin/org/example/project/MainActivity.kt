package org.example.project // O puedes cambiarlo a bo.edu.ucb.segura si moviste el archivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import bo.edu.ucb.segura.App // <--- Esta importación es la clave para conectar con tu nuevo App.kt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App() // Llamará a tu enrutador principal de UCB Segura
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}