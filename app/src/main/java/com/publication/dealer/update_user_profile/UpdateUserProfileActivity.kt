package com.publication.dealer.update_user_profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.publication.dealer.SessionManager
import com.publication.dealer.databinding.ActivityUpdateUserProfileBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.splash.SplashActivity
import com.publication.dealer.update_user_profile.model.UpdateUserModel
import com.publication.dealer.update_user_profile.viewmodel.UpdateUserViewModel
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserProfileBinding
    private val viewModel: UpdateUserViewModel by viewModel()
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupClickListeners()
        showUserDetails()
    }

    private fun setupClickListeners() {
        with(binding) {
            backBtn.setOnClickListener { navigateToGraphDashboard() }


            updateBtn.setOnClickListener {

                if (validateInputs()) {
                    callUpdateUserApi()
                }
            }
        }
    }

    private fun showUserDetails() {
        with(binding) {
            etUserId.setText(cleanValue(AppConstants.userData?.userId))
            etMobileNumber1.setText(cleanValue(AppConstants.userData?.mobileNumber))
            etMobileNumber2.setText(cleanValue(AppConstants.userData?.mobileNumber2))
            etMobileNumber3.setText(cleanValue(AppConstants.userData?.mobileNumber3))
            etTown.setText(cleanValue(AppConstants.userData?.town))
            etCity.setText(cleanValue(AppConstants.userData?.city))
            etDistrict.setText(cleanValue(AppConstants.userData?.district))
            etProvince.setText(cleanValue(AppConstants.userData?.province))
            etAddress.setText(cleanValue(AppConstants.userData?.address))
            etPostalCode.setText(cleanValue(AppConstants.userData?.postalCode))
        }
    }

    private fun cleanValue(value: String?): String {
        return if (value.isNullOrBlank() || value.equals("string", true) || value.equals("null", true)) {
            ""
        } else {
            value
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etMobileNumber1Error.visibility = View.GONE
        binding.etCityError.visibility = View.GONE
        binding.etAddressError.visibility = View.GONE
        binding.etPostalCodeError.visibility = View.GONE
        binding.etMobileNumber2Error.visibility = View.GONE
        binding.etMobileNumber3Error.visibility = View.GONE




        val mobileNumber = binding.etMobileNumber1.text.toString().trim()
        if (mobileNumber.isEmpty()) {
            binding.etMobileNumber1Error.visibility = View.VISIBLE
            binding.etMobileNumber1Error.text = "Contact Number Required"
            valid = false
        } else if (!mobileNumber.matches(Regex("^\\d{11}$"))){
            binding.etMobileNumber1Error.visibility = View.VISIBLE
            binding.etMobileNumber1Error.text = "Enter valid phone number"
            valid = false
        }

        val mobileNumber2 = binding.etMobileNumber2.text.toString().trim()
        if (mobileNumber2.isNotEmpty() && !mobileNumber2.matches(Regex("^\\d{11}$"))){
            binding.etMobileNumber2Error.visibility = View.VISIBLE
            binding.etMobileNumber2Error.text = "Enter valid phone number"
            valid = false
        }

        val mobileNumber3 = binding.etMobileNumber3.text.toString().trim()
        if (mobileNumber3.isNotEmpty() && !mobileNumber3.matches(Regex("^\\d{11}$"))){
            binding.etMobileNumber3Error.visibility = View.VISIBLE
            binding.etMobileNumber3Error.text = "Enter valid phone number"
            valid = false
        }


        val city = binding.etCity.text.toString().trim()
        if (city.isEmpty()) {
            binding.etCityError.visibility = View.VISIBLE
            binding.etCityError.text = "City Required"
            valid = false
        }


        val address = binding.etAddress.text.toString().trim()
        if (address.isEmpty()) {
            binding.etAddressError.visibility = View.VISIBLE
            binding.etAddressError.text = "Address Required"
            valid = false
        }


        val postalCode = binding.etPostalCode.text.toString().trim()
        if (postalCode.isEmpty()) {
            binding.etPostalCodeError.visibility = View.VISIBLE
            binding.etPostalCodeError.text = "Postal Code Required"
            valid = false
        }



        return valid
    }

    private fun callUpdateUserApi() {
        val updateUserRequest = UpdateUserModel(
            userId = binding.etUserId.text.toString().trim(),
            mobileNumber = binding.etMobileNumber1.text.toString().trim(),
            mobileNumber2 = binding.etMobileNumber2.text.toString().trim(),
            mobileNumber3 = binding.etMobileNumber3.text.toString().trim(),
            remarks = "Self Update",
            town = binding.etTown.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            district = binding.etDistrict.text.toString().trim(),
            province = binding.etProvince.text.toString().trim(),
            address = binding.etAddress.text.toString().trim(),
            postalCode = binding.etPostalCode.text.toString().trim(),
            fcmToken = "Token",
            modifiedBy = AppConstants.userData?.userId ?: ""
        )


        viewModel.updateUser(updateUserRequest).observe(this) { state ->
            when (state.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val response = state.data ?: run {
                        Toast.makeText(this, "Empty response", Toast.LENGTH_LONG).show()
                        return@observe
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        Toast.makeText(this, body.message, Toast.LENGTH_LONG).show()

                            AppConstants.userData?.apply {
                                mobileNumber = updateUserRequest.mobileNumber
                                mobileNumber2 = updateUserRequest.mobileNumber2
                                mobileNumber3 = updateUserRequest.mobileNumber3
                                remarks = updateUserRequest.remarks
                                town = updateUserRequest.town
                                city = updateUserRequest.city
                                district = updateUserRequest.district
                                province = updateUserRequest.province
                                postalCode = updateUserRequest.postalCode
                                address = updateUserRequest.address
                            }

                        AppConstants.userData?.let { updatedUser ->
                            sessionManager.userInfo(Gson().toJson(updatedUser))
                        }

                        navigateToGraphDashboard()


                    } else {
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


    private fun navigateToGraphDashboard() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToGraphDashboard()
    }
}
