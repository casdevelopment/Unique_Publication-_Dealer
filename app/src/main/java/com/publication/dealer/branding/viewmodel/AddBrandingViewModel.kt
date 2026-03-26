package com.publication.dealer.branding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.branding.model.AddBrandingModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AddBrandingViewModel(private val repository: Repository) : ViewModel() {

    fun addBranding(
        userIdBody : RequestBody? = null,
        type : RequestBody? = null,
        file: MultipartBody.Part? = null
    ): LiveData<NetworkStates<Response<AddBrandingModel>>> = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))
        try {
            val response = repository.addBranding(userIdBody, type, file)
            emit(NetworkStates.success(response))
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }
}
