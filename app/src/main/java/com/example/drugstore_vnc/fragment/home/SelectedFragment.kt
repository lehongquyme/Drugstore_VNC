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
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.category.ApdapterItemCategory
import com.example.drugstore_vnc.adapter.home.ApdapterHashTag
import com.example.drugstore_vnc.adapter.home.ApdapterProduct
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.DialogaddtocartBinding
import com.example.drugstore_vnc.databinding.FragmentSelectedBinding
import com.example.drugstore_vnc.model.home.ProductDemo
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectedFragment : Fragment() {
    private lateinit var binding: FragmentSelectedBinding
    private lateinit var viewModel: ViewModelProductAPI
    private lateinit var categoryGenerel: SelectProdductCategory
    private lateinit var listProduct: List<DataCategory>
    private lateinit var title: String
    private lateinit var apiService: TakeProductInCart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = this.arguments
        val gson = Gson()
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        val filter: String? = bundle?.getString("key")
        val category = arguments?.getString("CategoryProduct").toString()
        apiService =
            ClientAPI.getClientProduct(requireContext()).create(TakeProductInCart::class.java)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_selected, container, false
        )
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java]

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
                title =getString(R.string.promotional_Products)
                filterList(filter)
            }

            "all" -> {
                title =getString(R.string.all_Products)
                filterList(filter)
            }

            else -> {
                categoryGenerel = gson.fromJson(category, SelectProdductCategory::class.java)
                listProduct = categoryGenerel.listData
                title = categoryGenerel.title
                binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.toFloat())

                val spanCount = 2
                val layoutManager = GridLayoutManager(context, spanCount)
                binding.recyclerViewSelect.layoutManager = layoutManager
                val myListAdapter = ApdapterItemCategory(requireActivity())
                myListAdapter.setList(listProduct)
                binding.recyclerViewSelect.adapter = myListAdapter

            }

        }
        viewModel.responseDataDemo.observe(viewLifecycleOwner) { products ->
            products.response.let {
                binding.alertAmongCartCategory.text = it.total_cart.toString()
            }
        }

        binding.textView.text = title
        viewModel.fetchDataDemo()
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
                    override fun onItemClick(position: Int, item: ProductDemo?) {
                        showDialog(item)
                    }
                })
                binding.recyclerViewSelect.adapter = myListAdapter
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog( item: ProductDemo?) {
        var amongnow: Int = 1
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding: DialogaddtocartBinding = DialogaddtocartBinding.inflate(layoutInflater)
        var amongCart = 0
        viewModel.amongcart.observe(viewLifecycleOwner) { among ->
            var checkAmong = among.products.data.filter { data -> data.id == item!!.id }
                .filter { data -> data.so_luong > 0 }
            checkAmong.forEach { it ->
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

            val itemsHashTag= item.tags.map { it.name}.toMutableList()
            val adapterHashTag = ApdapterHashTag(itemsHashTag)
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
            binding.amongMinItemProduct.text = getString(R.string.minimumquantity)+" ${item.so_luong_toi_thieu}"
        } else if (item.so_luong_toi_thieu > 1) {
            amongnow = item.so_luong_toi_thieu
            binding.edtAmong.setText("$amongnow")
            binding.amongMinItemProduct.text =getString(R.string.minimumquantity)+" ${item.so_luong_toi_thieu}"
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
            binding.bonusCoinsItemProduct.text = getString(R.string.bonus)+" ${item.bonus_coins}"
        } else {
            binding.bonusCoinsItemProduct.visibility = View.GONE
        }

        binding.add.setOnClickListener {
            binding.sos.visibility = View.INVISIBLE
            if (item.so_luong_toi_da > 1) {
                amongnow = minOf(amongnow + 1, item.so_luong_toi_da)
            } else {
                amongnow = minOf(amongnow + 1)
            }
            binding.edtAmong.setText("$amongnow")
            if (amongnow == item.so_luong_toi_da) {
                binding.sos.visibility = View.VISIBLE
                binding.sos.text = getString(R.string.maxnimum)
            }
        }


        binding.minus.setOnClickListener {
            binding.sos.visibility = View.INVISIBLE
            if (item.so_luong_toi_thieu > 1) {
                amongnow = maxOf(amongnow - 1, item.so_luong_toi_thieu)
            } else {
                amongnow = maxOf(amongnow - 1, 1)
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
                        Toast.makeText(requireContext(), getString(R.string.addFail), Toast.LENGTH_SHORT).show()
                    } else {
                        StyleableToast.makeText(requireContext(),
                            getString(R.string.addProduct),
                            R.style.creatProduct).show()
                        viewModel.fetchDataDemo()
                        bottomSheetDialog.dismiss()
                    }
                }


            })

        }

        bottomSheetDialog.show()
    }

}