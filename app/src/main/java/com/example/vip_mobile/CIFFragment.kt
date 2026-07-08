package com.example.vip_mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.vip_mobile.data.model.NasabahSupabase
import com.example.vip_mobile.data.model.Product
import com.example.vip_mobile.data.model.RekeningSupabase
import com.example.vip_mobile.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class CIFFragment : Fragment() {

    private var selectedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)

        view.findViewById<Button>(R.id.btnSubmitOpenAccount).setOnClickListener {
            validateAndSubmit(view)
        }
    }

    private fun setupRecyclerView(view: View) {
        val products = listOf(
            Product("1", "VIP Silver", "Min. Rp 50.000", R.drawable.ic_coin, R.drawable.bg_badge_mustard),
            Product("2", "VIP Gold", "Min. Rp 500.000", R.drawable.ic_star, R.drawable.bg_badge_sky),
            Product("3", "VIP Platinum", "Min. Rp 5.000.000", R.drawable.ic_bolt, R.drawable.bg_badge_coral)
        )

        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)
        val adapter = ProductAdapter(products) { product ->
            selectedProduct = product
        }
        rvProducts.adapter = adapter
    }

    private fun validateAndSubmit(view: View) {
        val name = view.findViewById<EditText>(R.id.etNasabahName).text.toString().trim()
        val ktp = view.findViewById<EditText>(R.id.etNasabahKtp).text.toString().trim()
        val phone = view.findViewById<EditText>(R.id.etNasabahPhone).text.toString().trim()
        val address = view.findViewById<EditText>(R.id.etNasabahAddress).text.toString().trim()

        if (name.isEmpty() || ktp.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(context, "Harap lengkapi semua data nasabah", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedProduct == null) {
            Toast.makeText(context, "Pilih produk tabungan dulu", Toast.LENGTH_SHORT).show()
            return
        }

        openNewAccount(name, ktp, phone, address)
    }

    private fun openNewAccount(name: String, ktp: String, phone: String, address: String) {
        lifecycleScope.launch {
            try {
                val user = SupabaseClient.client.auth.retrieveUserForCurrentSession()
                val userId = user.id

                val noRekening = "88${(10000000..99999999).random()}"
                
                val newRekening = RekeningSupabase(
                    no_rekening = noRekening,
                    id_user = userId,
                    saldo = 0.0,
                    jenis_produk = selectedProduct?.name ?: "TABUNGAN BSA",
                    status = "AKTIF"
                )

                SupabaseClient.client.postgrest["rekening"].insert(newRekening)
                
                showSuccessDialog(phone)
                
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSuccessDialog(phone: String) {
        val dialog = SuccessDialogFragment.newInstance("Rekening berhasil dibuat untuk nomor $phone")
        dialog.setOnContinueListener {
            val intent = Intent(requireContext(), InitialDepositActivity::class.java).apply {
                putExtra("PHONE", phone)
                putExtra("PRODUCT_NAME", selectedProduct?.name)
            }
            startActivity(intent)
        }
        dialog.show(parentFragmentManager, "SuccessDialog")
    }
}
