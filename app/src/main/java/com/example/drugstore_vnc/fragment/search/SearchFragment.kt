package com.example.drugstore_vnc.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.category.ApdapterSearch
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.fragment.search.model.ListProduct
import com.example.drugstore_vnc.getAPI.ProductAPI
import com.example.drugstore_vnc.model.portfolio.Search
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment(),ApdapterSearch.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtSearch: EditText
    private var itemAdapter: ApdapterSearch? = null
    private lateinit var backSearch: ImageView
    private lateinit var apiServiceCart: TakeProductInCart
    private lateinit var apiServiceSearch: ProductAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)
        edtSearch = view.findViewById(R.id.edtSearch)
        backSearch = view.findViewById(R.id.backSearch)
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        backSearch.setOnClickListener {
        requireFragmentManager().popBackStack()
        requireFragmentManager().popBackStack()

        }
        searchEdit(edtSearch.text.toString())
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }


            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchEdit(s.toString())
            }
        })
        return view
    }

    override fun onSearchItemClick(item: ListProduct){
        val gson = Gson()
        val jsonString = gson.toJson(item)
        val bundle = bundleOf("SearchProduct" to jsonString)
        view?.findNavController()?.navigate(R.id.itemSearchFragment, bundle)
    }
    private fun searchEdit(s:String){
        apiServiceCart.fetchTakeSearch(s)
            .enqueue(object : Callback<Search> {
                override fun onResponse(
                    call: Call<Search>,
                    response: Response<Search>
                ) {

                    if (response.isSuccessful) {
                        val newItems = response.body()?.response
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        val retrofit = ClientAPI.getClientProduct(requireContext())
                        apiServiceSearch = retrofit.create(ProductAPI::class.java)
                        itemAdapter = ApdapterSearch(this@SearchFragment,newItems,apiServiceSearch)
                        recyclerView.adapter = itemAdapter

                    }
                }

                override fun onFailure(call: Call<Search>, t: Throwable) {

                }
            })
    }


}