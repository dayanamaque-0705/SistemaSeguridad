package bo.edu.ucb.segura.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.edu.ucb.segura.core.data.AppStateRepository

@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    onOpenMyReports: () -> Unit,
    onLogout: () -> Unit
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
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("←", color = textColor, fontSize = 20.sp)
                }

                Text(
                    text = "Mi perfil",
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

            Spacer(modifier = Modifier.height(20.dp))

            // AVATAR E INFORMACIÓN PRINCIPAL
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFF1E3A8A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "MV"
                    Text(initials, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = currentUser?.fullName ?: "Mariana Vargas",
                    color = textColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = currentUser?.email ?: "mariana.vargas@ucb.edu.bo",
                    color = Color(0xFF60A5FA),
                    fontSize = 13.sp
                )

                Text(
                    text = "Estudiante • Comunidad UCB",
                    color = grayText,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("✏️ Editar perfil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN MI PARTICIPACIÓN
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mi participación", color = grayText, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = cardColor
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("${reportsList.size}", color = textColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        Text("Reportes totales aportados", color = grayText, fontSize = 12.sp)

                        Divider(color = Color(0xFF1E293B))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenMyReports() },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("📋", fontSize = 16.sp)
                                Text("Mis reportes", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(">", color = grayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECCIÓN MI CUENTA
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mi cuenta", color = grayText, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = cardColor
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ProfileSettingItem(icon = "👤", title = "Cuenta y seguridad", subtitle = "Correo y acceso")
                        Divider(color = Color(0xFF1E293B))
                        ProfileSettingItem(icon = "🛡️", title = "Privacidad", subtitle = "Controla la información que compartes")
                        Divider(color = Color(0xFF1E293B))
                        ProfileSettingItem(icon = "🔔", title = "Notificaciones", subtitle = "Alertas y reportes")
                        Divider(color = Color(0xFF1E293B))
                        ProfileSettingItem(icon = "❓", title = "Ayuda y soporte", subtitle = "Estamos para ayudarte")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN DE CERRAR SESIÓN
            OutlinedButton(
                onClick = {
                    AppStateRepository.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🚪", fontSize = 16.sp)
                    Text("Cerrar sesión", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Comunidad UCB • Versión 1.0.0",
                color = Color(0xFF475569),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun ProfileSettingItem(
    icon: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF132238), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Column {
                Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }
        Text(">", color = Color(0xFF94A3B8), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
