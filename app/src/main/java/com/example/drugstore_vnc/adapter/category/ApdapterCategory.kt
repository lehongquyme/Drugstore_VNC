package com.example.drugstore_vnc.adapter.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.model.portfolio.Category
import com.example.drugstore_vnc.model.portfolio.item.CategoryItemProduct
import com.example.drugstore_vnc.model.portfolio.item.SelectProdductCategory
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApdapterCategory(
    private val context: Context,
    private val items: List<Category>,
    private val key: String,

    ) : RecyclerView.Adapter<ApdapterCategory.ViewHolder>() {
    private lateinit var apiServiceCart: TakeProductInCart

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemcategory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = items[position]
        val drugGroupMap = HashMap<String, Int>()
        drugGroupMap[key] = category.value
        val retrofit = ClientAPI.getClientProduct(context)
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        holder.textTitle.text = category.name
        holder.textTitle.setOnClickListener {
            apiServiceCart.fetchTakeItemCategory(drugGroupMap, 1)
                .enqueue(object : Callback<CategoryItemProduct> {
                    override fun onResponse(
                        call: Call<CategoryItemProduct>,
                        response: Response<CategoryItemProduct>
                    ) {
                        if (response.isSuccessful) {
                            val gson = Gson()
                            val jsonString = gson.toJson(
                                SelectProdductCategory(
                                    category.name,
                                    response.body()?.response?.data!!
                                )
                            )

                            val bundle = bundleOf("CategoryProduct" to jsonString)
                            holder.itemView.findNavController()
                                .navigate(R.id.selectedFragment, bundle)
                        }
                    }

                    override fun onFailure(call: Call<CategoryItemProduct>, t: Throwable) {
                    }
                })
        }
    }

    override fun getItemCount(): Int = items.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.itemTitleCategory)
    }
}
