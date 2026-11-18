package bo.edu.ucb.segura.features.auth.model

data class LoginRequest(
    val emailOrPhone: String,
    val passwordHash: String
)

data class RegisterRequest(
    val fullName: String,
    val ucbEmail: String,
    val phoneNumber: String,
    val studentIdCard: String,
    val passwordHash: String
)

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class CodeSent(val email: String, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
