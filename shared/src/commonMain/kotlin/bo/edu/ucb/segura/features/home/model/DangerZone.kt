package bo.edu.ucb.segura.features.home.model

enum class RiskLevel(
    val label: String,
    val colorHex: String,
    val argbFill: Long,   // Color de relleno transparente (ARGB)
    val argbBorder: Long  // Color de borde sólido (ARGB)
) {
    ALTO("Alto Riesgo", "#EF4444", 0x55EF4444, 0xFFDC2626),
    MEDIO("Riesgo Medio", "#F97316", 0x55F97316, 0xFFEA580C),
    BAJO("Riesgo Bajo", "#EAB308", 0x55EAB308, 0xFFD97706),
    SEGURA("Zona Segura", "#22C55E", 0x5522C55E, 0xFF16A34A)
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
