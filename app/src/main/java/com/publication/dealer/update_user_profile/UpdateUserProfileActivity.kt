package com.publication.dealer.update_user_profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.publication.dealer.databinding.ActivityUpdateUserProfileBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.update_user_profile.model.UpdateUserModel
import com.publication.dealer.update_user_profile.viewmodel.UpdateUserViewModel
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
import com.publication.dealer.user_dashboard.MainDashBoardActivity
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserProfileBinding
    private val viewModel: UpdateUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        showUserDetails()
    }


    private fun setupClickListeners() {
        with(binding) {

            backBtn.setOnClickListener {
                navigateToGraphDashboard()
            }

            updateBtn.setOnClickListener {
                if (validateInputs()) callUpdateUserApi()
            }

        }
    }

    private fun showUserDetails() {

        with(binding){



            etUserId.setText(AppConstants.userData?.userId ?: "")
            etMobileNumber1.setText(AppConstants.userData?.mobileNumber ?: "")


        }
    }


    private fun validateInputs(): Boolean {
        var valid = true

        binding.useridError.visibility = View.GONE
        binding.mobileNumberError.visibility = View.GONE
        binding.remarksError.visibility = View.GONE

        binding.townError.visibility = View.GONE
        binding.cityError.visibility = View.GONE
        binding.districtError.visibility = View.GONE
        binding.provinceError.visibility = View.GONE

        binding.postalCodeError.visibility = View.GONE


        val userId = binding.etUserId.text.toString().trim()
        val mobileNumber1 = binding.etMobileNumber1.text.toString().trim()
        val mobileNumber2 = binding.etMobileNumber2.text.toString().trim()
        val mobileNumber3 = binding.etMobileNumber3.text.toString().trim()
        val remarks = binding.etRemarks.text.toString().trim()

        val town = binding.etTown.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val province = binding.etProvince.text.toString().trim()

        val postalCode = binding.etPostalCode.text.toString().trim().toIntOrNull() ?: 0








        if (userId.isEmpty()) {
            binding.useridError.visibility = View.VISIBLE
            binding.useridError.setText ("UserId required")
            valid = false
        }


        if (mobileNumber1.isEmpty()) {
            binding.mobileNumberError.visibility = View.VISIBLE
            binding.mobileNumberError.setText ("Mobile number required")
            valid = false
        }else if (mobileNumber1.length < 11) {
            binding.mobileNumberError.visibility = View.VISIBLE
            binding.mobileNumberError.setText ("Mobile number must be at least 11 characters long.")
            valid = false
        }


        if (binding.etRemarks.text.toString().trim().isEmpty()) {
            binding.remarksError.visibility = View.VISIBLE
            binding.remarksError.setText ("Remarks required")
            valid = false
        }





        if (binding.etTown.text.toString().trim().isEmpty()) {
            binding.townError.visibility = View.VISIBLE
            binding.townError.setText ("Town required")
            valid = false
        }

        if (binding.etCity.text.toString().trim().isEmpty()) {
            binding.cityError.visibility = View.VISIBLE
            binding.cityError.setText ("city required")
            valid = false
        }

        if (binding.etDistrict.text.toString().trim().isEmpty()) {
            binding.districtError.visibility = View.VISIBLE
            binding.districtError.setText ("District required")
            valid = false
        }

        if (binding.etProvince.text.toString().trim().isEmpty()) {
            binding.provinceError.visibility = View.VISIBLE
            binding.provinceError.setText ("Province required")
            valid = false
        }

        if (binding.etPostalCode.text.toString().trim().isEmpty()) {
            binding.postalCodeError.visibility = View.VISIBLE
            binding.postalCodeError.setText ("Postal code  required")
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
            remarks = binding.etRemarks.text.toString().trim(),
            town = binding.etTown.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            district = binding.etDistrict.text.toString().trim(),
            province = binding.etProvince.text.toString().trim(),
            postalCode = binding.etPostalCode.text.toString().trim(),
            fcmToken = AppConstants.AUTH_TOKEN ?: "",   // or "" if not saved
            modifiedBy = AppConstants.userData?.userId ?: ""
        )



        viewModel.updateUser(updateUserRequest).observe(this) { apiResponse ->
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
                            startActivity(Intent(this@UpdateUserProfileActivity, GraphDashBoardActivity::class.java))
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

    private fun navigateToGraphDashboard() {

        startActivity(Intent(this@UpdateUserProfileActivity, GraphDashBoardActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToGraphDashboard()
    }
}