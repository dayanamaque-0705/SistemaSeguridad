package bo.edu.ucb.segura.features.auth.data

import bo.edu.ucb.segura.features.auth.model.User

/**
 * Fuente de datos local para la gestión de persistencia de sesión del estudiante UCB.
 */
class AuthLocalDataSource {

    private var currentUserSession: User? = User(
        id = "ucb-est-2026-7842",
        fullName = "Estudiante UCB Tupuraya",
        email = "estudiante.tupuraya@estudiantes.ucb.edu.bo",
        phoneNumber = "72910482",
        studentIdCard = "784210",
        career = "Ingeniería de Sistemas",
        campus = "Campus Tupuraya (Cochabamba)",
        isVerified = true,
        participationCount = 5
    )

    fun getCurrentUser(): User? = currentUserSession

    fun saveUserSession(user: User) {
        currentUserSession = user
    }

    fun clearSession() {
        currentUserSession = null
    }
}
