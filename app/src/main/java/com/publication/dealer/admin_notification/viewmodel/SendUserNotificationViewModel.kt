package com.publication.dealer.admin_notification.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.admin_notification.model.SendUserNotificationRequestmodel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class SendUserNotificationViewModel(private val repository: Repository) : ViewModel() {

    fun sendUserNotification(request: SendUserNotificationRequestmodel): LiveData<NetworkStates<Response<BaseResponse<Boolean>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.sendUserNotification(request)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
}