package bo.edu.ucb.segura.features.auth.application

import bo.edu.ucb.segura.features.auth.model.ValidationResult

/**
 * Caso de uso obligatorio de Negocio: Validación estricta de correo institucional UCB.
 * Restringe totalmente el acceso a cualquier correo que NO pertenezca a los dominios oficiales de la UCB.
 * Dominios autorizados: @ucb.edu.bo y @estudiantes.ucb.edu.bo
 */
class ValidateUcbEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        val cleanEmail = email.trim().lowercase()

        if (cleanEmail.isEmpty()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "El correo electrónico institucional no puede estar vacío."
            )
        }

        // Expresión regular para validar dominios institucionales de la Universidad Católica Boliviana
        val ucbDomainRegex = Regex("^[a-zA-Z0-9._%+-]+@(ucb\\.edu\\.bo|estudiantes\\.ucb\\.edu\\.bo)$")

        if (!ucbDomainRegex.matches(cleanEmail)) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Acceso restringido: Debe ingresar un correo válido de la Universidad Católica Boliviana (@ucb.edu.bo o @estudiantes.ucb.edu.bo). Acceso denegado a usuarios externos."
            )
        }

        return ValidationResult(isValid = true)
    }
}
