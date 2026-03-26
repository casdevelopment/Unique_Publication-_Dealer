package com.publication.dealer.admin_branding.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.publication.dealer.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.publication.dealer.admin_branding.AdminBrandingDetailsActivity
import com.publication.dealer.admin_branding.model.UserResponseModel
import com.publication.dealer.databinding.ItemAdminUserBinding

class AdminUsersAdapter(
    private val list: List<UserResponseModel>
) : RecyclerView.Adapter<AdminUsersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        with(holder.binding) {
            tvUserName.text = item.UserName ?: "N/A"
            tvAccountName.text = item.Account_Name ?: "N/A"

            tvStatus.text = if (item.isActive == true) "Active" else "Inactive"
            tvStatus.setTextColor(
                if (item.isActive == true)
                    ContextCompat.getColor(holder.itemView.context, R.color.green)
                else
                    ContextCompat.getColor(holder.itemView.context, R.color.red)
            )

            Glide.with(holder.itemView.context)
                .load(item.shopimageurl ?: "")
                .placeholder(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(imgUser)

            cardUser.setOnClickListener {
                val intent = Intent(holder.itemView.context, AdminBrandingDetailsActivity::class.java)
                intent.putExtra("userId", item.UserId)
                holder.itemView.context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int = list.size
}
