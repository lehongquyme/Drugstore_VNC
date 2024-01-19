package com.example.drugstore_vnc.adapter.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.portfolio.Category

class ApdapterLoading(private val itemList: List<Category>?) :
    RecyclerView.Adapter<ApdapterLoading.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemcategory, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.bind(item?.name ?: "")
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemTextView: TextView = itemView.findViewById(R.id.itemTitleCategory)
        fun bind(item: String) {
            itemTextView.text = item
        }
    }
}