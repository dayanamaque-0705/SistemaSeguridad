package bo.edu.ucb.segura.features.reports.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.edu.ucb.segura.core.data.AppStateRepository

@Composable
fun MyReportsScreen(
    onNavigateBack: () -> Unit
) {
    val backgroundColor = Color(0xFF0B132B)
    val cardColor = Color(0xFF1C2541)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    // Lista en tiempo real de reportes desde el AppStateRepository
    val reportsList by AppStateRepository.reportsState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Barra superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF334155), shape = RoundedCornerShape(18.dp))
                ) {
                    Text("←", color = textColor, fontSize = 18.sp)
                }
                Text("Mis Reportes & Historial", color = textColor, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(36.dp))
            }

            // Tarjeta de resumen de participación del estudiante UCB
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardColor
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total reportes aportados a la UCB", color = grayText, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${reportsList.size}", color = textColor, style = MaterialTheme.typography.headlineMedium, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(primaryBlue.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 22.sp)
                    }
                }
            }

            Text("Historial de incidentes notificados", color = textColor, style = MaterialTheme.typography.titleSmall, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)

            // Lista dinámica de reportes
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(reportsList) { report ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = cardColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = report.type,
                                    color = textColor,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (report.status.lowercase()) {
                                        "resuelto" -> Color(0xFF166534)
                                        "activo" -> Color(0xFF1E3A8A)
                                        else -> Color(0xFF854D0E)
                                    }
                                ) {
                                    Text(
                                        text = report.status,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(report.description, color = textColor, fontSize = 13.sp)
                            Text("📍 Ubicación: ${report.location}", color = grayText, fontSize = 12.sp)
                            Text("🕒 ${report.timestamp}", color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
