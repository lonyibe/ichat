package com.arua.ichat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arua.ichat.fragments.MyChatsFragment
import com.arua.ichat.fragments.SearchUserFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyChatsFragment()
            1 -> SearchUserFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}