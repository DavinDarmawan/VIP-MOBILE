package com.example.vip_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupMenuActions()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupMenuActions() {
        // Login button navigates to LoginActivity
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Other menu items show coming soon message
        val otherMenuIds = listOf(
            R.id.btnBalance,
            R.id.btnTransferVip,
            R.id.btnTransferBank,
            R.id.btnBills,
            R.id.btnVoucher,
            R.id.btnVipCard,
            R.id.btnBranding
        )

        otherMenuIds.forEach { viewId ->
            findViewById<Button>(viewId).setOnClickListener {
                showComingSoonMessage()
            }
        }
    }

    private fun showComingSoonMessage() {
        Toast.makeText(
            this,
            getString(R.string.feature_coming_soon),
            Toast.LENGTH_SHORT
        ).show()
    }
}