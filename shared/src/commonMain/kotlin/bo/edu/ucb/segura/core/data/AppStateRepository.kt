package bo.edu.ucb.segura.core.data

import bo.edu.ucb.segura.features.auth.model.User
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker
import bo.edu.ucb.segura.features.home.model.RiskLevel
import bo.edu.ucb.segura.features.reports.model.ReportModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

expect fun getCurrentTimeMillis(): Long

object AppStateRepository {

    // Callbacks para sincronización con Firebase (Usuarios y Reportes/Alertas)
    var onUserRegistered: ((User, String) -> Unit)? = null
    var onReportCreated: ((ReportModel) -> Unit)? = null
    var onPanicAlertTriggered: ((IncidentMarker) -> Unit)? = null

    private const val TEN_HOURS_MS = 10 * 60 * 60 * 1000L

    // 1. REGISTRO Y BASE DE DATOS DE USUARIOS
    private val registeredUsers = mutableMapOf<String, RegisteredAccount>(
        "estudiante.tupuraya@estudiantes.ucb.edu.bo" to RegisteredAccount(
            user = User(
                id = "ucb-est-2026-7842",
                fullName = "Estudiante UCB Tupuraya",
                email = "estudiante.tupuraya@estudiantes.ucb.edu.bo",
                phoneNumber = "72910482",
                studentIdCard = "784210",
                career = "Ingeniería de Sistemas",
                campus = "Campus Tupuraya (Cochabamba)",
                isVerified = true,
                participationCount = 5
            ),
            password = "password123"
        )
    )

    private val _currentUser = MutableStateFlow<User?>(registeredUsers["estudiante.tupuraya@estudiantes.ucb.edu.bo"]?.user)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // 2. ZONAS DE RIESGO Y REPORTES INICIALES
    private val initialDangerZones = listOf(
        DangerZone(
            id = "zone_tupuraya_bajo",
            name = "Campus UCB Tupuraya (Zona Baja)",
            description = "Área interna con vigilancia y bajo índice de incidentes.",
            centerLat = -17.3714,
            centerLng = -66.1432,
            radiusMeters = 75.0,
            riskLevel = RiskLevel.BAJO
        ),
        DangerZone(
            id = "zone_tupuraya_medio",
            name = "Pasaje Este UCB (Zona Media)",
            description = "Zona lateral con reporte ocasional de objetos descuidados.",
            centerLat = -17.3725,
            centerLng = -66.1425,
            radiusMeters = 65.0,
            riskLevel = RiskLevel.MEDIO
        ),
        DangerZone(
            id = "zone_queruqueru_alto",
            name = "Zona Queru Queru (Alto Riesgo)",
            description = "Sector con reportes frecuentes de robos y escasa iluminación nocturna.",
            centerLat = -17.3650,
            centerLng = -66.1350,
            radiusMeters = 100.0,
            riskLevel = RiskLevel.ALTO
        )
    )

    private val allStoredReports = mutableListOf<ReportModel>(
        ReportModel(
            id = "rep_1",
            type = "ROBO",
            description = "Sujeto en motocicleta interceptó a estudiante saliendo de clases.",
            location = "Pasaje Este UCB (Calle Oruro)",
            status = "En seguimiento",
            timestamp = "Hace 20 min",
            latitude = -17.3725,
            longitude = -66.1425,
            createdAtMillis = getCurrentTimeMillis() - (2 * 60 * 60 * 1000L) // 2 horas
        ),
        ReportModel(
            id = "rep_2",
            type = "SOSPECHOSO",
            description = "Individuo observando autos estacionados sin placa.",
            location = "Campus Tupuraya UCB",
            status = "Activo",
            timestamp = "Hace 4 horas",
            latitude = -17.3710,
            longitude = -66.1438,
            createdAtMillis = getCurrentTimeMillis() - (4 * 60 * 60 * 1000L)
        ),
        ReportModel(
            id = "rep_3",
            type = "ROBO",
            description = "Delincuente a pie arrebató bolso a transeúnte.",
            location = "Zona Queru Queru",
            status = "Activo",
            timestamp = "Hace 5 horas",
            latitude = -17.3650,
            longitude = -66.1350,
            createdAtMillis = getCurrentTimeMillis() - (5 * 60 * 60 * 1000L)
        )
    )

    private val allStoredMarkers = mutableListOf<IncidentMarker>(
        IncidentMarker(
            id = "inc_tupuraya_1",
            title = "Robo de celular en Pasaje Este",
            type = "ROBO",
            latitude = -17.3725,
            longitude = -66.1425,
            timestamp = "Hace 2 horas",
            description = "Sujeto en motocicleta interceptó a estudiante.",
            createdAtMillis = getCurrentTimeMillis() - (2 * 60 * 60 * 1000L)
        ),
        IncidentMarker(
            id = "inc_tupuraya_2",
            title = "Persona sospechosa en estacionamiento",
            type = "SOSPECHOSO",
            latitude = -17.3710,
            longitude = -66.1438,
            timestamp = "Hace 4 horas",
            description = "Individuo observando autos.",
            createdAtMillis = getCurrentTimeMillis() - (4 * 60 * 60 * 1000L)
        ),
        IncidentMarker(
            id = "inc_queru_1",
            title = "Arrebato de mochila en parada",
            type = "ROBO",
            latitude = -17.3650,
            longitude = -66.1350,
            timestamp = "Hace 5 horas",
            description = "Delincuente a pie.",
            createdAtMillis = getCurrentTimeMillis() - (5 * 60 * 60 * 1000L)
        )
    )

    private val _reportsState = MutableStateFlow<List<ReportModel>>(getValidRecentReports(allStoredReports))
    val reportsState: StateFlow<List<ReportModel>> = _reportsState.asStateFlow()

    private val _incidentMarkersState = MutableStateFlow<List<IncidentMarker>>(getValidRecentMarkers(allStoredMarkers))
    val incidentMarkersState: StateFlow<List<IncidentMarker>> = _incidentMarkersState.asStateFlow()

    private val _dangerZonesState = MutableStateFlow<List<DangerZone>>(computeInitialZones())
    val dangerZonesState: StateFlow<List<DangerZone>> = _dangerZonesState.asStateFlow()

    private fun getValidRecentMarkers(markers: List<IncidentMarker>): List<IncidentMarker> {
        val now = getCurrentTimeMillis()
        // Borrarse de la pantalla luego de 10 horas, pero permanecen en DB
        return markers.filter { m ->
            m.createdAtMillis == 0L || (now - m.createdAtMillis <= TEN_HOURS_MS)
        }
    }

    private fun getValidRecentReports(reports: List<ReportModel>): List<ReportModel> {
        val now = getCurrentTimeMillis()
        // Borrarse de la pantalla luego de 10 horas, pero permanecen en DB
        return reports.filter { r ->
            r.createdAtMillis == 0L || (now - r.createdAtMillis <= TEN_HOURS_MS)
        }
    }

    // Sincronizar reportes desde Firebase para que todos los usuarios los compartan en tiempo real
    fun syncReportsFromRemote(remoteReports: List<ReportModel>, remoteMarkers: List<IncidentMarker>) {
        allStoredReports.clear()
        allStoredReports.addAll(remoteReports)
        allStoredMarkers.clear()
        allStoredMarkers.addAll(remoteMarkers)

        _reportsState.value = getValidRecentReports(allStoredReports)
        _incidentMarkersState.value = getValidRecentMarkers(allStoredMarkers)
        updateZoneAndRiskLevels()
    }

    private fun computeInitialZones(): List<DangerZone> {
        val reports = _reportsState.value
        return initialDangerZones.map { zone ->
            val nearbyReportsCount = reports.count { r ->
                val dLat = r.latitude - zone.centerLat
                val dLng = r.longitude - zone.centerLng
                val dist = kotlin.math.sqrt(dLat * dLat + dLng * dLng) * 111000.0
                dist <= 500.0
            }
            val dynamicRadius = minOf(220.0, zone.radiusMeters + (nearbyReportsCount * 35.0))
            val newRisk = when {
                nearbyReportsCount == 0 -> RiskLevel.BAJO
                nearbyReportsCount == 1 -> RiskLevel.MEDIO
                else -> RiskLevel.ALTO
            }
            zone.copy(radiusMeters = dynamicRadius, riskLevel = newRisk)
        }
    }

    private fun updateZoneAndRiskLevels() {
        _dangerZonesState.value = computeInitialZones()
    }

    // VALIDACIÓN ESTRICTA DE RANGO GEOGRÁFICO (BoundingBox Tupuraya & Queru Queru)
    fun isWithinActiveRange(lat: Double, lng: Double): Boolean {
        val maxLat = -17.3635
        val minLat = -17.3785
        val maxLon = -66.1355
        val minLon = -66.1510
        return lat in minLat..maxLat && lng in minLon..maxLon
    }

    // 3. AUTENTICACIÓN Y REGISTRO
    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        studentIdCard: String,
        password: String
    ): Boolean {
        val cleanEmail = email.trim().lowercase()
        val user = User(
            id = "ucb-est-2026-${(1000..9999).random()}",
            fullName = fullName.trim(),
            email = cleanEmail,
            phoneNumber = phone.trim(),
            studentIdCard = studentIdCard.trim(),
            career = "Ingeniería de Sistemas",
            campus = "Campus Tupuraya (Cochabamba)",
            isVerified = true,
            participationCount = 1
        )
        registeredUsers[cleanEmail] = RegisteredAccount(user, password)
        _currentUser.value = user

        try {
            onUserRegistered?.invoke(user, password)
        } catch (_: Exception) {}

        return true
    }

    fun loginUser(emailOrPhone: String, password: String): Pair<Boolean, String?> {
        val input = emailOrPhone.trim().lowercase()
        val account = registeredUsers[input] ?: registeredUsers.values.find {
            it.user.phoneNumber == input || it.user.email == input
        }

        if (account == null) {
            val isUcbEmail = input.endsWith("@ucb.edu.bo") ||
                    input.endsWith("@estudiantes.ucb.edu.bo") ||
                    input.endsWith("@est.ucb.edu.bo") ||
                    input.endsWith("@cba.ucb.edu.bo")

            if (isUcbEmail) {
                if (password.length < 6) {
                    return Pair(false, "La contraseña debe tener al menos 6 caracteres.")
                }
                val newUser = User(
                    id = "ucb-est-2026-${(1000..9999).random()}",
                    fullName = input.substringBefore("@").replace(".", " "),
                    email = input,
                    phoneNumber = "70000000",
                    studentIdCard = "784200",
                    career = "Estudiante UCB",
                    campus = "Campus Tupuraya (Cochabamba)",
                    isVerified = true,
                    participationCount = 1
                )
                registeredUsers[input] = RegisteredAccount(newUser, password)
                _currentUser.value = newUser
                return Pair(true, null)
            }
            return Pair(false, "Acceso denegado: Usuario no registrado. Regístrese con su correo institucional UCB (@ucb.edu.bo).")
        }

        if (account.password != password) {
            return Pair(false, "Contraseña incorrecta. Verifique sus credenciales institucionales.")
        }

        _currentUser.value = account.user
        return Pair(true, null)
    }

    // 4. CREAR REPORTE (CON BLOQUEO ESTRICTO FUERA DE RANGO)
    fun createReport(
        type: String,
        description: String,
        locationName: String,
        latitude: Double,
        longitude: Double
    ): ReportModel? {
        if (!isWithinActiveRange(latitude, longitude)) {
            return null
        }

        val currentTime = getCurrentTimeMillis()
        val newId = "rep_${(1000..9999).random()}"
        val formattedType = when (type.lowercase()) {
            "robo" -> "ROBO"
            "riña", "rina" -> "RINA"
            "falta de luz", "falta_luz" -> "FALTA_LUZ"
            "persona sospechosa", "sospechoso" -> "SOSPECHOSO"
            else -> "ROBO"
        }

        val newReport = ReportModel(
            id = newId,
            type = formattedType,
            description = if (description.isBlank()) "Incidente reportado en $locationName" else description,
            location = locationName,
            status = "Activo",
            timestamp = "Justo ahora",
            latitude = latitude,
            longitude = longitude,
            createdAtMillis = currentTime
        )

        val newMarker = IncidentMarker(
            id = "inc_$newId",
            title = "Reporte: $formattedType",
            type = formattedType,
            latitude = latitude,
            longitude = longitude,
            timestamp = "Justo ahora",
            description = newReport.description,
            createdAtMillis = currentTime
        )

        allStoredReports.add(0, newReport)
        allStoredMarkers.add(0, newMarker)

        _reportsState.value = getValidRecentReports(allStoredReports)
        _incidentMarkersState.value = getValidRecentMarkers(allStoredMarkers)

        _currentUser.value?.let { u ->
            _currentUser.value = u.copy(participationCount = u.participationCount + 1)
        }

        // Actualizar tamaño y nivel de riesgo de las zonas dinámicamente
        updateZoneAndRiskLevels()

        try {
            onReportCreated?.invoke(newReport)
        } catch (_: Exception) {}

        return newReport
    }

    // 5. ALERTA DE PÁNICO (SOS)
    fun triggerPanicAlert(latitude: Double = -17.3714, longitude: Double = -66.1432): IncidentMarker {
        val currentTime = getCurrentTimeMillis()
        val panicMarker = IncidentMarker(
            id = "sos_${(1000..9999).random()}",
            title = "🚨 ALERTA DE PÁNICO SOS",
            type = "ROBO",
            latitude = latitude,
            longitude = longitude,
            timestamp = "EN CURSO",
            description = "Estudiante ${_currentUser.value?.fullName ?: "UCB"} activó alerta SOS.",
            createdAtMillis = currentTime
        )

        allStoredMarkers.add(0, panicMarker)
        _incidentMarkersState.value = getValidRecentMarkers(allStoredMarkers)

        try {
            onPanicAlertTriggered?.invoke(panicMarker)
        } catch (_: Exception) {}

        return panicMarker
    }

    fun logout() {
        _currentUser.value = null
    }
}

data class RegisteredAccount(
    val user: User,
    val password: String
)
