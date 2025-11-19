package com.example.newapp.network.repo

import com.example.newapp.user_dashboard.model.DashBoardRequestModel
import com.example.newapp.user_dashboard.model.DashBoardResponseData
import com.example.newapp.login.model.LoginRequestModel
import com.example.newapp.login.model.LoginResponseModel
import com.example.newapp.network.retofit.BaseResponse
import com.example.newapp.network.api.ApiInterface

import retrofit2.Response

class Repository(private val api: ApiInterface) {


    // Login
    suspend fun login(loginData: LoginRequestModel): Response<BaseResponse<LoginResponseModel>> {
        return api.login(loginData)
    }

    suspend fun dashBoardData(dashBoardRequestModel: DashBoardRequestModel): Response<BaseResponse<List<DashBoardResponseData>>> {
        return api.dashBoardData(dashBoardRequestModel)
    }
}
