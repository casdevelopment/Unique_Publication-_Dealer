package com.publication.dealer.create_user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.create_user.model.SignUpResponseModel
import com.publication.dealer.create_user.viewmodel.SignUpViewModel
import com.publication.dealer.databinding.ActivityCreateUserBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response

class CreateUserActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModel()
    private lateinit var binding: ActivityCreateUserBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            addUser.setOnClickListener {
                if (validateInputs()) callSignUpApi()
            }

            backBtn.setOnClickListener {
                navigateToAdminDashboard()
            }


        }
    }

    private fun navigateToAdminDashboard() {

        startActivity(Intent(this@CreateUserActivity, AdminDashBoardActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToAdminDashboard()
    }



    private fun validateInputs(): Boolean {
        var valid = true

        binding.userIdError.visibility = View.GONE
        binding.userNameError.visibility = View.GONE
        binding.mobileNumberError.visibility = View.GONE
        binding.partyCodeError.visibility = View.GONE
        binding.passwordError.visibility = View.GONE
        binding.confirmPasswordError.visibility = View.GONE

        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()






        if (binding.etUserId.text.toString().trim().isEmpty()) {
            binding.userIdError.visibility = View.VISIBLE
            binding.userIdError.setText ("User id required")

            valid = false
        }

        if (binding.etUserName.text.toString().trim().isEmpty()) {
            binding.userNameError.visibility = View.VISIBLE
            binding.userNameError.setText ("User name required")
            valid = false
        }

        if (binding.etPartyCode.text.toString().trim().isEmpty()) {
            binding.partyCodeError.visibility = View.VISIBLE
            binding.partyCodeError.setText ("party Code required")
            valid = false
        }

        if (binding.etMobileNumber.text.toString().trim().isEmpty()) {
            binding.mobileNumberError.visibility = View.VISIBLE
            binding.mobileNumberError.setText ("mobile Number required")
            valid = false
        }else if (binding.etMobileNumber.text.toString().trim().length < 11) {
            binding.mobileNumberError.visibility = View.VISIBLE
            binding.mobileNumberError.setText ("Mobile number must be at least 11 characters long.")
            valid = false
        }

        if (password.isEmpty()) {
            binding.passwordError.visibility = View.VISIBLE
            binding.passwordError.setText ("password required")
            valid = false
        } else if (password.length < 6) {
            binding.passwordError.visibility = View.VISIBLE
            binding.passwordError.setText ("Password must greater then 6")
            valid = false
        }

        if (confirm.isEmpty()) {
            binding.confirmPasswordError.visibility = View.VISIBLE
            binding.confirmPasswordError.setText ("Write password again")
            valid = false
        }

        if (confirm.isNotEmpty() && password != confirm) {
            binding.confirmPasswordError.visibility = View.VISIBLE
            binding.confirmPasswordError.setText ("Password do not match")
            valid = false
        }

        return valid
    }



    private fun callSignUpApi() {

        val signUpRequest = SignUpRequestModel(
            userId = binding.etUserId.text.toString().trim(),
            userName = binding.etUserName.text.toString().trim(),
            partyCode = binding.etPartyCode.text.toString().trim().toInt(),
            mobileNumber = binding.etMobileNumber.text.toString().trim(),
            userType = "User",
            addedBy = AppConstants.userData?.userId ?: "",
            password = binding.etPassword.text.toString().trim()
        )

        viewModel.signUp(signUpRequest).observe(this) { state ->

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



}








