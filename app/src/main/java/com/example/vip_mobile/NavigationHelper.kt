package com.example.vip_mobile

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationHelper {
    fun setupBottomNavigation(activity: Activity, bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (activity !is AdminDashboardActivity) {
                        activity.startActivity(Intent(activity, AdminDashboardActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_cs -> {
                    if (activity !is OpenAccountActivity) {
                        activity.startActivity(Intent(activity, OpenAccountActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_teller -> {
                    if (activity !is SavingTransactionActivity) {
                        activity.startActivity(Intent(activity, SavingTransactionActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_history -> {
                    if (activity !is TransactionHistoryActivity) {
                        activity.startActivity(Intent(activity, TransactionHistoryActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                R.id.nav_report -> {
                    if (activity !is BalanceActivity) {
                        activity.startActivity(Intent(activity, BalanceActivity::class.java))
                        activity.finish()
                    }
                    true
                }
                else -> false
            }
        }

        // Set selected item based on current activity
        when (activity) {
            is AdminDashboardActivity -> bottomNav.selectedItemId = R.id.nav_home
            is OpenAccountActivity -> bottomNav.selectedItemId = R.id.nav_cs
            is SavingTransactionActivity -> bottomNav.selectedItemId = R.id.nav_teller
            is TransactionHistoryActivity -> bottomNav.selectedItemId = R.id.nav_history
            is BalanceActivity -> bottomNav.selectedItemId = R.id.nav_report
        }
    }
}
