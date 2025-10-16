package com.arua.ichat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arua.ichat.databinding.ActivityChatBinding
import com.arua.ichat.network.*
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private var chatId: String? = null
    private lateinit var mSocket: Socket

    companion object {
        const val EXTRA_CHAT_ID = "extra_chat_id"
        const val EXTRA_USERNAME = "extra_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = systemBars.top)
            binding.inputLayout.updatePadding(bottom = systemBars.bottom)
            WindowInsetsCompat.CONSUMED.consumeSystemWindowInsets()
        }

        val username = intent.getStringExtra(EXTRA_USERNAME)
        chatId = intent.getStringExtra(EXTRA_CHAT_ID)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = username ?: "Chat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupSocket()

        if (chatId == null) {
            Toast.makeText(this, "Error: Chat ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchMessages(chatId!!)

        binding.sendButton.setOnClickListener {
            val content = binding.messageEditText.text.toString().trim()
            if (content.isNotEmpty()) {
                sendMessage(chatId!!, content)
                binding.messageEditText.text.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = TokenManager.getUserId(this)
        if (currentUserId == null) {
            Toast.makeText(this, "Authentication error!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        messageAdapter = MessageAdapter(messageList, currentUserId)
        binding.messagesRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }

    private fun setupSocket() {
        try {
            // Ensure you use your VPS IP here
            mSocket = IO.socket("http://104.225.141.13:5000")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Cannot connect to server", Toast.LENGTH_SHORT).show()
            return
        }

        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("Socket.IO", "Connected!")
            val userId = TokenManager.getUserId(this)
            val userJson = JSONObject().put("_id", userId)
            mSocket.emit("setup", userJson)
            chatId?.let { mSocket.emit("join chat", it) }
        }

        mSocket.on("message received") { args ->
            runOnUiThread {
                try {
                    val messageJson = args[0].toString()
                    val message = Gson().fromJson(messageJson, Message::class.java)
                    // Add message only if it belongs to the current chat
                    if (message.chat._id == chatId) {
                        messageAdapter.addMessage(message)
                        binding.messagesRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                } catch (e: Exception) {
                    Log.e("Socket.IO", "Error parsing received message", e)
                }
            }
        }

        mSocket.connect()
    }

    private fun fetchMessages(chatId: String) {
        val token = "Bearer ${TokenManager.getToken(this)}"
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMessages(token, chatId)
                if (response.isSuccessful && response.body() != null) {
                    messageList.clear()
                    messageList.addAll(response.body()!!)
                    messageAdapter.notifyDataSetChanged()
                    binding.messagesRecyclerView.scrollToPosition(messageList.size - 1)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, "Failed to load messages: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage(chatId: String, content: String) {
        val token = "Bearer ${TokenManager.getToken(this)}"
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.sendMessage(token, SendMessageRequest(chatId, content))
                if (response.isSuccessful && response.body() != null) {
                    val sentMessage = response.body()!!
                    // Add the message to our own UI instantly
                    messageAdapter.addMessage(sentMessage)
                    binding.messagesRecyclerView.scrollToPosition(messageList.size - 1)
                    // Emit the message via socket so the server can relay it to the other user
                    mSocket.emit("new message", Gson().toJson(sentMessage))
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}