package com.publication.dealer.branding

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.publication.dealer.branding.viewmodel.AddBrandingViewModel
import com.publication.dealer.databinding.ActivityEditBrandingBinding
import com.publication.dealer.network.Status
import com.publication.dealer.network.retofit.BaseResponse
import com.publication.dealer.util.AppUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class EditBrandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBrandingBinding
    private val viewModel: AddBrandingViewModel by viewModel()

    private var selectedImageFile: File? = null

    private var userId: String = ""
    private var typeId: Int = 0
    private var imageUrl: String? = null


    // ================= CAMERA =================
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { handleCameraImage(it) }
        }

    // ================= GALLERY =================
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleGalleryImage(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBrandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId") ?: ""
        typeId = intent.getIntExtra("typeId", 0)
        imageUrl = intent.getStringExtra("imageUrl") ?: ""

        setupClicks()
        prefillData()
    }

    // ================= PREFILL =================
    private fun prefillData() {

        if(typeId == 1){
            binding.tvTitle.text = "Update Main Hoarding"
            binding.tvSubTitle.text = "Main Hoarding"
        }else if(typeId == 2){
            binding.tvTitle.text = "Update POP"
            binding.tvSubTitle.text = "POP"
        }else if(typeId == 3){
            binding.tvTitle.text = "Update Shelf Talker"
            binding.tvSubTitle.text = "Shelf Talker"
        }else if(typeId == 4){
            binding.tvTitle.text = "Update Counter Top"
            binding.tvSubTitle.text = "Counter Top"
        }else{
            binding.tvTitle.text = "Update Branding"
            binding.tvSubTitle.text = "Other Branding"
        }


        if (!imageUrl.isNullOrEmpty()) {
            binding.imgHoarding.visibility = View.VISIBLE
            binding.uploadHoarding.visibility = View.GONE

            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(binding.imgHoarding.drawable)
                .into(binding.imgHoarding)
        }
    }

    private fun setupClicks() {

        binding.backBtn.setOnClickListener { finish() }

        binding.imageContainer.setOnClickListener { showImagePicker() }

        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) callAddApi()
        }


    }

    // ================= IMAGE PICKER =================
    private fun showImagePicker() {
        AlertDialog.Builder(this)
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                if (which == 0) openCamera() else openGallery()
            }.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleCameraImage(bitmap: Bitmap) {
        // Save bitmap to file
        selectedImageFile = File(cacheDir, "shop_${System.currentTimeMillis()}.jpg")
        FileOutputStream(selectedImageFile!!).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        // Display using Glide for proper scaling
        Glide.with(this)
            .load(selectedImageFile)
            .centerCrop()
            .into(binding.imgHoarding)

        binding.imgHoarding.visibility = View.VISIBLE
        binding.uploadHoarding.visibility = View.GONE
    }

    private fun handleGalleryImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)

        val file = File(cacheDir, "shop_${System.currentTimeMillis()}.jpg")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        selectedImageFile = file

        Glide.with(this)
            .load(file)
            .centerCrop()
            .into(binding.imgHoarding)

        binding.imgHoarding.visibility = View.VISIBLE
        binding.uploadHoarding.visibility = View.GONE
    }

    private fun validateInputs(): Boolean {

        var valid = true

        binding.etImageError.visibility = View.GONE

        if (selectedImageFile == null) {
            binding.etImageError.visibility = View.VISIBLE
            binding.etImageError.text = "Please select new image to update"
            return false
        }




        return valid
    }

    // ================= UPDATE API =================
    private fun callAddApi() {

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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

}
