package com.publication.dealer.login.model

data class LoginRequestModel(
    val userId: String,
    val password: String,
    val fcmToken: String
)
