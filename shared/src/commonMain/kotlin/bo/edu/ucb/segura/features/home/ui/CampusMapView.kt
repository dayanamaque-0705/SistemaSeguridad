package bo.edu.ucb.segura.features.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker

@Composable
expect fun CampusMapView(
    modifier: Modifier = Modifier,
    dangerZones: List<DangerZone> = emptyList(),
    incidentMarkers: List<IncidentMarker> = emptyList(),
    selectedRiskFilter: String? = null,
    selectedPickerLocation: Pair<Double, Double>? = null,
    showUserLocation: Boolean = true,
    centerOnLocation: Pair<Double, Double>? = null,
    onMapTap: ((Double, Double) -> Unit)? = null,
    onZoneClick: ((DangerZone) -> Unit)? = null,
    onMarkerClick: ((IncidentMarker) -> Unit)? = null
)
