package com.rie.alkisah.Screens.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rie.alkisah.Screens.viewmodel.MainVModel
import com.rie.alkisah.base.MrVMFactory
import com.rie.alkisah.database.adapter.MrLoadingAdapter
import com.rie.alkisah.database.adapter.MrPagingAdapter
import com.rie.alkisah.databinding.ActivityMainBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "store")

class MainActivity : AppCompatActivity() {
    private lateinit var mainVModel: MainVModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: MrVMFactory
    private lateinit var adapter: MrPagingAdapter
    private lateinit var userToken: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("token", "") ?: ""

        setupVModel()
        setupA()
        recyclerView()
        getStories()
    }

    private fun setupVModel() {
        factory = MrVMFactory.getInstance(this)
        mainVModel = ViewModelProvider(this, factory)[MainVModel::class.java]
        mainVModel.getUser().observe(this) { user ->
            val name = user.users
            binding.txtUsernameAccount.text = name.replaceFirstChar { it.uppercase() }
            saveUserLoginStatus(true)
        }
    }

    override fun onResume() {
        super.onResume()
        val isLoggedIn = isUserLoggedIn()
        if (!isLoggedIn) {
            navigateToLogin()
        } else {
            getStories()
            recyclerView()
        }
    }

    private fun recyclerView() {
        binding.rvKisah.layoutManager = LinearLayoutManager(this)
        binding.rvKisah.setHasFixedSize(true)
        adapter = MrPagingAdapter()
    }

    private fun getStories() {
        binding.rvKisah.adapter = adapter.withLoadStateFooter(
            footer = MrLoadingAdapter { adapter.retry() }
        )
        mainVModel.getStory().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }


    private fun setupA() {
        setupLogoutButton()
        setupUploadButton()
        setupRefreshLayout()
        setupMapsButton()
    }

    private fun setupLogoutButton() {
        binding.ivPhoto.setOnClickListener {
            logout()
        }
    }

    private fun setupUploadButton() {
        binding.favButton.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshRecyclerView()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupMapsButton() {
        binding.maps.setOnClickListener {
            startActivity(Intent(this, MrMapsActivity::class.java))
        }
    }

    private fun refreshRecyclerView() {
        recyclerView()
        getStories()
    }


    private fun logout() {
        mainVModel.logout()
        saveUserLoginStatus(false)
        navigateToLogin()
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun saveUserLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, MrLoginActivity::class.java))
        finishAffinity()
    }
}
