package com.publication.dealer.sales

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.publication.dealer.SessionManager
import com.publication.dealer.databinding.ActivitySalesDetailsBinding
import com.publication.dealer.network.Status
import com.publication.dealer.sales.Adapters.SalesDetailsAdapter
import com.publication.dealer.sales.model.SalesRequestModel
import com.publication.dealer.sales.viewmodel.SalesDetailsViewModel
import com.publication.dealer.sales.viewmodel.SalesViewModel
import com.publication.dealer.user_dashboard.GraphDashBoardActivity
import com.publication.dealer.util.AppUtil
import com.publication.dealer.util.showToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SalesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesDetailsBinding
    private val viewModel: SalesDetailsViewModel by viewModel()
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySalesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sno = intent.getLongExtra("sno", -1L)


        callApi(sno)

        binding.backBtn.setOnClickListener {
            navigateToSalesActivity()
        }
    }

    private fun callApi(sno: Long) {
        viewModel.salesDetails(sno).observe(this) { apiResponse ->
            when (apiResponse.status) {
                Status.LOADING -> AppUtil.startLoader(this@SalesDetailsActivity)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val dataList = apiResponse.data?.body()?.data
                    if (!dataList.isNullOrEmpty()) {
                        val adapter = SalesDetailsAdapter(dataList)
                        binding.recyclerView.layoutManager = LinearLayoutManager(this)
                        binding.recyclerView.adapter = adapter
                    } else {
                        showToast("No sales details found")
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    showToast(apiResponse.message ?: "Something went wrong")
                }
            }
        }
    }


    private fun navigateToSalesActivity() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
