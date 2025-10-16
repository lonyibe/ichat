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

// For User Search response and Chat members
data class User(
    val _id: String,
    val username: String,
    val pic: String
)

// For creating a new 1-on-1 chat
data class CreateChatRequest(
    val userId: String
)

// For the response when creating/accessing a chat
data class ChatResponse(
    val _id: String,
    val isGroupChat: Boolean,
    val users: List<User>
)

// For sending a new message
data class SendMessageRequest(
    val chatId: String,
    val content: String
)

// For representing a single message from both API and Socket
data class Message(
    val _id: String,
    val sender: User,
    val content: String,
    val chat: ChatResponse,
    val createdAt: String
)

// For the response when fetching the list of all chats
data class FullChatResponse(
    val _id: String,
    val chatName: String?,
    val isGroupChat: Boolean,
    val users: List<User>,
    val latestMessage: Message? // The latest message can be null if no messages have been sent
)