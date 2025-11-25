package com.publication.dealer.create_user.model

data class SignUpRequestModel(
    val userId: String,
    val userName: String,
    val partyCode: Int,
    val password: String,
    val mobileNumber: String
)