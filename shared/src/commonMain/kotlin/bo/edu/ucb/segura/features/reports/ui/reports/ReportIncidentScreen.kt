package bo.edu.ucb.segura.features.reports.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import bo.edu.ucb.segura.core.data.AppStateRepository
import bo.edu.ucb.segura.features.home.ui.CampusMapView

private fun formatCoord(value: Double): String {
    val factor = 10000.0
    val rounded = (value * factor).toInt() / factor
    return rounded.toString()
}

@Composable
fun ReportIncidentScreen(
    onNavigateBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var selectedType by remember { mutableStateOf("Robo") }
    var description by remember { mutableStateOf("") }
    var isPickerMapOpen by remember { mutableStateOf(false) }

    // Ubicación seleccionada en tiempo real (inicia en las coordenadas del Campus Tupuraya UCB)
    var selectedLat by remember { mutableStateOf(-17.3718) }
    var selectedLng by remember { mutableStateOf(-66.1435) }
    var locationName by remember { mutableStateOf("Campus Tupuraya (-17.3718, -66.1435)") }

    // Colores basados en el diseño oscuro institucional
    val backgroundColor = Color(0xFF0B132B)
    val cardColor = Color(0xFF1C2541)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barra superior del modal con título y botón cerrar (X)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reportar incidente",
                        color = textColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¿Qué está ocurriendo en el perímetro del campus?",
                        color = grayText,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF334155), shape = RoundedCornerShape(18.dp))
                ) {
                    Text("✕", color = textColor, fontSize = 16.sp)
                }
            }

            // Cuadrícula de opciones de incidentes (2x2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IncidentOptionCard(
                        title = "Robo / Asalto",
                        icon = "⚠️",
                        isSelected = selectedType == "Robo",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "Robo" }
                    )
                    IncidentOptionCard(
                        title = "Riña / Altercado",
                        icon = "👥",
                        isSelected = selectedType == "Riña",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "Riña" }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IncidentOptionCard(
                        title = "Falta de luz",
                        icon = "💡",
                        isSelected = selectedType == "Falta de luz",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "Falta de luz" }
                    )
                    IncidentOptionCard(
                        title = "Persona sospechosa",
                        icon = "👤",
                        isSelected = selectedType == "Persona sospechosa",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "Persona sospechosa" }
                    )
                }
            }

            // Campo de Descripción
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Descripción (opcional)", color = grayText, fontSize = 12.sp)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Detalla brevemente lo sucedido para alertar a la comunidad UCB...", color = Color(0xFF64748B), fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
            }

            // Tarjeta de Selección de Ubicación Dinámica en el Mapa
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isPickerMapOpen = true },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, primaryBlue)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1E3A8A), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📍", fontSize = 18.sp)
                        }
                        Column {
                            Text("Ubicación exacta del incidente", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(locationName, color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("👉 Toca aquí para abrir el mapa y presionar dónde ocurrió", color = grayText, fontSize = 10.sp)
                        }
                    }
                    Text("🗺️", fontSize = 20.sp)
                }
            }

            // Botón inferior de Enviar reporte
            Button(
                onClick = {
                    AppStateRepository.createReport(
                        type = selectedType,
                        description = description,
                        locationName = locationName,
                        latitude = selectedLat,
                        longitude = selectedLng
                    )
                    onSubmitSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("📋 Publicar e informar al mapa", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Tu reporte aparecerá inmediatamente en el mapa de seguridad UCB.",
                color = grayText,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // MODAL DE MAPA SELECCIONADOR DE UBICACIÓN DINÁMICO
        if (isPickerMapOpen) {
            Dialog(
                onDismissRequest = { isPickerMapOpen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Mapa interactivo con captura de toques (onMapTap) y marcador pin rojo en vivo (selectedPickerLocation)
                        CampusMapView(
                            modifier = Modifier.fillMaxSize(),
                            selectedPickerLocation = Pair(selectedLat, selectedLng),
                            onMapTap = { lat, lng ->
                                selectedLat = lat
                                selectedLng = lng
                                locationName = "Punto en mapa (${formatCoord(lat)}, ${formatCoord(lng)})"
                            },
                            onZoneClick = { zone ->
                                selectedLat = zone.centerLat
                                selectedLng = zone.centerLng
                                locationName = zone.name
                            },
                            onMarkerClick = { marker ->
                                selectedLat = marker.latitude
                                selectedLng = marker.longitude
                                locationName = "Cerca a: ${marker.title}"
                            }
                        )

                        // Cabecera flotante del mapa selector
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = cardColor.copy(alpha = 0.95f),
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("🎯 Toca en el mapa dónde ocurrió el hecho", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Presiona cualquier punto en el campus para mover el pin rojo", color = grayText, fontSize = 11.sp)
                                }
                                IconButton(onClick = { isPickerMapOpen = false }) {
                                    Text("✕", color = textColor, fontSize = 16.sp)
                                }
                            }
                        }

                        // Tarjeta inferior con confirmación de coordenadas fijadas
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(20.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = cardColor,
                            shadowElevation = 10.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Punto fijado con el pin:", color = grayText, fontSize = 11.sp)
                                Text("📍 $locationName", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Coordenadas: Lat ${formatCoord(selectedLat)}, Lng ${formatCoord(selectedLng)}", color = Color(0xFF60A5FA), fontSize = 11.sp)

                                Button(
                                    onClick = { isPickerMapOpen = false },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Confirmar esta ubicación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentOptionCard(
    title: String,
    icon: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF2563EB) else Color.Transparent
    val backgroundColor = Color(0xFF0F172A)

    Surface(
        modifier = modifier
            .height(75.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(icon, fontSize = 20.sp)
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 2,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
