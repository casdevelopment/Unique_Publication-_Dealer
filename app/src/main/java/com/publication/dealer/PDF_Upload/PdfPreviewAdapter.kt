package com.publication.dealer.PDF_Upload

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.publication.dealer.R
import com.publication.dealer.databinding.ItemPdfPreviewBinding
import com.publication.dealer.sales.model.SalesDetailResponseModel

class PdfPreviewAdapter(
    private val list: List<SalesDetailResponseModel>
) : RecyclerView.Adapter<PdfPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val itemName = view.findViewById<TextView>(R.id.tvItemName)
        val date = view.findViewById<TextView>(R.id.tvDate)
        val qty = view.findViewById<TextView>(R.id.tvQty)
        val rate = view.findViewById<TextView>(R.id.tvRate)
        val price = view.findViewById<TextView>(R.id.tvPrintedPrice)
        val discount = view.findViewById<TextView>(R.id.tvDiscount)
        val amount = view.findViewById<TextView>(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_preview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.itemName.text = item.itemName
        holder.date.text = item.saleDate
        holder.qty.text = "Qty: ${item.qty}"
        holder.rate.text = "Rate: ${item.rate}"
        holder.price.text = "Price: ${item.printedPrice}"
        holder.discount.text = "Discount: ${item.discount}"
        holder.amount.text = "Amount: ${item.amount}"
    }

    override fun getItemCount() = list.size
}
