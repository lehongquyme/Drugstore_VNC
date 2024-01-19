package com.example.drugstore_vnc.adapter.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.portfolio.item.DataCategory
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.CheckToPay
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApdapterItemCategory(private val context: Context) :
    RecyclerView.Adapter<ApdapterItemCategory.ViewHolder>() {
    private lateinit var items: List<DataCategory>
    fun setList(item: List<DataCategory>) {
        item.let {
            items = it
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemcategoryproduct, parent, false)
        return ViewHolder(view)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        CoroutineScope(Dispatchers.IO).launch{
            val isUrlReachable = AddImageSignUpGeneral.isUrlReachable(item.img_url)
            withContext(Dispatchers.Main) {
                if (isUrlReachable) {
                    Picasso.get().load(item.img_url).into(holder.imageView)
                } else {
                    holder.imageView.setImageResource(R.drawable.flashimage)
                }
            }
        }
        if (item.so_luong == 0) {
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(Color.BLACK, Color.WHITE)
            )
            holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            holder.btnAdd.setTextColor(textColorStateList)
            holder.btnAdd.isEnabled = false
        }
        if (!CheckToPay.check) {
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(Color.BLACK, Color.WHITE)
            )
            holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            holder.btnAdd.setTextColor(textColorStateList)
            holder.btnAdd.isEnabled = false
        }
        holder.nameItem.text = item.ten_san_pham
        holder.packing.text = item.quy_cach_dong_goi
        holder.price.text = "${item.don_gia} VND"
        if (item.discount_price < item.don_gia) {
            holder.price.text = "${item.don_gia} VND"
            holder.price.setTypeface(null, Typeface.NORMAL)
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageItemProductCategory)
        val nameItem: TextView = itemView.findViewById(R.id.nameItemProductCategory)
        val packing: TextView = itemView.findViewById(R.id.packingItemProductCategory)
        val price: TextView = itemView.findViewById(R.id.priceItemProductCategory)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddToCartCategory)
    }
}
