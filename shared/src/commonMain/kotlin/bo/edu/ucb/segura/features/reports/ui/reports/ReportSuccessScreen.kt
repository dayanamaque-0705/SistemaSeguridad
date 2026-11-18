package bo.edu.ucb.segura.features.reports.ui.reports

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
import androidx.compose.foundation.BorderStroke

@Composable
fun ReportSuccessScreen(
    onViewMoreReports: () -> Unit,
    onReturnToMap: () -> Unit
) {
    val backgroundColor = Color(0xFF0B132B)
    val cardColor = Color(0xFF1C2541)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = cardColor
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Círculo indicador de éxito
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(primaryBlue, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 32.sp)
                }

                Text(
                    text = "¡Gracias por reportar!",
                    color = textColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp
                )

                Text(
                    text = "Tu reporte ha sido enviado con éxito. Compartir información oportuna ayuda a proteger a toda la comunidad universitaria en el campus Tupuraya.",
                    color = grayText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
                Button(
                    onClick = onViewMoreReports,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Ver mis reportes", color = Color.White, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = onReturnToMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text("Volver al mapa", fontSize = 15.sp)
                }
            }
        }
    }
}