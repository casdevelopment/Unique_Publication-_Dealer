package com.publication.dealer.admin_dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.google.gson.Gson
import com.publication.dealer.PDF_Upload.model.PDFUploadRequest
import com.publication.dealer.PDF_Upload.viewmodel.UploadPdfViewModel
import com.publication.dealer.R
import com.publication.dealer.SessionManager
import com.publication.dealer.admin_branding.AdminBrandingActivity
import com.publication.dealer.admin_catalogue.CatalogueActivity
import com.publication.dealer.admin_notification.BroadcastNotificationActivity
import com.publication.dealer.admin_notification.SendUserNotificationActivity
import com.publication.dealer.create_user.CreateUserActivity
import com.publication.dealer.create_user.model.SignUpResponseModel
import com.publication.dealer.create_user.viewmodel.SignUpViewModel
import com.publication.dealer.databinding.ActivityAdminDashBoardBinding
import com.publication.dealer.inactivate_user.model.InactivateUserRequest
import com.publication.dealer.inactivate_user.viewmodel.InactivateUserViewModel
import com.publication.dealer.login.model.LoginResponseModel
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.reset_password.model.ResetPasswordRequest
import com.publication.dealer.reset_password.view_model.ResetPasswordViewModel
import com.publication.dealer.splash.SplashActivity
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AdminDashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashBoardBinding

    private val viewModel: ResetPasswordViewModel by viewModel()

    private val viewModelInactivate: InactivateUserViewModel by viewModel()

    private val sessionManager: SessionManager by inject()

    private lateinit var popupMenu: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupClickListeners()
        showAdminDetails()
        setupPopupMenu()
        binding.option.setOnClickListener {
            Log.v("option","option click")
            popupMenu.show()
        }
    }

    private fun setupClickListeners() {

        with(binding){
            btnAddUsers.setOnClickListener {

                startActivity(Intent(this@AdminDashBoardActivity, CreateUserActivity::class.java))

            }

            btnResetPassword.setOnClickListener {

                openResetPasswordDialog()
            }

            btnInactivateUser.setOnClickListener {

                openInactivateUserDialog()
            }

            btnCatalogue.setOnClickListener {

                startActivity(Intent(this@AdminDashBoardActivity, CatalogueActivity::class.java))


            }

            btnNotification.setOnClickListener {

                openNotificationDialog()
            }

            btnBranding.setOnClickListener {

                startActivity(Intent(this@AdminDashBoardActivity, AdminBrandingActivity::class.java))


            }

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
        val userIdError = dialog.findViewById<TextView>(R.id.userIdError)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {

            userIdError.visibility = View.GONE

            val userIdStr = etUserId.text.toString().trim()

            if (userIdStr.isEmpty()) {
                userIdError.visibility = View.VISIBLE
                userIdError.setText("User id required")
                return@setOnClickListener
            }
            callResetApi(userIdStr,dialog) // pass as Int


        }
        dialog.show()
    }

    private fun callResetApi(userIdToReset: String, dialog: Dialog) {


        val adminId = AppConstants.userData?.userId ?: "Admin"

        if (adminId.isEmpty()) {
            Toast.makeText(this, "Admin ID not found or invalid!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ResetPasswordRequest(
            adminUserID = adminId,
            resetUserID = userIdToReset
        )

        viewModel.resetPassword(request).observe(this) { state ->

            when (state.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = state.data ?: run {
                        Toast.makeText(this, "Empty response", Toast.LENGTH_LONG).show()
                        return@observe
                    }

                    // ✅ HTTP 2xx
                    if (response.isSuccessful && response.body() != null) {

                        val body = response.body()!!
                        Toast.makeText(this, body.message, Toast.LENGTH_LONG).show()

                        if (body.success) {
                            startActivity(Intent(this, AdminDashBoardActivity::class.java)
                            )
                            finish()
                        }

                    }
                    // ❗ HTTP error but API sent message (409, 400, etc)
                    else {
                        val errorMsg = try {
                            val errorJson = response.errorBody()?.string()
                            if (!errorJson.isNullOrEmpty()) {
                                Gson().fromJson(errorJson, BaseResponse::class.java)?.message
                            } else null
                        } catch (e: Exception) {
                            null
                        }

                        Toast.makeText(this, errorMsg ?: "Server error", Toast.LENGTH_LONG).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, state.message ?: "Network error", Toast.LENGTH_LONG).show()
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
        val userIdError = dialog.findViewById<TextView>(R.id.userIdError)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {

            userIdError.visibility = View.GONE

            val userIdStr = etUserId.text.toString().trim()

            if (userIdStr.isEmpty()) {
                userIdError.visibility = View.VISIBLE
                userIdError.setText("User id required")
                return@setOnClickListener
            }
            callInactivateApi(userIdStr,dialog) // pass as Int


        }
        dialog.show()
    }

    private fun callInactivateApi(userIdToReset: String, dialog: Dialog) {


        val adminId = AppConstants.userData?.userId ?: "Admin"

        if (adminId.isEmpty()) {
            Toast.makeText(this, "Admin ID not found or invalid!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = InactivateUserRequest(
            adminUserID = adminId,
            userID = userIdToReset
        )




        viewModelInactivate.inactivateUser(request).observe(this) { state ->

            when (state.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = state.data ?: run {
                        Toast.makeText(this, "Empty response", Toast.LENGTH_LONG).show()
                        return@observe
                    }

                    // ✅ HTTP 2xx
                    if (response.isSuccessful && response.body() != null) {

                        val body = response.body()!!
                        Toast.makeText(this, body.message, Toast.LENGTH_LONG).show()

                        if (body.success) {
                            startActivity(Intent(this, AdminDashBoardActivity::class.java)
                            )
                            finish()
                        }

                    }
                    // ❗ HTTP error but API sent message (409, 400, etc)
                    else {
                        val errorMsg = try {
                            val errorJson = response.errorBody()?.string()
                            if (!errorJson.isNullOrEmpty()) {
                                Gson().fromJson(errorJson, BaseResponse::class.java)?.message
                            } else null
                        } catch (e: Exception) {
                            null
                        }

                        Toast.makeText(this, errorMsg ?: "Server error", Toast.LENGTH_LONG).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, state.message ?: "Network error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupPopupMenu() {
        // Create ContextThemeWrapper for styling (optional)
        val wrapper = ContextThemeWrapper(this, R.style.PopupMenu)
        popupMenu = PopupMenu(wrapper, binding.option)

        // Inflate the menu
        popupMenu.menuInflater.inflate(R.menu.options_menu_admin, popupMenu.menu)

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
        AlertDialog.Builder(this@AdminDashBoardActivity)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, which ->
                sessionManager.logout()
                startActivity(Intent(this@AdminDashBoardActivity, SplashActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun openNotificationDialog() {

        val dialog = Dialog(this@AdminDashBoardActivity)
        dialog.setContentView(R.layout.dialog_notification)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnBroadcast = dialog.findViewById<Button>(R.id.btnBroadcast)
        val btnSendUser = dialog.findViewById<Button>(R.id.btnSendUser)

        btnBroadcast.setOnClickListener {

            startActivity(Intent(this@AdminDashBoardActivity, BroadcastNotificationActivity::class.java))
            dialog.dismiss()
        }

        btnSendUser.setOnClickListener {

            startActivity(Intent(this@AdminDashBoardActivity, SendUserNotificationActivity::class.java))

            dialog.dismiss()
        }
        dialog.show()
    }

}