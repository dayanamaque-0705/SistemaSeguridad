package bo.edu.ucb.segura.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val backgroundColor = Color(0xFF0F172A)
    val surfaceColor = Color(0xFF1E293B)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    val currentUser by AppStateRepository.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
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
                TextButton(onClick = onNavigateBack) {
                    Text("← Volver", color = textColor, fontSize = 16.sp)
                }
                Text("Perfil Institucional UCB", color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Cabecera del Usuario (Avatar e información)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = surfaceColor
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF1D4ED8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "UCB"
                        Text(initials, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = currentUser?.fullName ?: "Estudiante UCB",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser?.email ?: "estudiante@ucb.edu.bo",
                            color = Color(0xFF60A5FA),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Carné UCB: ${currentUser?.studentIdCard ?: "784210"}",
                            color = grayText,
                            fontSize = 11.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Comunidad Verificada UCB",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Tarjeta de participación
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = surfaceColor
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Aportes a la seguridad del campus", color = grayText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentUser?.participationCount ?: 1} Reportes aportados",
                        color = textColor,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Carrera: ${currentUser?.career ?: "Ingeniería de Sistemas"}",
                        color = grayText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Campus: ${currentUser?.campus ?: "Campus Tupuraya (Cochabamba)"}",
                        color = grayText,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de cerrar sesión
            Button(
                onClick = {
                    AppStateRepository.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Cerrar sesión", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
