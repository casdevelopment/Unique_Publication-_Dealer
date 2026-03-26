package com.publication.dealer.admin_branding.model

data class UserResponseModel(
    val UserId: String?,
    val UserName: String?,
    val Password: String?,
    val PartyCode: Int?,
    val isActive: Boolean?,
    val Addeddate: String?,
    val UserType: String?,
    val Account_ID: String?,
    val Account_Name: String?,
    val PartyGroup: String?,
    val Address: String?,
    val MobileNumber: String?,
    val City: String?,
    val District: String?,
    val Town: String?,
    val Province: String?,
    val shopimageurl: String?,
    val MobileNumber2: String?,
    val MobileNumber3: String?,
    val PostalCode: String?,
    val Purchaser: String?,
    val Purchaser_Phone: String?
)

data class UserBrandingModel(
    val UserID: String?,
    val MainHordingURL: String?,
    val pop: String?,
    val ShelfTalker: String?,
    val CounterTop: String?,
    val other: String?
)