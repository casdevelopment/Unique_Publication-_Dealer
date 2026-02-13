package com.publication.dealer.admin_catalogue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.admin_catalogue.model.AddCatalogRequestModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponseCatalog
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class GetCatalogByIdViewModel(private val repository: Repository) : ViewModel() {

    fun getCatalogById(catalogId: Int): LiveData<NetworkStates<Response<BaseResponseCatalog<AddCatalogRequestModel>>>> =
        liveData(Dispatchers.IO) {

            emit(NetworkStates.loading(null))

            try {
                val response = repository.getCatalogById(catalogId)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
}
