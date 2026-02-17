package com.private.antideletewa.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.private.antideletewa.R
import com.private.antideletewa.data.model.MessageEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter untuk RecyclerView pesan
 * Menggunakan ListAdapter dengan DiffUtil untuk performa optimal
 */
class MessageAdapter : ListAdapter<MessageEntity, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {
    
    // Click listener untuk item
    var onItemClick: ((MessageEntity) -> Unit)? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
        
        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(message)
        }
    }
    
    /**
     * ViewHolder untuk item pesan
     */
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderText: TextView = itemView.findViewById(R.id.senderText)
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        
        fun bind(message: MessageEntity) {
            senderText.text = message.sender
            messageText.text = message.message
            timeText.text = formatTime(message.timestamp)
        }
        
        /**
         * Format timestamp ke format jam yang mudah dibaca
         */
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
    
    /**
     * DiffUtil untuk efisiensi update RecyclerView
     */
    class MessageDiffCallback : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem == newItem
        }
    }
}
