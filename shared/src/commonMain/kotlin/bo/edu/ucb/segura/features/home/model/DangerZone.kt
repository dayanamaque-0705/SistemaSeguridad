package bo.edu.ucb.segura.features.home.model

enum class RiskLevel(
    val label: String,
    val colorHex: String,
    val argbFill: Long,   // Color de relleno transparente (ARGB)
    val argbBorder: Long, // Color de borde sólido (ARGB)
    val severityWeight: Int
) {
    ALTO("Alto Riesgo", "#D50000", 0x55D50000, 0xFFD50000, 3),
    MEDIO("Riesgo Medio", "#FF6D00", 0x55FF6D00, 0xFFFF6D00, 2),
    BAJO("Riesgo Bajo", "#FFD600", 0x55FFD600, 0xFFFFD600, 1),
    SEGURA("Zona Segura", "#22C55E", 0x5522C55E, 0xFF16A34A, 0)
}

data class DangerZone(
    val id: String,
    val name: String,
    val description: String,
    val centerLat: Double,
    val centerLng: Double,
    val radiusMeters: Double,
    val riskLevel: RiskLevel
)
