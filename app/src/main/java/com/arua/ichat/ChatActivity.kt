package com.arua.ichat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.arua.ichat.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_USERNAME = "extra_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- THIS IS THE FIX ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply the top inset as padding to the Toolbar
            binding.toolbar.updatePadding(top = systemBars.top)

            // Apply the bottom inset as padding to the message input layout
            binding.inputLayout.updatePadding(bottom = systemBars.bottom)

            // Consume the insets
            WindowInsetsCompat.CONSUMED.consumeSystemWindowInsets()
        }
        // --- END OF FIX ---

        val username = intent.getStringExtra(EXTRA_USERNAME)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = username ?: "Chat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // We will add message sending and receiving logic here later
    }
}