package com.example.chatkotlin.model

data class Chat(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val time: String = ""
)