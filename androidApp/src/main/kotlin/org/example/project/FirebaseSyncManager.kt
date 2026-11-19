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

/**
 * Gestor de conexión y sincronización con Firebase (Firestore & Realtime Database).
 * Separa claramente la arquitectura solicitada:
 * - risk_points: Puntos de riesgo estructurales / preventivos en Tupuraya y Queru Queru.
 * - reports: Reportes de incidentes generados en tiempo real por los estudiantes (compartidos entre usuarios).
 */
object FirebaseSyncManager {
    private const val TAG = "FirebaseSyncManager"

    fun initializeAndSyncTables(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }

            val db = FirebaseFirestore.getInstance()
            val rtdb = FirebaseDatabase.getInstance("https://ucbsegura-default-rtdb.firebaseio.com")

            // 1. Sincronizar nuevo usuario registrado
            AppStateRepository.onUserRegistered = { user, _ ->
                try {
                    val userMap = mapOf(
                        "id" to user.id,
                        "fullName" to user.fullName,
                        "email" to user.email,
                        "phoneNumber" to user.phoneNumber,
                        "studentIdCard" to user.studentIdCard,
                        "career" to user.career,
                        "campus" to user.campus,
                        "isVerified" to user.isVerified,
                        "participationCount" to user.participationCount
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

            // 3. Escuchar reportes en tiempo real desde Firebase para que todos los usuarios los vean compartidos
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

            // 4. Sincronizar Alertas de Pánico (SOS)
            AppStateRepository.onPanicAlertTriggered = { alert ->
                try {
                    val alertMap = mapOf(
                        "id" to alert.id,
                        "title" to alert.title,
                        "type" to alert.type,
                        "latitude" to alert.latitude,
                        "longitude" to alert.longitude,
                        "timestamp" to alert.timestamp,
                        "description" to alert.description,
                        "userEmail" to (AppStateRepository.currentUser.value?.email ?: "anonimo")
                    )
                    db.collection("panic_alerts").document(alert.id).set(alertMap)
                    rtdb.getReference("panic_alerts/${alert.id}").setValue(alertMap)
                    Log.d(TAG, "Alerta SOS sincronizada con Firebase: ${alert.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing panic alert", e)
                }
            }

            // 5. Inicializar Puntos de Riesgo estructurales (risk_points) oficiales: Tupuraya y Queru Queru
            val riskPoints = listOf(
                mapOf(
                    "id" to "zone_tupuraya_bajo",
                    "zone" to "Campus Tupuraya",
                    "title" to "Campus UCB Tupuraya (Zona Baja)",
                    "motivoRiesgo" to "Área interna con vigilancia y bajo índice de incidentes.",
                    "level" to "BAJO",
                    "latitude" to -17.3714,
                    "longitude" to -66.1432
                ),
                mapOf(
                    "id" to "zone_tupuraya_medio",
                    "zone" to "Campus Tupuraya",
                    "title" to "Pasaje Este UCB (Zona Media)",
                    "motivoRiesgo" to "Zona lateral con reporte ocasional de objetos descuidados.",
                    "level" to "MEDIO",
                    "latitude" to -17.3725,
                    "longitude" to -66.1425
                ),
                mapOf(
                    "id" to "zone_queruqueru_alto",
                    "zone" to "Queru Queru",
                    "title" to "Zona Queru Queru (Alto Riesgo)",
                    "motivoRiesgo" to "Sector con reportes frecuentes de robos y escasa iluminación nocturna.",
                    "level" to "ALTO",
                    "latitude" to -17.3650,
                    "longitude" to -66.1350
                )
            )

            riskPoints.forEach { point ->
                db.collection("risk_points").document(point["id"].toString()).set(point)
            }
            rtdb.getReference("risk_points").setValue(riskPoints)

            Log.d(TAG, "Firebase sincronizado: risk_points y reports (con tiempo de vida de 10h y sincronización en vivo) configurados.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseSyncManager", e)
        }
    }
}
