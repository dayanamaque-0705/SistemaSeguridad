package bo.edu.ucb.segura.features.reports.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportSuccessScreen(
    onViewMoreReports: () -> Unit,
    onReturnToMap: () -> Unit
) {
    val backgroundColor = Color(0xFF07111E)
    val cardColor = Color(0xFF0F1B2E)
    val itemBgColor = Color(0xFF132238)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BARRA SUPERIOR MATCH SCREENSHOT 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onReturnToMap,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("←", color = textColor, fontSize = 20.sp)
                }

                Text(
                    text = "Reporte enviado",
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "UCB",
                    color = grayText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CÍRCULO AZUL CON CHECK '✓'
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(primaryBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Gracias por reportar",
                color = textColor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Text(
                text = "Tu reporte fue enviado. Compartir lo que ocurre ayuda a cuidar a la comunidad.",
                color = grayText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
            )

            // TARJETA DE RESUMEN DEL REPORTE MATCH SCREENSHOT 3
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = cardColor
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(itemBgColor, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚠️", fontSize = 18.sp)
                        }
                        Column {
                            Text("Reporte comunitario", color = grayText, fontSize = 11.sp)
                            Text("Robo", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color(0xFF1E293B))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(itemBgColor, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📍", fontSize = 18.sp)
                        }
                        Column {
                            Text("Ubicación", color = grayText, fontSize = 11.sp)
                            Text("Mi ubicación actual", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(color = Color(0xFF1E293B))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(itemBgColor, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👥", fontSize = 18.sp)
                        }
                        Column {
                            Text("Visible para", color = grayText, fontSize = 11.sp)
                            Text("Comunidad UCB", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ⓘ", color = grayText, fontSize = 12.sp)
                Text(
                    text = "Este reporte comunitario no es una alerta de emergencia ni confirma asistencia en camino.",
                    color = grayText,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN VER MIS REPORTES
            Button(
                onClick = onViewMoreReports,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📋", fontSize = 18.sp)
                    Text("Ver mis reportes", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÓN VOLVER AL MAPA
            OutlinedButton(
                onClick = onReturnToMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🗺️", fontSize = 18.sp)
                    Text("Volver al mapa", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comunidad UCB • Versión 1.0.0",
                color = Color(0xFF475569),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
