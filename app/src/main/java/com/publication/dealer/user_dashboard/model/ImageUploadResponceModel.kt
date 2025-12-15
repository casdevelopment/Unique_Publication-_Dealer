package com.publication.dealer.user_dashboard.model

import com.google.gson.annotations.SerializedName

data class ImageUploadResponceModel (


    val success: Boolean,
    val message: String,
    @SerializedName("shopimageurl")
    val  shopimageurl: String?=null
)
