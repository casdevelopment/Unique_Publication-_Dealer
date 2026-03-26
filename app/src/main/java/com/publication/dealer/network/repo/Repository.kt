package com.publication.dealer.network.repo

import com.publication.dealer.PDF_Upload.model.PDFUploadRequest
import com.publication.dealer.admin_branding.model.UserBrandingModel
import com.publication.dealer.admin_branding.model.UserResponseModel
import com.publication.dealer.admin_catalogue.model.AddCatalogRequestModel
import com.publication.dealer.admin_notification.model.BroadCastRequestmodel
import com.publication.dealer.admin_notification.model.SendUserNotificationRequestmodel
import com.publication.dealer.branding.model.AddBrandingModel
import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.network.api.ApiInterface
import com.publication.dealer.network.retofit.BaseResponseCatalog
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import com.publication.dealer.sales.model.SalesDetailResponseModel
import com.publication.dealer.sales.model.SalesRequestModel
import com.publication.dealer.sales.model.SalesResponseModel
import com.publication.dealer.update_user_password.model.UpdateUserPasswordRequestModel
import com.publication.dealer.update_user_profile.model.UpdateUserModel
import com.publication.dealer.user_dashboard.model.ImageUploadResponceModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import java.io.File

class Repository(private val api: ApiInterface) {


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

    suspend fun sales(salesRequest: SalesRequestModel): Response<BaseResponse<List<SalesResponseModel>>> {
        return api.sales(salesRequest)
    }

    suspend fun salesDetails(sno: Long): Response<BaseResponse<List<SalesDetailResponseModel>>> {
        return api.salesDetails(sno)
    }

//    suspend fun uploadPdf(request: PDFUploadRequest): Response<BaseResponse<Boolean>> {
//        return api.uploadPdf(request)
//    }

    suspend fun uploadShopPdf(
        file: MultipartBody.Part
    ): Response<PDFUploadRequest> {
        return api.uploadShopPdf(file)
    }

    suspend fun createCatalog(request: AddCatalogRequestModel): Response<BaseResponse<Any>> {
        return api.createCatalog(request)
    }

    suspend fun getCatalog(): Response<BaseResponseCatalog<List<AddCatalogRequestModel>>> {
        return api.getCatalog()
    }

    suspend fun getCatalogById(catalogId: Int): Response<BaseResponseCatalog<AddCatalogRequestModel>> {
        return api.getCatalogById(catalogId)
    }

    suspend fun updateCatalog(request: AddCatalogRequestModel): Response<BaseResponse<Any>> {
        return api.updateCatalog(request)
    }

    suspend fun broadCastNotification(request: BroadCastRequestmodel): Response<BaseResponse<Boolean>> {
        return api.broadCastNotification(request)
    }

    suspend fun sendUserNotification(request: SendUserNotificationRequestmodel): Response<BaseResponse<Boolean>> {
        return api.sendUserNotification(request)
    }


    suspend fun getNotificationById(id: String): Response<BaseResponse<List<BroadCastRequestmodel>>> {
        return api.getNotificationById(id)
    }


    suspend fun getAllUsers(userID: String): Response<BaseResponse<List<UserResponseModel>>> {
        return api.getAllUsers(userID)
    }

    suspend fun addBranding(
        userIdBody: RequestBody? = null,
        type: RequestBody? = null,
        file: MultipartBody.Part? = null
    ): Response<AddBrandingModel> {
        return api.addBranding(userIdBody, type, file)
    }


    suspend fun getUserBranding(
        userid: String
    ): Response<BaseResponse<List<UserBrandingModel>>> {
        return api.getUserBranding(userid)
    }



}
