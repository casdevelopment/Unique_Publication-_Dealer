package com.publication.dealer.update_user_password.model

data class UpdateUserPasswordRequestModel (

    val  userId :  String,
    val  username :  String,
    val  password :  String,
    val  newPassword :  String,
    val  fcmToken :  String,
    )