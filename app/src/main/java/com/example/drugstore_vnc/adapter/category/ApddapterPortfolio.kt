package com.example.drugstore_vnc.adapter.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.pay.ResponseXX
import com.example.drugstore_vnc.model.portfolio.ResponseCategory
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApddapterPortfolio(
    private val listener: OnItemClickListener,
    private val context: Context,
) :
    RecyclerView.Adapter<ApddapterPortfolio.ViewHolder>() {
    private var items: MutableList<ResponseCategory> = mutableListOf()
    fun setList(item: MutableList<ResponseCategory>) {
        items = item
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemproductportfolio, parent, false)
        return ViewHolder(view)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        CoroutineScope(Dispatchers.IO).launch {
            val isUrlReachable = AddImageSignUpGeneral.isUrlReachable(item.icon)
            withContext(Dispatchers.Main) {
                if (isUrlReachable) {
                    Picasso.get().load(item.icon).into(holder.imageView)
                } else {
                    holder.imageView.setImageResource(R.drawable.flashimage)
                }
            }
        }
        when (item.key) {
            "hoat_chat" -> holder.title.text =context.getString(R.string.active)
            "nhom_thuoc" -> holder.title.text =context.getString(R.string.drugGroup)
            "nha_san_xuat" -> holder.title.text =context.getString(R.string.producer)
        }
        val listCategory = item.category
        val categoryAdapter = ApdapterCategory(context, listCategory, item.key)
        holder.recycCategory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }
        holder.iconImage.setOnClickListener {
            listener.onItemClick(position, ResponseXX(item.key, item.icon))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePortfolio)
        val title: TextView = itemView.findViewById(R.id.titlePortfolio)
        val iconImage: ImageView = itemView.findViewById(R.id.iconImagePortfolio)
        val recycCategory: RecyclerView = itemView.findViewById(R.id.recycCategory)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, value: ResponseXX)
    }
}