package bo.edu.ucb.segura.core.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data class VerifyCode(val email: String) : Screen("verify_code/$email") {
        companion object {
            const val ROUTE = "verify_code/{email}"
        }
    }
    data object Home : Screen("home")
    data object ReportIncident : Screen("report_incident")
    data class ReportSuccess(val reportId: String) : Screen("report_success/$reportId") {
        companion object {
            const val ROUTE = "report_success/{reportId}"
        }
    }
    data object MyReports : Screen("my_reports")
    data object UserProfile : Screen("user_profile")
    data object PanicAlert : Screen("panic_alert")
}
