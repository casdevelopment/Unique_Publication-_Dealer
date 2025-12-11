package com.publication.dealer.update_user_password

import android.content.Intent
import android.net.Uri
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
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
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

        startActivity(Intent(this@UpdateUserPasswordActivity, GraphDashBoardActivity::class.java))
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


        viewModel.updatePassword(updatePasswordRequest).observe(this) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val retrofitResponse = apiResponse.data // Response<BaseResponse<SignUpResponseModel>>

                    if (retrofitResponse != null) {

                        // Parse either body() or errorBody() correctly
                        val baseResponse: BaseResponse<Boolean> = if (retrofitResponse.isSuccessful && retrofitResponse.body() != null) {
                            retrofitResponse.body()!!
                        } else {
                            val errorJson = retrofitResponse.errorBody()?.string() ?: "{}"
                            try {
                                Gson().fromJson(errorJson, BaseResponse::class.java) as BaseResponse<Boolean>
                            } catch (e: Exception) {
                                BaseResponse(
                                    code = retrofitResponse.code(),
                                    message = "Something went wrong",
                                    success = false,
                                    data = null
                                )
                            }
                        }

                        // ALWAYS show API message
                        Toast.makeText(this, baseResponse.message, Toast.LENGTH_LONG).show()

                        // If success, dismiss dialog
                        if (baseResponse.success == true) {
                            startActivity(Intent(this@UpdateUserPasswordActivity, GraphDashBoardActivity::class.java))
                            finish()
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, apiResponse.message ?: "Network Error", Toast.LENGTH_LONG).show()
                }
            }
        }


    }

}








