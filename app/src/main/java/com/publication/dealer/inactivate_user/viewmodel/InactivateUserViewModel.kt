package com.publication.dealer.inactivate_user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.network.NetworkStates
import com.publication.dealer.network.repo.Repository
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject


class InactivateUserViewModel(private val repo: Repository) : ViewModel() {

    fun inactivateUser(request: InactivateUserRequest) = liveData(Dispatchers.IO) {
        emit(NetworkStates.loading(null))

        try {
            val response = repo.inactivateUser(request)
            val jsonString = response.body()?.string() ?: response.errorBody()?.string() ?: "{}"
            val jsonObject = JSONObject(jsonString)

            // Extract title if it exists, otherwise fallback
            val title = jsonObject.optString("title", jsonObject.optString("message", "Something went wrong"))

            if (response.isSuccessful) {
                emit(NetworkStates.success(title))
            } else {
                emit(NetworkStates.error(null, title))
            }
        } catch (e: Exception) {
            emit(NetworkStates.error(null, e.message ?: "Error occurred"))
        }
    }
}

