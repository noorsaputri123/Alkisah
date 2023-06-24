package com.rie.alkisah.Screens.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rie.alkisah.R
import com.rie.alkisah.Screens.viewmodel.MainVModel
import com.rie.alkisah.base.MrVMFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "store")

@SuppressLint("CustomSplashScreen")
class MrSplashActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainVModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mr_splash)

        mainViewModel = ViewModelProvider(this, MrVMFactory.getInstance(this)).get(MainVModel::class.java)

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, MrLoginActivity::class.java))
            }
            finish()
        }
    }
}
