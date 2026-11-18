package bo.edu.ucb.segura.features.reports.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

    // Ubicación seleccionada en tiempo real
    var selectedLat by remember { mutableStateOf(-17.3718) }
    var selectedLng by remember { mutableStateOf(-66.1435) }
    var locationName by remember { mutableStateOf("Mi ubicación actual") }

    // Colores basados en el diseño oscuro institucional
    val backgroundColor = Color(0xFF07111E).copy(alpha = 0.85f)
    val cardColor = Color(0xFF0F1B2E)
    val inputBgColor = Color(0xFF132238)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // DRAG HANDLE ARRIBA
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF334155), shape = RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            // BARRA SUPERIOR CON TÍTULO Y CERRAR (X)
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
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¿Qué está ocurriendo?",
                        color = grayText,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF1E293B), shape = CircleShape)
                ) {
                    Text("✕", color = textColor, fontSize = 16.sp)
                }
            }

            // CUADRÍCULA 2X2 DE TIPOS DE INCIDENTE CON BADGE AZUL '✓'
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IncidentOptionCard(
                        title = "Robo",
                        icon = "⚠️",
                        isSelected = selectedType == "Robo",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedType = "Robo" }
                    )
                    IncidentOptionCard(
                        title = "Riña",
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

            // CAMPO DE DESCRIPCIÓN
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Descripción (opcional)", color = grayText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Agrega un detalle breve que ayude a la comunidad...", color = Color(0xFF64748B), fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
            }

            // TARJETA DE MI UBICACIÓN ACTUAL / ELECCIÓN EN MAPA
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isPickerMapOpen = true },
                shape = RoundedCornerShape(14.dp),
                color = inputBgColor
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
                                .size(38.dp)
                                .background(Color(0xFF1E3A8A), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📍", fontSize = 18.sp)
                        }
                        Column {
                            Text("Mi ubicación actual", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(locationName, color = grayText, fontSize = 11.sp)
                        }
                    }
                    Text(">", color = grayText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // BOTÓN DE ENVIAR REPORTE
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
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📋", fontSize = 18.sp)
                    Text("Enviar reporte", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Los reportes son visibles para la comunidad UCB.",
                color = grayText,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // MODAL DE MAPA SELECCIONADOR DE UBICACIÓN
        if (isPickerMapOpen) {
            Dialog(
                onDismissRequest = { isPickerMapOpen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF07111E)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
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

                        // Cabecera flotante
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
                                    Text("🎯 Toca el mapa para fijar la ubicación", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Mueve el pin rojo para seleccionar el lugar del incidente", color = grayText, fontSize = 11.sp)
                                }
                                IconButton(onClick = { isPickerMapOpen = false }) {
                                    Text("✕", color = textColor, fontSize = 16.sp)
                                }
                            }
                        }

                        // Tarjeta inferior de confirmación
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
                                Text("Punto fijado:", color = grayText, fontSize = 11.sp)
                                Text("📍 $locationName", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                Button(
                                    onClick = { isPickerMapOpen = false },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Confirmar ubicación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
    val backgroundColor = Color(0xFF132238)

    Surface(
        modifier = modifier
            .height(75.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF1E293B), shape = RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 18.sp)
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 2,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }

            // BADGE AZUL '✓' EN LA ESQUINA SUPERIOR DERECHA
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(18.dp)
                        .background(Color(0xFF2563EB), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
