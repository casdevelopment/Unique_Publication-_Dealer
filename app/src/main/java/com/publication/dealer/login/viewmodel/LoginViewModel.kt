package com.publication.dealer.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.liveData
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import retrofit2.Response

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun login(loginRequest: LoginRequestModel): LiveData<NetworkStates<Response<BaseResponse<LoginResponseModel>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                emit(NetworkStates.success(data = repository.login(loginRequest)))
            } catch (e:Exception) {
                emit(NetworkStates.error(data = null,message = e.message ?: "Something went wrong"))
            }
        }
    }
}
