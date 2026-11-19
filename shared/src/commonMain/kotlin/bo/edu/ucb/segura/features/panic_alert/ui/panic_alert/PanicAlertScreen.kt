package bo.edu.ucb.segura.features.panic_alert.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.edu.ucb.segura.core.ui.UcbLogo
import kotlin.math.roundToInt

@Composable
fun PanicAlertScreen(
    onCancelAlert: () -> Unit
) {
    val sosRedBackground = Color(0xFF991B1B)
    val cardColor = Color(0xFF0F1B2E)
    val textColor = Color.White
    val grayText = Color(0xFFCBD5E1)

    // Estado del gesto de deslizar para cancelar
    var offsetX by remember { mutableStateOf(0f) }
    val maxDragPx = with(LocalDensity.current) { 220.dp.toPx() }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(sosRedBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BARRA SUPERIOR MATCH SCREENSHOT 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UcbLogo(modifier = Modifier.size(24.dp))
                    Text(
                        text = "UCB Segura",
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444)
                ) {
                    Text(
                        text = "🔴 ALERTA ACTIVA",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // CÍRCULO CON ICONO DE EMERGENCIA Y CHECK '✓'
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color(0xFFB91C1C), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFDC2626), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✓", color = Color(0xFFDC2626), fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Alerta enviada\na la comunidad",
                color = textColor,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                lineHeight = 34.sp
            )

            Text(
                text = "Miembros cercanos fueron notificados y pueden acompañarte.",
                color = grayText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )

            // TARJETA DE UBICACIÓN TEMPORAL ACTIVA
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardColor
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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
                                .background(Color(0xFF1E293B), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📍", fontSize = 18.sp)
                        }
                        Column {
                            Text("Ubicación temporal activa", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Se comparte mientras la alerta esté activa", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    Text("📶", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            // COMPONENTE DESLIZAR PARA CANCELAR ALERTA (SLIDER INTERACTIVO DE DESLIZAMIENTO)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                color = cardColor
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Texto centrado en la barra
                    Text(
                        text = "Deslizar para cancelar alerta",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Botón circular blanco deslizable
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                            .padding(6.dp)
                            .size(52.dp)
                            .background(Color.White, shape = CircleShape)
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state = rememberDraggableState { delta ->
                                    val newOffset = (offsetX + delta).coerceIn(0f, maxDragPx)
                                    offsetX = newOffset
                                    if (newOffset >= maxDragPx * 0.85f) {
                                        onCancelAlert()
                                    }
                                },
                                onDragStopped = {
                                    if (offsetX < maxDragPx * 0.85f) {
                                        offsetX = 0f
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("➔", color = Color(0xFF0F1B2E), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mantén la calma y dirígete a un lugar concurrido",
                color = Color(0xFFFECACA),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
