package com.example.vip_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vip_mobile.data.model.TransaksiSupabase

class TransactionAdapter(private val transactions: List<TransaksiSupabase>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type: TextView = view.findViewById(R.id.tvTrxType)
        val amount: TextView = view.findViewById(R.id.tvTrxAmount)
        val date: TextView = view.findViewById(R.id.tvTrxDate)
        val note: TextView = view.findViewById(R.id.tvTrxNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trx = transactions[position]
        holder.type.text = trx.jenis
        holder.amount.text = "Rp ${trx.nominal}"
        holder.date.text = trx.tgl_transaksi?.take(10) ?: "-"
        holder.note.text = trx.keterangan
    }

    override fun getItemCount() = transactions.size
}
