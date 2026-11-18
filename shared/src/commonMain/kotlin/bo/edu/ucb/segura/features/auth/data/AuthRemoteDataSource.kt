package bo.edu.ucb.segura.features.auth.data

import bo.edu.ucb.segura.features.auth.model.AuthResult
import bo.edu.ucb.segura.features.auth.model.LoginRequest
import bo.edu.ucb.segura.features.auth.model.RegisterRequest
import bo.edu.ucb.segura.features.auth.model.User

/**
 * Fuente de datos remota (API / Firebase UCB Backend).
 */
class AuthRemoteDataSource {

    suspend fun login(loginRequest: LoginRequest): AuthResult {
        // Simulación de autenticación exitosa contra el servidor UCB
        val email = if (loginRequest.emailOrPhone.contains("@")) {
            loginRequest.emailOrPhone
        } else {
            "estudiante.tupuraya@estudiantes.ucb.edu.bo"
        }

        val user = User(
            id = "ucb-est-2026-7842",
            fullName = "Estudiante UCB Tupuraya",
            email = email,
            phoneNumber = if (loginRequest.emailOrPhone.contains("@")) "72910482" else loginRequest.emailOrPhone,
            studentIdCard = "784210",
            career = "Ingeniería de Sistemas",
            campus = "Campus Tupuraya (Cochabamba)",
            isVerified = true,
            participationCount = 5
        )
        return AuthResult.Success(user)
    }

    suspend fun register(request: RegisterRequest): AuthResult {
        // Enviar código de verificación de 6 dígitos al correo institucional UCB
        return AuthResult.CodeSent(
            email = request.ucbEmail,
            message = "Se ha enviado un código de 6 dígitos a su correo institucional UCB: ${request.ucbEmail}"
        )
    }

    suspend fun verifyCode(email: String, code: String): AuthResult {
        if (code == "123456" || code.length == 6) {
            val newUser = User(
                id = "ucb-est-2026-${(1000..9999).random()}",
                fullName = "Estudiante UCB Registrado",
                email = email,
                phoneNumber = "70000000",
                studentIdCard = "889100",
                career = "Ingeniería de Sistemas",
                campus = "Campus Tupuraya (Cochabamba)",
                isVerified = true,
                participationCount = 1
            )
            return AuthResult.Success(newUser)
        }
        return AuthResult.Error("El código ingresado es incorrecto o ha expirado. Por favor intente nuevamente.")
    }
}
