package com.arua.ichat.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @Multipart
    @POST("api/users/signup")
    suspend fun signupUser(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part pic: MultipartBody.Part?
    ): Response<AuthResponse>

    @POST("api/users/login")
    suspend fun loginUser(@Body request: AuthRequest): Response<AuthResponse>

    @GET("api/users")
    suspend fun searchUsers(
        @Header("Authorization") token: String,
        @Query("search") searchTerm: String
    ): Response<List<User>>

    @POST("api/chat")
    suspend fun accessChat(
        @Header("Authorization") token: String,
        @Body request: CreateChatRequest
    ): Response<ChatResponse>
}