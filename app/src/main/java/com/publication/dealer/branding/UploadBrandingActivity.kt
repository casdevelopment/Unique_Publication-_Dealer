package com.publication.dealer.branding

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.publication.dealer.branding.viewmodel.AddBrandingViewModel
import com.publication.dealer.databinding.ActivityUploadBrandingBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class UploadBrandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBrandingBinding
    private val viewModel: AddBrandingViewModel by viewModel()

    private var selectedImageFile: File? = null

    private var userId: String = ""
    private var typeId: Int = 0


    // ------------------ GALLERY ------------------
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleGalleryImage(it) }
    }

    // ------------------ CAMERA ------------------
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { handleCameraImage(it) } ?: Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBrandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId") ?: ""
        typeId = intent.getIntExtra("typeId", 0)



        setupClicks()
        showTitle()
        resetImageViews()
    }

    private fun showTitle() {

        if(typeId == 1){
            binding.tvTitle.text = "Upload Main Hoarding"
            binding.tvSubTitle.text = "Main Hoarding"
        }else if(typeId == 2){
            binding.tvTitle.text = "Upload Poster"
            binding.tvSubTitle.text = "Poster"
        }else if(typeId == 3){
            binding.tvTitle.text = "Upload Shelf Talker"
            binding.tvSubTitle.text = "Shelf Talker"
        }else if(typeId == 4){
            binding.tvTitle.text = "Upload Counter Top"
            binding.tvSubTitle.text = "Counter Top"
        }else{
            binding.tvTitle.text = "Upload Branding"
            binding.tvSubTitle.text = "Other Branding"
        }


    }

    private fun setupClicks() {
        binding.backBtn.setOnClickListener { finish() }

      //  binding.btnCancel.setOnClickListener { finish() }

        binding.imageContainer.setOnClickListener { showImagePickerDialog() }

        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) callAddShopApi()
        }
    }

    // ================= IMAGE PICKER =================
    private fun showImagePickerDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpenCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> openCamera()
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(this)
                    .setTitle("Camera Permission Required")
                    .setMessage("Camera access is required to take shop images.")
                    .setPositiveButton("Grant") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null) // TakePicturePreview automatically opens camera and returns bitmap
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleCameraImage(bitmap: Bitmap) {
        // Show immediately
        binding.imgHoarding.visibility = View.VISIBLE
        binding.uploadHoarding.visibility = View.GONE
        binding.imgHoarding.setImageBitmap(bitmap)

        // Save bitmap to file for API upload
        selectedImageFile = File(getExternalFilesDir(null), "shop_${System.currentTimeMillis()}.jpg")
        FileOutputStream(selectedImageFile!!).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun handleGalleryImage(uri: Uri) {
        selectedImageFile = getFileFromUri(uri)
        binding.imgHoarding.visibility = View.VISIBLE
        binding.uploadHoarding.visibility = View.GONE

        Glide.with(this)
            .load(selectedImageFile)
            .centerCrop()
            .into(binding.imgHoarding)
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)!!
        val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        inputStream.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        return tempFile
    }

    private fun resetImageViews() {
        selectedImageFile = null
        binding.imgHoarding.visibility = View.GONE
        binding.uploadHoarding.visibility = View.VISIBLE
    }

    private fun validateInputs(): Boolean {

        var valid = true

        binding.etImageError.visibility = View.GONE


        if (selectedImageFile == null) {
            binding.etImageError.visibility = View.VISIBLE
            binding.etImageError.text = "Shop image required"
            valid = false
        }



        return valid
    }


    private fun TextView.show(msg: String) {
        visibility = View.VISIBLE
        text = msg
    }

    private fun callAddShopApi() {

        val userIdBody = userId.trim().toRequestBody()

        val type: RequestBody = typeId.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val file: MultipartBody.Part? = selectedImageFile?.let {
            MultipartBody.Part.createFormData(
                "file",
                it.name,
                it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
        }

        AppUtil.startLoader(this)
        viewModel.addBranding(userIdBody, type, file)
            .observe(this) { apiResponse ->
                AppUtil.stopLoader()
                when (apiResponse.status) {
                    Status.SUCCESS -> {
                        val retrofitResponse = apiResponse.data
                        if (retrofitResponse != null) {
                            if (retrofitResponse.isSuccessful) {
                                val baseResponse = retrofitResponse.body()
                                Toast.makeText(this, baseResponse?.message ?: "Branding added", Toast.LENGTH_SHORT).show()
                                if (baseResponse?.success == true) finish()
                            } else {
                                val errorMessage = try {
                                    val errorBody = retrofitResponse.errorBody()?.string()
                                    if (!errorBody.isNullOrEmpty()) {
                                        Gson().fromJson(errorBody, BaseResponse::class.java)?.message ?: "Something went wrong"
                                    } else "Something went wrong"
                                } catch (e: Exception) {
                                    "Something went wrong"
                                }
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Status.ERROR -> {
                        Toast.makeText(this, apiResponse.message ?: "Network error", Toast.LENGTH_SHORT).show()
                    }

                    Status.LOADING -> { /* handled by AppUtil.startLoader() */ }
                }
            }
    }


    private fun String.toRequestBody() = toRequestBody("text/plain".toMediaTypeOrNull())


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera()
        else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
    }
}
