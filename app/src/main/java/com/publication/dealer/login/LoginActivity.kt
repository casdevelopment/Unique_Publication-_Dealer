package com.publication.dealer.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

import com.google.gson.Gson
import com.publication.dealer.SessionManager
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.databinding.ActivityLoginBinding
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.login.viewmodel.LoginViewModel
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private val sessionManager: SessionManager by inject()

    private lateinit var binding: ActivityLoginBinding
    private var fcmToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get FCM token as early as possible
        getFCMToken()

        binding.loginBtn.setOnClickListener {
            if (validateInputs()) {
                callLoginApi()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        if (binding.etUserId.text.toString().trim().isEmpty()) {
            binding.etUserId.error = "UserId required"
            valid = false
        }
        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPassword.error = "Password required"
            valid = false
        }
        return valid
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM_TOKEN", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                fcmToken = task.result
                Log.d("FCM_TOKEN", fcmToken ?: "Token is null")
            }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FCM_PERMISSION", "Granted")
            } else {
                Log.d("FCM_PERMISSION", "Denied")
            }
        }
    }

    private fun callLoginApi() {
        Log.d("FCM_TOKEN--", "callLoginApi fcmToken "+fcmToken)
        // Ensure token is available; if not, send empty string
        val loginRequest = LoginRequestModel(
            userId = binding.etUserId.text.toString().trim(),
            password = binding.etPassword.text.toString().trim(),
            fcmToken = fcmToken ?: ""
        )

        viewModel.login(loginRequest).observe(this) { apiResponse ->
            when (apiResponse.status) {

                Status.LOADING -> AppUtil.startLoader(this@LoginActivity)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val retrofitResponse = apiResponse.data  // Response<BaseResponse<LoginResponseModel>>
                    if (retrofitResponse != null) {
                        if (retrofitResponse.isSuccessful && retrofitResponse.body() != null) {
                            val baseResponse = retrofitResponse.body()!!

                            Toast.makeText(this, baseResponse.message, Toast.LENGTH_SHORT).show()

                            if (baseResponse.success) {
                                val loginResponse = baseResponse.data
                                if (loginResponse != null) {

                                    // Save token & user info
                                    sessionManager.saveToken(AppConstants.Bearer + " " + loginResponse.token)
                                    AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
                                    sessionManager.userInfo(Gson().toJson(loginResponse))
                                    AppConstants.userData = Gson().fromJson(
                                        sessionManager.getUserInfo(),
                                        LoginResponseModel::class.java
                                    )

                                    // ✅ Request notification permission
                                    requestNotificationPermissionIfNeeded()
                                  //  subscribeToGlobalTopic()



                                    when (AppConstants.userData!!.userType) {

                                        "User" -> {
                                            FirebaseMessaging.getInstance().subscribeToTopic("all")
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d("FCM_TOPIC", "User subscribed to topic: all")
                                                    }
                                                }

                                            startActivity(Intent(this, GraphDashBoardActivity::class.java))
                                        }

                                        "Admin" -> {
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Log.d("FCM_TOPIC", "Admin unsubscribed from topic: all")
                                                    }
                                                }

                                            startActivity(Intent(this, AdminDashBoardActivity::class.java))
                                        }
                                    }

                                    finish()
                                }
                            }

                        } else {
                            val errorJson = retrofitResponse.errorBody()?.string()
                            val errorResponse = try {
                                Gson().fromJson(errorJson, BaseResponse::class.java)
                            } catch (e: Exception) {
                                null
                            }
                            val msg = errorResponse?.message ?: "Login failed"
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, apiResponse.message ?: "Network Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun subscribeToGlobalTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_TOPIC", "Subscribed to topic: all")
                } else {
                    Log.e("FCM_TOPIC", "Subscription failed", task.exception)
                }
            }
    }

}
