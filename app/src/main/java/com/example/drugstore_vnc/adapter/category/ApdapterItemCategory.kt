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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.portfolio.item.DataCategory
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.CheckToPay
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApdapterItemCategory(private val context: Context, private val check: Boolean) :
    RecyclerView.Adapter<ApdapterItemCategory.ViewHolder>() {
    private lateinit var items: List<DataCategory>
    @SuppressLint("NotifyDataSetChanged")
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

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
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


        if (item.so_luong == 0) {
            holder.endProduct.visibility = View.VISIBLE
            holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            holder.endProduct.text = context.getString(R.string.outStock)
            holder.price.visibility = View.GONE
            holder.sellPrice.visibility = View.GONE
        }
        if (item.khuyen_mai > 0) {
            holder.KM.text = "-${item.khuyen_mai}%"
        } else {
            holder.KM.visibility = View.GONE
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

        if (!check) {
            holder.price.visibility = View.INVISIBLE
            holder.btnAdd.visibility = View.VISIBLE
        } else {
            holder.menuManager.visibility = View.VISIBLE
            if (item.trang_thai == 1) {
                holder.eye.imageTintList = ContextCompat.getColorStateList(context, R.color.green)
            }
            if (item.ban_chay == 1) {
                holder.selling.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.green)

            }
            holder.editProduct.setOnClickListener {
                val bundle = bundleOf("URL" to "http://18.138.176.213/agency/products/edit/${item.id}")
                holder.editProduct.findNavController().navigate(R.id.webViewFragment, bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageItemProduct)
        val nameItem: TextView = itemView.findViewById(R.id.nameItemProduct)
        val packing: TextView = itemView.findViewById(R.id.packingItemProduct)
        val price: TextView = itemView.findViewById(R.id.priceItemProduct)
        val btnAdd: Button = itemView.findViewById(R.id.btnAddToCart)
        val KM: TextView = itemView.findViewById(R.id.txtItemKM)
        val endProduct: TextView = itemView.findViewById(R.id.endProduct)
        val sellPrice: TextView = itemView.findViewById(R.id.sellPriceItemProduct)
        val amongMax: TextView = itemView.findViewById(R.id.amongMaxItemProduct)
        val amongMin: TextView = itemView.findViewById(R.id.amongMinItemProduct)
        val menuManager: LinearLayout = itemView.findViewById(R.id.menuManager)
        val eye: ImageView = itemView.findViewById(R.id.eye)
        val selling: ImageView = itemView.findViewById(R.id.selling)
        val editProduct: ImageView = itemView.findViewById(R.id.editProduct)

    }
}
