package com.adwa.antidelete.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.adwa.antidelete.data.model.MessageEntity

/**
 * Data Access Object untuk operasi database pesan
 */
@Dao
interface MessageDao {
    
    /**
     * Insert pesan baru ke database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    /**
     * Get semua pesan, diurutkan descending berdasarkan timestamp (terbaru di atas)
     */
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): LiveData<List<MessageEntity>>
    
    /**
     * Get pesan berdasarkan sender tertentu
     */
    @Query("SELECT * FROM messages WHERE sender = :senderName ORDER BY timestamp DESC")
    fun getMessagesBySender(senderName: String): LiveData<List<MessageEntity>>
    
    /**
     * Hapus semua pesan dari sender tertentu
     */
    @Query("DELETE FROM messages WHERE sender = :senderName")
    suspend fun deleteMessagesBySender(senderName: String)
    
    /**
     * Hapus semua pesan
     */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
    
    /**
     * Search pesan berdasarkan keyword
     */
    @Query("SELECT * FROM messages WHERE message LIKE '%' || :keyword || '%' OR sender LIKE '%' || :keyword || '%' ORDER BY timestamp DESC")
    fun searchMessages(keyword: String): LiveData<List<MessageEntity>>
    
    /**
     * Get daftar semua sender unik
     */
    @Query("SELECT DISTINCT sender FROM messages ORDER BY sender ASC")
    fun getAllSenders(): LiveData<List<String>>
}
