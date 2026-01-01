package com.publication.dealer.user_dashboard

import android.os.Bundle
import android.app.DatePickerDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.publication.dealer.databinding.ActivityMainDashBoardBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.publication.dealer.R
import com.publication.dealer.SessionManager
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.network.Status
import com.publication.dealer.splash.SplashActivity
import com.publication.dealer.update_user_profile.UpdateUserProfileActivity
import com.publication.dealer.user_dashboard.adapter.DashBoardAdapter
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.user_dashboard.viewmodel.DashBoardViewModel
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import com.publication.dealer.util.showToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainDashBoardActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val viewModel: DashBoardViewModel by viewModel()
    private var selectedDateFrom: Date? = null
    private var selectedDateTo: Date? = null
    private lateinit var binding:ActivityMainDashBoardBinding
    private lateinit var dashBoardAdapter: DashBoardAdapter
    private lateinit var dashBoardListData: ArrayList<DashBoardResponseData>
    private val sessionManager: SessionManager by inject()
    private lateinit var popupMenu: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
      //  setContentView(R.layout.activity_main_dash_board)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        updateDateDisplays()
        setupClickListeners()
//        setUserData()
//        setupPopupMenu()
    }

//    private fun setUserData() {
//        with(binding){
//            userName.text= AppConstants.userData?.userName ?: "N/A"
//            mobileNumber.text= AppConstants.userData?.mobileNumber ?: "N/A"
//        }
//    }

    private fun setupClickListeners() {

        with(binding){
            tvSelectedDateFrom.setOnClickListener {
                showDatePicker(true) // true for Date From
            }

            tvSelectedDateTo.setOnClickListener {
                showDatePicker(false) // false for Date To
            }

            btnFetchData.setOnClickListener {
                fetchLedgerData()
            }

            backBtn.setOnClickListener {
                navigateToGraphDashboard()
            }

        }
    }

    private fun showDatePicker(isDateFrom: Boolean) {
        val currentDate = if (isDateFrom) selectedDateFrom ?: Date() else selectedDateTo ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = currentDate
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }

            if (isDateFrom) {
                selectedDateFrom = selectedDate.time
                // If Date To is before the new Date From, clear Date To
                if (selectedDateTo != null && selectedDateFrom!!.after(selectedDateTo!!)) {
                    selectedDateTo = null
                    Toast.makeText(this@MainDashBoardActivity, "End date cleared. Please select a new end date.", Toast.LENGTH_SHORT).show()
                }
            } else {
                selectedDateTo = selectedDate.time
                // If Date From is after the new Date To, clear Date From
                if (selectedDateFrom != null && selectedDateFrom!!.after(selectedDateTo!!)) {
                    selectedDateFrom = null
                    Toast.makeText(this@MainDashBoardActivity, "Start date cleared. Please select a new start date.", Toast.LENGTH_SHORT).show()
                }
            }
            updateDateDisplays()
        }, year, month, day)

        // Set max date to today
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun updateDateDisplays() {
        binding.tvSelectedDateFrom.text = selectedDateFrom?.let { dateFormatter.format(it) } ?: "Select Start Date"
        binding.tvSelectedDateTo.text = selectedDateTo?.let { dateFormatter.format(it) } ?: "Select End Date"

        // Update selected state for the drawable selector
        binding.tvSelectedDateFrom.isSelected = selectedDateFrom != null
        binding.tvSelectedDateTo.isSelected = selectedDateTo != null

        // Enable/disable fetch button based on selection
     //   binding.btnFetchData.isEnabled = selectedDateFrom != null && selectedDateTo != null

       /* if (binding.btnFetchData.isEnabled) {
            binding.btnFetchData.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            binding.btnFetchData.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        } else {
            binding.btnFetchData.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            binding.btnFetchData.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        }*/
    }

    private fun fetchLedgerData() {
        Log.v("selectedDateFrom", "fetchLedgerData ")
        if (selectedDateFrom == null || selectedDateTo == null) {
            Log.v("selectedDateFrom", "selectedDateFrom ")
            Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFrom = dateFormatter.format(selectedDateFrom!!)
        val dateTo = dateFormatter.format(selectedDateTo!!)

        if (dateFrom > dateTo) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
            return
        }

        val dashBoardResponseData = DashBoardRequestModel(dateFrom, dateTo)

        callApi(dashBoardResponseData)

    }

    private fun callApi(dashBoardResponseData: DashBoardRequestModel) {
        viewModel.dashBoardData(dashBoardResponseData).observe(this) { apiResponse ->

            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this@MainDashBoardActivity)
                Status.SUCCESS -> {
                    Log.v("callApiLedger", "callApiLedger Status.SUCCESS")
                    AppUtil.stopLoader()
                    if (apiResponse.data != null) {
                        if (apiResponse.data.isSuccessful) {

                            dashBoardListData =
                                apiResponse.data.body()?.data as ArrayList<DashBoardResponseData>

                            setupAdapter()

                        } else {
                            showToast(AppUtil.setApiErrorResponse(apiResponse.data.errorBody()))
                        }
                    }

                }

                Status.ERROR -> {
                    Log.v("callApiLedger", "callApiLedger Status.ERROR")
                    AppUtil.stopLoader()
                }
            }
            }


    }

    private fun setupAdapter() {
        Log.v("setupAdapter", "setupAdapter "+dashBoardListData.size.toString())
        binding.recyclerView.visibility=View.VISIBLE
        binding.tvEmptyState.visibility=View.GONE
        dashBoardAdapter = DashBoardAdapter(dashBoardListData, this@MainDashBoardActivity)
        binding.recyclerView.apply {
            this.layoutManager =
                LinearLayoutManager(this@MainDashBoardActivity, RecyclerView.VERTICAL, false)
            this.adapter = dashBoardAdapter
        }

       /* binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                historyAdapter.filter.filter(s)

            }
            override fun afterTextChanged(s: Editable?) {}
        })*/



    }

//    private fun setupPopupMenu() {
//        // Create ContextThemeWrapper for styling (optional)
//        val wrapper = ContextThemeWrapper(this, R.style.PopupMenu)
//        popupMenu = PopupMenu(wrapper, binding.option)
//
//        // Inflate the menu
//        popupMenu.menuInflater.inflate(R.menu.options_menu, popupMenu.menu)
//
//        // Handle menu item clicks
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.action_update_password -> {
//                    //updatePassword()
//                    true
//                }
//                R.id.action_logout -> {
//                    logout()
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // The click listener is already set in setupClickListeners()
//        // So no need to set it here again
//    }

    private fun navigateToGraphDashboard() {

       // startActivity(Intent(this@MainDashBoardActivity, GraphDashBoardActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}