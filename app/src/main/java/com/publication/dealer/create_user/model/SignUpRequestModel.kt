package com.publication.dealer.create_user.model

data class SignUpRequestModel(

   // val profile : String,
    val userId: String,
    val userName: String,
    val partyCode: Int,
    val mobileNumber: String,
    val userType : String,
    val addedBy : String,
    val password: String,

//    val mobileNumber2: String?,
//    val mobileNumber3: String?,
//
//    val town: String,
//    val city: String,
//    val district: String,
//    val province: String,
//    val postalCode: Int,
//    val address: String,


)