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

class ApdapterLoading(private val itemList: List<Category>?, private val context: Context,
                      private val key: String,

                      ) :
    RecyclerView.Adapter<ApdapterLoading.ItemViewHolder>() {
    private lateinit var apiServiceCart: TakeProductInCart

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemcategory, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList?.get(position)
        val retrofit = ClientAPI.getClientProduct(context)
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        val drugGroupMap = HashMap<String, Int>()
        drugGroupMap[key] = item!!.value
        holder.itemTextView.text= item.name ?:""
        holder.itemTextView.setOnClickListener {
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
                                        item.name,
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

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val itemTextView: TextView = itemView.findViewById(R.id.itemTitleCategory)
    }
}