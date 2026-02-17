package com.adwa.antidelete.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.adwa.antidelete.data.database.AppDatabase
import com.adwa.antidelete.data.model.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service untuk menangkap notifikasi WhatsApp & WhatsApp Business
 * HANYA menyimpan PRIVATE CHAT, GROUP CHAT diabaikan
 */
class WhatsAppNotificationListener : NotificationListenerService() {

    private val TAG = "WANotifListener"

    // Daftar package yang didukung
    private val SUPPORTED_PACKAGES = setOf(
        "com.whatsapp",       // WhatsApp biasa
        "com.whatsapp.w4b"   // WhatsApp Business
    )

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(applicationContext)
        Log.d(TAG, "Service started – monitoring: $SUPPORTED_PACKAGES")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            if (it.packageName in SUPPORTED_PACKAGES) {
                processNotification(it)
            }
        }
    }

    private fun processNotification(sbn: StatusBarNotification) {
        try {
            val extras: Bundle? = sbn.notification.extras ?: return

            val title  = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            val text   = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            val isGroup = extras.getBoolean("android.isGroupConversation", false)

            Log.d(TAG, "[${sbn.packageName}] title=$title | text=$text | group=$isGroup")

            if (title.isNullOrBlank() || text.isNullOrBlank()) return

            // FILTER 1: Group conversation flag
            if (isGroup) { Log.d(TAG, "SKIP group flag"); return }

            // FILTER 2: "Nama: pesan" format → group / broadcast
            if (text.contains(":")) { Log.d(TAG, "SKIP colon"); return }

            // FILTER 3: Delete indicators
            val lower = text.lowercase()
            if (lower.contains("this message was deleted") ||
                lower.contains("deleted this message") ||
                lower.contains("pesan ini telah dihapus") ||
                lower.contains("menghapus pesan ini")) {
                Log.d(TAG, "SKIP deleted"); return
            }

            // FILTER 4: Summary notification ("5 new messages")
            if (text.matches(Regex("\\d+ (new messages?|pesan baru).*", RegexOption.IGNORE_CASE))) {
                Log.d(TAG, "SKIP summary"); return
            }

            // Lolos semua filter → simpan
            saveMessage(title, text, sbn.packageName)

        } catch (e: Exception) {
            Log.e(TAG, "Process error: ${e.message}", e)
        }
    }

    private fun saveMessage(sender: String, message: String, packageName: String) {
        serviceScope.launch {
            try {
                database.messageDao().insertMessage(
                    MessageEntity(
                        sender      = sender,
                        message     = message,
                        timestamp   = System.currentTimeMillis(),
                        packageName = packageName
                    )
                )
                Log.d(TAG, "SAVED [$packageName] $sender: $message")
            } catch (e: Exception) {
                Log.e(TAG, "DB error: ${e.message}", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
}
