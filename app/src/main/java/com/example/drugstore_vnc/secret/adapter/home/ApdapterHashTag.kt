package com.example.drugstore_vnc.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R

class ApdapterHashTag(hashTags: MutableList<String>) :
    RecyclerView.Adapter<ApdapterHashTag.ViewHolder>() {
    private var items: MutableList<String> = hashTags
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemhashtag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hashTag = items[position]
        holder.hashTagText.text = hashTag
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hashTagText: TextView = itemView.findViewById(R.id.hashTagCartItem)
    }
}