package com.example.newapp.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.newapp.dashboard.MainDashBoardActivity
import com.example.newapp.R
import com.example.newapp.SessionManager
import com.example.newapp.login.LoginActivity
import com.example.newapp.util.AppConstants
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val sessionManager: SessionManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)




        lifecycleScope.launch {
            delay(2000)
            if (sessionManager.isLoggedIn()) {
                Log.v("isLoggedIn()", "isLoggedIn() "+sessionManager.isLoggedIn())

                val gson = Gson()
               // AppConstants.userData =gson.fromJson( sessionManager.getUserInfo(), LoginResponseModel::class.java)
                AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
                Log.v("AUTH_TOKEN", "  AppConstants.AUTH_TOKEN "+  AppConstants.AUTH_TOKEN)
                startActivity(Intent(this@SplashActivity, MainDashBoardActivity::class.java))
                finish()

            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }


    }
}