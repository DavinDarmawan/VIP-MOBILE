package com.example.vip_mobile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InitialDepositActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_deposit)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val phone = intent.getStringExtra("PHONE") ?: "-"
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: "-"

        // Update UI with data from Intent
        // (Assuming IDs in activity_initial_deposit.xml, might need to add them)
    }
}
