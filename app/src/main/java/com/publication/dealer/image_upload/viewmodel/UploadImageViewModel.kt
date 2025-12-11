package com.publication.dealer.image_upload.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import java.io.File

class UploadImageViewModel(private val repository: Repository) : ViewModel() {

    fun uploadUserImage(userId: String, file: File): LiveData<NetworkStates<Response<BaseResponse<Boolean>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.uploadUserImage(userId, file)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
}