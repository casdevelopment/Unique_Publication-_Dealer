package com.publication.dealer.sales.model

data class SalesDetailResponseModel(
    val sno: Long,
    val saleDate: String,
    val itemCode: Long,
    val itemName: String,
    val qty: Int,
    val rate: Double,
    val printedPrice: Double,
    val discount: Double,
    val amount: Double
)


