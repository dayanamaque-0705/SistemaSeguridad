package bo.edu.ucb.segura.features.auth.application

import bo.edu.ucb.segura.core.data.AppStateRepository
import bo.edu.ucb.segura.features.auth.model.AuthResult

/**
 * Caso de uso para iniciar sesión con validación estricta de correo institucional UCB o celular.
 */
class AuthenticateUserUseCase(
    private val validateUcbEmailUseCase: ValidateUcbEmailUseCase = ValidateUcbEmailUseCase()
) {
    suspend operator fun invoke(emailOrPhone: String, password: String): AuthResult {
        val input = emailOrPhone.trim()
        if (input.isEmpty()) {
            return AuthResult.Error("Por favor ingrese su correo institucional UCB o número de celular.")
        }

        if (password.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres.")
        }

        // Si contiene '@', validar que sea correo oficial de la UCB (@ucb.edu.bo o @estudiantes.ucb.edu.bo)
        if (input.contains("@")) {
            val emailValidation = validateUcbEmailUseCase(input)
            if (!emailValidation.isValid) {
                return AuthResult.Error(emailValidation.errorMessage ?: "Acceso restringido: Solo correo institucional UCB.")
            }
        }

        val (success, errorMsg) = AppStateRepository.loginUser(input, password)
        return if (success) {
            val user = AppStateRepository.currentUser.value!!
            AuthResult.Success(user)
        } else {
            AuthResult.Error(errorMsg ?: "Error al iniciar sesión.")
        }
    }
}
