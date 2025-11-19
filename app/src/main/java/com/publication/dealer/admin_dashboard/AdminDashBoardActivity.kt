package com.publication.dealer.admin_dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.publication.dealer.SessionManager
import com.publication.dealer.create_user.CreateUserActivity
import com.publication.dealer.databinding.ActivityAdminDashBoardBinding
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.splash.SplashActivity
import com.publication.dealer.util.AppConstants
import org.koin.android.ext.android.inject

class AdminDashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashBoardBinding

    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupClickListeners()
        showAdminDetails()
    }

    private fun setupClickListeners() {

        with(binding){
            btnAddUsers.setOnClickListener {

                startActivity(Intent(this@AdminDashBoardActivity, CreateUserActivity::class.java))
                finish()

            }

            btnResetPassword.setOnClickListener {

            }

            btnInactivateUser.setOnClickListener {

            }


            logOut.setOnClickListener { sessionManager.logout()
                startActivity(Intent(this@AdminDashBoardActivity, SplashActivity::class.java))
                finish()}
        }
    }

    private fun showAdminDetails() {

        AppConstants.userData = Gson().fromJson(
            sessionManager.getUserInfo(),
            LoginResponseModel::class.java
        )

        binding.tvAdminName.text = AppConstants.userData?.userName ?: "Admin"
        binding.tvAdminPhone.text = AppConstants.userData?.mobileNumber ?: "N/A"


    }
}