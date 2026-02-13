package com.publication.dealer.admin_notification.model

data class SendUserNotificationRequestmodel(

    val userid: String,
    val title: String,
    val message: String,
    val imageurl: String

)