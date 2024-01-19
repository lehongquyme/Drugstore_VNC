package com.example.drugstore_vnc.adapter.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.history.purchase.DataPurchaseHistory

class ApdapterPurchaseHistory(var context: Context) :
    RecyclerView.Adapter<ApdapterPurchaseHistory.ViewHolder>() {
    private var items: List<DataPurchaseHistory> = mutableListOf()
    private var mListener: OnItemClickListener? = null
    fun setList(item: List<DataPurchaseHistory>?) {
        if (item != null) {
            items = item
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase_history, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.oderPurchaseHistory.text = item.id.toString()
        holder.datePurchaseHistory.text = item.created_at.toString()
        holder.addressPurchaseHistory.text = item.dia_chi
        holder.pricePurchaseHistory.text = item.tong_tien.toString()
        holder.payPurchaseHistory.text = context.getString(R.string.waitPay)
        holder.pricePurchaseHistory.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.red
            )
        )
        holder.payPurchaseHistory.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.red
            )
        )
        if (item.trang_thai == 1) {
            holder.payPurchaseHistory.text = context.getString(R.string.paid)
            holder.pricePurchaseHistory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green
                )
            )
            holder.payPurchaseHistory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green
                )
            )
        }
        holder.constraintLayoutPurchase.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemClick(item.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val oderPurchaseHistory: TextView = itemView.findViewById(R.id.oderPurchaseHistory)
        val datePurchaseHistory: TextView = itemView.findViewById(R.id.datePurchaseHistory)
        val addressPurchaseHistory: TextView = itemView.findViewById(R.id.addressPurchaseHistory)
        val pricePurchaseHistory: TextView = itemView.findViewById(R.id.pricePurchaseHistory)
        val payPurchaseHistory: TextView = itemView.findViewById(R.id.payPurchaseHistory)
        val constraintLayoutPurchase: ConstraintLayout =
            itemView.findViewById(R.id.constraintLayoutPurchase)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }
}
