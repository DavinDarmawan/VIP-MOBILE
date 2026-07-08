package com.example.vip_mobile

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vip_mobile.data.remote.SupabaseClient
import com.example.vip_mobile.data.service.BankingService
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class BalanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance_report) // Sesuaikan dengan nama layout Anda
        
        loadBalance()
    }

    private fun loadBalance() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user == null) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val rekenings = BankingService.getRekeningByUserId(user.id)
                if (rekenings.isNotEmpty()) {
                    val rek = rekenings[0]
                    findViewById<TextView>(R.id.tvAccountBalance).text = "Rp ${rek.saldo}"
                    findViewById<TextView>(R.id.tvAccountNumber).text = rek.no_rekening
                }
            } catch (e: Exception) {
                Toast.makeText(this@BalanceActivity, "Gagal mengambil saldo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
