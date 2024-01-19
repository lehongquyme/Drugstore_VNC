

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.category.ApdapterLoading
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.model.pay.ResponseXX
import com.example.drugstore_vnc.model.portfolio.Category
import com.example.drugstore_vnc.model.portfolio.SelectCategory
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.CheckToPay
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectCategoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var apiServiceCart: TakeProductInCart
    private var itemAdapter: ApdapterLoading? = null
    private var itemList = mutableListOf<Category>()
    private var isLoading = false
    private var currentPage = 1
    private var PAGE_SIZE: Int = 0
    private lateinit var objectFromJson: ResponseXX
    private lateinit var progressBar: ProgressBar
    private lateinit var imageTitleCategory: ImageView
    private lateinit var backCategory: ImageView
    private lateinit var title: TextView
    private lateinit var search: EditText
    private lateinit var filter: String


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        filter = arguments?.getString("Category").toString()
        val gson = Gson()
        objectFromJson = gson.fromJson(filter, ResponseXX::class.java)
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        val view = inflater.inflate(R.layout.fragment_select_category, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewLoadMore)
        title = view.findViewById(R.id.textViewCategory)
        imageTitleCategory = view.findViewById(R.id.imageTitleCategory)
        search = view.findViewById(R.id.searchTextInputLayout)
        backCategory = view.findViewById(R.id.backCategory)
        backCategory.setOnClickListener {
            val fragmentManager = requireFragmentManager()
            fragmentManager.popBackStack()
        }
        Picasso.get().load(objectFromJson.url_payment).into(imageTitleCategory)
        when (objectFromJson.description) {
            "hoat_chat" -> title.text = getString(R.string.active)
            "nhom_thuoc" -> title.text =  getString(R.string.drugGroup)
            "nha_san_xuat" -> title.text =  getString(R.string.producer)
        }
        progressBar = view.findViewById(R.id.progressBarSelect)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        if (search.text.length < 2) {
            itemAdapter = ApdapterLoading(itemList)
            recyclerView.adapter = itemAdapter
            loadItems()

        }
        search.addTextChangedListener(object : TextWatcher {
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

                isLoading = true
                progressBar.visibility = View.VISIBLE
                val type = objectFromJson
                apiServiceCart.fetchTakeCategory(type.description, s.toString(), null)
                    .enqueue(object : Callback<SelectCategory> {
                        override fun onResponse(
                            call: Call<SelectCategory>,
                            response: Response<SelectCategory>
                        ) {
                            isLoading = false
                            progressBar.visibility = View.GONE

                            if (response.isSuccessful) {
                                val newItems = response.body()?.response?.data
                                itemAdapter = ApdapterLoading(newItems)
                                recyclerView.adapter = itemAdapter
                            }
                        }

                        override fun onFailure(call: Call<SelectCategory>, t: Throwable) {
                            isLoading = false
                            progressBar.visibility = View.GONE

                        }
                    })

            }
        })
        setupScrollListener()
        return view
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val isNotLoadingAndNotLastPage = !isLoading && currentPage < MAX_PAGES
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE

                val shouldPaginate =
                    isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                            isTotalMoreThanVisible

                if (shouldPaginate) {
                    currentPage++
                    loadItems()
                }
            }
        })
    }

    private fun loadItems() {

        isLoading = true
        progressBar.visibility = View.VISIBLE // Show ProgressBar while loading
        val type = objectFromJson
        apiServiceCart.fetchTakeCategory(type.description, null, currentPage)
            .enqueue(object : Callback<SelectCategory> {
                override fun onResponse(
                    call: Call<SelectCategory>,
                    response: Response<SelectCategory>
                ) {
                    isLoading = false
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val newItems = response.body()?.response?.data
                        PAGE_SIZE = newItems!!.size
                        MAX_PAGES = response.body()?.response?.last_page ?: 1

                        newItems.let {
                            val startPosition = itemList.size
                            itemList.addAll(it)
                            itemAdapter?.notifyItemRangeInserted(startPosition, it.size)
                            StyleableToast.makeText(
                                requireContext(),
                                getString(R.string.loaded)+" ${it.size + startPosition}",
                                R.style.loaded
                            ).show()

                        }
                    }
                }

                override fun onFailure(call: Call<SelectCategory>, t: Throwable) {
                    isLoading = false
                    progressBar.visibility = View.GONE

                }
            })
    }

    companion object {
        var MAX_PAGES = 5
    }
}
