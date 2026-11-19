package bo.edu.ucb.segura.features.reports.model

data class ReportModel(
    val id: String,
    val type: String,          // ROBO, RINA, FALTA_LUZ, SOSPECHOSO
    val description: String,
    val location: String,      // Ej. "Zona Campus Tupuraya - Calle Oruro"
    val status: String,        // "En seguimiento", "Resuelto", "Activo"
    val timestamp: String,
    val latitude: Double = -17.3714,
    val longitude: Double = -66.1432,
    val createdAtMillis: Long = 0L
)
