package org.example.project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import bo.edu.ucb.segura.core.data.AppStateRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio FCM para recepción de notificaciones Push de Emergencia SOS tipo WhatsApp.
 * Crea canales de alta importancia con sonido y vibración, omitiendo la notificación
 * en el teléfono del mismo estudiante que envió la alerta.
 */
class UcbFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM recibido: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val dataMap: Map<String, String> = remoteMessage.data
        val senderUid = dataMap["uid_emisor"]
        val currentUserId = AppStateRepository.currentUser.value?.id ?: ""

        // 1. Ignorar la notificación si el emisor coincide con el usuario actual
        if (!senderUid.isNull_or_empty_str() && senderUid == currentUserId) {
            Log.d(TAG, "Notificación ignorada: El usuario actual ($currentUserId) es el emisor del SOS.")
            return
        }

        val nombre = dataMap["nombre"] ?: remoteMessage.notification?.title ?: "Estudiante UCB"
        val latStr = dataMap["lat"] ?: "-17.3714"
        val lngStr = dataMap["lng"] ?: "-66.1432"
        val alertaId = dataMap["alertaId"] ?: "sos_${System.currentTimeMillis()}"
        val mensaje = dataMap["mensaje"] ?: remoteMessage.notification?.body ?: "🚨 ¡ALERTA SOS! Estudiante $nombre necesita ayuda en el campus."

        mostrarNotificacionAltaImportancia(
            context = applicationContext,
            alertaId = alertaId,
            titulo = "🚨 ALERTA SOS - UCB SEGURA",
            mensaje = mensaje,
            lat = latStr.toDoubleOrNull() ?: -17.3714,
            lng = lngStr.toDoubleOrNull() ?: -66.1432
        )
    }

    private fun String?.isNull_or_empty_str(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    companion object {
        private const val TAG = "UcbFCMService"
        const val CHANNEL_ID = "emergencias_ucb_channel"
        const val CHANNEL_NAME = "Emergencias SOS UCB"

        fun mostrarNotificacionAltaImportancia(
            context: Context,
            alertaId: String,
            titulo: String,
            mensaje: String,
            lat: Double,
            lng: Double
        ) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Crear el canal de alta importancia (con sonido y vibración)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Canal prioritario para alertas de pánico SOS de la comunidad UCB"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 800)
                    setSound(soundUri, audioAttributes)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Intent para abrir MainActivity centrado en las coordenadas de la emergencia
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_LAT", lat)
                putExtra("EXTRA_LNG", lng)
                putExtra("EXTRA_OPEN_SOS", true)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                alertaId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val logoResId = context.resources.getIdentifier("logo_ucb", "drawable", context.packageName)
                .takeIf { it != 0 } ?: android.R.drawable.ic_dialog_alert

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(logoResId)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 500, 200, 500, 200, 800))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(alertaId.hashCode(), notificationBuilder.build())
        }
    }
}
