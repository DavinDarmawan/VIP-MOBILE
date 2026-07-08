package com.example.vip_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vip_mobile.data.model.Product

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.containerProduct)
        val icon: ImageView = view.findViewById(R.id.ivProductIcon)
        val name: TextView = view.findViewById(R.id.tvProductName)
        val desc: TextView = view.findViewById(R.id.tvProductDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.name.text = product.name
        holder.desc.text = product.description
        holder.icon.setImageResource(product.iconRes)
        
        // Visual indicator for selection
        if (selectedPosition == position) {
            holder.container.setBackgroundResource(R.drawable.bg_card_mustard)
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_card_white)
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onItemClick(product)
        }
    }

    override fun getItemCount() = products.size
}
