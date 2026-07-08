package com.example.vip_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vip_mobile.data.model.RekeningSupabase

class CustomerAdapter(private val customers: List<RekeningSupabase>) :
    RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvCustomerName)
        val account: TextView = view.findViewById(R.id.tvCustomerAccount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rek = customers[position]
        // Karena kita tidak join nama di sini (BankingService.getAllRekening hanya return Rekening), 
        // kita tampilkan ID atau placeholder jika nama tidak ada di model Rekening.
        // Untuk demo, kita tampilkan No Rekening dan Jenis Produk.
        holder.name.text = rek.jenis_produk
        holder.account.text = rek.no_rekening
    }

    override fun getItemCount() = customers.size
}
