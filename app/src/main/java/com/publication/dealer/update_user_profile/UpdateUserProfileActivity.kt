package com.publication.dealer.update_user_profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.publication.dealer.R
import com.publication.dealer.databinding.ActivityUpdateUserProfileBinding
import com.publication.dealer.user_dashboard.MainDashBoardActivity
import com.publication.dealer.util.AppConstants

class UpdateUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserProfileBinding

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
                navigateToMainDashboard()
            }

            updateBtn.setOnClickListener {
                if (validateInputs()) callSignUpApi()
            }

        }
    }

    private fun showUserDetails() {

        with(binding){



            etUserId.setText(AppConstants.userData?.userId ?: "")
            etUserName.setText(AppConstants.userData?.userName ?: "")
            etMobileNumber1.setText(AppConstants.userData?.mobileNumber ?: "")


//            etMobileNumber1.setText(AppConstants.userData?.mobileNumber ?: "")
//            etMobileNumber1.setText(AppConstants.userData?.mobileNumber ?: "")

            etPartyCode.setText(AppConstants.userData?.partyCode?.toString() ?: "")

            etPartyGroup.setText(AppConstants.userData?.partyGroup ?: "")


//            etTown.setText(AppConstants.userData?.town ?: "")
//            etCity.setText(AppConstants.userData?.city ?: "")
//            etDistrict.setText(AppConstants.userData?.district ?: "")
//            etProvince.setText(AppConstants.userData?.province ?: "")


           // etPostalCode.setText(AppConstants.userData?.postalCode ?: "")

            etAddress.setText(AppConstants.userData?.address ?: "")

        }
    }


    private fun validateInputs(): Boolean {
        var valid = true

        binding.useridError.visibility = View.GONE
        binding.usernameError.visibility = View.GONE
        binding.mobileNumberError.visibility = View.GONE
        binding.townError.visibility = View.GONE
        binding.cityError.visibility = View.GONE
        binding.districtError.visibility = View.GONE
        binding.provinceError.visibility = View.GONE
        binding.postalCodeError.visibility = View.GONE
        binding.addressError.visibility = View.GONE

        val userId = binding.etUserId.text.toString().trim()
        val UserName = binding.etUserName.text.toString().trim()

        val mobileNumber1 = binding.etMobileNumber1.text.toString().trim()
        val mobileNumber2 = binding.etMobileNumber2.text.toString().trim()
        val mobileNumber3 = binding.etMobileNumber3.text.toString().trim()

        val partyGroup = binding.etPartyGroup.text.toString().trim()

        val partyCode = binding.etPartyCode.text.toString().trim().toIntOrNull() ?: 0
        val postalCode = binding.etPostalCode.text.toString().trim().toIntOrNull() ?: 0


        val town = binding.etTown.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val province = binding.etProvince.text.toString().trim()


        val address = binding.etAddress.text.toString().trim()




        if (userId.isEmpty()) {
            binding.useridError.visibility = View.VISIBLE
            binding.useridError.setText ("UserId required")
            valid = false
        }

        if (UserName.isEmpty()) {
            binding.usernameError.visibility = View.VISIBLE
            binding.usernameError.setText ("Name required")
            valid = false
        }

        if (mobileNumber1.isEmpty()) {
            binding.mobileNumberError.visibility = View.VISIBLE
            binding.mobileNumberError.setText ("Mobile number required")
            valid = false
        }

        if (town.isEmpty()) {
            binding.townError.visibility = View.VISIBLE
            binding.townError.setText ("Town required")
            valid = false
        }

        if (city.isEmpty()) {
            binding.cityError.visibility = View.VISIBLE
            binding.cityError.setText ("city required")
            valid = false
        }

        if (district.isEmpty()) {
            binding.districtError.visibility = View.VISIBLE
            binding.districtError.setText ("District required")
            valid = false
        }

        if (province.isEmpty()) {
            binding.provinceError.visibility = View.VISIBLE
            binding.provinceError.setText ("Province required")
            valid = false
        }

        if (postalCode == 0) {
            binding.postalCodeError.visibility = View.VISIBLE
            binding.postalCodeError.setText ("Postal code  required")
            valid = false
        }

        if (address.isEmpty()) {
            binding.addressError.visibility = View.VISIBLE
            binding.addressError.setText ("Address required")
            valid = false
        }


        return valid
    }

    private fun callSignUpApi(){

    }

    private fun navigateToMainDashboard() {

        startActivity(Intent(this@UpdateUserProfileActivity, MainDashBoardActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMainDashboard()
    }
}