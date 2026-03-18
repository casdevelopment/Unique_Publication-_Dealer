package com.publication.dealer.login.model

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(


    var purchaser : String?=null,
    var purchaser_Phone : String?=null,

    val userId : String?=null,
    val userName : String?=null,
    val password: String?=null,
    val userType : String?=null,
    val partyCode: Int?=null,
    var remarks : String?=null,
    val account_ID: String?=null,
    val account_Name: String?=null,
    var address: String?=null,
    val partyGroup: String?=null,
    val sellerType: String?=null,
    var mobileNumber: String?=null,
    var mobileNumber2: String?=null,
    var mobileNumber3: String?=null,
    val token: String?=null,
   // val "modifiedBy": null,
   // val "modifiedTime": null,
   // val "isActive: true,
    val  addedDate: String?=null,
    val addedBy: String?=null,
    var city: String?=null,
    var district: String?=null,
    var town: String?=null,
    var postalCode: String?=null,
    var province: String?=null,
    @SerializedName("shopimageurl")
    var  shopimageurl: String?=null

)

