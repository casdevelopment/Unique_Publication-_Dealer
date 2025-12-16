package com.publication.dealer.sales.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.publication.dealer.databinding.ItemSalesBinding
import com.publication.dealer.sales.model.SalesResponseModel
import java.text.SimpleDateFormat
import java.util.*

class SalesAdapter(
    private val salesList: List<SalesResponseModel>,
    private val onItemClick: (SalesResponseModel) -> Unit
) : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    inner class SalesViewHolder(val binding: ItemSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sales: SalesResponseModel) {

            binding.serialNo.text = "S.No "
            binding.tvSno.text =  sales.sno.toString()

            // Format sale date
            val formattedDate = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = parser.parse(sales.saleDate)
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date!!)
            } catch (e: Exception) {
                sales.saleDate
            }

            binding.tvSaleDate.text = formattedDate
            binding.tvTotalBooks.text = " " + sales.totalBooks.toString()

            // Format amount with comma and 2 decimal points
            binding.tvTotalAmount.text = sales.totalAmount.toString()


            binding.root.setOnClickListener {
                onItemClick(sales)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val binding = ItemSalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.bind(salesList[position])
    }

    override fun getItemCount(): Int = salesList.size
}
