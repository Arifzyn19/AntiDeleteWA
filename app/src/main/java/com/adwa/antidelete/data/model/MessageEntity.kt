package com.adwa.antidelete.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk menyimpan pesan WhatsApp yang ditangkap dari notifikasi
 * Hanya menyimpan private chat (1-to-1), group chat diabaikan
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Nama kontak pengirim
    val sender: String,
    
    // Isi pesan
    val message: String,
    
    // Timestamp dalam milliseconds
    val timestamp: Long = System.currentTimeMillis(),
    
    // Package name untuk validasi (default WhatsApp)
    val packageName: String = "com.whatsapp"
)
