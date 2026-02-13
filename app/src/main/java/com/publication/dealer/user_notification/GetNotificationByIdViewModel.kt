package com.publication.dealer.user_notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.admin_notification.model.BroadCastRequestmodel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class GetNotificationByIdViewModel(private val repository: Repository) : ViewModel() {

    fun getNotificationById(id: String): LiveData<NetworkStates<Response<BaseResponse<List<BroadCastRequestmodel>>>>> =
        liveData(Dispatchers.IO) {

            emit(NetworkStates.loading(null))

            try {
                val response = repository.getNotificationById(id)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
}
