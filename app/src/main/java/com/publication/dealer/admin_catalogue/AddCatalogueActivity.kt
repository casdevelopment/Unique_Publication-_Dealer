package com.publication.dealer.admin_catalogue

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.publication.dealer.PDF_Upload.viewmodel.UploadPdfViewModel
import com.publication.dealer.R
import com.publication.dealer.admin_catalogue.model.AddCatalogRequestModel
import com.publication.dealer.admin_catalogue.viewmodel.CreateCatalogViewModel
import com.publication.dealer.databinding.ActivityAddCatalogueBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppConstants
import com.publication.dealer.util.AppUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddCatalogueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCatalogueBinding


    private val viewModelUpload: UploadPdfViewModel by viewModel()

    private val PDF_PICK_CODE = 1001
    private var selectedPdfUri: Uri? = null
    private var selectedPdfName: String? = null
    private var pdfSelectedCallback: ((Uri, String) -> Unit)? = null

    private var uploadedPdfUrl: String? = null

    private val viewModelCreateCatalog: CreateCatalogViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddCatalogueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvStatusTitle.text = "Upload PDF"
        binding.tvStatusSubtitle.text = "Select a PDF file to start creating your catalog"
        binding.imgUploadStatus.setImageResource(R.drawable.pdfpng)
        binding.formFields.alpha = 0.5f
        binding.formFields.isEnabled = false
        binding.btnSubmitCatalog.visibility = View.GONE

        startPulseWithColorAnimation(binding.cardUploadPDF)




        setupClickListeners()

    }


    private fun startPulseWithColorAnimation(view: CardView) {
        // Subtle scale animation
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.02f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.02f)

        val pulseAnim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        pulseAnim.duration = 600
        pulseAnim.repeatCount = ObjectAnimator.INFINITE
        pulseAnim.repeatMode = ObjectAnimator.REVERSE

        // Background color animation using colors from colors.xml
        val colorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(view.context, R.color.orangelite),
            ContextCompat.getColor(view.context, R.color.orangelight)
        )

        colorAnimator.duration = 600
        colorAnimator.repeatCount = ValueAnimator.INFINITE
        colorAnimator.repeatMode = ValueAnimator.REVERSE
        colorAnimator.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            view.setCardBackgroundColor(color)
        }

        // Start animations
        pulseAnim.start()
        colorAnimator.start()

        // Save animators in tag to stop later if needed
        view.tag = listOf(pulseAnim, colorAnimator)
    }




    private fun setupClickListeners() {

        with(binding) {

            cardUploadPDF.setOnClickListener {

                val animators = binding.cardUploadPDF.tag as? List<*>
                animators?.forEach { (it as? Animator)?.cancel() }

                binding.cardUploadPDF.scaleX = 1f
                binding.cardUploadPDF.scaleY = 1f
                binding.cardUploadPDF.setCardBackgroundColor(Color.WHITE)



                showUploadPdfDialog()
            }

          btnSubmitCatalog.setOnClickListener {
                if (validateInputs()) {
                    callAddCatalogueApi()
                }
          }

            backBtn.setOnClickListener {
                finish()
            }

        }
    }

    private fun showUploadPdfDialog() {
        val dialog = Dialog(this@AddCatalogueActivity)
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
            startActivityForResult(intent, PDF_PICK_CODE)
        }

        // Upload PDF
        btnUpload.setOnClickListener {
            if (selectedPdfUri != null) {
                uploadPdf(selectedPdfUri!!, dialog)
            } else {
                Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show()
            }
        }



        // Update file name in EditText after selection
        pdfSelectedCallback = { uri, name ->
            selectedPdfUri = uri
            selectedPdfName = name
            etSelectedPdf.setText(name)
            tilSelectedPdf.hint = "Selected File"
        }


        dialog.show()
    }

    private fun uploadPdf(uri: Uri, dialog: Dialog) {

        val adminId = AppConstants.userData?.userId ?: "Admin"

        if (adminId.isEmpty()) {
            Toast.makeText(this, "Admin ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, getFileName(uri))

            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData(
                "file",  // 🔥 must match API parameter name
                file.name,
                requestFile
            )

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

                            Toast.makeText(
                                this, baseResponse?.message ?: "Upload successful", Toast.LENGTH_LONG).show()

                            if (baseResponse?.success == true) {

                                binding.tvStatusTitle.text = "Uploaded Successfully"
                                binding.tvStatusSubtitle.text = file.name
                                binding.imgUploadStatus.setImageResource(R.drawable.greentick) // Tick icon

                                // Enable form fields below
                                binding.formFields.alpha = 1f
                                binding.boardName.isEnabled = true
                                binding.catalogName.isEnabled = true
                                binding.catalogType.isEnabled = true
                                binding.btnSubmitCatalog.visibility = View.VISIBLE

                                uploadedPdfUrl = baseResponse.shoppdfurl

                                dialog.dismiss()
                            }

                        } else {
                            Toast.makeText(this, "Upload failed", Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        Toast.makeText(
                            this,
                            state.message ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
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
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow("_display_name"))
                }
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
        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK) {
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
            binding.catalogTypeError.text = "catalogue type required"
            valid = false
        }


        return valid
    }

    private fun callAddCatalogueApi() {


        val request = AddCatalogRequestModel(
            id = 0,
            boardName = binding.boardName.text.toString().trim(),
            catalogName = binding.catalogName.text.toString().trim(),
            catalogURL = uploadedPdfUrl!!,
            catalogType = binding.catalogType.text.toString().trim()
        )

            viewModelCreateCatalog.createCatalog(request).observe(this) { state ->

                when (state.status) {

                    Status.LOADING -> {
                        AppUtil.startLoader(this)
                    }

                    Status.SUCCESS -> {
                        AppUtil.stopLoader()

                        val response = state.data ?: run {
                            Toast.makeText(this, "Empty response", Toast.LENGTH_LONG).show()
                            return@observe
                        }

                        // ✅ HTTP 2xx
                        if (response.isSuccessful && response.body() != null) {

                            val body = response.body()!!
                            Toast.makeText(this, body.message, Toast.LENGTH_LONG).show()

                            if (body.success) {

                                finish()
                            }

                        }
                        else {
                            val errorMsg = try {
                                val errorJson = response.errorBody()?.string()
                                if (!errorJson.isNullOrEmpty()) {
                                    Gson().fromJson(errorJson, BaseResponse::class.java)?.message
                                } else null
                            } catch (e: Exception) {
                                null
                            }

                            Toast.makeText(this, errorMsg ?: "Server error", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    Status.ERROR -> {
                        AppUtil.stopLoader()
                        Toast.makeText(this, state.message ?: "Network error", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
