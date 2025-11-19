package com.publication.dealer.user_dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.liveData
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
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
