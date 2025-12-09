package com.publication.dealer.network.api

import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.create_user.model.SignUpResponseModel
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import okhttp3.ResponseBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {


    // Login API
    @POST("Dealer/login")
    suspend fun login(@Body loginRequest: LoginRequestModel): Response<BaseResponse<LoginResponseModel>>

    @POST("Dealer/get-ledger-report")
    suspend fun dashBoardData(@Body dashBoardRequestModel: DashBoardRequestModel): Response<BaseResponse<List<DashBoardResponseData>>>


    @POST("Dealer/create-user")
    suspend fun signUp(@Body signUpRequest: SignUpRequestModel): Response<BaseResponse<Boolean>>

    @POST("Dealer/resetuserpassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<BaseResponse<Boolean>>

    @POST("Dealer/inactivate-user")
    suspend fun inactivateUser(@Body request: InactivateUserRequest): Response<BaseResponse<Boolean>>




}
