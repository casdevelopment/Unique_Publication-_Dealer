package com.example.newapp.login.model

data class LoginRequestModel(
    val username: String,
    val password: String,
    val fcmToken: String
)
