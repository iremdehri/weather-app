package com.kampplus.hava.model

data class GrokRequest(
    val model: String = "llama3-8b-8192",
    val messages: List<GrokMessage>
)

data class GrokMessage(
    val role: String,
    val content: String
)

data class GrokResponse(
    val choices: List<GrokChoice>
)

data class GrokChoice(
    val message: GrokMessage
)
