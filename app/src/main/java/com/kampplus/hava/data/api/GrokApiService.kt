package com.kampplus.hava.data.api

import com.kampplus.hava.model.GrokRequest
import com.kampplus.hava.model.GrokResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GrokApiService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: GrokRequest
    ): GrokResponse
}
