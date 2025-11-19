package com.example.newapp.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.example.newapp.login.model.LoginRequestModel
import com.example.newapp.network.NetworkStates
import com.example.newapp.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.liveData
import com.example.newapp.login.model.LoginResponseModel
import com.example.newapp.network.retofit.BaseResponse
import retrofit2.Response

class DashBoardViewModel(private val repository: Repository) : ViewModel() {

    fun dashBoardData(dashBoardRequestModel: DashBoardRequestModel): LiveData<NetworkStates<Response<BaseResponse<List<DashBoardResponseData>>>>> {
        return liveData(Dispatchers.IO) {
            emit(NetworkStates.loading(null))
            try {
                emit(NetworkStates.success(data = repository.dashBoardData(dashBoardRequestModel)))
            } catch (e:Exception) {
                Log.v("callApiLedger", "callApiLedger Exception "+e.message)
                emit(NetworkStates.error(data = null,message = e.message ?: "Something went wrong"))
            }
        }
    }
}
