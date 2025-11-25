package com.publication.dealer.create_user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class SignUpViewModel(private val repository: Repository) : ViewModel() {

//    fun signUp(signUpRequest: SignUpRequestModel) = liveData<NetworkStates<Response<ResponseBody>>>(Dispatchers.IO) {
//            emit(NetworkStates.loading(null))
//            try {
//                val response = repository.signUp(signUpRequest)
//                emit(NetworkStates.success(response))
//            } catch (e: Exception) {
//                emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
//            }
//        }

    fun signUp(signUpRequest: SignUpRequestModel) = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))

        try {
            val response = repository.signUp(signUpRequest)

            // Read body or errorBody depending on HTTP status
            val jsonString = if (response.isSuccessful) {
                response.body()?.string()
            } else {
                response.errorBody()?.string()
            } ?: "{}"

            val jsonObject = JSONObject(jsonString)
            val success = jsonObject.optBoolean("success", false)
            val message = jsonObject.optString("message", "Something went wrong")

            if (success) {
                emit(NetworkStates.success(message))
            } else {
                emit(NetworkStates.error(null, message))
            }

        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Something went wrong"))
        }
    }




}
