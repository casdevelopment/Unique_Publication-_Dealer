package com.publication.dealer.user_notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.publication.dealer.databinding.ActivityNotificationDetailBinding

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: "No Title"
        val message = intent.getStringExtra("message") ?: "No Message"
        val imageUrl = intent.getStringExtra("imageUrl")

        binding.tvDetailTitle.text = title
        binding.tvDetailMessage.text = message

        if (!imageUrl.isNullOrEmpty()) {
            binding.ivDetailImage.visibility = android.view.View.VISIBLE
            Glide.with(this)
                .load(imageUrl)
                .into(binding.ivDetailImage)
        }
    }
}
