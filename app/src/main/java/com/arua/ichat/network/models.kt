package com.arua.ichat.network

// For Login/Signup requests
data class AuthRequest(
    val username: String,
    val password: String
)

// For Login/Signup responses
data class AuthResponse(
    val _id: String,
    val username: String,
    val token: String,
    val pic: String
)

// For User Search response
data class User(
    val _id: String,
    val username: String,
    val pic: String
)

// For creating a new 1-on-1 chat
data class CreateChatRequest(
    val userId: String // The user ID of the person we want to chat with
)

// For the response when creating/accessing a chat
data class ChatResponse(
    val _id: String,
    val isGroupChat: Boolean,
    val users: List<User>
)