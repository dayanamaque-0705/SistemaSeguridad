package org.example.project

import android.content.Context
import android.util.Log
import bo.edu.ucb.segura.core.data.AppStateRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Gestor de conexión y sincronización de colecciones (tablas) con Firebase
 * utilizando la configuración de google-services.json (Proyecto: ucbsegura).
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

            // Registrar callback para cuando un estudiante cree una cuenta nueva y se registre en Firebase Realtime DB y Firestore
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

                    // Guardar en Firestore (colección "users")
                    db.collection("users").document(user.email).set(userMap)
                        .addOnSuccessListener { Log.d(TAG, "Nuevo estudiante guardado en Firestore: ${user.email}") }
                        .addOnFailureListener { e -> Log.e(TAG, "Error guardando estudiante en Firestore", e) }

                    // Guardar en Realtime Database (tabla "users")
                    rtdb.getReference("users/$safeKey").setValue(userMap)
                        .addOnSuccessListener { Log.d(TAG, "Nuevo estudiante guardado en Realtime Database: ${user.email}") }

                } catch (e: Exception) {
                    Log.e(TAG, "Error al sincronizar nuevo usuario con Firebase", e)
                }
            }

            // 1. Sincronizar usuario inicial por defecto
            val defaultUser = mapOf(
                "id" to "ucb-est-2026-7842",
                "fullName" to "Estudiante UCB Tupuraya",
                "email" to "estudiante.tupuraya@estudiantes.ucb.edu.bo",
                "phoneNumber" to "72910482",
                "studentIdCard" to "784210",
                "career" to "Ingeniería de Sistemas",
                "campus" to "Campus Tupuraya (Cochabamba)",
                "isVerified" to true,
                "participationCount" to 5
            )
            db.collection("users").document("estudiante.tupuraya@estudiantes.ucb.edu.bo").set(defaultUser)
            rtdb.getReference("users/estudiante_tupuraya").setValue(defaultUser)

            // 2. Sincronizar zonas de riesgo iniciales
            val zones = listOf(
                mapOf("id" to "zone_1", "name" to "Pasaje Este UCB (Calle Oruro)", "riskLevel" to "ALTO", "centerLat" to -17.3725, "centerLng" to -66.1425, "radiusMeters" to 65.0),
                mapOf("id" to "zone_2", "name" to "Cruce Av. Circunvalación & Atahuallpa", "riskLevel" to "ALTO", "centerLat" to -17.3698, "centerLng" to -66.1440, "radiusMeters" to 80.0),
                mapOf("id" to "zone_6", "name" to "Entrada Principal UCB (Tupuraya)", "riskLevel" to "SEGURA", "centerLat" to -17.3714, "centerLng" to -66.1432, "radiusMeters" to 45.0)
            )
            zones.forEach { zone -> db.collection("danger_zones").document(zone["id"].toString()).set(zone) }
            rtdb.getReference("danger_zones").setValue(zones)

            Log.d(TAG, "Conexión a Firebase Firestore & Realtime Database inicializada exitosamente.")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar Firebase", e)
        }
    }
}
