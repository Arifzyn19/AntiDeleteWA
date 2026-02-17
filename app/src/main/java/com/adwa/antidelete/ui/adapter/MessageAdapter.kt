package com.adwa.antidelete.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adwa.antidelete.R
import com.adwa.antidelete.data.model.MessageEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter untuk RecyclerView pesan
 * Menampilkan badge berbeda untuk WA biasa vs WA Business
 */
class MessageAdapter : ListAdapter<MessageEntity, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    var onItemClick: ((MessageEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
        holder.itemView.setOnClickListener { onItemClick?.invoke(message) }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderText: TextView  = itemView.findViewById(R.id.senderText)
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timeText: TextView    = itemView.findViewById(R.id.timeText)
        private val badgeText: TextView   = itemView.findViewById(R.id.badgeText)

        fun bind(message: MessageEntity) {
            senderText.text  = message.sender
            messageText.text = message.message
            timeText.text    = formatTime(message.timestamp)

            // Tampilkan badge WA Business vs WA biasa
            when (message.packageName) {
                "com.whatsapp.w4b" -> {
                    badgeText.text = "WA Business"
                    badgeText.setBackgroundColor(Color.parseColor("#1A6B3E"))
                    badgeText.visibility = View.VISIBLE
                }
                "com.whatsapp" -> {
                    badgeText.text = "WhatsApp"
                    badgeText.setBackgroundColor(Color.parseColor("#1DB954"))
                    badgeText.visibility = View.VISIBLE
                }
                else -> badgeText.visibility = View.GONE
            }
        }

        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity) =
            oldItem == newItem
    }
}
