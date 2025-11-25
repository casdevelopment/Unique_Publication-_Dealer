package com.publication.dealer.user_dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.publication.dealer.R
import com.publication.dealer.SessionManager
import com.publication.dealer.databinding.ActivityGraphDashBoardBinding
import com.publication.dealer.network.Status
import com.publication.dealer.splash.SplashActivity
import com.publication.dealer.user_dashboard.model.DashBoardRequestModel
import com.publication.dealer.user_dashboard.model.DashBoardResponseData
import com.publication.dealer.user_dashboard.viewmodel.DashBoardViewModel
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import com.publication.dealer.util.showToast
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class GraphDashBoardActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphDashBoardBinding
    private val sessionManager: SessionManager by inject()
    private val viewModel: DashBoardViewModel by viewModel()
    private lateinit var popupMenu: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGraphDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUserData()
        setupBarChart()
        val dashBoardResponseData = DashBoardRequestModel(getFirstDateOfCurrentYear(), getCurrentDate())
        callApi(dashBoardResponseData)
        setupPopupMenu()
        binding.option.setOnClickListener {
            Log.v("option","option click")
            popupMenu.show()
        }
        binding.viewReport.setOnClickListener {
            startActivity(Intent(this@GraphDashBoardActivity, MainDashBoardActivity::class.java))
        }
    }
    private fun setUserData() {
        with(binding){
            userName.text= AppConstants.userData?.userName ?: "N/A"
            mobileNumber.text= AppConstants.userData?.mobileNumber ?: "N/A"
            address.text= AppConstants.userData?.address ?: "N/A"
            partyGroup.text= AppConstants.userData?.partyGroup ?: "N/A"
            accountName.text= AppConstants.userData?.account_Name ?: "N/A"
        }
    }

    private fun callApi(dashBoardResponseData: DashBoardRequestModel) {
        viewModel.dashBoardData(dashBoardResponseData).observe(this) { apiResponse ->

            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this@GraphDashBoardActivity)
                Status.SUCCESS -> {
                    Log.v("callApiLedger", "callApiLedger Status.SUCCESS")
                    AppUtil.stopLoader()
                    if (apiResponse.data != null) {
                        if (apiResponse.data.isSuccessful) {

                            val dashBoardListData =
                                apiResponse.data.body()?.data as ArrayList<DashBoardResponseData>

                            setupChartWithData(Gson().toJson(dashBoardListData))

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

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getFirstDateOfCurrentYear(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun setupBarChart() {
        with(binding){
            // Basic chart configuration
            barChart.setDrawGridBackground(false)
            barChart.setDrawBarShadow(false)
            barChart.setDrawValueAboveBar(true)

            // Remove description label completely
            barChart.description = null

            // X-axis configuration
            val xAxis = barChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.textColor = Color.BLACK
            xAxis.textSize = 10f
            xAxis.setCenterAxisLabels(false)
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.labelRotationAngle = -45f
            xAxis.yOffset = 15f

            // Y-axis left configuration
            val leftAxis = barChart.axisLeft
            leftAxis.setDrawGridLines(true)
            leftAxis.gridColor = Color.parseColor("#EEEEEE")
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f
            leftAxis.textColor = Color.BLACK
            leftAxis.textSize = 10f

            // Y-axis right configuration
            barChart.axisRight.isEnabled = false

            // Legend configuration
            val legend = barChart.legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(false)
            legend.textColor = Color.BLACK
            legend.textSize = 11f
            legend.xEntrySpace = 10f
            legend.yEntrySpace = 5f

            // Enable interactions
            barChart.setTouchEnabled(true)
            barChart.setPinchZoom(true)
            barChart.setDoubleTapToZoomEnabled(true)
            barChart.setScaleEnabled(true)

            // Increase bottom offset for rotated labels
            barChart.setExtraOffsets(20f, 20f, 20f, 40f)
        }
    }

    private fun setupChartWithData(jsonString: String) {
        try {
            Log.v("setupChartWithData","setupChartWithData "+jsonString)
            val dataArray = JSONArray(jsonString)

            // Group by month - FIXED: Store both display text and sortable date
            val monthlySummaries = mutableMapOf<String, MonthlySummary>()
            val monthEntries = mutableListOf<MonthEntry>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)

                // Skip opening balance as it's too large and distorts the chart
                if ("OB" == item.getString("VTYPE")) {
                    continue
                }

                val vdate = item.getString("VDATE")
                var debit = item.getDouble("Debit")
                var credit = item.getDouble("Credit")

                // Filter out negative values for better visualization
                debit = maxOf(debit, 0.0)
                credit = maxOf(credit, 0.0)

                // Parse date to get month format with proper sorting
                val monthEntry = parseDateToMonthEntry(vdate)

                val monthKey = monthEntry.displayText
                if (!monthlySummaries.containsKey(monthKey)) {
                    monthlySummaries[monthKey] = MonthlySummary(monthKey)
                    monthEntries.add(monthEntry)
                }

                val summary = monthlySummaries[monthKey]!!
                summary.addDebit(debit)
                summary.addCredit(credit)
                summary.addTransaction() // Count transactions
            }

            // Sort months chronologically using the sortable date
            monthEntries.sortBy { it.sortableDate }

            // Create data sets for grouped bars - ONLY include non-zero values
            val debitEntries = ArrayList<BarEntry>()
            val creditEntries = ArrayList<BarEntry>()
            val filteredMonths = mutableListOf<String>() // Track months that have data

            for (i in monthEntries.indices) {
                val monthEntry = monthEntries[i]
                val summary = monthlySummaries[monthEntry.displayText]!!

                // Convert to millions for better readability
                val debitInMillions = (summary.totalDebit / 1000000).toFloat()
                val creditInMillions = (summary.totalCredit / 1000000).toFloat()

                // Only add entries if at least one value is greater than zero
                if (debitInMillions > 0 || creditInMillions > 0) {
                    val entryIndex = filteredMonths.size.toFloat()
                    debitEntries.add(BarEntry(entryIndex, debitInMillions))
                    creditEntries.add(BarEntry(entryIndex, creditInMillions))
                    filteredMonths.add(monthEntry.displayText)
                }
            }

            // Check if we have data to display
            if (debitEntries.isEmpty() && creditEntries.isEmpty()) {
                Toast.makeText(this, "No valid transaction data to display", Toast.LENGTH_LONG).show()
                return
            }

            // Create bar data sets
            val debitDataSet = BarDataSet(debitEntries, "Debit")
            debitDataSet.color = Color.parseColor("#FF6B6B")
            debitDataSet.valueTextColor = Color.BLACK
            debitDataSet.valueTextSize = 6f
            debitDataSet.setDrawValues(true)

            val creditDataSet = BarDataSet(creditEntries, "Credit")
            creditDataSet.color = Color.parseColor("#4ECDC4")
            creditDataSet.valueTextColor = Color.BLACK
            creditDataSet.valueTextSize = 6f
            creditDataSet.setDrawValues(true)

            // Remove zero values from display
            debitDataSet.setDrawValues(!debitEntries.all { it.y == 0f })
            creditDataSet.setDrawValues(!creditEntries.all { it.y == 0f })

            // Space between bars configuration
            val groupSpace = 0.4f
            val barSpace = 0.4f
            val barWidth = 0.8f

            val data = BarData(debitDataSet, creditDataSet)
            data.barWidth = barWidth

            // Set value formatter to show values in millions (hide zero values)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else if (value < 1) "%.2fM".format(value) else "%.1fM".format(value)
                }
            })

            // Apply data to chart
            binding.barChart.data = data

            // Calculate the correct x-axis range for grouping
            val groupCount = filteredMonths.size.toFloat()
            binding.barChart.xAxis.axisMinimum = -0.5f
            binding.barChart.xAxis.axisMaximum = groupCount - 0.5f + (groupCount * groupSpace)

            // Apply grouping with proper parameters
            binding.barChart.groupBars(-0.5f, groupSpace, barSpace)

            // Show months at intervals based on total count
            binding.barChart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    val totalMonths = filteredMonths.size

                    // Show all months if few, otherwise show at intervals
                    return when {
                        totalMonths <= 8 -> {
                            // Show all months
                            if (index in filteredMonths.indices) filteredMonths[index] else ""
                        }
                        totalMonths <= 16 -> {
                            // Show every 2nd month
                            if (index in filteredMonths.indices && index % 2 == 0) filteredMonths[index] else ""
                        }
                        else -> {
                            // Show every 3rd month
                            if (index in filteredMonths.indices && index % 3 == 0) filteredMonths[index] else ""
                        }
                    }
                }
            }

            // Set Y-axis formatter for millions
            binding.barChart.axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "%.1fM".format(value)
                }
            }

            // Set appropriate label count
            binding.barChart.xAxis.setLabelCount(filteredMonths.size, true)

            // Adjust label rotation based on number of months
            val totalMonths = filteredMonths.size
            if (totalMonths > 12) {
                binding.barChart.xAxis.labelRotationAngle = -45f
                binding.barChart.xAxis.textSize = 9f
                binding.barChart.setExtraOffsets(20f, 20f, 20f, 45f)
            } else {
                binding.barChart.xAxis.labelRotationAngle = -45f
                binding.barChart.xAxis.textSize = 10f
                binding.barChart.setExtraOffsets(20f, 20f, 20f, 40f)
            }

            // Refresh chart with animation
            binding.barChart.invalidate()
            binding.barChart.animateY(1500)

            // Show monthly summary
            showMonthlySummary(monthlySummaries, filteredMonths.size)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // FIXED: Use MonthEntry to store both display text and sortable date
    private data class MonthEntry(
        val displayText: String,  // What to show on chart: "Feb", "Mar '24", etc.
        val sortableDate: Date    // For proper chronological sorting
    )

    private fun parseDateToMonthEntry(dateString: String): MonthEntry {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: Date()

            val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
            val dataYear = yearFormat.format(date)

            val displayText = if (dataYear != currentYear) {
                // For previous years: "Feb '24"
                val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                val month = monthFormat.format(date)
                "$month '${dataYear.substring(2)}"
            } else {
                // For current year: "Feb"
                val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                monthFormat.format(date)
            }

            MonthEntry(displayText, date)
        } catch (e: Exception) {
            // Fallback
            val currentDate = Date()
            MonthEntry("Unknown", currentDate)
        }
    }

    private fun showMonthlySummary(monthlySummaries: Map<String, MonthlySummary>, filteredMonthCount: Int) {
        val totalDebit = monthlySummaries.values.sumByDouble { it.totalDebit }
        val totalCredit = monthlySummaries.values.sumByDouble { it.totalCredit }
        val monthsCount = monthlySummaries.size
        val totalTransactions = monthlySummaries.values.sumBy { it.transactionCount }


        binding.totalDebit.text= "Total Debit: ${"%.2f".format(totalDebit / 1000000)}M"
        binding.totalCredit.text= "Total Credit: ${"%.2f".format(totalCredit / 1000000)}M"
        /*val summary = "Monthly Transaction Summary\n" +
                "Total Debit: ${"%.2f".format(totalDebit / 1000000)}M\n" +
                "Total Credit: ${"%.2f".format(totalCredit / 1000000)}M\n" +
                "Months with transactions: $monthsCount\n" +
                "Months displayed: $filteredMonthCount\n" +
                "Total Transactions: $totalTransactions"
        binding.summary.text=summary*/

        //Toast.makeText(this, summary, Toast.LENGTH_LONG).show()
    }

    // Helper class to store monthly summaries
    private data class MonthlySummary(
        val month: String,
        var totalDebit: Double = 0.0,
        var totalCredit: Double = 0.0,
        var transactionCount: Int = 0
    ) {
        fun addDebit(debit: Double) {
            totalDebit += debit
        }

        fun addCredit(credit: Double) {
            totalCredit += credit
        }

        fun addTransaction() {
            transactionCount++
        }
    }
    private fun setupPopupMenu() {
        // Create ContextThemeWrapper for styling (optional)
        val wrapper = ContextThemeWrapper(this, R.style.PopupMenu)
        popupMenu = PopupMenu(wrapper, binding.option)

        // Inflate the menu
        popupMenu.menuInflater.inflate(R.menu.options_menu, popupMenu.menu)

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_update_password -> {
                    //updatePassword()
                    true
                }
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        // The click listener is already set in setupClickListeners()
        // So no need to set it here again
    }

    private fun logout() {
        AlertDialog.Builder(this@GraphDashBoardActivity)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, which ->
                sessionManager.logout()
                startActivity(Intent(this@GraphDashBoardActivity, SplashActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}