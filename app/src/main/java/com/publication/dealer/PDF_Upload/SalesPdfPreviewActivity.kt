package com.publication.dealer.PDF_Upload

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.publication.dealer.R
import com.publication.dealer.databinding.ActivitySalesPdfPreviewBinding
import com.publication.dealer.sales.model.SalesDetailResponseModel
import java.io.File
import java.io.FileOutputStream

class SalesPdfPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesPdfPreviewBinding
    private var salesList: ArrayList<SalesDetailResponseModel> = arrayListOf()

    companion object {
        fun start(context: Context, list: ArrayList<SalesDetailResponseModel>) {
            val intent = Intent(context, SalesPdfPreviewActivity::class.java)
            intent.putExtra("salesList", list)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesPdfPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get sales list from intent
        salesList = intent.getSerializableExtra("salesList") as? ArrayList<SalesDetailResponseModel>
            ?: arrayListOf()

        // RecyclerView preview
        binding.recyclerPdf.layoutManager = LinearLayoutManager(this)
        binding.recyclerPdf.adapter = PdfPreviewAdapter(salesList)

        // Save PDF button
        binding.btnSavePdf.setOnClickListener {
            showFileNameDialog()
        }
    }

    // ---------------- CUSTOM FILE NAME DIALOG ----------------
    private fun showFileNameDialog() {
        val dialog = android.app.Dialog(this, R.style.CustomDialog)
        dialog.setContentView(R.layout.dialog_file_name)
        dialog.setCancelable(false)

        val etFileName = dialog.findViewById<EditText>(R.id.etFileName)
        val btnCancel = dialog.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnCancel)
        val btnSave = dialog.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnSave)

        etFileName.setText("Sales_Report")

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val fileName = etFileName.text.toString().trim()
            if (fileName.isEmpty()) {
                Toast.makeText(this, "File name required", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                generatePdf("$fileName.pdf")
            }
        }

        // Optional: make dialog width match parent nicely
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()
    }

    // ---------------- PDF GENERATION ----------------
    private fun generatePdf(fileName: String) {
        try {
            val document = PdfDocument()
            val pageWidth = 595   // A4
            val pageHeight = 842

            val paint = Paint()
            paint.textSize = 11f

            // Load image from drawable (logo)
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo) // replace with your logo
            val scaledLogo = android.graphics.Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)

            var pageNumber = 1
            var y = 50f

            var page = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )
            var canvas = page.canvas

            // ---------- TITLE WITH LOGO ----------
            // ---------- TITLE WITH LOGO ----------
            paint.textSize = 16f
            paint.isFakeBoldText = true

            val title = "Sales Report"

// Calculate text width to center it
            val textWidth = paint.measureText(title)
            val centerX = (pageWidth - textWidth) / 2

            canvas.drawText(title, centerX, y, paint)

// Draw logo at top-right corner
            val logoX = pageWidth - scaledLogo.width - 20f  // 20px padding from right
            val logoY = y - scaledLogo.height + 10f        // adjust vertical position to align with text
            canvas.drawBitmap(scaledLogo, logoX, logoY, paint)

            paint.textSize = 11f
            paint.isFakeBoldText = false
            y += 60f  // move below title for table header


            // ---------- HEADER ----------
            paint.isFakeBoldText = true
            canvas.drawText("Date", 20f, y, paint)
            canvas.drawText("Item", 120f, y, paint)
            canvas.drawText("Qty", 340f, y, paint)
            canvas.drawText("Rate", 390f, y, paint)
            canvas.drawText("Amount", 460f, y, paint)
            paint.isFakeBoldText = false
            y += 20f

            // ---------- ROWS ----------
            for (item in salesList) {
                canvas.drawText(item.saleDate.take(10), 20f, y, paint)
                canvas.drawText(item.itemName.take(28), 120f, y, paint)
                canvas.drawText(item.qty.toString(), 340f, y, paint)
                canvas.drawText(item.rate.toString(), 390f, y, paint)
                canvas.drawText(item.amount.toString(), 460f, y, paint)

                y += 18f

                // New page if required
                if (y > pageHeight - 60) {
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    )
                    canvas = page.canvas
                    y = 50f

                    // Draw logo on new page top-right
                    canvas.drawBitmap(scaledLogo, logoX, y - 40f, paint)

                    // Redraw header on new page
                    paint.isFakeBoldText = true
                    canvas.drawText("Date", 20f, y, paint)
                    canvas.drawText("Item", 120f, y, paint)
                    canvas.drawText("Qty", 340f, y, paint)
                    canvas.drawText("Rate", 390f, y, paint)
                    canvas.drawText("Amount", 460f, y, paint)
                    paint.isFakeBoldText = false
                    y += 20f
                }
            }

            document.finishPage(page)

            // ---------- SAVE PDF ----------
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savePdfForAndroidQAndAbove(document, fileName)
            } else {
                savePdfForLegacy(document, fileName)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    // ---------------- SAVE FOR ANDROID Q+ ----------------
    private fun savePdfForAndroidQAndAbove(document: PdfDocument, fileName: String) {
        try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = contentResolver.insert(
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                values
            ) ?: throw Exception("Failed to save PDF")

            contentResolver.openOutputStream(uri)?.use {
                document.writeTo(it)
            }
            document.close()
            Toast.makeText(this, "PDF saved in Downloads\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ---------------- SAVE FOR LEGACY (API <29) ----------------
    private fun savePdfForLegacy(document: PdfDocument, fileName: String) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use {
                document.writeTo(it)
            }
            document.close()
            Toast.makeText(this, "PDF saved in Downloads\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
