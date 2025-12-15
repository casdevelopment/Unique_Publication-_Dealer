package com.publication.dealer.create_user.model

data class SignUpRequestModel(

    val userId: String,
    val userName: String,
    val partyCode: Int,
    val mobileNumber: String,
    val userType : String,
    val addedBy : String,
    val password: String,


)