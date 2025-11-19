package com.example.newapp.login.model

data class LoginResponseModel(
    val userId: String,
    val userName: String,
    val userType: String,
    val partyCode: Int,
    val sellerType: String,
    val account_ID: String,
    val token: String)

data class UserData(
    val userId: String,
    val userName: String,
    val userType: String,
    val partyCode: Int,
    val sellerType: String,
    val account_ID: String,
    val token: String
)
