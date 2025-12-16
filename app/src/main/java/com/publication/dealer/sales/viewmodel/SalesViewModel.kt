package com.publication.dealer.sales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.sales.model.SalesRequestModel
import com.publication.dealer.sales.model.SalesResponseModel
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class SalesViewModel(private val repository: Repository) : ViewModel()  {

    fun sales(salesRequest: SalesRequestModel): LiveData<NetworkStates<Response<BaseResponse<List<SalesResponseModel>>>>>
    {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                emit(NetworkStates.success(data = repository.sales(salesRequest)))
            } catch (e:Exception) {
                emit(NetworkStates.error(data = null,message = e.message ?: "Something went wrong"))
            }
        }
    }
}