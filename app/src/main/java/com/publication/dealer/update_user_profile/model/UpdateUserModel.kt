package com.publication.dealer.update_user_profile.model

data class UpdateUserModel (

    val userId : String,
    val mobileNumber: String,
    val mobileNumber2: String?,
    val mobileNumber3: String?,
    val remarks: String,
    val town: String,
    val city: String,
    val district: String,
    val province: String,
    val postalCode: String,
    val fcmToken: String,
    val modifiedBy: String,


)