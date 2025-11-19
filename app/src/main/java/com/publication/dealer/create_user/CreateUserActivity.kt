package com.publication.dealer.create_user

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.publication.dealer.R
import com.publication.dealer.admin_dashboard.AdminDashBoardActivity
import com.publication.dealer.databinding.ActivityCreateUserBinding
import com.publication.dealer.databinding.ActivityLoginBinding

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, AdminDashBoardActivity::class.java))
        }

    }
}