package com.example.drugstore_vnc.adapter.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.home.ProductInCartCustomer
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApdapterProductPay(
    private val context: Context,
) : RecyclerView.Adapter<ApdapterProductPay.ViewHolder>() {

    private var items: MutableList<ProductInCartCustomer> = mutableListOf()


    fun setList(item: MutableList<ProductInCartCustomer>) {
        items = item
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemrecycleviewlistpay, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        CoroutineScope(Dispatchers.IO).launch {
            val isUrlReachable = AddImageSignUpGeneral.isUrlReachable(item.img_url)
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
        val itemsHashTag = item.tags.map { it.name }.toMutableList()
        val adapterHashTag = ApdapterHashTag(itemsHashTag)
        holder.hashTag.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterHashTag
        }
        holder.nameItem.text = item.ten_san_pham
        holder.packing.text = item.quy_cach_dong_goi
        holder.price.text = item.don_gia.formatAsVND()
        if (item.discount_price < item.don_gia) {
            holder.price.text = item.don_gia.formatAsVND()
            holder.price.setTypeface(null, Typeface.NORMAL)
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.sellPrice.text = item.discount_price.formatAsVND()
            holder.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.sellPrice.visibility = View.GONE
        }
        if (item.so_luong_toi_thieu > 0) {
            holder.amongMin.text =
                context.getString(R.string.minimumquantity) + " ${item.so_luong_toi_thieu}"
        } else {
            holder.amongMin.visibility = View.GONE
        }
        if (item.so_luong_toi_da > 0) {
            holder.amongMax.text =
                context.getString(R.string.maximumquantity) + " ${item.so_luong_toi_da}"
        } else {
            holder.amongMax.visibility = View.GONE
        }
        if (item.bonus_coins > 0) {
            holder.bonusCoins.text =
                context.getString(R.string.bonus_Coins) + " ${item.bonus_coins} "
        } else {
            holder.bonusCoins.visibility = View.GONE
        }
        var amongnow: Int = item.so_luong
        holder.edtAmongItem.setText("x$amongnow")
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun Int.formatAsVND(): String {
        val formattedString = String.format("%,d VND", this)
        return formattedString.replace(",", ".")
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val KM: TextView = itemView.findViewById(R.id.txtItemKMPay)
        val imageView: ImageView = itemView.findViewById(R.id.imageItemProductPay)
        val hashTag: RecyclerView = itemView.findViewById(R.id.hashTagPay)
        val nameItem: TextView = itemView.findViewById(R.id.nameItemProductPay)
        val packing: TextView = itemView.findViewById(R.id.packingItemProductPay)
        val amongMin: TextView = itemView.findViewById(R.id.amongMinItemProductPay)
        val amongMax: TextView = itemView.findViewById(R.id.amongMaxItemProductPay)
        val bonusCoins: TextView = itemView.findViewById(R.id.bonusCoinsItemProductPay)
        val price: TextView = itemView.findViewById(R.id.priceItemProductPay)
        val sellPrice: TextView = itemView.findViewById(R.id.sellPriceItemProductPay)
        val edtAmongItem: TextView = itemView.findViewById(R.id.amongInPay)
    }
}
