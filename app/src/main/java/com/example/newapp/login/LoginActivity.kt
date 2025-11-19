package com.example.newapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newapp.dashboard.MainDashBoardActivity
import com.example.newapp.SessionManager
import com.example.newapp.databinding.ActivityLoginBinding
import com.example.newapp.login.model.LoginRequestModel
import com.example.newapp.network.Status
import com.example.newapp.login.viewmodel.LoginViewModel
import com.example.newapp.util.AppConstants

import com.example.newapp.util.AppUtil
import com.google.gson.Gson
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
        if (binding.etUsername.text.toString().trim().isEmpty()) {
            binding.etUsername.error = "Username required"

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
            username = binding.etUsername.text.toString().trim(),
            password = binding.etPassword.text.toString().trim(),
            fcmToken = "YOUR_FCM_TOKEN_HERE"
        )

        viewModel.login(loginRequest).observe(this) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this@LoginActivity)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val loginResponse = apiResponse.data?.body()?.data
                    if (loginResponse != null) {
                        Log.v("apiResponse", "loginResponse $loginResponse")
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        sessionManager.saveToken(AppConstants.Bearer + " " +  loginResponse.token)
                        AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
                        sessionManager.userInfo(loginResponse.toString())
                        val gson = Gson()
                       // AppConstants.userData  =gson.fromJson( sessionManager.getUserInfo(), LoginResponseModel::class.java)
                      //  Log.v("apiResponse", "userData $AppConstants.userData")
                        startActivity(Intent(this@LoginActivity, MainDashBoardActivity::class.java))
                        finish()
                    }

                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, "Login Failed: ${apiResponse.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
