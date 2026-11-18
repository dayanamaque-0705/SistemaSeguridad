package bo.edu.ucb.segura.core.data

import bo.edu.ucb.segura.features.auth.model.User
import bo.edu.ucb.segura.features.home.model.DangerZone
import bo.edu.ucb.segura.features.home.model.IncidentMarker
import bo.edu.ucb.segura.features.home.model.RiskLevel
import bo.edu.ucb.segura.features.reports.model.ReportModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppStateRepository {

    // 1. REGISTRO Y BASE DE DATOS DE USUARIOS (DBA MEMORIA PERSISTENTE)
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

    // 2. INCIDENTES Y ZONAS DE RIESGO DE MAPA TUPURAYA
    private val initialDangerZones = listOf(
        DangerZone(
            id = "zone_1",
            name = "Pasaje Este UCB (Calle Oruro)",
            description = "Zona con escaso alumbrado nocturno. Reportes frecuentes de robos de objetos personales entre 19:00 y 22:00 hrs.",
            centerLat = -17.3725,
            centerLng = -66.1425,
            radiusMeters = 65.0,
            riskLevel = RiskLevel.ALTO
        ),
        DangerZone(
            id = "zone_2",
            name = "Cruce Av. Circunvalación & Atahuallpa",
            description = "Alto tráfico vehicular y despojos por motocicletas al paso.",
            centerLat = -17.3698,
            centerLng = -66.1440,
            radiusMeters = 80.0,
            riskLevel = RiskLevel.ALTO
        ),
        DangerZone(
            id = "zone_3",
            name = "Calle Oquendo & Calama (Parada de Micros)",
            description = "Zona de aglomeración. Mantener pertenencias al frente.",
            centerLat = -17.3732,
            centerLng = -66.1445,
            radiusMeters = 55.0,
            riskLevel = RiskLevel.MEDIO
        ),
        DangerZone(
            id = "zone_4",
            name = "Ciclovía Este / Sector Parque",
            description = "Tramo oscuro y poco transitado durante la noche. Transitar acompañado.",
            centerLat = -17.3705,
            centerLng = -66.1415,
            radiusMeters = 70.0,
            riskLevel = RiskLevel.MEDIO
        ),
        DangerZone(
            id = "zone_5",
            name = "Plaza de Comidas Exterior",
            description = "Precaución con laptops y celulares descuidados en mesas exteriores.",
            centerLat = -17.3718,
            centerLng = -66.1438,
            radiusMeters = 40.0,
            riskLevel = RiskLevel.BAJO
        ),
        DangerZone(
            id = "zone_6",
            name = "Entrada Principal UCB (Tupuraya)",
            description = "Punto de control seguro con vigilancia privada 24/7 y cámaras de monitoreo.",
            centerLat = -17.3714,
            centerLng = -66.1432,
            radiusMeters = 45.0,
            riskLevel = RiskLevel.SEGURA
        )
    )

    private val initialIncidentMarkers = mutableListOf(
        IncidentMarker(
            id = "inc_1",
            title = "Robo de celular",
            type = "ROBO",
            latitude = -17.3726,
            longitude = -66.1424,
            timestamp = "Hace 20 min",
            description = "Sujeto en motocicleta sustrajo teléfono a estudiante."
        ),
        IncidentMarker(
            id = "inc_2",
            title = "Persona sospechosa",
            type = "SOSPECHOSO",
            latitude = -17.3730,
            longitude = -66.1442,
            timestamp = "Hace 1 hora",
            description = "Persona observando estacionamiento de vehículos."
        ),
        IncidentMarker(
            id = "inc_3",
            title = "Falta de alumbrado",
            type = "FALTA_LUZ",
            latitude = -17.3708,
            longitude = -66.1418,
            timestamp = "Ayer 20:30",
            description = "Postes de luz apagados en calle lateral."
        ),
        IncidentMarker(
            id = "inc_4",
            title = "Altercado / Riña",
            type = "RINA",
            latitude = -17.3697,
            longitude = -66.1436,
            timestamp = "Hace 3 horas",
            description = "Discusión fuerte en parada de transporte público."
        )
    )

    private val initialReports = mutableListOf(
        ReportModel(
            id = "rep_1",
            type = "ROBO",
            description = "Sujeto en motocicleta sustrajo teléfono a estudiante.",
            location = "Calle Calama y Oquendo",
            status = "En seguimiento",
            timestamp = "Hace 20 min",
            latitude = -17.3726,
            longitude = -66.1424
        ),
        ReportModel(
            id = "rep_2",
            type = "FALTA_LUZ",
            description = "Postes de luz apagados en calle lateral.",
            location = "Zona Pasaje Este Campus Tupuraya",
            status = "Resuelto",
            timestamp = "Ayer 20:30",
            latitude = -17.3708,
            longitude = -66.1418
        ),
        ReportModel(
            id = "rep_3",
            type = "SOSPECHOSO",
            description = "Persona observando estacionamiento de vehículos.",
            location = "Av. Circunvalación",
            status = "En seguimiento",
            timestamp = "Hace 1 hora",
            latitude = -17.3730,
            longitude = -66.1442
        )
    )

    private val _dangerZonesState = MutableStateFlow<List<DangerZone>>(initialDangerZones)
    val dangerZonesState: StateFlow<List<DangerZone>> = _dangerZonesState.asStateFlow()

    private val _incidentMarkersState = MutableStateFlow<List<IncidentMarker>>(initialIncidentMarkers)
    val incidentMarkersState: StateFlow<List<IncidentMarker>> = _incidentMarkersState.asStateFlow()

    private val _reportsState = MutableStateFlow<List<ReportModel>>(initialReports)
    val reportsState: StateFlow<List<ReportModel>> = _reportsState.asStateFlow()

    // 3. AUTENTICACIÓN Y REGISTRO DE USUARIOS INSTITUCIONALES
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

    // 4. CREAR REPORTE Y SINCRONIZAR DE INMEDIATO AL MAPA Y A "MIS REPORTES"
    fun createReport(
        type: String,
        description: String,
        locationName: String,
        latitude: Double,
        longitude: Double
    ): ReportModel {
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
            description = if (description.isBlank()) "Incidente reportado por estudiante en $locationName" else description,
            location = locationName,
            status = "Activo",
            timestamp = "Hace un momento",
            latitude = latitude,
            longitude = longitude
        )

        val newMarker = IncidentMarker(
            id = "inc_$newId",
            title = "Reporte: $formattedType",
            type = formattedType,
            latitude = latitude,
            longitude = longitude,
            timestamp = "Justo ahora",
            description = newReport.description
        )

        val currentReports = _reportsState.value.toMutableList()
        currentReports.add(0, newReport)
        _reportsState.value = currentReports

        val currentMarkers = _incidentMarkersState.value.toMutableList()
        currentMarkers.add(0, newMarker)
        _incidentMarkersState.value = currentMarkers

        _currentUser.value?.let { u ->
            _currentUser.value = u.copy(participationCount = u.participationCount + 1)
        }

        return newReport
    }

    // 5. EMBARGAR ALERTA DE PÁNICO (SOS) EN EL MAPA
    fun triggerPanicAlert(latitude: Double = -17.3714, longitude: Double = -66.1432): IncidentMarker {
        val panicMarker = IncidentMarker(
            id = "sos_${(1000..9999).random()}",
            title = "🚨 ALERTA DE PÁNICO EN VIVO",
            type = "ROBO",
            latitude = latitude,
            longitude = longitude,
            timestamp = "EN CURSO",
            description = "Estudiante UCB activó la alerta de pánico en el campus."
        )

        val currentMarkers = _incidentMarkersState.value.toMutableList()
        currentMarkers.add(0, panicMarker)
        _incidentMarkersState.value = currentMarkers

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
