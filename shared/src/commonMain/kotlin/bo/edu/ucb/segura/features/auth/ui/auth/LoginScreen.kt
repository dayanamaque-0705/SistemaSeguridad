package bo.edu.ucb.segura.features.auth.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.edu.ucb.segura.core.data.AppStateRepository
import bo.edu.ucb.segura.features.auth.application.AuthenticateUserUseCase
import bo.edu.ucb.segura.features.auth.application.ValidateUcbEmailUseCase
import bo.edu.ucb.segura.features.auth.model.AuthResult
import bo.edu.ucb.segura.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val authenticateUserUseCase = remember { AuthenticateUserUseCase() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UcbNavyDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo & Header UCB Segura
            Surface(
                modifier = Modifier.size(88.dp),
                shape = RoundedCornerShape(24.dp),
                color = UcbBlueSecondary,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "UCB Logo",
                        tint = UcbGold,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "UCB SEGURA",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = UcbTextPrimary,
                letterSpacing = 2.sp
            )
            Text(
                text = "Sistema Oficial de Monitoreo y Alertas\nCampus Tupuraya • Gestión 2026",
                style = MaterialTheme.typography.bodyMedium,
                color = UcbGold,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Card Formulario de Inicio de Sesión
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = UcbCardDark),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Acceso Estudiantil UCB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = UcbTextPrimary
                    )
                    Text(
                        text = "Restringido exclusivamente a correos institucionales @ucb.edu.bo",
                        style = MaterialTheme.typography.bodySmall,
                        color = UcbTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    errorMessage?.let { msg ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = {
                            emailOrPhone = it
                            errorMessage = null
                        },
                        label = { Text("Correo Institucional UCB") },
                        placeholder = { Text("ej: nombre.apellido@est.ucb.edu.bo") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = UcbBlueAccent)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedLabelColor = UcbBlueAccent,
                            unfocusedLabelColor = UcbTextSecondary,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = UcbBlueAccent)
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedLabelColor = UcbBlueAccent,
                            unfocusedLabelColor = UcbTextSecondary,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                val result = authenticateUserUseCase(emailOrPhone, password)
                                isLoading = false
                                when (result) {
                                    is AuthResult.Success -> onLoginSuccess()
                                    is AuthResult.Error -> errorMessage = result.message
                                    else -> {}
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UcbBlueSecondary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = UcbTextPrimary,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "INICIAR SESIÓN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = UcbTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Crear cuenta
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Aún no tienes cuenta institucional? ",
                    color = UcbTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Registrarme",
                        color = UcbGold,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var studentIdCard by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val validateUcbEmailUseCase = remember { ValidateUcbEmailUseCase() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UcbNavyDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Registro de Estudiante UCB",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = UcbTextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Acceso exclusivo para miembros de la Universidad Católica Boliviana San Pablo",
                style = MaterialTheme.typography.bodySmall,
                color = UcbGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = UcbCardDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    errorMessage?.let { msg ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            errorMessage = null
                        },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = UcbBlueAccent) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Correo institucional UCB") },
                        placeholder = { Text("ej: nombre@est.ucb.edu.bo") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = UcbBlueAccent) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            errorMessage = null
                        },
                        label = { Text("Número de Celular") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = UcbBlueAccent) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = studentIdCard,
                        onValueChange = {
                            studentIdCard = it
                            errorMessage = null
                        },
                        label = { Text("Carné Institucional UCB") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = UcbBlueAccent) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Crea tu Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = UcbBlueAccent) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                        },
                        label = { Text("Confirma tu Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = UcbBlueAccent) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UcbBlueAccent,
                            unfocusedBorderColor = UcbBorderDark,
                            focusedTextColor = UcbTextPrimary,
                            unfocusedTextColor = UcbTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (fullName.trim().isBlank()) {
                                errorMessage = "Por favor ingrese su nombre completo."
                                return@Button
                            }

                            val emailValidation = validateUcbEmailUseCase(email)
                            if (!emailValidation.isValid) {
                                errorMessage = emailValidation.errorMessage
                                return@Button
                            }

                            if (phone.filter { it.isDigit() }.length < 8) {
                                errorMessage = "Ingrese un celular válido de 8 dígitos."
                                return@Button
                            }

                            if (studentIdCard.trim().isBlank()) {
                                errorMessage = "Ingrese su número de carné o código de estudiante UCB."
                                return@Button
                            }

                            if (password.length < 6) {
                                errorMessage = "La contraseña debe tener al menos 6 caracteres."
                                return@Button
                            }

                            if (password != confirmPassword) {
                                errorMessage = "Las contraseñas no coinciden."
                                return@Button
                            }

                            // Registrar usuario en la base de datos de la app
                            AppStateRepository.registerUser(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                studentIdCard = studentIdCard,
                                password = password
                            )

                            onRegisterSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UcbBlueSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CREAR MI CUENTA INSTITUCIONAL", color = UcbTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta UCB? Iniciar sesión", color = UcbGold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
