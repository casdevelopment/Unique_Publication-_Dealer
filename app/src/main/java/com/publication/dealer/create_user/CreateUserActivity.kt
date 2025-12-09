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
import com.publication.dealer.util.AppUtil
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response

class CreateUserActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModel()
    private lateinit var binding: ActivityCreateUserBinding

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.eProfileImage.setImageURI(uri)
        }
    }

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

            binding.eProfileImage.setOnClickListener {
                pickImageLauncher.launch("image/*")
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

        if (selectedImageUri == null) {
            binding.imageError.text = "Add profile image"
            binding.imageError.visibility = View.VISIBLE

            binding.eProfileImage.strokeColor = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
            binding.eProfileImage.strokeWidth = 4f
            valid = false
        } else {
            binding.imageError.visibility = View.GONE
            binding.eProfileImage.strokeWidth = 0f
        }



        if (binding.etUserId.text.toString().trim().isEmpty()) {
            binding.etUserId.error = "UserId required"
            valid = false
        }

        if (binding.etUserName.text.toString().trim().isEmpty()) {
            binding.etUserName.error = "Name required"
            valid = false
        }

        if (binding.etPartyCode.text.toString().trim().isEmpty()) {
            binding.etPartyCode.error = "Party Code required"
            valid = false
        }

        if (binding.etMobileNumber1.text.toString().trim().isEmpty()) {
            binding.etMobileNumber1.error = "Mobile number required"
            valid = false
        }

        if (binding.etUserTown.text.toString().trim().isEmpty()) {
            binding.etUserTown.error = "Town required"
            valid = false
        }
        if (binding.etUserCity.text.toString().trim().isEmpty()) {
            binding.etUserCity.error = "City required"
            valid = false
        }
        if (binding.etUserDistrict.text.toString().trim().isEmpty()) {
            binding.etUserDistrict.error = "District required"
            valid = false
        }
        if (binding.etUserProvince.text.toString().trim().isEmpty()) {
            binding.etUserProvince.error = "Province required"
            valid = false
        }

        if (binding.etUserPostalCode.text.toString().trim().isEmpty()) {
            binding.etUserPostalCode.error = "Postal Code required"
            valid = false
        }

        if (binding.etUserAddress.text.toString().trim().isEmpty()) {
            binding.etUserAddress.error = "Address required"
            valid = false
        }


        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            valid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
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
            profile = "profile string abcdefghijklmnopqrstuvwxyz",
            userId = binding.etUserId.text.toString().trim(),
            userName = binding.etUserName.text.toString().trim(),
            partyCode = binding.etPartyCode.text.toString().trim().toInt(),
            mobileNumber = binding.etMobileNumber1.text.toString().trim(),
            mobileNumber2 = binding.etMobileNumber2.text.toString().trim(),
            mobileNumber3 = binding.etMobileNumber3.text.toString().trim(),

            town = binding.etUserTown.text.toString().trim(),
            city = binding.etUserCity.text.toString().trim(),
            district = binding.etUserDistrict.text.toString().trim(),
            province = binding.etUserProvince.text.toString().trim(),

            postalCode = binding.etUserPostalCode.text.toString().trim().toInt(),

            address = binding.etUserAddress.text.toString().trim(),

            password = binding.etPassword.text.toString().trim(),

        )



        viewModel.signUp(signUpRequest).observe(this) { apiResponse ->
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
                            startActivity(Intent(this@CreateUserActivity, AdminDashBoardActivity::class.java))
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








