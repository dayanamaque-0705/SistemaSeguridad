package bo.edu.ucb.segura.features.reports.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
    onNavigateBack: () -> Unit,
    onReturnToMap: () -> Unit
) {
    val backgroundColor = Color(0xFF07111E)
    val cardColor = Color(0xFF0F1B2E)
    val itemBgColor = Color(0xFF132238)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    val currentUser by AppStateRepository.currentUser.collectAsState()
    val reportsList by AppStateRepository.reportsState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BARRA SUPERIOR MATCH SCREENSHOT 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("←", color = textColor, fontSize = 20.sp)
                }

                Text(
                    text = "Mis reportes",
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

            // PERFIL DEL ESTUDIANTE EN LA PARTE SUPERIOR
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF1E3A8A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "MV"
                    Text(initials, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(
                        text = currentUser?.fullName ?: "Mariana Vargas",
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mi participación en la comunidad UCB",
                        color = grayText,
                        fontSize = 11.sp
                    )
                }
            }

            // TARJETA DE RESUMEN: "12 Reportes totales"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardColor
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("${reportsList.size}", color = textColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    Text("Reportes totales", color = grayText, fontSize = 12.sp)
                }
            }

            // TAB "Todos - 12"
            Surface(
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = primaryBlue
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Todos • ${reportsList.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Text("Vista de ejemplo • ${reportsList.size} de ${reportsList.size} reportes", color = grayText, fontSize = 11.sp)

            // LISTA DE TARJETAS DE REPORTES MATCH SCREENSHOT 3
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reportsList) { report ->
                    val (iconStr, iconBg) = when (report.type.uppercase()) {
                        "ROBO" -> "⚠️" to Color(0xFF7F1D1D)
                        "FALTA_LUZ" -> "💡" to Color(0xFF1E3A8A)
                        "SOSPECHOSO" -> "👤" to Color(0xFF581C87)
                        else -> "👥" to Color(0xFF7C2D12)
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = cardColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(itemBgColor, shape = RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(iconStr, fontSize = 18.sp)
                                    }
                                    Column {
                                        Text(report.type, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(report.location, color = grayText, fontSize = 11.sp)
                                    }
                                }
                                Text(">", color = grayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = report.description,
                                color = textColor,
                                fontSize = 12.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = itemBgColor
                                ) {
                                    Text(
                                        text = report.status,
                                        color = grayText,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "Ver seguimiento",
                                    color = Color(0xFF60A5FA),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { }
                                )
                            }
                        }
                    }
                }
            }

            // BOTÓN "Volver al mapa"
            OutlinedButton(
                onClick = onReturnToMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🗺️", fontSize = 16.sp)
                    Text("Volver al mapa", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Comunidad UCB • Versión 1.0.0",
                color = Color(0xFF475569),
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
            )
        }
    }
}
