package com.publication.dealer.sales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.publication.dealer.SessionManager
import com.publication.dealer.databinding.ActivitySalesBinding
import com.publication.dealer.network.Status
import com.publication.dealer.sales.Adapters.SalesAdapter
import com.publication.dealer.sales.model.SalesRequestModel
import com.publication.dealer.sales.model.SalesResponseModel
import com.publication.dealer.sales.viewmodel.SalesViewModel
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import com.publication.dealer.util.showToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SalesActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val viewModel: SalesViewModel by viewModel()
    private val sessionManager: SessionManager by inject()

    private var selectedDateFrom: Date? = null
    private var selectedDateTo: Date? = null
    private lateinit var binding:ActivitySalesBinding

    private lateinit var salesAdapter: SalesAdapter
    private val salesList = ArrayList<SalesResponseModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateDateDisplays()
        setupClickListeners()

    }


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
                    Toast.makeText(this@SalesActivity, "End date cleared. Please select a new end date.", Toast.LENGTH_SHORT).show()
                }
            } else {
                selectedDateTo = selectedDate.time
                // If Date From is after the new Date To, clear Date From
                if (selectedDateFrom != null && selectedDateFrom!!.after(selectedDateTo!!)) {
                    selectedDateFrom = null
                    Toast.makeText(this@SalesActivity, "Start date cleared. Please select a new start date.", Toast.LENGTH_SHORT).show()
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

        val partyCode = AppConstants.userData?.partyCode ?: 0
        val salesRequest = SalesRequestModel(partyCode,dateFrom, dateTo)

        callApi(salesRequest)

    }

    private fun callApi(salesRequest: SalesRequestModel) {


        viewModel.sales(salesRequest).observe(this) { apiResponse ->

            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this@SalesActivity)
                Status.SUCCESS -> {
                    Log.v("callApiLedger", "callApiLedger Status.SUCCESS")
                    AppUtil.stopLoader()
                    if (apiResponse.data != null) {
                        if (apiResponse.data.isSuccessful) {




                            val data = apiResponse.data.body()?.data
                            if (!data.isNullOrEmpty()) {
                                salesList.clear()
                                salesList.addAll(data)
                                setupRecyclerView()

                                binding.recyclerView.visibility = View.VISIBLE
                                binding.tvEmptyState.visibility = View.GONE
                            } else {

                                binding.recyclerView.visibility = View.GONE
                                binding.tvEmptyState.visibility = View.VISIBLE
                                Toast.makeText(this, "No sales found", Toast.LENGTH_SHORT).show()
                            }


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

    private fun setupRecyclerView() {
        salesAdapter = SalesAdapter(salesList) { salesItem ->
            val intent = Intent(this, SalesDetailsActivity::class.java)
            intent.putExtra("sno", salesItem.sno)
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SalesActivity)
            adapter = salesAdapter
        }
    }






    private fun navigateToGraphDashboard() {

        startActivity(Intent(this@SalesActivity, GraphDashBoardActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToGraphDashboard()
    }
}