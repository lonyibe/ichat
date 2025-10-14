package com.arua.ichat.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/users/signup")
    suspend fun signupUser(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("/api/users/login")
    suspend fun loginUser(@Body authRequest: AuthRequest): Response<AuthResponse>

}
