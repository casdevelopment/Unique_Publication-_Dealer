package com.publication.dealer.admin_branding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.publication.dealer.admin_branding.model.UserBrandingModel
import com.publication.dealer.admin_branding.viewmodel.GetAdminBrandingViewModel
import com.publication.dealer.databinding.ActivityAdminBrandingDetailsBinding
import com.publication.dealer.image_function.ImageViewingActivity
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminBrandingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBrandingDetailsBinding
    private val viewModel: GetAdminBrandingViewModel by viewModel()

    private var userid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminBrandingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userid = intent.getStringExtra("userId") ?: ""

        fetchShopDetails(userid)
    }

    private fun fetchShopDetails(userid: String) {

        viewModel.getUserBranding(userid).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> AppUtil.startLoader(this)

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val response = apiResponse.data

                    if (response != null && response.isSuccessful) {

                        val body = response.body()

                        if (body?.success == true && !body.data.isNullOrEmpty()) {

                            showContent()

                            val item = body.data[0]
                            bindData(item)

                        } else {

                            showEmptyState()
                           // Toast.makeText(this, body?.message ?: "No data found", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        showError(response)

                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    AppUtil.stopLoader()
                    showEmptyState()
                   Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindData(data: UserBrandingModel) {

        hideAllImages()

        bindImageWithCard(binding.cardMainHoarding, binding.imgHoarding, data.MainHordingURL)
        bindImageWithCard(binding.cardPOP, binding.imgPoster, data.pop)
        bindImageWithCard(binding.cardShelfTalker, binding.imgShelf, data.ShelfTalker)
        bindImageWithCard(binding.cardCounterTop, binding.imgCounter, data.CounterTop)
        bindImageWithCard(binding.cardOther, binding.imgOther, data.other)
    }
//    private fun bindImageWithCard(card: View, imageView: ImageView, url: String?) {
//
//        if (!url.isNullOrEmpty()) {
//
//            card.visibility = View.VISIBLE
//
//            Glide.with(this)
//                .load(url)
//                .centerCrop()
//                .into(imageView)
//
//        } else {
//            card.visibility = View.GONE
//        }
//    }

    private fun bindImageWithCard(card: View, imageView: ImageView, url: String?) {

        if (!url.isNullOrEmpty()) {
            card.visibility = View.VISIBLE

            Glide.with(this)
                .load(url)
                .centerCrop()
                .into(imageView)

            // Open next activity on click
            imageView.setOnClickListener {
                val intent = Intent(this, ImageViewingActivity::class.java)
                intent.putExtra("image_url", url)  // pass the URL
                startActivity(intent)
            }

        } else {
            card.visibility = View.GONE
            imageView.setOnClickListener(null) // remove click if no image
        }
    }

    private fun hideAllImages() {
        binding.cardMainHoarding.visibility = View.GONE
        binding.cardCounterTop.visibility = View.GONE
        binding.cardShelfTalker.visibility = View.GONE
        binding.cardPOP.visibility = View.GONE
        binding.cardOther.visibility = View.GONE
    }

    private fun showError(response: retrofit2.Response<*>?) {
        val message = try {
            val errorBody = response?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                Gson().fromJson(errorBody, BaseResponse::class.java).message
            } else "Something went wrong"
        } catch (e: Exception) {
            "Something went wrong"
        }

      //  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyState() {
        hideAllImages()
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun showContent() {
        binding.layoutEmpty.visibility = View.GONE
    }
}