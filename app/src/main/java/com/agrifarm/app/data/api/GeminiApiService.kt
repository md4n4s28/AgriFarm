package com.agrifarm.app.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiApiService {
    @POST("openai/v1/chat/completions")
    suspend fun generateContent(
        @Header("Authorization") auth: String,
        @Body request: GroqRequest
    ): GroqResponse
}

data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<Message>,
    val temperature: Float = 0.7f
)

data class Message(
    val role: String,
    val content: String
)

data class GroqResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
