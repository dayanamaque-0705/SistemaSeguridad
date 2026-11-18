package bo.edu.ucb.segura.features.auth.model

/**
 * Entidad pura que representa a un Usuario Institucional UCB verificado.
 */
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val studentIdCard: String,
    val career: String = "Ingeniería de Sistemas",
    val campus: String = "Campus Tupuraya (Cochabamba)",
    val isVerified: Boolean = true,
    val participationCount: Int = 3
)
