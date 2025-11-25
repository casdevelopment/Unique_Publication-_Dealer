package com.publication.dealer.network.repo

import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.network.api.ApiInterface
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import okhttp3.ResponseBody

import retrofit2.Response

class Repository(private val api: ApiInterface) {


    // Login
    suspend fun login(loginData: LoginRequestModel): Response<BaseResponse<LoginResponseModel>> {
        return api.login(loginData)
    }

    suspend fun dashBoardData(dashBoardRequestModel: DashBoardRequestModel): Response<BaseResponse<List<DashBoardResponseData>>> {
        return api.dashBoardData(dashBoardRequestModel)
    }

    suspend fun signUp(signUpRequest: SignUpRequestModel): Response<ResponseBody> {
        return api.signUp(signUpRequest)
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Response<ResponseBody> {
        return api.resetPassword(request)
    }

    suspend fun inactivateUser(request: InactivateUserRequest): Response<ResponseBody> {
        return api.inactivateUser(request)
    }



}
