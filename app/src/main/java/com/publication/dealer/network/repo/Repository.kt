package com.publication.dealer.network.repo

import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.create_user.model.SignUpResponseModel
import com.publication.dealer.image_upload.viewmodel.UploadImageViewModel
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.network.api.ApiInterface
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import com.publication.dealer.update_user_password.model.UpdateUserPasswordRequestModel
import com.publication.dealer.update_user_profile.model.UpdateUserModel
import com.publication.dealer.user_dashboard.model.ImageUploadResponceModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import java.io.File

class Repository(private val api: ApiInterface) {


    // Login
    suspend fun login(loginData: LoginRequestModel): Response<BaseResponse<LoginResponseModel>> {
        return api.login(loginData)
    }

    suspend fun dashBoardData(dashBoardRequestModel: DashBoardRequestModel): Response<BaseResponse<List<DashBoardResponseData>>> {
        return api.dashBoardData(dashBoardRequestModel)
    }

    suspend fun signUp(signUpRequest: SignUpRequestModel): Response<BaseResponse<Boolean>> {
        return api.signUp(signUpRequest)
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Response<BaseResponse<Boolean>> {
        return api.resetPassword(request)
    }

    suspend fun inactivateUser(request: InactivateUserRequest): Response<BaseResponse<Boolean>> {
        return api.inactivateUser(request)
    }

    suspend fun updateUser(updateUserRequest: UpdateUserModel): Response<BaseResponse<Boolean>> {
        return api.updateUser(updateUserRequest)
    }


    suspend fun uploadUserImage(userId: String, file: File): Response<ImageUploadResponceModel> {
        val userIdPart = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), file)
        )
        return api.uploadUserImage(userIdPart, filePart)
    }



    suspend fun updatePassword(updatePasswordRequest: UpdateUserPasswordRequestModel): Response<BaseResponse<Boolean>> {
        return api.updatePassword(updatePasswordRequest)
    }



}
