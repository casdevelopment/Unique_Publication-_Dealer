package com.publication.dealer.admin_branding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.admin_branding.model.UserBrandingModel
import com.publication.dealer.admin_branding.model.UserResponseModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import com.publication.dealer.network.retofit.BaseResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class GetAdminBrandingViewModel(private val repository: Repository) : ViewModel() {


    fun getAllUsers(userId: String): LiveData<NetworkStates<Response<BaseResponse<List<UserResponseModel>>>>>
    {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                val response = repository.getAllUsers(userId)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
    }


    fun getUserBranding(
        userid: String
    ): LiveData<NetworkStates<Response<BaseResponse<List<UserBrandingModel>>>>> =
        liveData(Dispatchers.IO) {

            emit(NetworkStates.loading(null))

            try {
                val response = repository.getUserBranding(userid)
                emit(NetworkStates.success(response))
            } catch (e: Exception) {
                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
            }
        }
   }
