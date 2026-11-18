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
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker
import bo.edu.ucb.segura.features.home.model.RiskLevel

@Composable
fun HomeScreen(
    onNavigateToReport: () -> Unit,
    onTriggerPanicAlert: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenMyReports: () -> Unit
) {
    val mapBackground = Color(0xFF0B132B)
    val cardColor = Color(0xFF1E293B)
    val primaryBlue = Color(0xFF2563EB)
    val sosRed = Color(0xFFDC2626)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    // Estados observados en tiempo real desde AppStateRepository
    val dangerZones by AppStateRepository.dangerZonesState.collectAsState()
    val incidentMarkers by AppStateRepository.incidentMarkersState.collectAsState()
    val currentUser by AppStateRepository.currentUser.collectAsState()

    // Estado del filtro de riesgo seleccionado (null / "TODOS")
    var selectedRiskFilter by remember { mutableStateOf<String?>("TODOS") }

    // Estado de re-enfoque de ubicación en el mapa
    var centerOnLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

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

        // --- 2. BARRA SUPERIOR (HEADER) CON LOGO Y PERFIL DEL ESTUDIANTE ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
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
                            .background(primaryBlue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛡️", fontSize = 16.sp)
                    }
                    Column {
                        Text(
                            text = "UCB Segura",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Campus Tupuraya",
                            color = grayText,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onOpenProfile,
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    val initials = currentUser?.fullName?.take(2)?.uppercase() ?: "UCB"
                    Text(initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 3. BARRA DE FILTROS DE RIESGO Y BOTÓN "AQUÍ ESTOY" ---
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 112.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardColor.copy(alpha = 0.95f),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🗺️ Mapa de Riesgo en Vivo (${incidentMarkers.size} reportes)",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChipItem(
                            label = "Todos",
                            color = primaryBlue,
                            isSelected = selectedRiskFilter == "TODOS" || selectedRiskFilter == null,
                            onClick = { selectedRiskFilter = "TODOS" }
                        )
                        FilterChipItem(
                            label = "Alto",
                            color = Color(0xFFEF4444),
                            isSelected = selectedRiskFilter == "ALTO",
                            onClick = { selectedRiskFilter = "ALTO" }
                        )
                        FilterChipItem(
                            label = "Medio",
                            color = Color(0xFFF97316),
                            isSelected = selectedRiskFilter == "MEDIO",
                            onClick = { selectedRiskFilter = "MEDIO" }
                        )
                        FilterChipItem(
                            label = "Bajo",
                            color = Color(0xFFEAB308),
                            isSelected = selectedRiskFilter == "BAJO",
                            onClick = { selectedRiskFilter = "BAJO" }
                        )
                        FilterChipItem(
                            label = "Seguro",
                            color = Color(0xFF22C55E),
                            isSelected = selectedRiskFilter == "SEGURA",
                            onClick = { selectedRiskFilter = "SEGURA" }
                        )
                    }
                }
            }

            // Botón flotante "📍 Aquí estoy" para centrar el GPS
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
fun FilterChipItem(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color else Color(0xFF0F172A),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(if (isSelected) Color.White else color, shape = CircleShape)
            )
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
