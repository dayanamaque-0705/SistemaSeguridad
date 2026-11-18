package bo.edu.ucb.segura.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker

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
    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Text("🗺️ Vista de Mapa (Web) - Zonas: ${dangerZones.size}", color = Color.White)
    }
}
