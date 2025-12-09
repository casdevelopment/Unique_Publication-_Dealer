package com.publication.dealer.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.publication.dealer.user_dashboard.MainDashBoardActivity
import com.publication.dealer.R
import com.publication.dealer.SessionManager
import com.publication.dealer.login.LoginActivity
import com.publication.dealer.util.AppConstants
import com.google.gson.Gson
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val sessionManager: SessionManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)



//        val mockUser = LoginResponseModel(
//            userId = "12345",
//            userName = "Basit Ali",
//            userType = "Admin",
//            partyCode = 101,
//            sellerType = "Dealer",
//            account_ID = "ACC123",
//            token = "mock_token",
//            mobileNumber = "+92 300 3145451082"  // if you want to show phone number
//        )
//
//        sessionManager.saveToken(AppConstants.Bearer + " " +  mockUser.token)
//        AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
//
//        // saving user info
//        sessionManager.userInfo(Gson().toJson(mockUser))


        lifecycleScope.launch {
            delay(2000)
            if (sessionManager.isLoggedIn()) {
                // setting token
                AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()

                //  getting user info
                AppConstants.userData =Gson().fromJson( sessionManager.getUserInfo(), LoginResponseModel::class.java)

                if(AppConstants.userData!!.userType.equals("User")){
                    startActivity(Intent(this@SplashActivity, GraphDashBoardActivity::class.java))
                    finish()
                }else if (AppConstants.userData!!.userType.equals("Admin")){
                    startActivity(Intent(this@SplashActivity, AdminDashBoardActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }



//            if (true) {
//                // setting token
//              //  AppConstants.AUTH_TOKEN = sessionManager.getToken().toString()
//
//                //  getting user info
//                AppConstants.userData =Gson().fromJson( sessionManager.getUserInfo(), LoginResponseModel::class.java)
//
//                if(AppConstants.userData!!.userType.equals("User")){
//                    startActivity(Intent(this@SplashActivity, MainDashBoardActivity::class.java))
//                    finish()
//                }else if (AppConstants.userData!!.userType.equals("Admin")){
//                    startActivity(Intent(this@SplashActivity, AdminDashBoardActivity::class.java))
//                    finish()
//                }
//            } else {
//                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//                finish()
//            }
//
//
        }


    }
}