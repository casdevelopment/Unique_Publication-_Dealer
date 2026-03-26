package com.publication.dealer.image_function

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.publication.dealer.databinding.ActivityImageViewingBinding


class ImageViewingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageUrl = intent.getStringExtra("image_url")

        Glide.with(this)
            .load(imageUrl)
            .placeholder(binding.ivPaymentSlip.drawable)
            .into(binding.ivPaymentSlip)



//        photoView.setOnClickListener { finish() }

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}