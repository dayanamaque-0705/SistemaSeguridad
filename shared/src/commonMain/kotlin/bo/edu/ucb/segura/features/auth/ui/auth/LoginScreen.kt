package bo.edu.ucb.segura.features.auth.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bo.edu.ucb.segura.core.data.AppStateRepository
import bo.edu.ucb.segura.features.auth.application.ValidateUcbEmailUseCase

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var selectedAuthType by remember { mutableStateOf("EMAIL") } // "EMAIL" or "PHONE"
    var emailOrPhone by remember { mutableStateOf("estudiante.tupuraya@estudiantes.ucb.edu.bo") }
    var isCodeSentStep by remember { mutableStateOf(false) }
    var otpDigit1 by remember { mutableStateOf("1") }
    var otpDigit2 by remember { mutableStateOf("2") }
    var otpDigit3 by remember { mutableStateOf("3") }
    var otpDigit4 by remember { mutableStateOf("4") }
    var otpDigit5 by remember { mutableStateOf("5") }
    var otpDigit6 by remember { mutableStateOf("6") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val backgroundColor = Color(0xFF07111E)
    val cardColor = Color(0xFF0F1B2E)
    val inputBgColor = Color(0xFF132238)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)
    val validateUcbEmailUseCase = remember { ValidateUcbEmailUseCase() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BARRA SUPERIOR CON REGRESO Y ETIQUETA UCB
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCodeSentStep) {
                    IconButton(
                        onClick = { isCodeSentStep = false },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("←", color = textColor, fontSize = 20.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(36.dp))
                }

                Text(
                    text = if (isCodeSentStep) "Verificar código" else "Iniciar sesión",
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "UCB",
                    color = grayText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ILUSTRACIÓN DEL SHIELD UCB CON MARCADORES DE SEGURIDAD
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF1E293B), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFF1E3A8A), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛡️", fontSize = 36.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isCodeSentStep) {
                // --- PANTALLA 1: INICIAR SESIÓN CON SEGMENTED TAB TOGGLE ---
                Text(
                    text = "Inicia sesión",
                    color = textColor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Text(
                    text = "Ingresa tu correo electrónico institucional o número de celular para recibir un código de acceso.",
                    color = grayText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )

                // TAB TOGGLE CORREO / CELULAR
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = inputBgColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (selectedAuthType == "EMAIL") primaryBlue else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedAuthType = "EMAIL"
                                    emailOrPhone = "estudiante.tupuraya@estudiantes.ucb.edu.bo"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Correo electrónico",
                                color = if (selectedAuthType == "EMAIL") Color.White else grayText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (selectedAuthType == "PHONE") primaryBlue else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedAuthType = "PHONE"
                                    emailOrPhone = "+591 72910482"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Número de celular",
                                color = if (selectedAuthType == "PHONE") Color.White else grayText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                errorMessage?.let { msg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        color = Color(0xFF7F1D1D),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // CAMPO DE ENTRADA
                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = {
                        emailOrPhone = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(
                            text = if (selectedAuthType == "EMAIL") "estudiante@estudiantes.ucb.edu.bo" else "+591 77300330",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ⓘ", color = grayText, fontSize = 12.sp)
                    Text(
                        text = "Te enviaremos un código de 6 dígitos para continuar.",
                        color = grayText,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedAuthType == "EMAIL") {
                            val validation = validateUcbEmailUseCase(emailOrPhone)
                            if (!validation.isValid) {
                                errorMessage = validation.errorMessage
                                return@Button
                            }
                        }
                        errorMessage = null
                        isCodeSentStep = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Enviar código", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿No tienes una cuenta? ", color = grayText, fontSize = 13.sp)
                    Text(
                        text = "Crear una cuenta",
                        color = Color(0xFF60A5FA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToRegister() }
                    )
                }
            } else {
                // --- PANTALLA 2: VERIFICAR CÓDIGO OTP DE 6 DÍGITOS ---
                Text(
                    text = "Revisa tu correo",
                    color = textColor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Ingresa el código de 6 dígitos enviado a", color = grayText, fontSize = 13.sp)
                    Text(emailOrPhone, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("✏️", fontSize = 12.sp, modifier = Modifier.clickable { isCodeSentStep = false })
                }

                // 6 BOXES CÓDIGO VERIFICACIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OtpBoxItem(value = otpDigit1, onValueChange = { otpDigit1 = it.take(1) }, modifier = Modifier.weight(1f))
                    OtpBoxItem(value = otpDigit2, onValueChange = { otpDigit2 = it.take(1) }, modifier = Modifier.weight(1f))
                    OtpBoxItem(value = otpDigit3, onValueChange = { otpDigit3 = it.take(1) }, modifier = Modifier.weight(1f))
                    OtpBoxItem(value = otpDigit4, onValueChange = { otpDigit4 = it.take(1) }, modifier = Modifier.weight(1f))
                    OtpBoxItem(value = otpDigit5, onValueChange = { otpDigit5 = it.take(1) }, modifier = Modifier.weight(1f))
                    OtpBoxItem(value = otpDigit6, onValueChange = { otpDigit6 = it.take(1) }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔒", fontSize = 12.sp)
                    Text("No compartas este código con otras personas.", color = grayText, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val (success, err) = AppStateRepository.loginUser(emailOrPhone, "password123")
                        if (success) {
                            onLoginSuccess()
                        } else {
                            errorMessage = err
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Continuar", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("¿No recibiste el correo? Reenviar código", color = Color(0xFF60A5FA), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = inputBgColor
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📩", fontSize = 14.sp)
                        Text(
                            text = "Revisa también la carpeta de spam o verifica que tu correo esté bien escrito.",
                            color = grayText,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comunidad UCB • Versión 1.0.0",
                color = Color(0xFF475569),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun OtpBoxItem(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF132238),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("Mariana Vargas") }
    var email by remember { mutableStateOf("mariana.vargas@ucb.edu.bo") }
    var phone by remember { mutableStateOf("+591 72910482") }
    var acceptedTerms by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val backgroundColor = Color(0xFF07111E)
    val inputBgColor = Color(0xFF132238)
    val primaryBlue = Color(0xFF2563EB)
    val textColor = Color.White
    val grayText = Color(0xFF94A3B8)
    val validateUcbEmailUseCase = remember { ValidateUcbEmailUseCase() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BARRA SUPERIOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("←", color = textColor, fontSize = 20.sp)
                }

                Text(
                    text = "Crear cuenta",
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "UCB",
                    color = grayText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ILUSTRACIÓN DEL SHIELD UCB
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFF1E293B), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF1E3A8A), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛡️", fontSize = 32.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Sé parte de UCB Segura",
                color = textColor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Text(
                text = "Crea tu cuenta para reportar y cuidar a nuestra comunidad.",
                color = grayText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            // CAMPOS DEL FORMULARIO DE REGISTRO
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                errorMessage?.let { msg ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF7F1D1D),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Text("Nombre completo", color = grayText, fontSize = 12.sp)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Mariana Vargas", color = Color(0xFF64748B)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    singleLine = true
                )

                Text("Correo electrónico institucional", color = grayText, fontSize = 12.sp)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("example@gmail.com", color = Color(0xFF64748B)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    singleLine = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("ⓘ", color = grayText, fontSize = 12.sp)
                    Text(
                        text = "Recibirás un código de 6 dígitos para verificar tu cuenta. No necesitas contraseña.",
                        color = grayText,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = primaryBlue)
                    )
                    Text(
                        text = "Acepto los Términos de uso y la Política de privacidad.",
                        color = grayText,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        val validation = validateUcbEmailUseCase(email)
                        if (!validation.isValid) {
                            errorMessage = validation.errorMessage
                            return@Button
                        }
                        if (!acceptedTerms) {
                            errorMessage = "Debes aceptar los Términos de uso."
                            return@Button
                        }

                        AppStateRepository.registerUser(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            studentIdCard = "784210",
                            password = "password123"
                        )
                        onRegisterSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Crear cuenta", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya tienes una cuenta? ", color = grayText, fontSize = 13.sp)
                Text(
                    text = "Iniciar sesión",
                    color = Color(0xFF60A5FA),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comunidad UCB • Versión 1.0.0",
                color = Color(0xFF475569),
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
