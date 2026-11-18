package bo.edu.ucb.segura.features.auth.data

import bo.edu.ucb.segura.features.auth.model.AuthResult
import bo.edu.ucb.segura.features.auth.model.LoginRequest
import bo.edu.ucb.segura.features.auth.model.RegisterRequest
import bo.edu.ucb.segura.features.auth.model.User

/**
 * Repositorio de Autenticación que gestiona persistencia local y fuentes remotas.
 */
class AuthRepository(
    private val localDataSource: AuthLocalDataSource = AuthLocalDataSource(),
    private val remoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) {
    fun getActiveUser(): User? = localDataSource.getCurrentUser()

    suspend fun login(loginRequest: LoginRequest): AuthResult {
        val result = remoteDataSource.login(loginRequest)
        if (result is AuthResult.Success) {
            localDataSource.saveUserSession(result.user)
        }
        return result
    }

    suspend fun register(request: RegisterRequest): AuthResult {
        return remoteDataSource.register(request)
    }

    suspend fun verifyCode(email: String, code: String): AuthResult {
        val result = remoteDataSource.verifyCode(email, code)
        if (result is AuthResult.Success) {
            localDataSource.saveUserSession(result.user)
        }
        return result
    }

    fun logout() {
        localDataSource.clearSession()
    }
}
