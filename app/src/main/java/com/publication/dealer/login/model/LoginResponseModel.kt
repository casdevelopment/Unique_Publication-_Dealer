package com.publication.dealer.login.model

data class LoginResponseModel(
    val userId: String?=null,
    val userName: String?=null,
    val userType: String?=null,
    val partyCode: Int?=null,
    val sellerType: String?=null,
    val account_ID: String?=null,

    val token: String?=null,
    val mobileNumber: String?=null,

    val account_Name: String?=null,
    val address: String?=null,
    val partyGroup: String?=null

)

