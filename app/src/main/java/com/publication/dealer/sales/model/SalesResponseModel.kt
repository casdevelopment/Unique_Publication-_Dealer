package com.publication.dealer.sales.model

data class SalesResponseModel(
    val sno: Long,
    val saleDate: String,
    val totalBooks: Int,
    val totalAmount: Int
)
