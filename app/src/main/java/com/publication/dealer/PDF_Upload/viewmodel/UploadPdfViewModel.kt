package com.publication.dealer.PDF_Upload.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.PDF_Upload.model.PDFUploadRequest
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import retrofit2.Response

class UploadPdfViewModel(private val repository: Repository) : ViewModel() {

    fun uploadShopPdf(
        file: MultipartBody.Part
    ): LiveData<NetworkStates<Response<PDFUploadRequest>>> =
        liveData(Dispatchers.IO) {

            emit(NetworkStates.loading(null))
            try {
                val response = repository.uploadShopPdf(file)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
}
