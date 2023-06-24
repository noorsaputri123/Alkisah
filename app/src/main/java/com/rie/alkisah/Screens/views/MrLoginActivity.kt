package com.rie.alkisah.Screens.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rie.alkisah.R
import com.rie.alkisah.Screens.viewmodel.MrAuthVModel
import com.rie.alkisah.base.MrVMFactory
import com.rie.alkisah.base.Result
import com.rie.alkisah.databinding.ActivityMrLoginBinding
import com.rie.alkisah.model.MrUser

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "store")

class MrLoginActivity : AppCompatActivity() {
    private lateinit var mrAuthVModel: MrAuthVModel
    private lateinit var binding: ActivityMrLoginBinding
    private lateinit var factory: MrVMFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMrLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupVModel()
        setupAc()
        playA()
    }

    private fun playA() {
        val views = arrayOf(
            binding.edLoginEmail,
            binding.edLoginPassword,
            binding.buttonLogin,
            binding.suggestRegister
        )
        AnimatorSet().apply {
            playSequentially(*views.map {
                ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(411)
            }.toTypedArray())
            startDelay = 325
        }.start()
    }

    private fun setupVModel() {
        factory = MrVMFactory.getInstance(this)
        mrAuthVModel = ViewModelProvider(this, factory)[MrAuthVModel::class.java]
        mrAuthVModel.getUser().observe(this){
            Log.d("CheckToken", it.token)
        }
    }

    private fun setupAc() {
        binding.registernow.setOnClickListener {
            startActivity(Intent(this, MrRegisterActivity::class.java))
        }
        binding.buttonLogin.setOnClickListener {
            val email = binding.edLoginEmail.text?.trim().toString()
            val password = binding.edLoginPassword.text?.trim().toString()
            if (email.isEmpty() || password.isEmpty()) {
                showAlertDialog(getString(R.string.ops), "Email dan password tidak boleh kosong")
            } else if (!isValidEmail(email)) {
                binding.edLoginEmail.apply {
                    error = resources.getString(R.string.email_invalid)
                    requestFocus()
                }
            } else if (password.length < 8) {
                binding.edLoginPassword.apply {
                    error = resources.getString(R.string.password_minimum_character)
                    requestFocus()
                }
            } else {
                hideKeyboard()
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        showLoading(true)
        mrAuthVModel.doingLogin(email, password).observe(this) { user ->
            when (user) {
                is Result.Success -> {
                    showLoading(false)
                    val response = user.data
                    val name = response.loginResult?.name.toString()
                    val token = response.loginResult?.token.toString()
                    saveUserData(MrUser(name, email, token, true))
                    Log.d("CekTokenLogin", "Token: $token - Email: $email - Name: $name")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showSnackBar(user.error)
                    showLoading(false)
                }
                else -> {
                    showSnackBar("Something went wrong")
                    showLoading(false)
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun saveUserData(user: MrUser) {
        mrAuthVModel.saveUser(user)
    }

    private fun showSnackBar(value: String) {
        Snackbar.make(
            binding.root, value, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.overlayBg.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
