package com.publication.dealer.admin_branding

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.publication.dealer.admin_branding.adapter.AdminUsersAdapter
import com.publication.dealer.admin_branding.model.UserResponseModel
import com.publication.dealer.admin_branding.viewmodel.GetAdminBrandingViewModel
import com.publication.dealer.network.Status
import com.publication.dealer.databinding.ActivityAdminBrandingBinding
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.widget.doOnTextChanged

class AdminBrandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBrandingBinding
    private val viewModel: GetAdminBrandingViewModel by viewModel()

    private lateinit var adapter: AdminUsersAdapter
    private val catalogList = ArrayList<UserResponseModel>()
    private val fullList = ArrayList<UserResponseModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBrandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupRecyclerView()
        setupSearch()

    }

    private fun setupClickListeners() {

        with(binding){


            backBtn.setOnClickListener {
                finish()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        callApi()
    }



    private fun setupRecyclerView() {

        adapter = AdminUsersAdapter(catalogList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminBrandingActivity)
            adapter = this@AdminBrandingActivity.adapter
        }
    }


    private fun callApi() {
        val userId = AppConstants.userData?.userId ?: ""

        viewModel.getAllUsers(userId).observe(this) { response ->

            when (response.status) {

                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    if (response.data?.isSuccessful == true) {

                        val data = response.data.body()?.data

                        if (!data.isNullOrEmpty()) {

                            // 🔹 FILTER: only UserType = "User"
                            val filteredData = data.filter { it.UserType == "User" }

                            catalogList.clear()
                            catalogList.addAll(filteredData)
                            adapter.notifyDataSetChanged()

                            fullList.clear()
                            fullList.addAll(filteredData) // save full list for search

                            binding.recyclerView.visibility = if (filteredData.isNotEmpty()) View.VISIBLE else View.GONE
                            binding.tvEmptyState.visibility = if (filteredData.isEmpty()) View.VISIBLE else View.GONE

                            setupSearch() // add search listener

                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        }
                    }
                }

                Status.ERROR -> AppUtil.stopLoader()
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().trim().lowercase()
            val filtered = fullList.filter {
                it.UserName?.lowercase()?.contains(query) == true ||
                        it.Account_Name?.lowercase()?.contains(query) == true
            }

            catalogList.clear()
            catalogList.addAll(filtered)
            adapter.notifyDataSetChanged()

            binding.recyclerView.visibility = if (filtered.isNotEmpty()) View.VISIBLE else View.GONE
            binding.tvEmptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        }
    }

}
