package com.arua.ichat.network

import com.google.gson.annotations.SerializedName

// Data class to represent the response from a successful login or signup
data class AuthResponse(
    @SerializedName("_id")
    val id: String,
    val username: String,
    val token: String
)

// Data class for the request body when logging in or signing up
data class AuthRequest(
    val username: String,
    val password: String
)
