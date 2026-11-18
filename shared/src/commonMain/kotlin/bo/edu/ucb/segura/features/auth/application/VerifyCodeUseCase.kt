package bo.edu.ucb.segura.features.auth.application

import bo.edu.ucb.segura.features.auth.data.AuthRepository
import bo.edu.ucb.segura.features.auth.model.AuthResult

/**
 * Caso de uso para verificación del código de 6 dígitos enviado al correo institucional UCB.
 */
class VerifyCodeUseCase(
    private val repository: AuthRepository = AuthRepository()
) {
    suspend operator fun invoke(email: String, code: String): AuthResult {
        val cleanCode = code.trim()
        if (cleanCode.length != 6 || !cleanCode.all { it.isDigit() }) {
            return AuthResult.Error("El código de verificación debe constar exactamente de 6 dígitos numéricos.")
        }

        return repository.verifyCode(email, cleanCode)
    }
}
