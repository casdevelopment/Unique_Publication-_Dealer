package com.publication.dealer.network.retofit

data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val success: Boolean,
    val data: T?
)

data class BaseResponseCatalog<T>(
    val count: Int,
    val success: Boolean,
    val data: T?
)

