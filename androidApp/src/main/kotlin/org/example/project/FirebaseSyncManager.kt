package org.example.project

import android.content.Context
import android.util.Log
import bo.edu.ucb.segura.core.data.AppStateRepository
import bo.edu.ucb.segura.core.data.getCurrentTimeMillis
import bo.edu.ucb.segura.features.home.model.IncidentMarker
import bo.edu.ucb.segura.features.reports.model.ReportModel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Gestor de conexión y sincronización con Firebase (Firestore, Realtime Database y Cloud Messaging).
 * Sincroniza:
 * - Suscripción al tema 'emergencias_ucb'
 * - Guardado de Alertas SOS en /alertas_activas
 * - Disparo de notificaciones de alta importancia tipo WhatsApp para todos los estudiantes
 */
object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"

    private var usersListener: ValueEventListener? = null
    private var reportsListener: ValueEventListener? = null
    private var alertsListener: ValueEventListener? = null
    private var riskPointsListener: ValueEventListener? = null

    fun stopSync() {
        try {
            val rtdb = FirebaseDatabase.getInstance("https://ucbsegura-default-rtdb.firebaseio.com")
            usersListener?.let { rtdb.getReference("users").removeEventListener(it) }
            reportsListener?.let { rtdb.getReference("reports").removeEventListener(it) }
            alertsListener?.let { rtdb.getReference("alertas_activas").removeEventListener(it) }
            riskPointsListener?.let { rtdb.getReference("risk_points").removeEventListener(it) }
            Log.d(TAG, "Todos los listeners en tiempo real de Firebase fueron removidos correctamente.")
        } catch (e: Exception) {
            Log.e(TAG, "Error deteniendo listeners de Firebase", e)
        }
    }

    fun initializeAndSyncTables(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }

            val db = FirebaseFirestore.getInstance()
            val rtdb = FirebaseDatabase.getInstance("https://ucbsegura-default-rtdb.firebaseio.com")

            // 0. Suscripción automática de cada usuario al tema "emergencias_ucb"
            FirebaseMessaging.getInstance().subscribeToTopic("emergencias_ucb")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Suscrito exitosamente al tema FCM: emergencias_ucb")
                    } else {
                        Log.e(TAG, "Error al suscribirse al tema emergencias_ucb", task.exception)
                    }
                }

            // 0.B Escuchar nodo /users o /usuarios en tiempo real para sincronización de credenciales y teléfono
            usersListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val remoteUsers = mutableListOf<Pair<bo.edu.ucb.segura.features.auth.model.User, String>>()
                        for (child in snapshot.children) {
                            val map = child.value as? Map<*, *> ?: continue
                            val id = map["id"]?.toString() ?: continue
                            val fullName = map["fullName"]?.toString() ?: map["nombre"]?.toString() ?: ""
                            val email = map["email"]?.toString() ?: map["correo"]?.toString() ?: ""
                            val phone = map["phoneNumber"]?.toString() ?: map["phone"]?.toString() ?: map["telefono"]?.toString() ?: ""
                            val card = map["studentIdCard"]?.toString() ?: "784200"
                            val campus = map["campus"]?.toString() ?: "Campus Tupuraya (Cochabamba)"
                            val verified = (map["isVerified"] as? Boolean) ?: true
                            val count = (map["participationCount"] as? Number)?.toInt() ?: 1
                            val pwd = map["password"]?.toString() ?: "password123"

                            val user = bo.edu.ucb.segura.features.auth.model.User(
                                id = id,
                                fullName = fullName,
                                email = email,
                                phoneNumber = phone,
                                studentIdCard = card,
                                campus = campus,
                                isVerified = verified,
                                participationCount = count
                            )
                            remoteUsers.add(Pair(user, pwd))
                        }
                        if (remoteUsers.isNotEmpty()) {
                            AppStateRepository.syncUsersFromRemote(remoteUsers)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing remote users", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Users listener cancelled", error.toException())
                }
            }
            usersListener?.let { rtdb.getReference("users").addValueEventListener(it) }
            usersListener?.let { rtdb.getReference("usuarios").addValueEventListener(it) }

            // 1. Sincronizar nuevo usuario registrado
            AppStateRepository.onUserRegistered = { user, password ->
                try {
                    val userMap = mapOf(
                        "id" to user.id,
                        "fullName" to user.fullName,
                        "email" to user.email,
                        "phoneNumber" to user.phoneNumber,
                        "studentIdCard" to user.studentIdCard,
                        "campus" to user.campus,
                        "isVerified" to user.isVerified,
                        "participationCount" to user.participationCount,
                        "password" to password
                    )
                    val safeKey = user.email.replace(".", "_").replace("@", "_")

                    db.collection("users").document(user.email).set(userMap)
                        .addOnSuccessListener { Log.d(TAG, "Usuario registrado guardado en Firestore: ${user.email}") }

                    rtdb.getReference("users/$safeKey").setValue(userMap)
                        .addOnSuccessListener { Log.d(TAG, "Usuario registrado guardado en Realtime DB: ${user.email}") }

                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing registered user", e)
                }
            }

            // 2. Sincronizar reportes de incidentes en tiempo real (reports)
            AppStateRepository.onReportCreated = { report ->
                try {
                    val reportMap = mapOf(
                        "id" to report.id,
                        "category" to report.type,
                        "description" to report.description,
                        "location" to report.location,
                        "status" to report.status,
                        "timestamp" to report.timestamp,
                        "latitude" to report.latitude,
                        "longitude" to report.longitude,
                        "userEmail" to (AppStateRepository.currentUser.value?.email ?: "anonimo"),
                        "createdAtMillis" to report.createdAtMillis
                    )
                    db.collection("reports").document(report.id).set(reportMap)
                    rtdb.getReference("reports/${report.id}").setValue(reportMap)
                    Log.d(TAG, "Reporte sincronizado con Firebase (reports): ${report.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing report", e)
                }
            }

            // 3. Escuchar reportes en tiempo real desde Firebase para compartirlos entre todos los usuarios
            rtdb.getReference("reports").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val remoteReports = mutableListOf<ReportModel>()
                        val remoteMarkers = mutableListOf<IncidentMarker>()
                        for (child in snapshot.children) {
                            val map = child.value as? Map<*, *> ?: continue
                            val id = map["id"]?.toString() ?: continue
                            val type = map["category"]?.toString() ?: map["type"]?.toString() ?: "ROBO"
                            val desc = map["description"]?.toString() ?: ""
                            val loc = map["location"]?.toString() ?: ""
                            val status = map["status"]?.toString() ?: "Activo"
                            val time = map["timestamp"]?.toString() ?: "Hace un momento"
                            val lat = (map["latitude"] as? Number)?.toDouble() ?: -17.3714
                            val lng = (map["longitude"] as? Number)?.toDouble() ?: -66.1432
                            val createdMillis = (map["createdAtMillis"] as? Number)?.toLong() ?: getCurrentTimeMillis()

                            val report = ReportModel(
                                id = id,
                                type = type,
                                description = desc,
                                location = loc,
                                status = status,
                                timestamp = time,
                                latitude = lat,
                                longitude = lng,
                                createdAtMillis = createdMillis
                            )
                            val marker = IncidentMarker(
                                id = "inc_$id",
                                title = "Reporte: $type",
                                type = type,
                                latitude = lat,
                                longitude = lng,
                                timestamp = time,
                                description = desc,
                                createdAtMillis = createdMillis
                            )
                            remoteReports.add(report)
                            remoteMarkers.add(marker)
                        }
                        if (remoteReports.isNotEmpty()) {
                            AppStateRepository.syncReportsFromRemote(remoteReports, remoteMarkers)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing remote reports", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Reports listener cancelled", error.toException())
                }
            })

            // 4. Sincronizar Alertas de Pánico (SOS) al nodo /alertas_activas y disparar notificación
            AppStateRepository.onPanicAlertTriggered = { alert ->
                try {
                    val currentUser = AppStateRepository.currentUser.value
                    val alertPayload = mapOf(
                        "alertaId" to alert.id,
                        "uid_emisor" to (currentUser?.id ?: "ucb-est-2026-7842"),
                        "nombre" to (currentUser?.fullName ?: "Estudiante UCB"),
                        "email" to (currentUser?.email ?: "estudiante@ucb.edu.bo"),
                        "lat" to alert.latitude,
                        "lng" to alert.longitude,
                        "timestamp" to alert.timestamp,
                        "mensaje" to "🚨 ¡ALERTA SOS EN VIVO! Estudiante ${currentUser?.fullName ?: "UCB"} necesita ayuda en el campus."
                    )
                    rtdb.getReference("alertas_activas/${alert.id}").setValue(alertPayload)
                    db.collection("panic_alerts").document(alert.id).set(alertPayload)
                    Log.d(TAG, "Alerta SOS guardada en /alertas_activas y Firestore: ${alert.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing panic alert", e)
                }
            }

            // 5. Escuchar emergencias SOS en /alertas_activas para activar notificaciones de alta importancia en vivo
            rtdb.getReference("alertas_activas").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        for (child in snapshot.children) {
                            val map = child.value as? Map<*, *> ?: continue
                            val senderUid = map["uid_emisor"]?.toString()
                            val currentUserId = AppStateRepository.currentUser.value?.id ?: ""

                            // Omitir en el teléfono del mismo estudiante que envió la alerta
                            if (senderUid != null && senderUid.isNotEmpty() && senderUid == currentUserId) {
                                continue
                            }

                            val alertaId = map["alertaId"]?.toString() ?: child.key ?: continue
                            val nombre = map["nombre"]?.toString() ?: "Estudiante UCB"
                            val mensaje = map["mensaje"]?.toString() ?: "🚨 ¡ALERTA SOS EN VIVO! Estudiante $nombre necesita ayuda."
                            val lat = (map["lat"] as? Number)?.toDouble() ?: -17.3714
                            val lng = (map["lng"] as? Number)?.toDouble() ?: -66.1432

                            UcbFirebaseMessagingService.mostrarNotificacionAltaImportancia(
                                context = context,
                                alertaId = alertaId,
                                titulo = "🚨 ALERTA SOS - UCB SEGURA",
                                mensaje = mensaje,
                                lat = lat,
                                lng = lng
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando alertas activas", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Alertas activas listener cancelled", error.toException())
                }
            })

            // 6. Inicializar Puntos de Riesgo estructurales (risk_points) oficiales: Tupuraya y Queru Queru
            val riskPoints = listOf(
                mapOf(
                    "id" to "zone_queruqueru_alto",
                    "zone" to "Queru Queru",
                    "title" to "Zona Queru Queru (Alto Riesgo)",
                    "motivoRiesgo" to "Sector con reportes frecuentes de robos y escasa iluminación nocturna.",
                    "level" to "ALTO",
                    "latitude" to -17.36076,
                    "longitude" to -66.1400
                ),
                mapOf(
                    "id" to "zone_tupuraya_medio",
                    "zone" to "Campus Tupuraya",
                    "title" to "Pasaje Oeste UCB (Riesgo Medio)",
                    "motivoRiesgo" to "Zona lateral con reporte ocasional de objetos descuidados.",
                    "level" to "MEDIO",
                    "latitude" to -17.3709,
                    "longitude" to -66.1477
                ),
                mapOf(
                    "id" to "zone_tupuraya_bajo",
                    "zone" to "Campus Tupuraya",
                    "title" to "Campus UCB Tupuraya (Riesgo Bajo)",
                    "motivoRiesgo" to "Área interna con vigilancia y bajo índice de incidentes.",
                    "level" to "BAJO",
                    "latitude" to -17.3714,
                    "longitude" to -66.1432
                )
            )

            riskPoints.forEach { point ->
                db.collection("risk_points").document(point["id"].toString()).set(point)
            }
            rtdb.getReference("risk_points").setValue(riskPoints)

            // Callback para actualización remota de estado de reportes ("activo" -> "resuelto")
            AppStateRepository.onReportStatusUpdated = { reportId, status ->
                updateReportStatusInFirebase(reportId, status)
            }

            Log.d(TAG, "Firebase Cloud Messaging y Realtime Sync inicializados correctamente.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseSyncManager", e)
        }
    }

    fun updateReportStatusInFirebase(reportId: String, newStatus: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val rtdb = FirebaseDatabase.getInstance("https://ucbsegura-default-rtdb.firebaseio.com")

            rtdb.getReference("reports/$reportId/status").setValue(newStatus)
            db.collection("reports").document(reportId).update("status", newStatus)
            Log.d(TAG, "Estado del reporte $reportId actualizado a '$newStatus' en Firebase Realtime DB")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estado del reporte en Firebase", e)
        }
    }
}
