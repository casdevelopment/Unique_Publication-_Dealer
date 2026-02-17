package com.publication.dealer.user_notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.publication.dealer.admin_notification.model.BroadCastRequestmodel
import com.publication.dealer.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val list: List<BroadCastRequestmodel>,
    private val onItemClick: (BroadCastRequestmodel) -> Unit // Click listener
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        with(holder.binding) {
            tvTitle.text = item.title
            tvMessage.text = item.message

            // Handle click
            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
