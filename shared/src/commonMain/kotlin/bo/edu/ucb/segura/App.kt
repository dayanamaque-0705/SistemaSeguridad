package bo.edu.ucb.segura

import androidx.compose.runtime.*
import bo.edu.ucb.segura.features.auth.ui.auth.LoginScreen
import bo.edu.ucb.segura.features.auth.ui.auth.RegisterScreen
import bo.edu.ucb.segura.features.home.ui.HomeScreen
import bo.edu.ucb.segura.features.reports.ui.reports.ReportIncidentScreen
import bo.edu.ucb.segura.features.reports.ui.reports.ReportSuccessScreen
import bo.edu.ucb.segura.features.reports.ui.reports.MyReportsScreen
import bo.edu.ucb.segura.features.panic_alert.ui.PanicAlertScreen
import bo.edu.ucb.segura.features.profile.ui.UserProfileScreen

// Definimos las rutas de todas las pantallas de UCB Segura
sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Home : Screen()
    object ReportIncident : Screen()
    object ReportSuccess : Screen()
    object MyReports : Screen()
    object PanicAlert : Screen()
    object Profile : Screen()
}

@Composable
fun App() {
    // Estado que guarda la pantalla actual en la que se encuentra el usuario (inicia en Login)
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    // Cambia de pantalla dinámicamente según la opción seleccionada
    when (currentScreen) {
        is Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.Home },
            onNavigateToRegister = { currentScreen = Screen.Register }
        )
        is Screen.Register -> RegisterScreen(
            onRegisterSuccess = { currentScreen = Screen.Home },
            onNavigateToLogin = { currentScreen = Screen.Login }
        )
        is Screen.Home -> HomeScreen(
            onNavigateToReport = { currentScreen = Screen.ReportIncident },
            onTriggerPanicAlert = { currentScreen = Screen.PanicAlert },
            onOpenProfile = { currentScreen = Screen.Profile },
            onOpenMyReports = { currentScreen = Screen.MyReports }
        )
        is Screen.ReportIncident -> ReportIncidentScreen(
            onNavigateBack = { currentScreen = Screen.Home },
            onSubmitSuccess = { currentScreen = Screen.ReportSuccess }
        )
        is Screen.ReportSuccess -> ReportSuccessScreen(
            onViewMoreReports = { currentScreen = Screen.MyReports },
            onReturnToMap = { currentScreen = Screen.Home }
        )
        is Screen.MyReports -> MyReportsScreen(
            onNavigateBack = { currentScreen = Screen.Home }
        )
        is Screen.PanicAlert ->PanicAlertScreen(
            onCancelAlert = { currentScreen = Screen.Home }
        )
        is Screen.Profile -> UserProfileScreen(
            onNavigateBack = { currentScreen = Screen.Home },
            onLogout = { currentScreen = Screen.Login }
        )
    }
}