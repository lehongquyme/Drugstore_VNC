package com.example.drugstore_vnc.adapter.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.home.totalprice.Voucher

class ApdapterVoucher(context: Context, private val items: List<Voucher>) : ArrayAdapter<Voucher>(context, R.layout.itemvoucher, items) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.itemvoucher, parent, false)

        val textTitle = rowView.findViewById<TextView>(R.id.textTitle)
        val textContent = rowView.findViewById<TextView>(R.id.textContent)

        val voucher = getItem(position)
        textTitle.text = voucher?.name
        textContent.text = voucher?.dateComment

        return rowView
    }
}