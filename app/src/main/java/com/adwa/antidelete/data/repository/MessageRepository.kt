package com.adwa.antidelete.data.repository

import androidx.lifecycle.LiveData
import com.adwa.antidelete.data.dao.MessageDao
import com.adwa.antidelete.data.model.MessageEntity

/**
 * Repository untuk mengelola operasi data pesan
 * Menyediakan abstraksi antara ViewModel dan data source
 */
class MessageRepository(private val messageDao: MessageDao) {
    
    // LiveData untuk semua pesan
    val allMessages: LiveData<List<MessageEntity>> = messageDao.getAllMessages()
    
    /**
     * Insert pesan baru (dipanggil dari NotificationListener)
     */
    suspend fun insertMessage(message: MessageEntity) {
        messageDao.insertMessage(message)
    }
    
    /**
     * Get pesan berdasarkan sender
     */
    fun getMessagesBySender(sender: String): LiveData<List<MessageEntity>> {
        return messageDao.getMessagesBySender(sender)
    }
    
    /**
     * Hapus pesan dari sender tertentu
     */
    suspend fun deleteMessagesBySender(sender: String) {
        messageDao.deleteMessagesBySender(sender)
    }
    
    /**
     * Hapus semua pesan
     */
    suspend fun deleteAllMessages() {
        messageDao.deleteAllMessages()
    }
    
    /**
     * Search pesan
     */
    fun searchMessages(keyword: String): LiveData<List<MessageEntity>> {
        return messageDao.searchMessages(keyword)
    }
    
    /**
     * Get semua sender unik
     */
    fun getAllSenders(): LiveData<List<String>> {
        return messageDao.getAllSenders()
    }
}
