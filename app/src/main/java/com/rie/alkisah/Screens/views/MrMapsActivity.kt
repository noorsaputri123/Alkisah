package com.rie.alkisah.Screens.views

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.rie.alkisah.R
import com.rie.alkisah.Screens.viewmodel.MrMapsVModel
import com.rie.alkisah.base.Result
import com.rie.alkisah.base.MrVMFactory
import com.rie.alkisah.databinding.ActivityMrMapsBinding
import java.io.IOException
import java.util.*

class MrMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMrMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var userToken: String
    private lateinit var mapViewModel: MrMapsVModel
    private lateinit var viewModelFactory: MrVMFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMrMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Maps"
        setupViewModel()
        setupView()
    }

    private fun setupViewModel() {
        viewModelFactory = MrVMFactory.getInstance(this)
        mapViewModel = ViewModelProvider(this, viewModelFactory).get(MrMapsVModel::class.java)
        mapViewModel.getUser().observe(this) { user ->
            userToken = user.token
        }
    }

    private fun setupView() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.fr_map_list) as SupportMapFragment
        try {
            mapFragment.getMapAsync(this)
        } catch (e: Exception) {
            Log.e(TAG, "setupView: ${e.message}")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setPadding(0, 150, 0, 0)
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        getUser()
        getLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            try {
                if (isGranted) {
                    getLocation()
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener {
                try {
                    val myLocation = mMap.myLocation
                    if (myLocation != null) {
                        val myLatLng = LatLng(myLocation.latitude, myLocation.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f))
                        true
                    } else {
                        showSnackbar("Lokasi tidak tersedia")
                        false
                    }
                } catch (e: Exception) {
                    showSnackbar("Terjadi kesalahan saat mendapatkan lokasi")
                    false
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun getUser() {
        val token = "Bearer $userToken"
        mapViewModel.getStoryLocation(token).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    result.data.listStory?.forEach { listStoryItem ->
                        val name = listStoryItem.name
                        val latLng = LatLng(listStoryItem.lat, listStoryItem.lon)
                        val addressName = getAddressName(listStoryItem.lat, listStoryItem.lon)
                        val snippet = addressName ?: "Alamat tidak ditemukan"
                        mMap.addMarker(MarkerOptions().position(latLng).title(name).snippet(snippet))
                        boundsBuilder.include(latLng)
                    }
                }
                is Result.Error -> {
                    showSnackbar("Terjadi kesalahan: ${result.error}")
                }
                else -> {}
            }
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MrMapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.isNotEmpty()) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            Log.e("GetAddressName", "GetAddressName: $e")
        }
        return addressName
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "MrMapsActivity"
    }
}
