package com.rie.alkisah.Screens.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.rie.alkisah.databinding.ActivityMrRegisterBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "store")

class MrRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMrRegisterBinding
    private lateinit var mrAuthVModel: MrAuthVModel
    private lateinit var factory: MrVMFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMrRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupVModel()
        setupAc()
        playAnimation()
    }

    private fun setupVModel() {
        factory = MrVMFactory.getInstance(this)
        mrAuthVModel = ViewModelProvider(this, factory)[MrAuthVModel::class.java]
    }

    private fun playAnimation() {
        val views = arrayOf(
            binding.edRegisterName,
            binding.edRegisterEmail,
            binding.edRegisterPassword,
            binding.edRegisterConfirmPass,
            binding.buttonRegister,
            binding.suggestLogin
        )
        AnimatorSet().apply {
            playSequentially(*views.map {
                ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(411)
            }.toTypedArray())
            startDelay = 325
        }.start()
    }

    private fun setupAc() {
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, MrLoginActivity::class.java))
            finishAffinity()
        }

        binding.buttonRegister.setOnClickListener {
            val fullName = binding.edRegisterName.text?.trim().toString()
            val emailRegister = binding.edRegisterEmail.text?.trim().toString()
            val passwordRegister = binding.edRegisterPassword.text?.trim().toString()
            val confirmPasswordRegister = binding.edRegisterConfirmPass.text?.trim().toString()

            when {
                isRegistrationInputInvalid(fullName, emailRegister, passwordRegister, confirmPasswordRegister) -> {
                    showAlertDialog(getString(R.string.ops), "Semua inputan tidak boleh kosong")
                }
                !isValidEmail(emailRegister) -> {
                    binding.edRegisterEmail.error = resources.getString(R.string.email_invalid)
                    binding.edRegisterEmail.requestFocus()
                }
                passwordRegister.length < 8 -> {
                    binding.edRegisterPassword.error =
                        resources.getString(R.string.password_minimum_character)
                    binding.edRegisterPassword.requestFocus()
                }
                confirmPasswordRegister != passwordRegister -> {
                    binding.edRegisterConfirmPass.error = "Password tidak sama"
                    binding.edRegisterConfirmPass.requestFocus()
                }
                else -> {
                    hideKeyboard()
                    register(fullName, emailRegister, passwordRegister)
                }
            }
        }
    }

    private fun isRegistrationInputInvalid(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
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
            create().show()
        }
    }

    private fun register(name: String, email: String, password: String) {
        showLoading(true)
        mrAuthVModel.doingRegister(name, email, password).observe(this) { user ->
            when (user) {
                is Result.Success -> {
                    showLoading(false)
                    showRegistrationSuccessDialog()
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

    private fun showRegistrationSuccessDialog() {
        AlertDialog.Builder(this@MrRegisterActivity).apply {
            setTitle("Yeah!")
            setMessage("Akun Anda berhasil dibuat dan siap digunakan. Login dan lihat apa yang dilakukan orang lain!")
            setPositiveButton("Lanjutkan") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showSnackBar(value: String) {
        Snackbar.make(
            binding.buttonRegister, value, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.overlayBg.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}