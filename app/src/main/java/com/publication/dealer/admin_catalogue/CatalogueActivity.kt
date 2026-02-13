package com.publication.dealer.admin_catalogue

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.publication.dealer.databinding.ActivityCatalogueBinding
import com.publication.dealer.network.Status
import com.publication.dealer.admin_catalogue.adapter.CatalogAdapter
import com.publication.dealer.admin_catalogue.model.AddCatalogRequestModel
import com.publication.dealer.admin_catalogue.viewmodel.GetCatalogViewModel
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogueBinding
    private val viewModel: GetCatalogViewModel by viewModel()

    private lateinit var adapter: CatalogAdapter
    private val catalogList = ArrayList<AddCatalogRequestModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCatalogueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupRecyclerView()

    }

    private fun setupClickListeners() {

        with(binding){



            fabAdd.setOnClickListener {
                startActivity(Intent(this@CatalogueActivity, AddCatalogueActivity::class.java))
                // finish()
            }

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

        adapter = CatalogAdapter(catalogList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CatalogueActivity)
            adapter = this@CatalogueActivity.adapter
        }
    }

    private fun callApi() {

        viewModel.getCatalog().observe(this) { response ->

            when (response.status) {

                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    if (response.data?.isSuccessful == true) {

                        val data = response.data.body()?.data

                        if (!data.isNullOrEmpty()) {
                            catalogList.clear()
                            catalogList.addAll(data)
                            adapter.notifyDataSetChanged()

                            binding.recyclerView.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        }
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
