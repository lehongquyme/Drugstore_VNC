package com.example.drugstore_vnc.adapter.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.home.ProductDemo
import com.example.drugstore_vnc.util.AddImageSignUpGeneral.isUrlReachable
import com.example.drugstore_vnc.util.CheckToPay
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ApdapterProduct(private val context: Context) :
    RecyclerView.Adapter<ApdapterProduct.ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private var checkAdd = true
    private lateinit var items: List<ProductDemo>
    private var itemsHashTag = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setAdd(check: Boolean) {
        check.let {
            checkAdd = it
            notifyDataSetChanged()
        }
    }

    fun setList(item: List<ProductDemo>) {
        item.let {
            items = it
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemrecyclehome, parent, false)
        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        CoroutineScope(Dispatchers.IO).launch {
            val isUrlReachable = isUrlReachable(item.img_url)
            withContext(Dispatchers.Main) {
                if (isUrlReachable) {
                    Picasso.get().load(item.img_url).into(holder.imageView)
                } else {
                    holder.imageView.setImageResource(R.drawable.flashimage)
                }
            }
        }
        if (item.khuyen_mai > 0) {
            holder.KM.text = "-${item.khuyen_mai}%"
        } else {
            holder.KM.visibility = View.GONE
        }
        itemsHashTag = item.tags.map { it.name }.toMutableList()
        val recyclerView = ApdapterHashTag(itemsHashTag)
        holder.hashTag.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = recyclerView
        }
        holder.endProduct.visibility = View.GONE
        if (item.so_luong == 0) {
            holder.endProduct.visibility = View.VISIBLE
            holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            holder.endProduct.text = "Out Of Stock"
            holder.price.visibility = View.GONE
            holder.sellPrice.visibility = View.GONE
        }
        holder.nameItem.text = item.ten_san_pham
        holder.packing.text = item.quy_cach_dong_goi
        holder.price.text = "${item.don_gia} VND"
        if (item.discount_price < item.don_gia) {
            holder.price.text = "${item.don_gia} VND"
            holder.price.setTypeface(null, Typeface.NORMAL)
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.sellPrice.text = "${item.discount_price} VND"
            holder.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.sellPrice.visibility = View.GONE
        }
        if (item.so_luong_toi_thieu > 0) {
            holder.amongMin.text = "Minimum quantity ${item.so_luong_toi_thieu}"
        } else {
            holder.amongMin.visibility = View.GONE
        }
        if (item.so_luong_toi_da > 0) {
            holder.amongMax.text = "Maximum quantity ${item.so_luong_toi_da}"
        } else {
            holder.amongMax.visibility = View.GONE
        }
        if (item.bonus_coins > 0) {
            holder.bonusCoins.text = "Bonus ${item.bonus_coins} Coins"
        } else {
            holder.bonusCoins.visibility = View.GONE
        }
        holder.btnAdd.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemClick(position, item)
            }
        }
        if (!CheckToPay.check) {
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(-android.R.attr.state_enabled)
                ), intArrayOf(Color.BLACK, Color.WHITE)
            )
            holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            holder.btnAdd.setTextColor(textColorStateList)
            holder.btnAdd.isEnabled = false
            holder.sellPrice.visibility = View.GONE
            holder.price.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val KM: TextView = itemView.findViewById(R.id.txtItemKM)
        val imageView: ImageView = itemView.findViewById(R.id.imageItemProduct)
        val hashTag: RecyclerView = itemView.findViewById(R.id.recyclerViewHashTag)
        val nameItem: TextView = itemView.findViewById(R.id.nameItemProduct)
        val packing: TextView = itemView.findViewById(R.id.packingItemProduct)
        val amongMin: TextView = itemView.findViewById(R.id.amongMinItemProduct)
        val amongMax: TextView = itemView.findViewById(R.id.amongMaxItemProduct)
        val bonusCoins: TextView = itemView.findViewById(R.id.bonusCoinsItemProduct)
        val price: TextView = itemView.findViewById(R.id.priceItemProduct)
        val sellPrice: TextView = itemView.findViewById(R.id.sellPriceItemProduct)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddToCart)
        val endProduct: TextView = itemView.findViewById(R.id.endProduct)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: ProductDemo?)
    }
}