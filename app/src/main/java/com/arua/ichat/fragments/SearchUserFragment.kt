package com.arua.ichat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arua.ichat.ChatActivity
import com.arua.ichat.databinding.FragmentSearchUserBinding
import com.arua.ichat.network.CreateChatRequest
import com.arua.ichat.network.RetrofitClient
import com.arua.ichat.network.TokenManager
import com.arua.ichat.network.User
import kotlinx.coroutines.launch

class SearchUserFragment : Fragment() {

    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        // Pass the onUserClicked function to the adapter
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
        // This function is called when a user item is clicked
        showLoading(true)
        val token = "Bearer ${TokenManager.getToken(requireContext())}"

        lifecycleScope.launch {
            try {
                // Call the backend to create or access the chat
                val response = RetrofitClient.instance.accessChat(token, CreateChatRequest(user._id))
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    // Chat access successful, now open the chat activity
                    val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                        putExtra(ChatActivity.EXTRA_USER_ID, user._id)
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