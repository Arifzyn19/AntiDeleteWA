package com.private.antideletewa.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.private.antideletewa.data.database.AppDatabase
import com.private.antideletewa.data.model.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service untuk menangkap notifikasi WhatsApp
 * HANYA menyimpan PRIVATE CHAT, GROUP CHAT diabaikan
 */
class WhatsAppNotificationListener : NotificationListenerService() {
    
    private val TAG = "WANotificationListener"
    private val WHATSAPP_PACKAGE = "com.whatsapp"
    
    // Coroutine scope untuk operasi database
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private lateinit var database: AppDatabase
    
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(applicationContext)
        Log.d(TAG, "Service created")
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        sbn?.let { notification ->
            // Filter hanya notifikasi dari WhatsApp
            if (notification.packageName == WHATSAPP_PACKAGE) {
                processWhatsAppNotification(notification)
            }
        }
    }
    
    /**
     * Proses notifikasi WhatsApp
     */
    private fun processWhatsAppNotification(sbn: StatusBarNotification) {
        try {
            val notification = sbn.notification
            val extras: Bundle? = notification.extras
            
            if (extras != null) {
                // Ambil title (nama kontak) dan text (isi pesan)
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
                
                // Cek apakah ini group conversation
                val isGroup = extras.getBoolean("android.isGroupConversation", false)
                
                Log.d(TAG, "Title: $title, Text: $text, IsGroup: $isGroup")
                
                // Validasi data
                if (title.isNullOrEmpty() || text.isNullOrEmpty()) {
                    Log.d(TAG, "Title atau text kosong, skip")
                    return
                }
                
                // FILTER 1: Skip jika group conversation
                if (isGroup) {
                    Log.d(TAG, "Group conversation detected, skip")
                    return
                }
                
                // FILTER 2: Skip jika text mengandung ":" (indikasi group message)
                // Format group: "Nama: pesan"
                if (text.contains(":")) {
                    Log.d(TAG, "Text contains ':', kemungkinan group message, skip")
                    return
                }
                
                // FILTER 3: Skip jika pesan mengandung kata "deleted" atau "dihapus"
                val lowerText = text.lowercase()
                if (lowerText.contains("deleted") || 
                    lowerText.contains("dihapus") ||
                    lowerText.contains("this message was deleted") ||
                    lowerText.contains("pesan ini telah dihapus")) {
                    Log.d(TAG, "Message contains delete indicator, skip")
                    return
                }
                
                // FILTER 4: Skip notifikasi sistem WhatsApp
                if (title.contains("WhatsApp") && 
                    (text.contains("messages") || text.contains("pesan"))) {
                    Log.d(TAG, "System notification, skip")
                    return
                }
                
                // Jika lolos semua filter, simpan ke database
                saveMessage(title, text)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing notification: ${e.message}", e)
        }
    }
    
    /**
     * Simpan pesan ke database
     */
    private fun saveMessage(sender: String, message: String) {
        serviceScope.launch {
            try {
                val messageEntity = MessageEntity(
                    sender = sender,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    packageName = WHATSAPP_PACKAGE
                )
                
                database.messageDao().insertMessage(messageEntity)
                Log.d(TAG, "Message saved: $sender - $message")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error saving message: ${e.message}", e)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
}
