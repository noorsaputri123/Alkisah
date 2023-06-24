package com.rie.alkisah.Screens.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.rie.alkisah.R
import com.rie.alkisah.Screens.reduceFileImage
import com.rie.alkisah.Screens.rotateFile
import com.rie.alkisah.Screens.uriToFile
import com.rie.alkisah.Screens.viewmodel.UploadVModel
import com.rie.alkisah.Screens.views.MainActivity
import com.rie.alkisah.base.MrVMFactory
import com.rie.alkisah.base.Result
import com.rie.alkisah.databinding.ActivityUploadBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService

// Noor Saputri
class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var uploadVModel: UploadVModel
    private lateinit var userToken: String
    private lateinit var factory: MrVMFactory
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var isCheckSwitchLoc: Boolean = false
    private var getFile: File? = null
    private var lat: Double? = null
    private var lon: Double? = null

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()
        setupUI()
        setupActionBar()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showPermissionDeniedError()
            }
        }
    }

    private fun setupUI() {
        switchUser(isCheckSwitchLoc)
        setupAction()
    }

    private fun setupViewModel() {
        factory = MrVMFactory.getInstance(this)
        uploadVModel = ViewModelProvider(this, factory)[UploadVModel::class.java]
        uploadVModel.getUser().observe(this) { user ->
            userToken = user.token
        }
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.tambah_cerita)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun switchUser(isCheckSwitchLoc: Boolean) {
        binding.switchLocation.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            this@UploadActivity.isCheckSwitchLoc = isChecked
            if (this@UploadActivity.isCheckSwitchLoc) {
                Log.d("TAG", "switchUser: $isCheckSwitchLoc")
                getMyLocation()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionDeniedError() {
        Toast.makeText(
            this,
            R.string.no_permission,
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun setupAction() {
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.edAdd.setOnClickListener { uploadStory() }
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text?.trim().toString()
        if (description.isEmpty() && getFile == null) {
            showAlertDialog(getString(R.string.oops), getString(R.string.must_fill_description))
        } else if (description.isEmpty()) {
            binding.edAddDescription.error = resources.getString(R.string.must_filled)
            binding.edAddDescription.requestFocus()
        } else if (!isCheckSwitchLoc) {
            Toast.makeText(this, R.string.select_location_first, Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }

    private fun startCamera() {
        val intent = Intent(this, MrCameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun openGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg = result.data?.data as Uri
                selectedImg.let { uri ->
                    val myFile = uriToFile(uri, this@UploadActivity)
                    getFile = myFile
                    binding.previewImg.setImageURI(uri)
                }
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation = LocationServices.getFusedLocationProviderClient(this)
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    val addressName = getAddressName(lat!!, lon!!)
                    Snackbar.make(
                        binding.root,
                        addressName.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Failed to get location",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                showPermissionDeniedError()
            }
        }

    private fun uploadImage() {
        showLoading(true)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description =
                binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val token = "Bearer $userToken"

            uploadVModel.addStory(token, imageMultipart, description, lat, lon).observe(this) {
                when (it) {
                    is Result.Success -> {
                        showLoading(false)
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this,
                            "${R.string.upload_failed}: " + it.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addressName: String? = null
        try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                addressName = address.getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 100
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        const val CAMERA_X_RESULT = 200
    }
}
