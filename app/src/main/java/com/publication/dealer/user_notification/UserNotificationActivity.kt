package com.publication.dealer.user_notification

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.publication.dealer.network.Status
import com.publication.dealer.admin_notification.model.BroadCastRequestmodel
import com.publication.dealer.databinding.ActivityUserNotificationBinding
import com.publication.dealer.util.AppConstants.userData
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserNotificationBinding
    private val viewModel: GetNotificationByIdViewModel by viewModel()

    private lateinit var adapter: NotificationAdapter
    private val catalogList = ArrayList<BroadCastRequestmodel>()

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupRecyclerView()

         userId = userData?.userName ?: "N/A"

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
        callApi(userId)
    }



    private fun setupRecyclerView() {

        adapter = NotificationAdapter(catalogList)

        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(this@UserNotificationActivity)
            adapter = this@UserNotificationActivity.adapter
        }
    }

    private fun callApi(userId: String) {

        viewModel.getNotificationById(userId).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    if (apiResponse.data?.isSuccessful == true) {

                        val data = apiResponse.data.body()?.data

                        if (!data.isNullOrEmpty()) {
                            catalogList.clear()
                            catalogList.addAll(data)
                            adapter.notifyDataSetChanged()

                            binding.rvNotifications.visibility = View.VISIBLE
                            binding.layoutEmpty.visibility = View.GONE
                        } else {
                            binding.rvNotifications.visibility = View.GONE
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.textFirst.text = "No Notifications Yet"
                            binding.textSecond.text = "You will see new updates here"
                        }
                    }
                    else{

                        binding.rvNotifications.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.textFirst.text = "Something went wrong"
                        binding.textSecond.text = "Please check your internet connection and try again"

                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    binding.rvNotifications.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.textFirst.text = "Something went wrong"
                    binding.textSecond.text = "Please check your internet connection and try again"
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
