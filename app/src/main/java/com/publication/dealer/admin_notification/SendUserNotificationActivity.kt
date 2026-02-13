package com.publication.dealer.admin_notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.publication.dealer.admin_notification.model.SendUserNotificationRequestmodel
import com.publication.dealer.admin_notification.viewmodel.SendUserNotificationViewModel
import com.publication.dealer.databinding.ActivitySendUserNotificationBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendUserNotificationActivity : AppCompatActivity() {

    private val viewModel: SendUserNotificationViewModel by viewModel()
    private lateinit var binding: ActivitySendUserNotificationBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendUserNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            send.setOnClickListener {
                if (validateInputs()) callSignUpApi()
            }

            backBtn.setOnClickListener {
                navigateToAdminDashboard()
            }


        }
    }

    private fun navigateToAdminDashboard() {

        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToAdminDashboard()
    }



    private fun validateInputs(): Boolean {
        var valid = true

        binding.etUserIdError.visibility = View.GONE
        binding.etTitleError.visibility = View.GONE
        binding.etMessageError.visibility = View.GONE
        binding.imageurlError.visibility = View.GONE




        if (binding.etUserId.text.toString().trim().isEmpty()) {
            binding.etUserIdError.visibility = View.VISIBLE
            binding.etUserIdError.setText ("User Id required")

            valid = false
        }

        if (binding.etTitle.text.toString().trim().isEmpty()) {
            binding.etTitleError.visibility = View.VISIBLE
            binding.etTitleError.setText ("title required")

            valid = false
        }

        if (binding.etMessage.text.toString().trim().isEmpty()) {
            binding.etMessageError.visibility = View.VISIBLE
            binding.etMessageError.setText ("Message required")
            valid = false
        }

//        if (binding.etImageurl.text.toString().trim().isEmpty()) {
//            binding.imageurlError.visibility = View.VISIBLE
//            binding.imageurlError.setText ("Image url required")
//            valid = false
//        }



        return valid
    }



    private fun callSignUpApi() {

        val request = SendUserNotificationRequestmodel(
            userid = binding.etUserId.text.toString().trim(),
            title = binding.etTitle.text.toString().trim(),
            message = binding.etMessage.text.toString().trim(),
            imageurl = binding.etImageurl.text.toString().trim()
        )

        viewModel.sendUserNotification(request).observe(this) { state ->

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

                            navigateToAdminDashboard()
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



}








