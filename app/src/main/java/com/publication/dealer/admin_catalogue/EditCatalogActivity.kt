package com.publication.dealer.admin_catalogue

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.publication.dealer.PDF_Upload.viewmodel.UploadPdfViewModel
import com.publication.dealer.R
import com.publication.dealer.admin_catalogue.model.AddCatalogRequestModel
import com.publication.dealer.admin_catalogue.viewmodel.GetCatalogByIdViewModel
import com.publication.dealer.admin_catalogue.viewmodel.UpdateCatalogViewModel
import com.publication.dealer.databinding.ActivityEditCatalogBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class EditCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCatalogBinding
    private val viewModel: GetCatalogByIdViewModel by viewModel()


    private var CatalogDetails: AddCatalogRequestModel? = null
    private var catalogId: Int = 0

    private val viewModelUpload: UploadPdfViewModel by viewModel()
    private val viewModelUpdate: UpdateCatalogViewModel by viewModel()


    private var selectedPdfUri: Uri? = null
    private var selectedPdfName: String? = null
    private var uploadedPdfUrl: String? = null
    private var pdfSelectedCallback: ((Uri, String) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)


        catalogId = intent.getIntExtra("ID", 0)

        if (catalogId == 0) {
            Toast.makeText(this, "Invalid  id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchCatalogDetails(catalogId)
        setupClickListeners()


    }


    private fun fetchCatalogDetails(catalogId: Int) {

        viewModel.getCatalogById(catalogId).observe(this) { apiResponse ->

            when (apiResponse.status) {

                Status.LOADING -> {
                    AppUtil.startLoader(this)
                }

                Status.SUCCESS -> {
                    AppUtil.stopLoader()

                    val retrofitResponse = apiResponse.data

                    if (retrofitResponse != null && retrofitResponse.isSuccessful) {

                        val baseResponse = retrofitResponse.body()
                        //  Toast.makeText(this, baseResponse?.message, Toast.LENGTH_SHORT).show()

                        if (baseResponse?.success == true) {


                                baseResponse.data?.let {
                                    CatalogDetails = it      // ✅ save data
                                    bindData(it)
                                }

                        }

                    } else {
                        showError(retrofitResponse)
                    }
                }

               Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindData(catalog: AddCatalogRequestModel) {

        binding.boardName.setText(catalog.boardName)
        binding.catalogName.setText(catalog.catalogName)
        binding.catalogType.setText(catalog.catalogType)

        val fileName = catalog.catalogURL.substringAfterLast("/")
        binding.tvStatusSubtitle.text = fileName // make sure you have a TextView in XML with id tvPdfName

        uploadedPdfUrl = catalog.catalogURL

    }

    private fun showError(response: retrofit2.Response<*>?) {
        val message = try {
            val errorBody = response?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                Gson().fromJson(
                    errorBody,
                    BaseResponse::class.java
                ).message
            } else "Something went wrong"
        } catch (e: Exception) {
            "Something went wrong"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { finish() }

        // Replace PDF
        binding.imgUploadStatus.setOnClickListener {
            showUploadPdfDialog()
        }

        // Update catalog
        binding.btnSubmitCatalog.setOnClickListener {
            if (validateInputs()) {
                callUpdateCatalogApi()
            }
        }
    }

    private fun showUploadPdfDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_upload_pdf)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnSelect = dialog.findViewById<Button>(R.id.btnSelectPdf)
        val btnUpload = dialog.findViewById<Button>(R.id.btnUploadPdf)
        val etSelectedPdf = dialog.findViewById<EditText>(R.id.etSelectedPdf)
        val tilSelectedPdf = dialog.findViewById<TextInputLayout>(R.id.resetIdLayout)
        tilSelectedPdf.hint = "Select file"

        // Select PDF
        btnSelect.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, 1001)
        }

        // Upload PDF
        btnUpload.setOnClickListener {
            if (selectedPdfUri != null) uploadPdf(selectedPdfUri!!, dialog)
            else Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show()
        }

        pdfSelectedCallback = { uri, name ->
            selectedPdfUri = uri
            selectedPdfName = name
            etSelectedPdf.setText(name)
            tilSelectedPdf.hint = "Selected File"
        }

        dialog.show()
    }

    private fun uploadPdf(uri: Uri, dialog: Dialog) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, getFileName(uri))
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            viewModelUpload.uploadShopPdf(body).observe(this) { state ->
                when (state.status) {
                    Status.LOADING -> {
                        AppUtil.startLoader(this)
                        binding.tvStatusTitle.text = "Uploading..."
                        binding.tvStatusSubtitle.text = "Please wait..."
                        binding.imgUploadStatus.setImageResource(R.drawable.pdfpng)
                    }
                    Status.SUCCESS -> {
                        AppUtil.stopLoader()
                        val response = state.data
                        if (response != null && response.isSuccessful) {
                            val baseResponse = response.body()
                            Toast.makeText(this, baseResponse?.message ?: "Upload successful", Toast.LENGTH_LONG).show()
                            if (baseResponse?.success == true) {
                                binding.tvStatusTitle.text = "Uploaded Successfully"
                                binding.tvStatusSubtitle.text = file.name
                                binding.imgUploadStatus.setImageResource(R.drawable.greentick)
                                uploadedPdfUrl = baseResponse.shoppdfurl
                                dialog.dismiss()
                            }
                        } else Toast.makeText(this, "Upload failed", Toast.LENGTH_LONG).show()
                    }
                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        Toast.makeText(this, state.message ?: "Network error", Toast.LENGTH_LONG).show()
                    }
                }
            }

        } catch (e: Exception) {
            Toast.makeText(this, "File error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) result = it.getString(it.getColumnIndexOrThrow("_display_name"))
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) result = result?.substring(cut + 1)
        }
        return result ?: "file.pdf"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileName(uri)
                pdfSelectedCallback?.invoke(uri, fileName)
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.boardNameError.visibility = View.GONE
        binding.catalogNameError.visibility = View.GONE
        binding.catalogTypeError.visibility = View.GONE

        val boardName = binding.boardName.text.toString().trim()
        if (boardName.isEmpty()) {
            binding.boardNameError.visibility = View.VISIBLE
            binding.boardNameError.text = "Board name required"
            valid = false
        }

        val catalogName = binding.catalogName.text.toString().trim()
        if (catalogName.isEmpty()) {
            binding.catalogNameError.visibility = View.VISIBLE
            binding.catalogNameError.text = "Catalogue name required"
            valid = false
        }

        val catalogType = binding.catalogType.text.toString().trim()
        if (catalogType.isEmpty()) {
            binding.catalogTypeError.visibility = View.VISIBLE
            binding.catalogTypeError.text = "Catalogue type required"
            valid = false
        }

        return valid
    }

    private fun callUpdateCatalogApi() {
        val request = AddCatalogRequestModel(
            id = catalogId,
            boardName = binding.boardName.text.toString().trim(),
            catalogName = binding.catalogName.text.toString().trim(),
            catalogURL = uploadedPdfUrl ?: "",
            catalogType = binding.catalogType.text.toString().trim()
        )

        viewModelUpdate.updateCatalog(request).observe(this) { state ->
            when (state.status) {
                Status.LOADING -> AppUtil.startLoader(this)
                Status.SUCCESS -> {
                    AppUtil.stopLoader()
                    val response = state.data
                    if (response != null && response.isSuccessful) {
                        val body = response.body()
                        Toast.makeText(this, body?.message ?: "Updated successfully", Toast.LENGTH_LONG).show()
                        if (body?.success == true) finish()
                    } else {
                        val errorMsg = try {
                            val errorJson = response?.errorBody()?.string()
                            if (!errorJson.isNullOrEmpty()) Gson().fromJson(errorJson, BaseResponse::class.java)?.message else null
                        } catch (e: Exception) {
                            null
                        }
                        Toast.makeText(this, errorMsg ?: "Server error", Toast.LENGTH_LONG).show()
                    }
                }
                Status.ERROR -> {
                    AppUtil.stopLoader()
                    Toast.makeText(this, state.message ?: "Network error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

//    private fun showError(response: retrofit2.Response<*>?) {
//        val message = try {
//            val errorBody = response?.errorBody()?.string()
//            if (!errorBody.isNullOrEmpty()) Gson().fromJson(errorBody, BaseResponse::class.java).message else "Something went wrong"
//        } catch (e: Exception) {
//            "Something went wrong"
//        }
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
}