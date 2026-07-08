package com.example.vip_mobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransactionHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        NavigationHelper.setupBottomNavigation(this, bottomNav)
    }
}
