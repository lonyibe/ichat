package com.arua.ichat

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.arua.ichat.databinding.ActivityMainBinding
import com.arua.ichat.fragments.CallsFragment
import com.arua.ichat.fragments.ChatsFragment
import com.arua.ichat.fragments.GroupsFragment
import com.arua.ichat.fragments.SearchFragment // Import the new SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            replaceFragment(ChatsFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_chats -> {
                    replaceFragment(ChatsFragment())
                    true
                }
                R.id.navigation_groups -> {
                    replaceFragment(GroupsFragment())
                    true
                }
                R.id.navigation_calls -> {
                    replaceFragment(CallsFragment())
                    true
                }
                R.id.navigation_search -> {
                    replaceFragment(SearchFragment()) // Show the SearchFragment
                    true // Return true to show the item as selected
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}