package com.example.vip_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.vip_mobile.data.remote.SupabaseClient
import com.example.vip_mobile.data.service.BankingService
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadBalance(view)

        view.findViewById<Button>(R.id.btnSubmitTransfer).setOnClickListener {
            val toRek = view.findViewById<EditText>(R.id.etDestAccount).text.toString().trim()
            val amountStr = view.findViewById<EditText>(R.id.etAmount).text.toString().trim()
            val note = view.findViewById<EditText>(R.id.etTransferNote).text.toString().trim()

            if (toRek.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(context, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            processTransaction(view, toRek, amountStr.toDouble(), note)
        }
    }

    private fun loadBalance(view: View) {
        val user = SupabaseClient.client.auth.currentUserOrNull() ?: return
        lifecycleScope.launch {
            try {
                val rekenings = BankingService.getRekeningByUserId(user.id)
                if (rekenings.isNotEmpty()) {
                    view.findViewById<TextView>(R.id.tvCurrentBalance).text = "Rp ${rekenings[0].saldo}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun processTransaction(view: View, toRek: String, amount: Double, note: String) {
        val user = SupabaseClient.client.auth.currentUserOrNull() ?: return
        
        lifecycleScope.launch {
            try {
                val rekenings = BankingService.getRekeningByUserId(user.id)
                if (rekenings.isEmpty()) {
                    Toast.makeText(context, "Rekening tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                val fromRek = rekenings[0].no_rekening
                val success = BankingService.transferVIP(fromRek, toRek, amount, note)
                
                if (success) {
                    Toast.makeText(context, "Transaksi Berhasil!", Toast.LENGTH_LONG).show()
                    loadBalance(view) // Refresh saldo
                } else {
                    Toast.makeText(context, "Transaksi Gagal.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
