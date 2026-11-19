package bo.edu.ucb.segura.core.data

actual fun getCurrentTimeMillis(): Long = kotlin.js.Date.now().toLong()
