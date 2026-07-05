package com.example.vip_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vip_mobile.data.model.AuthError
import com.example.vip_mobile.data.model.AuthResult
import com.example.vip_mobile.data.model.LoginRequest
import com.example.vip_mobile.data.repository.AuthRepository
import com.example.vip_mobile.data.repository.AuthRepositoryImpl
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.vip_mobile.data.storage.TokenStore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val authRepository: AuthRepository = AuthRepositoryImpl()
    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        setupInsets()
        setupActions()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupActions() {
        loginButton = findViewById(R.id.btnLoginSubmit)

        loginButton.setOnClickListener {
            val phone = findViewById<TextInputEditText>(R.id.etLoginPhone).text.toString().trim()
            val password = findViewById<TextInputEditText>(R.id.etLoginPassword).text.toString().trim()

            when {
                phone.isEmpty() -> showToast(getString(R.string.auth_error_required))
                password.isEmpty() -> showToast(getString(R.string.auth_error_required))
                else -> submitLogin(phone, password)
            }
        }

        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun submitLogin(phone: String, password: String) {
        lifecycleScope.launch {
            setLoadingState(true)
            val result = authRepository.login(LoginRequest(phone = phone, password = password))
            setLoadingState(false)

            when (result) {
                is AuthResult.Success -> {
                    showToast(getString(R.string.auth_success_login))
                    val token = result.user.token
                    if (token != null && token.isNotEmpty()) {
                        TokenStore.saveToken(this@LoginActivity, token)
                    }
                    finish()
                }

                is AuthResult.Error -> {
                    val errorMessage = when (result.error) {
                        AuthError.INVALID_CREDENTIALS -> getString(R.string.auth_error_invalid_credentials)
                        AuthError.PHONE_ALREADY_REGISTERED -> getString(R.string.auth_error_phone_exists)
                    }
                    showToast(errorMessage)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        loginButton.isEnabled = !isLoading
        loginButton.text = if (isLoading) {
            getString(R.string.auth_loading_login)
        } else {
            getString(R.string.auth_login_button)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}




