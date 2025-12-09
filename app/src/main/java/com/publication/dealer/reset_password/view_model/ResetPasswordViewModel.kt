package com.publication.dealer.reset_password.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.create_user.model.SignUpResponseModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class ResetPasswordViewModel(private val repository: Repository) : ViewModel() {

    fun resetPassword(request: ResetPasswordRequest): LiveData<NetworkStates<Response<BaseResponse<Boolean>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.resetPassword(request)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }


}
