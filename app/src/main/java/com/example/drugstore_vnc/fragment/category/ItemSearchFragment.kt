package com.example.drugstore_vnc.fragment.category

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.home.ApdapterHashTag
import com.example.drugstore_vnc.adapter.home.ApdapterProduct
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.DialogaddtocartBinding
import com.example.drugstore_vnc.fragment.search.model.ListProduct
import com.example.drugstore_vnc.model.home.ProductDemo
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
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

class ItemSearchFragment : Fragment() {
private lateinit var numberCart:TextView
private lateinit var back:ImageView
private lateinit var listCart:ConstraintLayout
private lateinit var recyclerViewItemSearch:RecyclerView
    private lateinit var apiServiceCart: TakeProductInCart

    private lateinit var viewModel: ViewModelProductAPI
    private lateinit var objectFromJson: ListProduct
    private lateinit var filter: String
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ViewModelProvider(
            this, ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java].also { viewModel = it }
        viewModel.fetchDataDemo()
        filter = arguments?.getString("SearchProduct").toString()
        val gson = Gson()
        objectFromJson = gson.fromJson(filter, ListProduct::class.java)
        val list = objectFromJson.dataProduct
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        val view= inflater.inflate(R.layout.fragment_item_search, container, false)
        numberCart = view.findViewById(R.id.numberCart)
        back = view.findViewById(R.id.backItemSearchProduct)
        listCart = view.findViewById(R.id.cartInItemSearch)
        listCart.setOnClickListener {
            val bundle = bundleOf("key" to "Cart")
            view?.findNavController()?.navigate(R.id.listCartFragment, bundle)
      }
        recyclerViewItemSearch = view.findViewById(R.id.recyclerViewItemSearch)
        val spanCount = 2
        val layoutManager = GridLayoutManager(context, spanCount)
        recyclerViewItemSearch.layoutManager = layoutManager

        val myListAdapter = ApdapterProduct(requireContext())
        myListAdapter.setList(list)
        myListAdapter.setOnItemClickListener(object : ApdapterProduct.OnItemClickListener {
            override fun onItemClick(position: Int, item: ProductDemo?) {
                showDialog(item)
            }
        })
        recyclerViewItemSearch.adapter = myListAdapter
        back.setOnClickListener {
            val fragmentManager = requireFragmentManager()
            fragmentManager.popBackStack()        }
        viewModel.responseDataDemo.observe(viewLifecycleOwner) { products ->
           val alertAmong=products.response.total_cart
            if (alertAmong > 0) {
                numberCart.visibility = View.VISIBLE
                if (alertAmong > 98) numberCart.text = "99+"
                else numberCart.text = alertAmong.toString()

            } else {
                numberCart.visibility = View.GONE
            }
        }


        return  view
    }
    @SuppressLint("SetTextI18n")
    private fun showDialog(item: ProductDemo?) {
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

            val itemsHashTag = item.tags.map { it.name }.toMutableList()
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
            binding.amongMinItemProduct.text =
                getString(R.string.amongmin) + " ${item.so_luong_toi_thieu}"
        } else if (item.so_luong_toi_thieu > 1) {
            amongnow = item.so_luong_toi_thieu
            binding.edtAmong.setText("$amongnow")
            binding.amongMinItemProduct.text =
                getString(R.string.amongmax) + " ${item.so_luong_toi_thieu}"
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
                    if (s.isEmpty() || s.toString().toInt() == 0) {
                        binding.sos.text = getString(R.string.addFailCheck)
                        binding.sos.visibility = View.VISIBLE
                        amongnow = 0
                    } else {
                        amongnow = s.toString().toInt()
                    }
                }

            }
        })
        if (item.bonus_coins > 0) {
            binding.bonusCoinsItemProduct.text =
                getString(R.string.bonus) + " ${item.bonus_coins} " + R.string.coins
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
            if (binding.edtAmong.text != null) {
                if (binding.edtAmong.text.isEmpty() || binding.edtAmong.text.toString()
                        .toInt() == 0
                ) {
                    StyleableToast.makeText(
                        requireContext(),
                        getString(R.string.addFailCheck),
                        R.style.failImage
                    ).show()
                } else {
                    val call = apiServiceCart.fetchAddCartCustomersUser(
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
                                viewModel.fetchDataDemo()
                                bottomSheetDialog.dismiss()
                            }
                        }


                    })
                }
            }

            bottomSheetDialog.show()
        }

    }
}