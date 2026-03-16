package com.publication.dealer.sales.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.publication.dealer.databinding.ItemSalesDetailsBinding
import com.publication.dealer.sales.model.SalesDetailResponseModel
import java.text.SimpleDateFormat
import java.util.*

class SalesDetailsAdapter(
    private val salesList: List<SalesDetailResponseModel>
) : RecyclerView.Adapter<SalesDetailsAdapter.SalesDetailsViewHolder>() {

    inner class SalesDetailsViewHolder(
        private val binding: ItemSalesDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SalesDetailResponseModel) {

            binding.tvSno.text = item.sno.toString()
            binding.tvSaleDate.text = "  " + formatDate(item.saleDate)
            binding.tvItemName.text = item.itemName
            binding.tvItemCode.text =item.itemCode.toString()



            binding.tvQty.text = item.qty.toString()

            // ✅ Proper money formatting
            binding.tvRate.text = formatAmount(item.rate)
            binding.tvPrintedPrice.text = formatAmount(item.printedPrice)
            binding.tvDiscount.text = formatAmount(item.discount)
            binding.tvAmount.text = formatAmount(item.amount)

            // ✅ Date formatting

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesDetailsViewHolder {
        val binding = ItemSalesDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SalesDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesDetailsViewHolder, position: Int) {
        holder.bind(salesList[position])
    }

    override fun getItemCount(): Int = salesList.size

    // ---------------- HELPERS ----------------

    private fun formatAmount(value: Double): String {
        return String.format("%.2f", value)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(dateString)
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
}
