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
                    callUpdateUserApi()
            }
        }
    }

    private fun showUserDetails() {
        with(binding) {
            etUserId.setText(cleanValue(AppConstants.userData?.userId))
            etMobileNumber1.setText(cleanValue(AppConstants.userData?.mobileNumber))
            etMobileNumber2.setText(cleanValue(AppConstants.userData?.purchaseNumber1))
            etMobileNumber3.setText(cleanValue(AppConstants.userData?.purchaseNumber2))
            etTown.setText(cleanValue(AppConstants.userData?.town))
            etCity.setText(cleanValue(AppConstants.userData?.city))
            etDistrict.setText(cleanValue(AppConstants.userData?.district))
            etProvince.setText(cleanValue(AppConstants.userData?.province))
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



    private fun callUpdateUserApi() {
        val updateUserRequest = UpdateUserModel(
            userId = binding.etUserId.text.toString().trim(),
            mobileNumber = binding.etMobileNumber1.text.toString().trim(),
            purchaseNumber1 = binding.etMobileNumber2.text.toString().trim(),
            purchaseNumber2 = binding.etMobileNumber3.text.toString().trim(),
            remarks = "Self Update",
            town = binding.etTown.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            district = binding.etDistrict.text.toString().trim(),
            province = binding.etProvince.text.toString().trim(),
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
                                purchaseNumber1 = updateUserRequest.purchaseNumber1
                                purchaseNumber2 = updateUserRequest.purchaseNumber2
                                remarks = updateUserRequest.remarks
                                town = updateUserRequest.town
                                city = updateUserRequest.city
                                district = updateUserRequest.district
                                province = updateUserRequest.province
                                postalCode = updateUserRequest.postalCode
                            }

                            AppConstants.userData?.let { updatedUser ->
                                sessionManager.userInfo(Gson().toJson(updatedUser))
                            }

                        startActivity(Intent(this, GraphDashBoardActivity::class.java))
                            finish()



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
        startActivity(Intent(this@UpdateUserProfileActivity, GraphDashBoardActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToGraphDashboard()
    }
}
