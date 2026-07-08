package com.example.vip_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.vip_mobile.data.remote.SupabaseClient
import com.example.vip_mobile.data.service.BankingService
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class ReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadBalance(view)
    }

    private fun loadBalance(view: View) {
        val user = SupabaseClient.client.auth.currentUserOrNull() ?: return

        lifecycleScope.launch {
            try {
                val rekenings = BankingService.getRekeningByUserId(user.id)
                if (rekenings.isNotEmpty()) {
                    val rek = rekenings[0]
                    view.findViewById<TextView>(R.id.tvAccountBalance).text = "Rp ${rek.saldo}"
                    view.findViewById<TextView>(R.id.tvAccountNumber).text = rek.no_rekening
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengambil saldo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
