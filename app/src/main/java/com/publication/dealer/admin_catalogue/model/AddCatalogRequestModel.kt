package com.publication.dealer.admin_catalogue.model

data class AddCatalogRequestModel(
    val id: Int = 0,
    val boardName: String,
    val catalogName: String,
    val catalogURL: String,
    val catalogType: String
)