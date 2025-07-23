package com.example.drugstore_vnc.adapter.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.fragment.search.model.ListProduct
import com.example.drugstore_vnc.fragment.search.model.ReturnDataProduct
import com.example.drugstore_vnc.model.portfolio.ResponseSearch
import com.example.drugstore_vnc.postAPI.ProductAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApdapterSearch(
    private val clickListener: OnItemClickListener,
    private val itemList: List<ResponseSearch>?,
    var apiServiceCart: ProductAPI,
) : RecyclerView.Adapter<ApdapterSearch.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemsearch, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.itemTextView.text = item?.ten_san_pham ?: ""
        holder.itemTextView.setOnClickListener {
            apiServiceCart.fetchDataProduct(item!!.ten_san_pham)
                .enqueue(object : Callback<ReturnDataProduct> {
                    override fun onResponse(
                        call: Call<ReturnDataProduct>,
                        response: Response<ReturnDataProduct>
                    ) {
                        if (response.isSuccessful) {
                            val newItems = response.body()?.response?.dataProduct
                            clickListener.onSearchItemClick(ListProduct(newItems!!))
                        }
                    }

                    override fun onFailure(call: Call<ReturnDataProduct>, t: Throwable) {
                    }
                })
        }
        if (item!!.ban_chay == 0)
            holder.itemSellingSearch.visibility =
                View.GONE else holder.itemSellingSearch.visibility = View.VISIBLE
        if (item.khuyen_mai == 0)
            holder.itemDiscountSearch.visibility =
                View.GONE else holder.itemDiscountSearch.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTextView: TextView = itemView.findViewById(R.id.itemSearch)
        val itemDiscountSearch: ImageView = itemView.findViewById(R.id.itemDiscountSearch)
        val itemSellingSearch: ImageView = itemView.findViewById(R.id.itemSellingSearch)
    }

    interface OnItemClickListener {
        fun onSearchItemClick(item: ListProduct)
    }
}