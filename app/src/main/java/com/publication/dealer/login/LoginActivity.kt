package com.publication.dealer.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.publication.dealer.user_dashboard.MainDashBoardActivity
import com.publication.dealer.SessionManager
import com.publication.dealer.databinding.ActivityLoginBinding
import com.publication.dealer.login.model.LoginRequestModel
import com.publication.dealer.network.Status
import com.publication.dealer.login.viewmodel.LoginViewModel
import com.publication.dealer.util.AppConstants

import com.publication.dealer.util.AppUtil
import com.google.gson.Gson
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.retofit.BaseResponse
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private val sessionManager: SessionManager by inject()

    private lateinit var binding: ActivityLoginBinding

 /*   private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGoToSignup: TextView*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // setContentView(R.layout.activity_login)

     //   etUsername = findViewById(R.id.etUsername)
     //   etPassword = findViewById(R.id.etPassword)
     //   loginBtn = findViewById(R.id.loginBtn)
       // progressBar = findViewById(R.id.progressBar)
     //   tvGoToSignup = findViewById(R.id.GoToSignup)

        binding.loginBtn.setOnClickListener {
            if (validateInputs()) {
                callLoginApi()
            }
        }

//        tvGoToSignup.setOnClickListener {
//            startActivity(Intent(this, SignupActivity::class.java))
//        }
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

    private fun callLoginApi() {
        val loginRequest = LoginRequestModel(
            binding.etUserId.text.toString().trim(),
             binding.etPassword.text.toString().trim(),
            fcmToken = "YOUR_FCM_TOKEN_HERE"
        )

//        viewModel.login(loginRequest).observe(this) { apiResponse ->
//            when (apiResponse.status) {
//                Status.LOADING -> AppUtil.startLoader(this@LoginActivity)
//
//                Status.SUCCESS -> {
//                    AppUtil.stopLoader()
//                    val baseResponse = apiResponse.data?.body()
//                    if (baseResponse != null) {
//                        // Show the server message from response
//                        Toast.makeText(this, baseResponse.message, Toast.LENGTH_SHORT).show()
//
//                        if (baseResponse.success) {
//                            val loginResponse = baseResponse.data
//                            if (loginResponse != null) {
//                                // save token & user info
//                                sessionManager.saveToken(AppConstants.Bearer + " " +  loginResponse.token)
//                                AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
//                                sessionManager.userInfo(Gson().toJson(loginResponse))
//                                AppConstants.userData = Gson().fromJson(sessionManager.getUserInfo(), LoginResponseModel::class.java)
//
//                                // Navigate based on user type
//                                if (AppConstants.userData!!.userType == "User") {
//                                    startActivity(Intent(this@LoginActivity, MainDashBoardActivity::class.java))
//                                } else if (AppConstants.userData!!.userType == "Admin") {
//                                    startActivity(Intent(this@LoginActivity, AdminDashBoardActivity::class.java))
//                                }
//                                finish()
//                            }
//                        }
//                    }
//                }
//
//                Status.ERROR -> {
//                    AppUtil.stopLoader()
//                    // In case of network failure or exception
//                    Toast.makeText(this, "Login Failed: ${apiResponse.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }



        viewModel.login(loginRequest).observe(this) { apiResponse ->
            when (apiResponse.status) {

                Status.LOADING -> AppUtil.startLoader(this@LoginActivity)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val retrofitResponse = apiResponse.data  // Response<BaseResponse<LoginResponseModel>>
                    if (retrofitResponse != null) {
                        if (retrofitResponse.isSuccessful && retrofitResponse.body() != null) {
                            val baseResponse = retrofitResponse.body()!!

                            // ALWAYS show server message
                            Toast.makeText(this, baseResponse.message, Toast.LENGTH_SHORT).show()
                            // Only continue if backend success = true
                            if (baseResponse.success) {

                                val loginResponse = baseResponse.data
                                if (loginResponse != null) {

                                    // Save token & user info
                                    sessionManager.saveToken(AppConstants.Bearer + " " + loginResponse.token)
                                    AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
                                    sessionManager.userInfo(Gson().toJson(loginResponse))
                                    AppConstants.userData = Gson().fromJson(sessionManager.getUserInfo(), LoginResponseModel::class.java)

                                    // Navigate based on user type
                                    when (AppConstants.userData!!.userType) {
                                        "User" -> startActivity(Intent(this, MainDashBoardActivity::class.java))
                                        "Admin" -> startActivity(Intent(this, AdminDashBoardActivity::class.java))
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
                    Toast.makeText(this, apiResponse.message ?: "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}
