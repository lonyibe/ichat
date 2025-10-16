package com.arua.ichat.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arua.ichat.R
import com.arua.ichat.network.User
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class UserAdapter(
    private val users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val profileImageView: ShapeableImageView = itemView.findViewById(R.id.profileImageView)

        fun bind(user: User, onItemClick: (User) -> Unit) {
            usernameTextView.text = user.username
            itemView.setOnClickListener { onItemClick(user) }

            // FIX: Check if the URL is absolute or relative
            var displayPicUrl = user.pic
            if (!displayPicUrl.startsWith("http")) {
                displayPicUrl = "http://104.225.141.13:5000$displayPicUrl"
            }

            Glide.with(itemView.context)
                .load(displayPicUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(profileImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position], onItemClick)
    }

    override fun getItemCount() = users.size
}