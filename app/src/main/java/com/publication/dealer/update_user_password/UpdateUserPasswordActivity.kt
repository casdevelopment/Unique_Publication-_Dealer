package com.publication.dealer.update_user_password


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.publication.dealer.databinding.ActivityUpdateUserPasswordBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.update_user_password.model.UpdateUserPasswordRequestModel
import com.publication.dealer.update_user_password.viewmodel.UpdateUserPasswordViewModel
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpdateUserPasswordActivity : AppCompatActivity() {

    private val viewModel: UpdateUserPasswordViewModel by viewModel()
    private lateinit var binding: ActivityUpdateUserPasswordBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }


    private fun setupClickListeners() {
        with(binding) {
            updateBtn.setOnClickListener {
                if (validateInputs()) callUpdatePasswordApi()
            }

            backBtn.setOnClickListener {
                navigateToDashboard()
            }


        }
    }

    private fun navigateToDashboard() {

        finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToDashboard()
    }



    private fun validateInputs(): Boolean {
        var valid = true


        binding.passwordError.visibility = View.GONE
        binding.newPasswordError.visibility = View.GONE


        val userId = AppConstants.userData?.userId ?: ""
        val userName = AppConstants.userData?.userName ?: ""




        if (userId.isEmpty()) {

            Toast.makeText(this,"User Id not found", Toast.LENGTH_LONG).show()

            valid = false
        }

        if (userName.isEmpty()) {

            Toast.makeText(this,"User Name not found", Toast.LENGTH_LONG).show()

            valid = false
        }


        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.passwordError.visibility = View.VISIBLE
            binding.passwordError.setText ("password required")
            valid = false
        } else if (binding.etPassword.text.toString().trim().length < 6) {
            binding.passwordError.visibility = View.VISIBLE
            binding.passwordError.setText ("Password must greater then 6")
            valid = false
        }


        if (binding.etNewPassword.text.toString().trim().isEmpty()) {
            binding.newPasswordError.visibility = View.VISIBLE
            binding.newPasswordError.setText ("password required")
            valid = false
        } else if (binding.etNewPassword.text.toString().trim().length < 6) {
            binding.newPasswordError.visibility = View.VISIBLE
            binding.newPasswordError.setText ("Password must greater then 6")
            valid = false
        }



        return valid
    }


    private fun callUpdatePasswordApi() {
        val updatePasswordRequest = UpdateUserPasswordRequestModel(
            userId = AppConstants.userData?.userId ?: "",
            username = AppConstants.userData?.userName ?: "",
            password = binding.etPassword.text.toString().trim(),
            newPassword = binding.etNewPassword.text.toString().trim(),
            fcmToken = AppConstants.userData?.token ?: "",
        )



        viewModel.updatePassword(updatePasswordRequest).observe(this) { state ->

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

                            navigateToDashboard()
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








