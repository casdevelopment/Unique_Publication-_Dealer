package com.example.newapp.user_dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newapp.R
import com.example.newapp.databinding.DashboardItemBinding
import com.example.newapp.user_dashboard.model.DashBoardResponseData

class DashBoardAdapter(private val items: List<DashBoardResponseData>, var mCtx: Context) :
    RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

    private var filteredItemList: List<DashBoardResponseData> = items
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: DashboardItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.dashboard_item, parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: DashBoardResponseData = filteredItemList[position]




        holder.binding.tvAccountName.text = item.Account_Name
        holder.binding.tvAccountId.text = "ID: ${item.Account_ID}"
        holder.binding.tvDescription.text = item.DESCE
        holder.binding.tvDebit.text = formatAmount(item.Debit!!)
        holder.binding.tvCredit.text = formatAmount(item.Credit!!)
        holder.binding.tvBalance.text = formatAmount(getBalance(item))

        setAmountColors(holder.binding,item)

    }
    private fun formatAmount(amount: Double): String {
        return String.format("%,.2f", amount)
    }
    fun getBalance(item: DashBoardResponseData): Double {
        return item.Debit!! - item.Credit!!
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    inner class ViewHolder(val binding: DashboardItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    /*override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.getDefault())
                val results = FilterResults()
                if (query.isNullOrEmpty()) {
                    results.values = items
                } else {
                    val filteredList = items.filter { it.challenge_title!!.lowercase(Locale.getDefault())
                        .contains(query) }
                    results.values = filteredList
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as? List<DashBoardResponseData> ?: items
                notifyDataSetChanged()
            }
        }
    }*/

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun setAmountColors(binding: DashboardItemBinding, item: DashBoardResponseData) {


        // Debit color (red for positive, gray for zero)
        if (item.Debit!! > 0) {
            binding.tvDebit.setTextColor(ContextCompat.getColor(mCtx, R.color.debit_red))
            binding.tvDebit.setBackgroundResource(R.drawable.amount_background_debit)
        } else {
            binding.tvDebit.setTextColor(ContextCompat.getColor(mCtx, R.color.text_gray))
            binding.tvDebit.setBackgroundResource(R.drawable.amount_background_neutral)
        }

        // Credit color (green for positive, gray for zero)
        if (item.Credit!! > 0) {
            binding.tvCredit.setTextColor(ContextCompat.getColor(mCtx, R.color.credit_green))
            binding.tvCredit.setBackgroundResource(R.drawable.amount_background_credit)
        } else {
            binding.tvCredit.setTextColor(ContextCompat.getColor(mCtx   , R.color.text_gray))
            binding.tvCredit.setBackgroundResource(R.drawable.amount_background_neutral)
        }

        // Balance color (red for negative, green for positive, blue for zero)
        val balance =getBalance(item)
        when {
            balance > 0 -> {
                binding.tvBalance.setTextColor(ContextCompat.getColor(mCtx, R.color.positive_green))
                binding.tvBalance.setBackgroundResource(R.drawable.amount_background_positive)
            }
            balance < 0 -> {
                binding.tvBalance.setTextColor(ContextCompat.getColor(mCtx, R.color.negative_red))
                binding.tvBalance.setBackgroundResource(R.drawable.amount_background_negative)
            }
            else -> {
                binding.tvBalance.setTextColor(ContextCompat.getColor(mCtx, R.color.text_gray))
                binding.tvBalance.setBackgroundResource(R.drawable.amount_background_neutral)
            }
        }
    }

}