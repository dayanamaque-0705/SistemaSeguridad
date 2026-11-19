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
                campus = "Campus Tupuraya (Cochabamba)",
                isVerified = true,
                participationCount = 5
            ),
            password = "password123"
        )
    )

    private val _currentUser = MutableStateFlow<User?>(registeredUsers["estudiante.tupuraya@estudiantes.ucb.edu.bo"]?.user)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // 2. ZONAS DE RIESGO Y REPORTES INICIALES (DISPERSAS EN EL ÁREA PERMITIDA)
    private val initialDangerZones = listOf(
        DangerZone(
            id = "zone_queruqueru_alto",
            name = "Zona Queru Queru (Alto Riesgo)",
            description = "Sector con reportes frecuentes de robos y escasa iluminación nocturna.",
            centerLat = -17.36076,
            centerLng = -66.1400,
            radiusMeters = 90.0,
            riskLevel = RiskLevel.ALTO
        ),
        DangerZone(
            id = "zone_tupuraya_medio",
            name = "Pasaje Oeste UCB (Riesgo Medio)",
            description = "Zona lateral con reporte ocasional de objetos descuidados.",
            centerLat = -17.3709,
            centerLng = -66.1477,
            radiusMeters = 70.0,
            riskLevel = RiskLevel.MEDIO
        ),
        DangerZone(
            id = "zone_tupuraya_bajo",
            name = "Campus UCB Tupuraya (Riesgo Bajo)",
            description = "Área interna con vigilancia y bajo índice de incidentes.",
            centerLat = -17.3714,
            centerLng = -66.1432,
            radiusMeters = 75.0,
            riskLevel = RiskLevel.BAJO
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
            createdAtMillis = getCurrentTimeMillis() - (2 * 60 * 60 * 1000L)
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
            type = "FALTA_LUZ",
            description = "Falta de alumbrado público en pasaje cercano a la universidad.",
            location = "Zona Queru Queru Norte",
            status = "Activo",
            timestamp = "Hace 5 horas",
            latitude = -17.3670,
            longitude = -66.1400,
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
            title = "Falta de luz en calle principal",
            type = "FALTA_LUZ",
            latitude = -17.3670,
            longitude = -66.1400,
            timestamp = "Hace 5 horas",
            description = "Luminaria pública apagada.",
            createdAtMillis = getCurrentTimeMillis() - (5 * 60 * 60 * 1000L)
        )
    )

    private val _reportsState = MutableStateFlow<List<ReportModel>>(emptyList())
    val reportsState: StateFlow<List<ReportModel>> = _reportsState.asStateFlow()

    private val _incidentMarkersState = MutableStateFlow<List<IncidentMarker>>(emptyList())
    val incidentMarkersState: StateFlow<List<IncidentMarker>> = _incidentMarkersState.asStateFlow()

    private val _dangerZonesState = MutableStateFlow<List<DangerZone>>(emptyList())
    val dangerZonesState: StateFlow<List<DangerZone>> = _dangerZonesState.asStateFlow()

    val latestPanicLocation = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        syncReportsFromRemote(allStoredReports, allStoredMarkers)
    }

    var onReportStatusUpdated: ((String, String) -> Unit)? = null

    fun calculateHaversineDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0
        val dLat = (lat2 - lat1) * kotlin.math.PI / 180.0
        val dLon = (lon2 - lon1) * kotlin.math.PI / 180.0
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(lat1 * kotlin.math.PI / 180.0) * kotlin.math.cos(lat2 * kotlin.math.PI / 180.0) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }

    private fun isReportActive(report: ReportModel, now: Long): Boolean {
        val isStatusActive = when (report.status.trim().lowercase()) {
            "resuelto", "cancelado" -> false
            else -> true
        }
        val isRecent = report.createdAtMillis == 0L || (now - report.createdAtMillis <= TEN_HOURS_MS)
        return isStatusActive && isRecent
    }

    fun computeDynamicDangerZones(activeReports: List<ReportModel>): List<DangerZone> {
        if (activeReports.isEmpty()) return emptyList()

        val clusters = mutableListOf<MutableList<ReportModel>>()

        for (report in activeReports) {
            var addedToCluster = false
            for (cluster in clusters) {
                val centerLat = cluster.map { it.latitude }.average()
                val centerLng = cluster.map { it.longitude }.average()
                val dist = calculateHaversineDistanceMeters(report.latitude, report.longitude, centerLat, centerLng)
                if (dist <= 80.0) {
                    cluster.add(report)
                    addedToCluster = true
                    break
                }
            }
            if (!addedToCluster) {
                clusters.add(mutableListOf(report))
            }
        }

        return clusters.mapIndexed { index, cluster ->
            val centerLat = cluster.map { it.latitude }.average()
            val centerLng = cluster.map { it.longitude }.average()
            val count = cluster.size

            val radiusMeters = when (count) {
                1 -> 40.0
                2 -> 75.0
                else -> 110.0 + ((count - 3) * 15.0).coerceAtMost(100.0)
            }

            val highestRisk = cluster.map { report ->
                when (report.type.trim().uppercase()) {
                    "ROBO" -> RiskLevel.ALTO
                    "SOSPECHOSO", "RINA", "RIÑA" -> RiskLevel.MEDIO
                    "FALTA_LUZ", "FALTA DE LUZ" -> RiskLevel.BAJO
                    else -> RiskLevel.BAJO
                }
            }.maxByOrNull { it.severityWeight } ?: RiskLevel.BAJO

            DangerZone(
                id = "dyn_zone_$index",
                name = "Zona de Riesgo (${highestRisk.label})",
                description = "$count reporte(s) activo(s) en este sector. Tipos: ${cluster.map { it.type }.distinct().joinToString()}",
                centerLat = centerLat,
                centerLng = centerLng,
                radiusMeters = radiusMeters,
                riskLevel = highestRisk
            )
        }
    }

    private fun normalizePhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return if (digits.length > 8 && digits.startsWith("591")) {
            digits.drop(3)
        } else {
            digits
        }
    }

    fun syncUsersFromRemote(remoteUsers: List<Pair<User, String>>) {
        for ((user, pwd) in remoteUsers) {
            val cleanEmail = user.email.trim().lowercase()
            val cleanPhone = normalizePhone(user.phoneNumber)
            if (cleanEmail.isNotBlank()) {
                val existingAccount = registeredUsers[cleanEmail] ?: registeredUsers.values.find { it.user.email.lowercase() == cleanEmail }
                val existingPwd = existingAccount?.password
                val finalPwd = if ((pwd.isBlank() || pwd == "password123") && !existingPwd.isNullOrBlank() && existingPwd != "password123") existingPwd else pwd
                registeredUsers[cleanEmail] = RegisteredAccount(user, finalPwd)
            }
            if (cleanPhone.isNotBlank()) {
                val existingAccount = registeredUsers[cleanPhone] ?: registeredUsers.values.find { normalizePhone(it.user.phoneNumber) == cleanPhone }
                val existingPwd = existingAccount?.password
                val finalPwd = if ((pwd.isBlank() || pwd == "password123") && !existingPwd.isNullOrBlank() && existingPwd != "password123") existingPwd else pwd
                registeredUsers[cleanPhone] = RegisteredAccount(user, finalPwd)
            }
        }
        _currentUser.value?.let { current ->
            val updated = registeredUsers[current.email.lowercase()]?.user
                ?: registeredUsers.values.find { it.user.id == current.id }?.user
            if (updated != null && updated != current) {
                _currentUser.value = updated
            }
        }
    }

    fun syncReportsFromRemote(remoteReports: List<ReportModel>, remoteMarkers: List<IncidentMarker>) {
        allStoredReports.clear()
        allStoredReports.addAll(remoteReports)
        allStoredMarkers.clear()
        allStoredMarkers.addAll(remoteMarkers)

        val now = getCurrentTimeMillis()
        val activeReports = allStoredReports.filter { isReportActive(it, now) }
        val activeMarkers = allStoredMarkers.filter { m ->
            val matching = allStoredReports.find { r -> "inc_${r.id}" == m.id || r.id == m.id }
            if (matching != null) isReportActive(matching, now) else (m.createdAtMillis == 0L || now - m.createdAtMillis <= TEN_HOURS_MS)
        }

        _reportsState.value = activeReports
        _incidentMarkersState.value = activeMarkers
        _dangerZonesState.value = computeDynamicDangerZones(activeReports)
    }

    fun updateReportStatus(reportId: String, newStatus: String) {
        val report = allStoredReports.find { it.id == reportId }
        if (report != null) {
            val updatedReport = report.copy(status = newStatus)
            val index = allStoredReports.indexOf(report)
            if (index >= 0) {
                allStoredReports[index] = updatedReport
            }
            val now = getCurrentTimeMillis()
            val activeReports = allStoredReports.filter { isReportActive(it, now) }
            val activeMarkers = allStoredMarkers.filter { m ->
                val matching = allStoredReports.find { r -> "inc_${r.id}" == m.id || r.id == m.id }
                if (matching != null) isReportActive(matching, now) else (m.createdAtMillis == 0L || now - m.createdAtMillis <= TEN_HOURS_MS)
            }
            _reportsState.value = activeReports
            _incidentMarkersState.value = activeMarkers
            _dangerZonesState.value = computeDynamicDangerZones(activeReports)

            try {
                onReportStatusUpdated?.invoke(reportId, newStatus)
            } catch (_: Exception) {}
        }
    }

    // VALIDACIÓN ESTRICTA DE RANGO GEOGRÁFICO (BoundingBox Tupuraya & Queru Queru)
    fun isWithinActiveRange(lat: Double, lng: Double): Boolean {
        val maxLat = -17.3600
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
        val cleanPhone = normalizePhone(phone)
        val user = User(
            id = "ucb-est-2026-${(1000..9999).random()}",
            fullName = fullName.trim(),
            email = cleanEmail,
            phoneNumber = phone.trim(),
            studentIdCard = studentIdCard.trim(),
            campus = "Campus Tupuraya (Cochabamba)",
            isVerified = true,
            participationCount = 1
        )
        registeredUsers[cleanEmail] = RegisteredAccount(user, password)
        if (cleanPhone.isNotBlank()) {
            registeredUsers[cleanPhone] = RegisteredAccount(user, password)
        }
        _currentUser.value = user

        try {
            onUserRegistered?.invoke(user, password)
        } catch (_: Exception) {}

        return true
    }

    fun loginUser(emailOrPhone: String, password: String): Pair<Boolean, String?> {
        val input = emailOrPhone.trim().lowercase().replace(" ", "")
        val normalizedInput = normalizePhone(emailOrPhone)
        val isPhoneInput = emailOrPhone.trim().all { it.isDigit() || it == '+' || it == ' ' } && (emailOrPhone.any { it.isDigit() } && !emailOrPhone.contains("@"))

        val account = registeredUsers[input]
            ?: registeredUsers[normalizedInput]
            ?: registeredUsers.values.find {
                it.user.email.lowercase() == input ||
                normalizePhone(it.user.phoneNumber) == normalizedInput ||
                it.user.phoneNumber.replace(" ", "").lowercase() == input
            }

        if (account == null) {
            if (isPhoneInput) {
                return Pair(
                    false,
                    "Acceso denegado: El número de teléfono ($emailOrPhone) no está registrado previamente en la base de datos. Debe registrarse primero."
                )
            } else {
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
        }

        if (isPhoneInput) {
            val userEmail = account.user.email.trim().lowercase()
            val isUcbEmail = userEmail.endsWith("@ucb.edu.bo") ||
                    userEmail.endsWith("@estudiantes.ucb.edu.bo") ||
                    userEmail.endsWith("@est.ucb.edu.bo") ||
                    userEmail.endsWith("@cba.ucb.edu.bo")

            if (userEmail.isBlank() || !isUcbEmail) {
                return Pair(
                    false,
                    "Acceso denegado: El número de teléfono ($emailOrPhone) no tiene un correo institucional UCB (@ucb.edu.bo) registrado y asociado en la base de datos."
                )
            }
        }

        if (account.password.isNotBlank() && account.password != password) {
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

        val now = getCurrentTimeMillis()
        val activeReports = allStoredReports.filter { isReportActive(it, now) }
        val activeMarkers = allStoredMarkers.filter { m ->
            val matching = allStoredReports.find { r -> "inc_${r.id}" == m.id || r.id == m.id }
            if (matching != null) isReportActive(matching, now) else (m.createdAtMillis == 0L || now - m.createdAtMillis <= TEN_HOURS_MS)
        }

        _reportsState.value = activeReports
        _incidentMarkersState.value = activeMarkers
        _dangerZonesState.value = computeDynamicDangerZones(activeReports)

        try {
            onReportCreated?.invoke(newReport)
        } catch (_: Exception) {}

        return newReport
    }

    // 5. ALERTA DE PÁNICO (SOS)
    fun triggerPanicAlert(latitude: Double = -17.3714, longitude: Double = -66.1432): IncidentMarker {
        val currentTime = getCurrentTimeMillis()
        latestPanicLocation.value = Pair(latitude, longitude)

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
        val now = getCurrentTimeMillis()
        val activeReports = allStoredReports.filter { isReportActive(it, now) }
        val activeMarkers = allStoredMarkers.filter { m ->
            val matching = allStoredReports.find { r -> "inc_${r.id}" == m.id || r.id == m.id }
            if (matching != null) isReportActive(matching, now) else (m.createdAtMillis == 0L || now - m.createdAtMillis <= TEN_HOURS_MS)
        }
        _reportsState.value = activeReports
        _incidentMarkersState.value = activeMarkers
        _dangerZonesState.value = computeDynamicDangerZones(activeReports)

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
