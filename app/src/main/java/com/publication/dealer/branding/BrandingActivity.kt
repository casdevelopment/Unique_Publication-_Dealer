package com.publication.dealer.branding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.publication.dealer.admin_branding.viewmodel.GetAdminBrandingViewModel
import com.publication.dealer.databinding.ActivityBrandingBinding
import com.publication.dealer.image_function.ImageViewingActivity
import com.publication.dealer.network.Status
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrandingBinding



    private val viewModel: GetAdminBrandingViewModel by viewModel()

    private var userid: String = ""

    private var currentHoardingUrl: String? = null
    private var currentPosterUrl: String? = null
    private var currentShelfUrl: String? = null
    private var currentCounterUrl: String? = null
    private var currentOtherUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBrandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userid = AppConstants.userData?.userId ?: ""

        setupClickListeners()
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

                            val data = body.data[0] // ✅ get first item

                            currentHoardingUrl = data.MainHordingURL
                            currentPosterUrl = data.pop
                            currentShelfUrl = data.ShelfTalker
                            currentCounterUrl = data.CounterTop
                            currentOtherUrl = data.other

                            bind(currentHoardingUrl, binding.imgHoarding, binding.uploadHoarding, binding.editHoarding, 1)
                            bind(currentPosterUrl, binding.imgPoster, binding.uploadPoster, binding.editPoster, 2)
                            bind(currentShelfUrl, binding.imgShelf, binding.uploadShelf, binding.editShelf, 3)
                            bind(currentCounterUrl, binding.imgCounter, binding.uploadCounter, binding.editCounter, 4)
                            bind(currentOtherUrl, binding.imgOther, binding.uploadOther, binding.editOther, 5)

                        } else {
                            // optional: show empty state if needed
                        }

                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.uploadHoarding.setOnClickListener { openUploadScreen(1) }
        binding.uploadPoster.setOnClickListener { openUploadScreen(2) }
        binding.uploadShelf.setOnClickListener { openUploadScreen(3) }
        binding.uploadCounter.setOnClickListener { openUploadScreen(4) }
        binding.uploadOther.setOnClickListener { openUploadScreen(5) }

        binding.editHoarding.setOnClickListener { openEditScreen(1, currentHoardingUrl) }
        binding.editPoster.setOnClickListener { openEditScreen(2, currentPosterUrl) }
        binding.editShelf.setOnClickListener { openEditScreen(3, currentShelfUrl) }
        binding.editCounter.setOnClickListener { openEditScreen(4, currentCounterUrl) }
        binding.editOther.setOnClickListener { openEditScreen(5, currentOtherUrl) }
    }

    private fun openUploadScreen(typeId: Int) {
        val intent = Intent(this, UploadBrandingActivity::class.java)
        intent.putExtra("userId", userid)
        intent.putExtra("typeId", typeId)
        startActivity(intent)
    }

    private fun openEditScreen(typeId: Int, imageUrl: String?) {
        val intent = Intent(this, EditBrandingActivity::class.java)
        intent.putExtra("userId", userid)
        intent.putExtra("typeId", typeId)
        intent.putExtra("imageUrl", imageUrl)
        startActivity(intent)
    }

//    private fun bind(url: String?, image: View, upload: View, edit: View) {
//        if (!url.isNullOrEmpty()) {
//            image.visibility = View.VISIBLE
//            upload.visibility = View.GONE
//            edit.visibility = View.VISIBLE
//            Glide.with(this).load(url).into(image as android.widget.ImageView)
//        } else {
//            image.visibility = View.GONE
//            upload.visibility = View.VISIBLE
//            edit.visibility = View.GONE
//        }
//    }


    private fun bind(url: String?, image: View, upload: View, edit: View, typeId: Int) {
        if (!url.isNullOrEmpty()) {
            image.visibility = View.VISIBLE
            upload.visibility = View.GONE
            edit.visibility = View.VISIBLE
            Glide.with(this).load(url).into(image as android.widget.ImageView)

            // Tap to open ImageActivity
            image.setOnClickListener {
                val intent = Intent(this, ImageViewingActivity::class.java)
                intent.putExtra("image_url", url)
                startActivity(intent)
            }

            // Edit button still opens edit screen
            edit.setOnClickListener { openEditScreen(typeId, url) }

        } else {
            // No image
            image.visibility = View.GONE
            upload.visibility = View.VISIBLE
            edit.visibility = View.GONE

            // Tap on upload opens upload screen
            upload.setOnClickListener { openUploadScreen(typeId) }
        }
    }
}