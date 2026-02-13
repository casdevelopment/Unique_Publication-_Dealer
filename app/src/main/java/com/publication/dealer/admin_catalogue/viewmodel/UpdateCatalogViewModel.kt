package com.publication.dealer.admin_catalogue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.admin_catalogue.model.AddCatalogRequestModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class UpdateCatalogViewModel(private val repository: Repository) : ViewModel() {

    fun updateCatalog(request: AddCatalogRequestModel): LiveData<NetworkStates<Response<BaseResponse<Any>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response: Response<BaseResponse<Any>> = repository.updateCatalog(request)
                emit(NetworkStates.success(data = response))
            } catch (e: Exception) {
                emit(NetworkStates.error(data = null, message = e.message ?: "Something went wrong"))
            }
        }
    }

}
