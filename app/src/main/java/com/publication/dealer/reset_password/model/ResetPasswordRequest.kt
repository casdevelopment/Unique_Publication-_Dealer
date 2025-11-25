package com.publication.dealer.reset_password.model

data class ResetPasswordRequest(
    val adminUserID: String?=null,
    val resetUserID: String?=null,

)