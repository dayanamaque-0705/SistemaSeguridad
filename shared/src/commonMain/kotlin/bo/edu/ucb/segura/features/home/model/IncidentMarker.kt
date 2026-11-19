package bo.edu.ucb.segura.features.home.model

data class IncidentMarker(
    val id: String,
    val title: String,
    val type: String, // ROBO, RINA, FALTA_LUZ, SOSPECHOSO
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val description: String = "",
    val createdAtMillis: Long = 0L
)
