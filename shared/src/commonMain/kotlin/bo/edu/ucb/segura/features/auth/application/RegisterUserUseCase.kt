package bo.edu.ucb.segura.features.auth.application

import bo.edu.ucb.segura.features.auth.data.AuthRepository
import bo.edu.ucb.segura.features.auth.model.AuthResult
import bo.edu.ucb.segura.features.auth.model.RegisterRequest

/**
 * Caso de uso para registro de estudiante UCB validando obligatoriamente el dominio institucional.
 */
class RegisterUserUseCase(
    private val repository: AuthRepository = AuthRepository(),
    private val validateUcbEmailUseCase: ValidateUcbEmailUseCase = ValidateUcbEmailUseCase()
) {
    suspend operator fun invoke(request: RegisterRequest): AuthResult {
        if (request.fullName.trim().isEmpty()) {
            return AuthResult.Error("Ingrese su nombre completo de estudiante UCB.")
        }

        val emailValidation = validateUcbEmailUseCase(request.ucbEmail)
        if (!emailValidation.isValid) {
            return AuthResult.Error(emailValidation.errorMessage ?: "Dominio de correo no autorizado.")
        }

        if (request.phoneNumber.filter { it.isDigit() }.length < 8) {
            return AuthResult.Error("El número celular debe tener al menos 8 dígitos.")
        }

        if (request.studentIdCard.trim().isEmpty()) {
            return AuthResult.Error("Ingrese su número de carné institucional de estudiante UCB.")
        }

        return repository.register(request)
    }
}
