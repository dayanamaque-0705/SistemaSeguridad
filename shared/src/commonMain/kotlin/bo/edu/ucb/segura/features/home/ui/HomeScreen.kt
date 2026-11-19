package bo.edu.ucb.segura.features.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import bo.edu.ucb.segura.core.ui.UcbLogo
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker
import bo.edu.ucb.segura.features.home.model.RiskLevel

@Composable
fun HomeScreen(
    isDarkMode: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onNavigateToReport: () -> Unit,
    onTriggerPanicAlert: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenMyReports: () -> Unit
) {
    // Paleta de colores adaptable Claro / Oscuro
    val mapBackground = if (isDarkMode) Color(0xFF07111E) else Color(0xFFF1F5F9)
    val cardColor = if (isDarkMode) Color(0xFF0F1B2E) else Color(0xFFFFFFFF)
    val primaryBlue = Color(0xFF2563EB)
    val sosRed = Color(0xFFDC2626)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val grayText = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Estados observados en tiempo real desde AppStateRepository
    val dangerZones by AppStateRepository.dangerZonesState.collectAsState()
    val incidentMarkers by AppStateRepository.incidentMarkersState.collectAsState()
    val currentUser by AppStateRepository.currentUser.collectAsState()
    val latestPanicLocation by AppStateRepository.latestPanicLocation.collectAsState()

    // Estado del filtro de riesgo seleccionado (null / "TODOS")
    var selectedRiskFilter by remember { mutableStateOf<String?>("TODOS") }

    // Estado de re-enfoque de ubicación en el mapa
    var centerOnLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(latestPanicLocation) {
        latestPanicLocation?.let { loc ->
            centerOnLocation = loc
        }
    }

    // Estado de la Zona o Incidente seleccionado para mostrar detalle
    var selectedZone by remember { mutableStateOf<DangerZone?>(null) }
    var selectedIncident by remember { mutableStateOf<IncidentMarker?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mapBackground)
    ) {
        // --- 1. CAPA DE MAPA INTERACTIVO CON ZONAS DE RIESGO, INCIDENTES Y PUNTO AZUL "AQUÍ ESTOY" ---
        CampusMapView(
            modifier = Modifier.fillMaxSize(),
            dangerZones = dangerZones,
            incidentMarkers = incidentMarkers,
            selectedRiskFilter = selectedRiskFilter,
            showUserLocation = true,
            centerOnLocation = centerOnLocation,
            onZoneClick = { zone ->
                selectedZone = zone
                selectedIncident = null
            },
            onMarkerClick = { incident ->
                selectedIncident = incident
                selectedZone = null
            }
        )

        // --- 2. BARRA SUPERIOR (HEADER) MATCH SCREENSHOT 2 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardColor.copy(alpha = 0.92f),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF1E3A8A), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        UcbLogo(modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "UCB Segura",
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // BOTÓN DE CAMBIO DE TEMA CLARO / OSCURO
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(42.dp)
                        .background(cardColor.copy(alpha = 0.92f), shape = CircleShape)
                ) {
                    Text(if (isDarkMode) "☀️" else "🌙", fontSize = 18.sp)
                }

                // BOTÓN DE PERFIL "MV"
                Button(
                    onClick = onOpenProfile,
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "MV"
                    Text(initials, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 3. TARJETA FLOTANTE "Riesgo esta noche" Y "📍 Aquí estoy" ---
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 104.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardColor.copy(alpha = 0.95f),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Riesgo esta noche",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RiskIndicator(color = Color(0xFFEAB308), label = "Bajo")
                        RiskIndicator(color = Color(0xFFF97316), label = "Medio")
                        RiskIndicator(color = Color(0xFFEF4444), label = "Alto")
                    }
                }
            }

            // BOTÓN DE ENFOQUE EN VIVO DE GPS "📍 Aquí estoy"
            Surface(
                modifier = Modifier.clickable {
                    centerOnLocation = Pair(-17.3714, -66.1432)
                },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E3A8A),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF60A5FA), shape = CircleShape)
                    )
                    Text(
                        text = "📍 Aquí estoy (Centrar GPS)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- 4. DETALLE DE ZONA O INCIDENTE SELECCIONADO (TARJETA POPUP) ---
        AnimatedVisibility(
            visible = selectedZone != null || selectedIncident != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = cardColor,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val badgeColor = when (selectedZone?.riskLevel) {
                                RiskLevel.ALTO -> Color(0xFFEF4444)
                                RiskLevel.MEDIO -> Color(0xFFF97316)
                                RiskLevel.BAJO -> Color(0xFFEAB308)
                                RiskLevel.SEGURA -> Color(0xFF22C55E)
                                else -> Color(0xFF2563EB)
                            }
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(badgeColor, shape = CircleShape)
                            )
                            Text(
                                text = selectedZone?.name ?: selectedIncident?.title ?: "",
                                color = textColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = {
                                selectedZone = null
                                selectedIncident = null
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("✕", color = grayText, fontSize = 14.sp)
                        }
                    }

                    selectedZone?.let { zone ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (zone.riskLevel) {
                                RiskLevel.ALTO -> Color(0xFF7F1D1D)
                                RiskLevel.MEDIO -> Color(0xFF7C2D12)
                                RiskLevel.BAJO -> Color(0xFF713F12)
                                RiskLevel.SEGURA -> Color(0xFF14532D)
                            }
                        ) {
                            Text(
                                text = "Nivel: ${zone.riskLevel.label} (${zone.radiusMeters.toInt()}m radio)",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = zone.description,
                            color = grayText,
                            fontSize = 13.sp
                        )
                    }

                    selectedIncident?.let { incident ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E3A8A)
                            ) {
                                Text(
                                    text = incident.type,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = incident.timestamp,
                                color = grayText,
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        Text(
                            text = incident.description,
                            color = textColor,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                AppStateRepository.triggerPanicAlert(
                                    latitude = selectedZone?.centerLat ?: selectedIncident?.latitude ?: -17.3714,
                                    longitude = selectedZone?.centerLng ?: selectedIncident?.longitude ?: -66.1432
                                )
                                onTriggerPanicAlert()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = sosRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🚨 Activar SOS en esta zona", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- 5. BARRA INFERIOR FLOTANTE (BOTONES DE ACCIÓN: SOS, REPORTAR, MIS REPORTES) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenMyReports,
                    modifier = Modifier
                        .size(54.dp)
                        .background(cardColor, shape = RoundedCornerShape(18.dp))
                ) {
                    Text("📍", fontSize = 22.sp)
                }

                Button(
                    onClick = {
                        AppStateRepository.triggerPanicAlert()
                        onTriggerPanicAlert()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = sosRed),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚨", fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Necesito ayuda", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Enviar alerta ahora", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                        }
                    }
                }

                Button(
                    onClick = onNavigateToReport,
                    modifier = Modifier.size(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📋", fontSize = 18.sp)
                        Text("Reportar", color = grayText, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RiskIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, shape = CircleShape)
        )
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
    }
}
