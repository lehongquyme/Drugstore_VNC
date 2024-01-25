@file:Suppress("DEPRECATION")

package com.example.drugstore_vnc.fragment.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.category.ApdapterItemCategory
import com.example.drugstore_vnc.adapter.home.ApdapterHashTag
import com.example.drugstore_vnc.adapter.home.ApdapterProduct
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.DialogaddtocartBinding
import com.example.drugstore_vnc.databinding.FragmentSelectedBinding
import com.example.drugstore_vnc.model.portfolio.item.CategoryItemProduct
import com.example.drugstore_vnc.model.portfolio.item.DataCategory
import com.example.drugstore_vnc.model.portfolio.item.SelectProdductCategory
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
import com.example.drugstore_vnc.viewModelFatory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectedFragment : Fragment(), ApdapterItemCategory.OnItemClickListener {
    private lateinit var binding: FragmentSelectedBinding
    private lateinit var viewModel: ViewModelProductAPI
    private lateinit var categoryGenerel: SelectProdductCategory
    private lateinit var listProduct: List<DataCategory>
    private lateinit var title: String
    private lateinit var apiService: TakeProductInCart
    private lateinit var gson: Gson
    private lateinit var apiServiceCart: TakeProductInCart
    private var hc: Int? = null
    private var category: String? = null
    private var nt: Int? = null
    private var nsx: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService =
            ClientAPI.getClientProduct(requireContext()).create(TakeProductInCart::class.java)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java]
        gson = Gson()
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        CheckToPay.binding?.bottomNavigationView?.visibility = View.GONE
        val filter: String? = arguments?.getString("key")
        val category: String? = arguments?.getString("category")
        val categoryProduct = arguments?.getString("CategoryProduct").toString()
        val agency = arguments?.getString("ItemAgencyProduct").toString()
        hc = arguments?.getInt("hc")
        nt = arguments?.getInt("nt")
        nsx = arguments?.getInt("nsx")
        viewModel.fetchDataDemo()
        viewModel.responseDataDemo.observe(viewLifecycleOwner) { products ->
            products.response.let {
                alertAmong(it.total_cart)
                binding.alertAmongCartCategory.text = it.total_cart.toString()
            }
        }
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_selected, container, false
        )
        when (category) {

            "ban_chay" -> {
                title = getString(R.string.selling_Products)
                setupUI(title, true)
            }


            "khuyen_mai" -> {
                title = getString(R.string.promotional_Products)
                setupUI(title, true)
            }

            "all" -> {
                title = getString(R.string.all_Products)
                setupUI(title, true)
            }
        }
        when (filter) {
            "ban_chay" -> {
                title = getString(R.string.selling_Products)
                filterList(filter)
            }

            "moi" -> {
                title = getString(R.string.new_Products)
                filterList(filter)
            }

            "khuyen_mai" -> {
                title = getString(R.string.promotional_Products)
                filterList(filter)
            }

            "all" -> {
                title = getString(R.string.all_Products)
                filterList(filter)
            }
        }
        setupUI(agency, true)
        setupUI(
            categoryProduct, false
        )

        binding.textView.text = title
        binding.backHome.setOnClickListener {
            val fragmentManager = requireFragmentManager()
            fragmentManager.popBackStack()
        }
        binding.cartInFragmentHome.setOnClickListener {
            val bundle = bundleOf("key" to "Cart")
            view?.findNavController()?.navigate(R.id.listCartFragment, bundle)
        }
        return binding.root
    }

    private fun filterList(key: String?) {
        viewModel.responseDataDemo.observe(viewLifecycleOwner) { products ->
            products.response.let {

                val check = it.member_status == 2
                val productItemPromotional = it.products.flatMap { typeProduct ->
                    typeProduct.data.filter {
                        typeProduct.value == key
                    }
                }
                val spanCount = 2
                val layoutManager = GridLayoutManager(context, spanCount)
                binding.recyclerViewSelect.layoutManager = layoutManager
                val myListAdapter = ApdapterProduct(requireContext())
                myListAdapter.setAdd(check)
                myListAdapter.setList(productItemPromotional)
                myListAdapter.setOnItemClickListener(object : ApdapterProduct.OnItemClickListener {
                    override fun onItemClick(position: Int, item: DataCategory?) {
                        showDialog(item)
                    }
                })
                binding.recyclerViewSelect.adapter = myListAdapter
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    private fun showDialog(item: DataCategory?) {
        var amongnow = 1
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding: DialogaddtocartBinding = DialogaddtocartBinding.inflate(layoutInflater)
        var amongCart = 0
        viewModel.amongcart.observe(viewLifecycleOwner) { among ->
            val checkAmong = among.products.data.filter { data -> data.id == item!!.id }
                .filter { data -> data.so_luong > 0 }
            checkAmong.forEach {
                amongCart = it.so_luong
            }

        }

        bottomSheetDialog.setContentView(binding.root)
        GlobalScope.launch(Dispatchers.IO) {
            val isUrlReachable = AddImageSignUpGeneral.isUrlReachable(item?.img_url ?: "")

            withContext(Dispatchers.Main) {
                if (isUrlReachable) {
                    if (item != null) {
                        Picasso.get().load(item.img_url).into(binding.imageItemProduct)
                    }
                } else {
                    binding.imageItemProduct.setImageResource(R.drawable.flashimage)
                }
            }
        }
        if ((item?.khuyen_mai ?: 0) > 0) {
            binding.txtItemKM.text = "-${item!!.khuyen_mai}%"
        } else {
            binding.txtItemKM.visibility = View.GONE
        }


        if (item != null) {

            val itemsHashTag = item.tags?.map { it.name }?.toMutableList()
            val adapterHashTag = itemsHashTag?.let { ApdapterHashTag(it) }
            binding.listHashTag.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = adapterHashTag
            }

        } else {
            binding.listHashTag.visibility = View.GONE
        }


        binding.nameItemProduct.text = item?.ten_san_pham ?: "null"
        binding.packingItemProduct.text = item?.quy_cach_dong_goi ?: "null"
        binding.priceItemProduct.text = "${item?.don_gia} VND"
        if ((item?.discount_price?.toInt() ?: 0) < item!!.don_gia) {
            binding.priceItemProduct.text = "${item.don_gia} VND"
            binding.priceItemProduct.setTypeface(null, Typeface.NORMAL)
            binding.priceItemProduct.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.black
                )
            )
            binding.sellPriceItemProduct.text = "${item.discount_price} VND"
            binding.priceItemProduct.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.sellPriceItemProduct.visibility = View.GONE
        }
        if (item.so_luong_toi_thieu == 0) {
            amongnow = 1
            binding.edtAmong.setText("$amongnow")
            binding.amongMinItemProduct.text =
                getString(R.string.minimumquantity) + " ${item.so_luong_toi_thieu}"
        } else if (item.so_luong_toi_thieu > 1) {
            amongnow = item.so_luong_toi_thieu
            binding.edtAmong.setText("$amongnow")
            binding.amongMinItemProduct.text =
                getString(R.string.minimumquantity) + " ${item.so_luong_toi_thieu}"
        } else {

            binding.amongMinItemProduct.visibility = View.GONE
        }
        binding.edtAmong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty()) {
                        amongnow = s.toString().toInt()
                    }
                }
            }
        })
        if (item.bonus_coins > 0) {
            binding.bonusCoinsItemProduct.text = getString(R.string.bonus) + " ${item.bonus_coins}"
        } else {
            binding.bonusCoinsItemProduct.visibility = View.GONE
        }

        binding.add.setOnClickListener {
            binding.sos.visibility = View.INVISIBLE
            amongnow = if (item.so_luong_toi_da > 1) {
                minOf(amongnow + 1, item.so_luong_toi_da)
            } else {
                minOf(amongnow + 1)
            }
            binding.edtAmong.setText("$amongnow")
            if (amongnow == item.so_luong_toi_da) {
                binding.sos.visibility = View.VISIBLE
                binding.sos.text = getString(R.string.maxnimum)
            }
        }


        binding.minus.setOnClickListener {
            binding.sos.visibility = View.INVISIBLE
            amongnow = if (item.so_luong_toi_thieu > 1) {
                maxOf(amongnow - 1, item.so_luong_toi_thieu)
            } else {
                maxOf(amongnow - 1, 1)
            }
            binding.edtAmong.setText("$amongnow")
            if (amongnow == 1 || amongnow == item.so_luong_toi_thieu) {
                binding.sos.visibility = View.VISIBLE
                binding.sos.text = getString(R.string.minimum)
            }
        }
        binding.addToCard.setOnClickListener {
            val call = apiService.fetchAddCartCustomersUser(
                item.id,
                binding.edtAmong.text.toString().toInt() + amongCart,
            )
            CallAPI().callRetrofitPostCart(call, object : CallAPI.AuthCallbackAddToCard {
                override fun onAddToCart(check: Int) {
                    if (check > 0) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.addFail),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        StyleableToast.makeText(
                            requireContext(),
                            getString(R.string.addProduct),
                            R.style.creatProduct
                        ).show()
                        viewModel.fetchDataDemo()
                        bottomSheetDialog.dismiss()
                    }
                }


            })

        }

        bottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun alertAmong(among: Int) {

        if (among > 0) {
            binding.alertAmongCartCategory.visibility = View.VISIBLE
            if (among > 98) binding.alertAmongCartCategory.text = "99+"
            else binding.alertAmongCartCategory.text = among.toString()

        } else {
            binding.alertAmongCartCategory.visibility = View.GONE
        }
    }

    private fun setupUIManagerShop(data: String?) {
        if (data != null && data != "null") {
            val spanCount = 2
            binding.cartIn.visibility = View.VISIBLE
            binding.cartInFragmentHome.visibility = View.INVISIBLE
            apiServiceCart.fetchTakeItemAgency(data, "", hc, nt, nsx)
                .enqueue(object : Callback<CategoryItemProduct> {
                    override fun onResponse(
                        call: Call<CategoryItemProduct>,
                        response: Response<CategoryItemProduct>
                    ) {
                        if (response.isSuccessful) {
                            listProduct = response.body()?.response?.data!!
                            if (listProduct.isNotEmpty()) {
                                val layoutManager = GridLayoutManager(context, spanCount)
                                binding.recyclerViewSelect.layoutManager = layoutManager
                                val myListAdapter = ApdapterItemCategory(requireActivity(), true)
                                myListAdapter.setList(listProduct)
                                binding.recyclerViewSelect.adapter = myListAdapter
                            } else {
                                binding.imageSalesHistory.visibility = View.VISIBLE
                                binding.txtSalesHistory.visibility = View.VISIBLE
                                Glide.with(requireContext())
                                    .asGif()
                                    .load(R.drawable.fail_history)
                                    .into(binding.imageSalesHistory)
                            }
                        }
                    }

                    override fun onFailure(call: Call<CategoryItemProduct>, t: Throwable) {
                    }
                })
        }
    }

    private fun setupUI(tittle: String, check: Boolean) {
        if (tittle != "null") {
            if (check) {
                binding.cartIn.isEnabled = false
                if (CheckToPay.check) {
                    binding.cartIn.isEnabled = true
                    binding.cartIn.setOnClickListener {
                        val bundle =
                            bundleOf("URL" to "http://18.138.176.213/agency/products/create")
                        view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
                    }
                }
                binding.cartIn.visibility = View.VISIBLE
                binding.cartInFragmentHome.visibility = View.INVISIBLE
                title = tittle
                loadData()

            } else {
                categoryGenerel = gson.fromJson(tittle, SelectProdductCategory::class.java)
                listProduct = categoryGenerel.listData
                title = categoryGenerel.title
                binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.toFloat())
                if (listProduct.isNotEmpty()) {
                    val layoutManager = GridLayoutManager(context, 2)
                    binding.recyclerViewSelect.layoutManager = layoutManager
                    val myListAdapter = ApdapterItemCategory(requireActivity(), false)
                    myListAdapter.setList(listProduct)
                    myListAdapter.setOnItemClickListener(object :
                        ApdapterItemCategory.OnItemClickListener {
                        override fun onEyeClick() {
                            loadData()
                        }

                        override fun onItemClick(position: Int, item: DataCategory?) {
                            showDialog(item)
                        }
                    })
                    binding.recyclerViewSelect.adapter = myListAdapter

                } else {
                    binding.recyclerViewSelect.visibility = View.GONE
                    binding.imageSalesHistory.visibility = View.VISIBLE
                    binding.txtSalesHistory.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .asGif()
                        .load(R.drawable.fail_history)
                        .into(binding.imageSalesHistory)
                }

            }

        }

    }

    private fun loadData() {
        if (!category.isNullOrEmpty() || category != "null") {
            apiServiceCart.fetchTakeItemAgency(category, "", hc, nt, nsx)
                .enqueue(object : Callback<CategoryItemProduct> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<CategoryItemProduct>,
                        response: Response<CategoryItemProduct>
                    ) {
                        if (response.isSuccessful) {
                            listProduct = response.body()?.response?.data!!
                            if (listProduct.isNotEmpty()) {
                                val layoutManager = GridLayoutManager(context, 2)
                                binding.recyclerViewSelect.layoutManager = layoutManager
                                val myListAdapter = ApdapterItemCategory(requireActivity(), true)
                                myListAdapter.setList(listProduct)
                                myListAdapter.setOnItemClickListener(this@SelectedFragment)
                                binding.recyclerViewSelect.adapter = myListAdapter
                                myListAdapter.notifyDataSetChanged()
                            } else {
                                binding.recyclerViewSelect.visibility = View.GONE
                                binding.imageSalesHistory.visibility = View.VISIBLE
                                binding.txtSalesHistory.visibility = View.VISIBLE
                                Glide.with(requireContext())
                                    .asGif()
                                    .load(R.drawable.fail_history)
                                    .into(binding.imageSalesHistory)
                            }
                        }
                    }

                    override fun onFailure(call: Call<CategoryItemProduct>, t: Throwable) {
                    }
                })
        }
    }

    override fun onEyeClick() {
        loadData()
    }

    override fun onItemClick(position: Int, item: DataCategory?) {
        TODO("Not yet implemented")
    }


}