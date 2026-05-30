package com.example.vip_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vip_mobile.data.model.AuthError
import com.example.vip_mobile.data.model.AuthResult
import com.example.vip_mobile.data.model.RegisterRequest
import com.example.vip_mobile.data.repository.AuthRepository
import com.example.vip_mobile.data.repository.AuthRepositoryImpl
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val authRepository: AuthRepository = AuthRepositoryImpl()
    private lateinit var registerButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        setupInsets()
        setupActions()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupActions() {
        registerButton = findViewById(R.id.btnRegisterSubmit)

        registerButton.setOnClickListener {
            val name = findViewById<TextInputEditText>(R.id.etRegisterName).text.toString().trim()
            val phone = findViewById<TextInputEditText>(R.id.etRegisterPhone).text.toString().trim()
            val password = findViewById<TextInputEditText>(R.id.etRegisterPassword).text.toString().trim()
            val confirmPassword = findViewById<TextInputEditText>(R.id.etRegisterConfirmPassword).text.toString().trim()

            when {
                name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    showToast(getString(R.string.auth_error_required))
                }
                password != confirmPassword -> {
                    showToast(getString(R.string.auth_error_password_mismatch))
                }
                else -> submitRegister(name, phone, password)
            }
        }

        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener {
            finish()
        }
    }

    private fun submitRegister(name: String, phone: String, password: String) {
        lifecycleScope.launch {
            setLoadingState(true)
            val result = authRepository.register(
                RegisterRequest(
                    name = name,
                    phone = phone,
                    password = password
                )
            )
            setLoadingState(false)

            when (result) {
                is AuthResult.Success -> {
                    showToast(getString(R.string.auth_success_register))
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }

                is AuthResult.Error -> {
                    val errorMessage = when (result.error) {
                        AuthError.PHONE_ALREADY_REGISTERED -> getString(R.string.auth_error_phone_exists)
                        AuthError.INVALID_CREDENTIALS -> getString(R.string.auth_error_invalid_credentials)
                    }
                    showToast(errorMessage)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        registerButton.isEnabled = !isLoading
        registerButton.text = if (isLoading) {
            getString(R.string.auth_loading_register)
        } else {
            getString(R.string.auth_register_button)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


