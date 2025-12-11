package com.publication.dealer.update_user_profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.update_user_profile.model.UpdateUserModel
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class UpdateUserViewModel(private val repository: Repository) : ViewModel() {

    fun updateUser(updateUserRequest: UpdateUserModel): LiveData<NetworkStates<Response<BaseResponse<Boolean>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.updateUser(updateUserRequest)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }
}
