package com.arua.ichat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arua.ichat.databinding.ItemChatBinding
import com.arua.ichat.network.FullChatResponse
import com.arua.ichat.network.TokenManager
import com.bumptech.glide.Glide

class ChatAdapter(
    private val chats: List<FullChatResponse>,
    private val onItemClick: (FullChatResponse) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: FullChatResponse) {
            val currentUserId = TokenManager.getUserId(itemView.context)

            val displayName: String
            var displayPicUrl: String

            if (chat.isGroupChat) {
                displayName = chat.chatName ?: "Group Chat"
                displayPicUrl = "https://icon-library.com/images/group-icon-png/group-icon-png-10.jpg" // Placeholder
            } else {
                val otherUser = chat.users.find { it._id != currentUserId }
                displayName = otherUser?.username ?: "Unknown User"
                displayPicUrl = otherUser?.pic ?: ""
            }

            binding.usernameTextView.text = displayName
            binding.latestMessageTextView.text = chat.latestMessage?.content ?: "No messages yet"

            // FIX: Check if the URL is absolute or relative
            if (!displayPicUrl.startsWith("http")) {
                displayPicUrl = "http://104.225.141.13:5000$displayPicUrl"
            }

            Glide.with(itemView.context)
                .load(displayPicUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.profileImageView)

            itemView.setOnClickListener {
                onItemClick(chat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size
}