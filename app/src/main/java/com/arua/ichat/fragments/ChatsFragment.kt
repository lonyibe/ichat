package com.arua.ichat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arua.ichat.ChatActivity
import com.arua.ichat.ChatAdapter
import com.arua.ichat.databinding.FragmentChatsBinding
import com.arua.ichat.network.FullChatResponse
import com.arua.ichat.network.RetrofitClient
import com.arua.ichat.network.TokenManager
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    // Keep a reference to the list to clear it
    private val chatList = mutableListOf<FullChatResponse>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        setupRecyclerView()
        fetchUserChats()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatList, ::onChatClicked)
        binding.chatsRecyclerView.adapter = chatAdapter
    }

    private fun fetchUserChats() {
        showLoading(true)
        val token = "Bearer ${TokenManager.getToken(requireContext())}"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.fetchChats(token)
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val chats = response.body()!!
                    if (chats.isEmpty()) {
                        binding.emptyTextView.visibility = View.VISIBLE
                        binding.chatsRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyTextView.visibility = View.GONE
                        binding.chatsRecyclerView.visibility = View.VISIBLE
                        // Clear the list before adding new items to prevent duplicates
                        chatList.clear()
                        chatList.addAll(chats)
                        chatAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load chats", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onChatClicked(chat: FullChatResponse) {
        val currentUserId = TokenManager.getUserId(requireContext())
        // For 1-on-1 chats, find the other user to pass their name to the ChatActivity
        val otherUser = chat.users.find { it._id != currentUserId }

        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CHAT_ID, chat._id)
            val chatName = if (chat.isGroupChat) chat.chatName else otherUser?.username
            putExtra(ChatActivity.EXTRA_USERNAME, chatName ?: "Chat")
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // The onResume method has been removed to prevent automatic reloading and race conditions.
}