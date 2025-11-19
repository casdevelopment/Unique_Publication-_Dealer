package com.example.newapp.network.api

import com.example.newapp.dashboard.DashBoardRequestModel
import com.example.newapp.dashboard.DashBoardResponseData
import com.example.newapp.login.model.LoginRequestModel
import com.example.newapp.login.model.LoginResponseModel
import com.example.newapp.network.retofit.BaseResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {


    // Login API
    @POST("Dealer/login")
    suspend fun login(@Body loginRequest: LoginRequestModel): Response<BaseResponse<LoginResponseModel>>

    @POST("Dealer/get-ledger-report")
    suspend fun dashBoardData(@Body dashBoardRequestModel: DashBoardRequestModel): Response<BaseResponse<List<DashBoardResponseData>>>

    // Add more APIs here as needed
}
