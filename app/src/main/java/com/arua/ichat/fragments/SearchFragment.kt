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
import com.arua.ichat.databinding.FragmentSearchBinding
import com.arua.ichat.network.CreateChatRequest
import com.arua.ichat.network.RetrofitClient
import com.arua.ichat.network.TokenManager
import com.arua.ichat.network.User
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        binding.searchButton.setOnClickListener {
            val searchTerm = binding.searchEditText.text.toString().trim()
            if (searchTerm.isNotEmpty()) {
                searchForUsers(searchTerm)
            }
        }
    }

    private fun searchForUsers(query: String) {
        showLoading(true)
        val token = "Bearer ${TokenManager.getToken(requireContext())}"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.searchUsers(token, query)
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    if (users.isEmpty()) {
                        binding.emptyTextView.visibility = View.VISIBLE
                        binding.usersRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyTextView.visibility = View.GONE
                        binding.usersRecyclerView.visibility = View.VISIBLE
                        binding.usersRecyclerView.adapter = UserAdapter(users, ::onUserClicked)
                    }
                } else {
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onUserClicked(user: User) {
        showLoading(true)
        val token = "Bearer ${TokenManager.getToken(requireContext())}"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.accessChat(token, CreateChatRequest(user._id))
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val chat = response.body()!!
                    val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                        // FIX: Refer to the constant via the ChatActivity class
                        putExtra(ChatActivity.EXTRA_CHAT_ID, chat._id)
                        putExtra(ChatActivity.EXTRA_USERNAME, user.username)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Could not start chat", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}