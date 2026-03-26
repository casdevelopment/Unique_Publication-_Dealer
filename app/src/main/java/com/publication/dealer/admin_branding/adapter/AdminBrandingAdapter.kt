//package com.publication.dealer.admin_branding.adapter
//
//import android.content.Intent
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.publication.dealer.admin_branding.model.UserResponseModel
//import com.publication.dealer.admin_catalogue.EditCatalogActivity
//import com.publication.dealer.databinding.ItemCatalogListBinding
//
//class AdminBrandingAdapter(
//    private val list: List<UserResponseModel>
//) : RecyclerView.Adapter<AdminBrandingAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemCatalogListBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemCatalogListBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val item = list[position]
//
//        with(holder.binding) {
//
//            // Set Data
//            tvCatalogName.text = item.catalogName
//            tvBoardName.text = "Board: ${item.boardName}"
//            tvCatalogType.text = item.catalogType
//            // tvCatalogUrl.text = item.catalogURL
//
//            // View PDF Click
//            btnViewPdf.setOnClickListener {
//
//                if (!item.catalogURL.isNullOrEmpty()) {
//
//                    try {
//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.setDataAndType(Uri.parse(item.catalogURL), "application/pdf")
//                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//                        root.context.startActivity(intent)
//
//                    } catch (e: Exception) {
//                        Toast.makeText(
//                            root.context,
//                            "No application found to open PDF",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                } else {
//                    Toast.makeText(
//                        root.context,
//                        "Invalid PDF URL",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//            editBtn.setOnClickListener {
//                val context = holder.itemView.context
//                val intent = Intent(context, EditCatalogActivity::class.java)
//                intent.putExtra("ID", item.id)
//                context.startActivity(intent)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = list.size
//}
