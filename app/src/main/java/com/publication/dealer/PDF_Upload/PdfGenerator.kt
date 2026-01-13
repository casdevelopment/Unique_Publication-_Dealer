package com.publication.dealer.PDF_Upload

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.publication.dealer.R
import com.publication.dealer.sales.model.SalesDetailResponseModel
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun createSalesPdf(context: Context, salesList: ArrayList<SalesDetailResponseModel>) {

        // Show file name dialog immediately
        val dialog = android.app.Dialog(context)
        dialog.setContentView(R.layout.dialog_file_name)
        dialog.setCancelable(false)

        val etFileName = dialog.findViewById<EditText>(R.id.etFileName)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
        val btnSave = dialog.findViewById<AppCompatButton>(R.id.btnSave)

        etFileName.setText("Sales_Report")

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val fileName = etFileName.text.toString().trim()
            if (fileName.isEmpty()) {
                Toast.makeText(context, "File name required", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                generatePdf(context, salesList, "$fileName.pdf")
            }
        }

        // Optional: nice width
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun generatePdf(context: Context, salesList: ArrayList<SalesDetailResponseModel>, fileName: String) {
        try {
            val document = PdfDocument()
            val pageWidth = 595   // A4
            val pageHeight = 842

            val paint = Paint()
            paint.textSize = 11f

            // Logo
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
            val scaledLogo = android.graphics.Bitmap.createScaledBitmap(logoBitmap, 50, 50, false)

            var pageNumber = 1
            var y = 50f

            var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            var canvas = page.canvas

            // Title + logo
            paint.textSize = 16f
            paint.isFakeBoldText = true
            val title = "Sales Report"
            val textWidth = paint.measureText(title)
            val centerX = (pageWidth - textWidth) / 2
            canvas.drawText(title, centerX, y, paint)

            val logoX = pageWidth - scaledLogo.width - 20f
            val logoY = y - scaledLogo.height + 10f
            canvas.drawBitmap(scaledLogo, logoX, logoY, paint)

            paint.textSize = 11f
            paint.isFakeBoldText = false
            y += 60f

            // Header
            paint.isFakeBoldText = true
            canvas.drawText("Date", 20f, y, paint)
            canvas.drawText("Item", 120f, y, paint)
            canvas.drawText("Qty", 340f, y, paint)
            canvas.drawText("Rate", 390f, y, paint)
            canvas.drawText("Amount", 460f, y, paint)
            paint.isFakeBoldText = false
            y += 20f

            // Rows
            for (item in salesList) {
                canvas.drawText(item.saleDate.take(10), 20f, y, paint)
                canvas.drawText(item.itemName.take(28), 120f, y, paint)
                canvas.drawText(item.qty.toString(), 340f, y, paint)
                canvas.drawText(item.rate.toString(), 390f, y, paint)
                canvas.drawText(item.amount.toString(), 460f, y, paint)

                y += 18f

                // New page
                if (y > pageHeight - 60) {
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                    canvas = page.canvas
                    y = 50f

                    // Logo on new page
                    canvas.drawBitmap(scaledLogo, logoX, y - 40f, paint)

                    // Header on new page
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

            // Save PDF
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savePdfForAndroidQAndAbove(context, document, fileName)
            } else {
                savePdfForLegacy(context, document, fileName)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun savePdfForAndroidQAndAbove(context: Context, document: PdfDocument, fileName: String) {
        try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                values
            ) ?: throw Exception("Failed to save PDF")

            context.contentResolver.openOutputStream(uri)?.use {
                document.writeTo(it)
            }
            document.close()
            Toast.makeText(context, "PDF saved in Downloads\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun savePdfForLegacy(context: Context, document: PdfDocument, fileName: String) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use {
                document.writeTo(it)
            }
            document.close()
            Toast.makeText(context, "PDF saved in Downloads\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
