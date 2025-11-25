package com.publication.dealer.admin_dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.publication.dealer.R
import com.publication.dealer.SessionManager
import com.publication.dealer.create_user.CreateUserActivity
import com.publication.dealer.create_user.viewmodel.SignUpViewModel
import com.publication.dealer.databinding.ActivityAdminDashBoardBinding
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.inactivate_user.viewmodel.InactivateUserViewModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.Status
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import com.publication.dealer.reset_password.view_model.ResetPasswordViewModel
import com.publication.dealer.splash.SplashActivity
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminDashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashBoardBinding

    private val viewModel: ResetPasswordViewModel by viewModel()

    private val viewModelInactivate: InactivateUserViewModel by viewModel()

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

                openResetPasswordDialog()
            }

            btnInactivateUser.setOnClickListener {

                openInactivateUserDialog()
            }


            logOut.setOnClickListener { sessionManager.logout()
                startActivity(Intent(this@AdminDashBoardActivity, SplashActivity::class.java))
                finish()}
        }
    }

    private fun showAdminDetails() {

        with(binding){
            userName.text= AppConstants.userData?.userName ?: "N/A"
            mobileNumber.text= AppConstants.userData?.mobileNumber ?: "N/A"
            address.text= AppConstants.userData?.address ?: "N/A"
            partyGroup.text= AppConstants.userData?.partyGroup ?: "N/A"
            accountName.text= AppConstants.userData?.account_Name ?: "N/A"
        }
    }



    private fun openResetPasswordDialog() {

        val dialog = Dialog(this@AdminDashBoardActivity)
        dialog.setContentView(R.layout.dialog_reset_password)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val etUserId = dialog.findViewById<EditText>(R.id.etUserId)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {

            val userIdStr = etUserId.text.toString().trim()

            if (userIdStr.isEmpty()) {
                etUserId.error = "User id required"
                return@setOnClickListener
            }
            callResetApi(userIdStr,dialog) // pass as Int


        }
        dialog.show()
    }

    private fun callResetApi(userIdToReset: String, dialog: Dialog) {

        AppConstants.userData = Gson().fromJson(
            sessionManager.getUserInfo(),
            LoginResponseModel::class.java
        )

        val adminId = AppConstants.userData?.userId ?: "Admin"

        if (adminId.isEmpty()) {
            Toast.makeText(this, "Admin ID not found or invalid!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ResetPasswordRequest(
            adminUserID = adminId,
            resetUserID = userIdToReset
        )

        viewModel.resetPassword(request).observe(this) { result ->
            when (result.status) {
                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, result.data ?: "Success", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, result.message ?: "Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun openInactivateUserDialog() {

        val dialog = Dialog(this@AdminDashBoardActivity)
        dialog.setContentView(R.layout.dialog_inactivate_user)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val etUserId = dialog.findViewById<EditText>(R.id.etUserId)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {

            val userIdStr = etUserId.text.toString().trim()

            if (userIdStr.isEmpty()) {
                etUserId.error = "User id required"
                return@setOnClickListener
            }
            callInactivateApi(userIdStr,dialog) // pass as Int


        }
        dialog.show()
    }

    private fun callInactivateApi(userIdToReset: String, dialog: Dialog) {

        AppConstants.userData = Gson().fromJson(
            sessionManager.getUserInfo(),
            LoginResponseModel::class.java
        )

        val adminId = AppConstants.userData?.userId ?: "Admin"

        if (adminId.isEmpty()) {
            Toast.makeText(this, "Admin ID not found or invalid!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = InactivateUserRequest(
            adminUserID = adminId,
            userID = userIdToReset
        )

        viewModelInactivate.inactivateUser(request).observe(this) { result ->
            when (result.status) {
                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, result.data ?: "Success", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, result.message ?: "Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}