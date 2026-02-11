package com.publication.dealer.network.api

import com.publication.dealer.PDF_Upload.model.PDFUploadRequest
import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import com.publication.dealer.sales.model.SalesDetailResponseModel
import com.publication.dealer.sales.model.SalesRequestModel
import com.publication.dealer.sales.model.SalesResponseModel
import com.publication.dealer.update_user_password.model.UpdateUserPasswordRequestModel
import com.publication.dealer.update_user_profile.model.UpdateUserModel
import com.publication.dealer.user_dashboard.model.ImageUploadResponceModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    @POST("Dealer/update-user")
    suspend fun updateUser(@Body updateUserRequest: UpdateUserModel): Response<BaseResponse<Boolean>>


    @Multipart
    @POST("Dealer/upload-shop-image")
    suspend fun uploadUserImage(
        @Part("userId") userId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponceModel>

    @POST("Dealer/change-password")
    suspend fun updatePassword(@Body updatePasswordRequest: UpdateUserPasswordRequestModel): Response<BaseResponse<Boolean>>


    @POST("Dealer/sales-summary")
    suspend fun sales(@Body salesRequest: SalesRequestModel): Response<BaseResponse<List<SalesResponseModel>>>

    @GET("Dealer/sales-detail/{sno}")
    suspend fun salesDetails(
        @Path("sno") sno: Long
    ): Response<BaseResponse<List<SalesDetailResponseModel>>>

//    @POST("Dealer/upload-shop-pdf")
//    suspend fun uploadPdf(@Body request: PDFUploadRequest): Response<BaseResponse<Boolean>>

    @Multipart
    @POST("Dealer/upload-shop-pdf")
    suspend fun uploadShopPdf(
        @Part file: MultipartBody.Part
    ): Response<BaseResponse<Any>>



}
