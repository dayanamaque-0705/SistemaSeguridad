package bo.edu.ucb.segura.features.home.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File

// Fuente de mapas pública de OpenStreetMap Francia (Sin bloqueos 403, 100% gratuita y sin API Key)
private val OsmFrTileSource = XYTileSource(
    "OSM-France",
    0,
    19,
    256,
    ".png",
    arrayOf(
        "https://a.tile.openstreetmap.fr/osmfr/",
        "https://b.tile.openstreetmap.fr/osmfr/",
        "https://c.tile.openstreetmap.fr/osmfr/"
    ),
    "© OpenStreetMap contributors, OSM France"
)

@Composable
actual fun CampusMapView(
    modifier: Modifier,
    dangerZones: List<DangerZone>,
    incidentMarkers: List<IncidentMarker>,
    selectedRiskFilter: String?,
    selectedPickerLocation: Pair<Double, Double>?,
    showUserLocation: Boolean,
    centerOnLocation: Pair<Double, Double>?,
    onMapTap: ((Double, Double) -> Unit)?,
    onZoneClick: ((DangerZone) -> Unit)?,
    onMarkerClick: ((IncidentMarker) -> Unit)?
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val config = Configuration.getInstance()
            config.userAgentValue = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 UCBSegura/1.0"
            
            // Ruta de caché limpia para evitar imágenes 403 o archivos corruptos
            val freshCacheDir = File(context.cacheDir, "osmdroid_tiles_v3")
            if (!freshCacheDir.exists()) freshCacheDir.mkdirs()
            config.osmdroidBasePath = freshCacheDir
            config.osmdroidTileCache = freshCacheDir

            config.load(context, context.getSharedPreferences("osmdroid_prefs_v3", Context.MODE_PRIVATE))

            MapView(context).apply {
                setTileSource(OsmFrTileSource)
                setMultiTouchControls(true)

                // Límites geográficos estrictos (BoundingBox) para Tupuraya y Queru Queru
                val campusBounds = BoundingBox(
                    -17.3635, // Norte (Queru Queru / Parque Lincoln)
                    -66.1355, // Este (Tupuraya / Gral. Galindo)
                    -17.3785, // Sur (Río Rocha / Av. Uyuni / Circunvalación)
                    -66.1510  // Oeste (Av. Pando / Libertador Bolívar)
                )
                setScrollableAreaLimitDouble(campusBounds)
                minZoomLevel = 15.5
                maxZoomLevel = 19.0

                // Centro inicial Campus Tupuraya (Cochabamba)
                controller.setZoom(17.0)
                controller.setCenter(GeoPoint(-17.3714, -66.1432))
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // Centrar mapa si se proporciona un punto específico de enfoque
            centerOnLocation?.let { (lat, lng) ->
                mapView.controller.animateTo(GeoPoint(lat, lng))
            }

            // 0. Capturar toques instantáneos (onSingleTapUp) en cualquier punto del mapa para seleccionar ubicación sin retraso
            if (onMapTap != null) {
                val instantTapOverlay = object : Overlay() {
                    override fun onSingleTapUp(e: android.view.MotionEvent, mapView: MapView): Boolean {
                        val projection = mapView.projection
                        val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                        onMapTap.invoke(geoPoint.latitude, geoPoint.longitude)
                        return true
                    }
                }
                mapView.overlays.add(instantTapOverlay)
            }

            // 1. Capa de Ubicación Actual en Tiempo Real del Estudiante con Seguimiento GPS activo
            if (showUserLocation) {
                try {
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(mapView.context), mapView).apply {
                        enableMyLocation()
                        enableFollowLocation() // Mantiene el mapa centrado y se mueve en tiempo real al caminar/desplazarse
                        runOnFirstFix {
                            mapView.post {
                                myLocation?.let { geoPoint ->
                                    mapView.controller.animateTo(geoPoint)
                                }
                            }
                        }
                    }
                    mapView.overlays.add(locationOverlay)
                } catch (_: Exception) {
                }
            }

            // 2. Marcador de Selección de Ubicación para Reportes ("📍 Punto del Reporte")
            selectedPickerLocation?.let { (lat, lng) ->
                val pickerMarker = Marker(mapView).apply {
                    position = GeoPoint(lat, lng)
                    title = "📍 Punto Seleccionado"
                    snippet = "Ubicación fijada para el reporte (${"%.4f".format(lat)}, ${"%.4f".format(lng)})"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createPickerPinBitmapDrawable(mapView.context)
                }
                mapView.overlays.add(pickerMarker)
            }

            // 3. Dibujar el Rectángulo Perimetral (BoundingBox) con Línea Negra Gruesa
            val boundaryPolygon = Polygon(mapView).apply {
                val boundsPoints = listOf(
                    GeoPoint(-17.3635, -66.1510), // Noroeste
                    GeoPoint(-17.3635, -66.1355), // Noreste
                    GeoPoint(-17.3785, -66.1355), // Sureste
                    GeoPoint(-17.3785, -66.1510)  // Suroeste
                )
                points = boundsPoints
                fillPaint.color = 0x0A2563EB.toInt()
                outlinePaint.color = android.graphics.Color.BLACK
                outlinePaint.strokeWidth = 8f
                title = "Perímetro Activo UCB Segura (Tupuraya & Queru Queru)"
            }
            mapView.overlays.add(boundaryPolygon)

            // 4. Dibujar Polígonos de Zonas de Riesgo (SIN BORDES en los círculos de riesgo)
            val filteredZones = if (selectedRiskFilter == null || selectedRiskFilter == "TODOS") {
                dangerZones
            } else {
                dangerZones.filter { it.riskLevel.name == selectedRiskFilter || it.riskLevel.label.equals(selectedRiskFilter, ignoreCase = true) }
            }

            filteredZones.forEach { zone ->
                val polygon = Polygon(mapView).apply {
                    val center = GeoPoint(zone.centerLat, zone.centerLng)
                    points = Polygon.pointsAsCircle(center, zone.radiusMeters)
                    fillPaint.color = zone.riskLevel.argbFill.toInt()
                    outlinePaint.strokeWidth = 0f // SIN BORDES EN LOS CÍRCULOS DE RIESGO
                    outlinePaint.color = android.graphics.Color.TRANSPARENT
                    title = zone.name
                    snippet = "${zone.riskLevel.label}\n${zone.description}"
                    setOnClickListener { poly, _, _ ->
                        onZoneClick?.invoke(zone)
                        poly.showInfoWindow()
                        true
                    }
                }
                mapView.overlays.add(polygon)
            }

            // 5. Dibujar Marcadores de Incidentes
            incidentMarkers.forEach { incident ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(incident.latitude, incident.longitude)
                    title = incident.title
                    snippet = "${incident.type} - ${incident.timestamp}\n${incident.description}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = createIncidentBitmapDrawable(mapView.context, incident.type)
                    setOnMarkerClickListener { m, _ ->
                        onMarkerClick?.invoke(incident)
                        m.showInfoWindow()
                        true
                    }
                }
                mapView.overlays.add(marker)
            }

            mapView.invalidate()
        }
    )
}

private fun createPickerPinBitmapDrawable(context: Context): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (44 * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, borderPaint)

    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFDC2626.toInt()
        style = Paint.Style.FILL
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, (sizePx / 2f) - (3 * density), bgPaint)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 20 * density
        textAlign = Paint.Align.CENTER
    }
    val yPos = (sizePx / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
    canvas.drawText("🎯", sizePx / 2f, yPos, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun createIncidentBitmapDrawable(context: Context, type: String): BitmapDrawable {
    val (emoji, colorInt) = when (type.uppercase()) {
        "ROBO" -> "🚨" to 0xFFDC2626.toInt()
        "RINA" -> "👥" to 0xFFEA580C.toInt()
        "FALTA_LUZ" -> "💡" to 0xFFD97706.toInt()
        "SOSPECHOSO" -> "👤" to 0xFF9333EA.toInt()
        else -> "⚠️" to 0xFF2563EB.toInt()
    }

    val density = context.resources.displayMetrics.density
    val sizePx = (38 * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Círculo de fondo con el color de riesgo (sin bordes)
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorInt
        style = Paint.Style.FILL
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, bgPaint)

    // Dibujar Emoji o Icono central
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 18 * density
        textAlign = Paint.Align.CENTER
    }
    val yPos = (sizePx / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
    canvas.drawText(emoji, sizePx / 2f, yPos, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}
