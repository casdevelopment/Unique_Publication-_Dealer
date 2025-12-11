package com.publication.dealer.update_user_password.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.update_user_password.model.UpdateUserPasswordRequestModel
import kotlinx.coroutines.Dispatchers
import retrofit2.Response


class UpdateUserPasswordViewModel(private val repository: Repository) : ViewModel() {

    fun updatePassword(updatePasswordRequest: UpdateUserPasswordRequestModel): LiveData<NetworkStates<Response<BaseResponse<Boolean>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.updatePassword(updatePasswordRequest)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
}