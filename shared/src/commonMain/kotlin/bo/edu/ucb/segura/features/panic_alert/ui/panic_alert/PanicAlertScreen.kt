package bo.edu.ucb.segura.features.panic_alert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.edu.ucb.segura.features.panic_alert.application.TriggerPanicAlertUseCase

@Composable
fun PanicAlertScreen(
    onCancelAlert: () -> Unit
) {
    val triggerAlert = remember { TriggerPanicAlertUseCase() }

    // Estado para verificar si el rastreo temporal está activo
    var alertActive by remember { mutableStateOf(true) }

    val sosRedBackground = Color(0xFF991B1B) // Fondo rojo oscuro de alerta activa
    val surfaceRed = Color(0xFFB91C1C)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(sosRedBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "🚨 ALERTA ACTIVA",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 22.sp
            )

            // Círculo indicador de emergencia
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = surfaceRed
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("⚠️", fontSize = 40.sp)
                }
            }

            Text(
                text = "Alerta enviada a la comunidad UCB",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "El rastreo de ubicación GPS está activo de forma temporal. Contactos y compañeros han sido notificados en las inmediaciones del campus Tupuraya.",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para cancelar / desactivar la alerta de pánico
            Button(
                onClick = {
                    alertActive = false
                    onCancelAlert()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Deslizar para cancelar alerta", color = Color.Red, fontSize = 15.sp)
            }
        }
    }
}