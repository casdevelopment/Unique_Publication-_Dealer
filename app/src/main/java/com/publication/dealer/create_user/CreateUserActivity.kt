package com.publication.dealer.create_user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.create_user.model.SignUpRequestModel
import com.publication.dealer.create_user.viewmodel.SignUpViewModel
import com.publication.dealer.databinding.ActivityCreateUserBinding
import com.publication.dealer.network.Status
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
            signupBtn.setOnClickListener {
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
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        if (binding.etUserId.text.toString().trim().isEmpty()) {
            binding.etUserId.error = "UserId required"
            valid = false
        }

        if (binding.etUserName.text.toString().trim().isEmpty()) {
            binding.etUserName.error = "Name required"
            valid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            valid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            valid = false
        }

        if (binding.etMobileNumber.text.toString().trim().isEmpty()) {
            binding.etMobileNumber.error = "Mobile number required"
            valid = false
        }

        if (binding.etPartyCode.text.toString().trim().isEmpty()) {
            binding.etPartyCode.error = "Party Code required"
            valid = false
        }

        if (confirm.isEmpty()) {
            binding.etConfirmPassword.error = "Enter Password Again"
            valid = false
        }

        if (confirm.isNotEmpty() && password != confirm) {
            binding.etConfirmPassword.error = "Passwords do not match"
            valid = false
        }

        return valid
    }


    private fun callSignUpApi() {
        val signUpRequest = SignUpRequestModel(
            userId = binding.etUserId.text.toString().trim(),
            userName = binding.etUserName.text.toString().trim(),
            partyCode = binding.etPartyCode.text.toString().trim().toInt(),
            password = binding.etPassword.text.toString().trim(),
            mobileNumber = binding.etMobileNumber.text.toString().trim()
        )

        viewModel.signUp(signUpRequest).observe(this) { apiResponse ->
            AppUtil.stopLoader() // stop loader in any case

            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    // Show success message from backend
                    Toast.makeText(this, apiResponse.data ?: "Success", Toast.LENGTH_LONG).show()

                    startActivity(Intent(this@CreateUserActivity, AdminDashBoardActivity::class.java))
                    finish()
                }

                Status.ERROR -> {
                    // Show error message from backend
                    Toast.makeText(this, apiResponse.message ?: "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }


    }



}