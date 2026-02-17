package com.adwa.antidelete.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.adwa.antidelete.data.database.AppDatabase
import com.adwa.antidelete.data.model.MessageEntity
import com.adwa.antidelete.data.repository.MessageRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk MainActivity
 * Mengelola data pesan dan operasi UI
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: MessageRepository
    
    // LiveData untuk semua pesan
    val allMessages: LiveData<List<MessageEntity>>
    
    init {
        val messageDao = AppDatabase.getDatabase(application).messageDao()
        repository = MessageRepository(messageDao)
        allMessages = repository.allMessages
    }
    
    /**
     * Insert pesan baru (jika perlu manual insert)
     */
    fun insertMessage(message: MessageEntity) = viewModelScope.launch {
        repository.insertMessage(message)
    }
    
    /**
     * Get pesan berdasarkan sender
     */
    fun getMessagesBySender(sender: String): LiveData<List<MessageEntity>> {
        return repository.getMessagesBySender(sender)
    }
    
    /**
     * Hapus pesan dari sender tertentu
     */
    fun deleteMessagesBySender(sender: String) = viewModelScope.launch {
        repository.deleteMessagesBySender(sender)
    }
    
    /**
     * Hapus semua pesan
     */
    fun deleteAllMessages() = viewModelScope.launch {
        repository.deleteAllMessages()
    }
    
    /**
     * Search pesan
     */
    fun searchMessages(keyword: String): LiveData<List<MessageEntity>> {
        return repository.searchMessages(keyword)
    }
}
