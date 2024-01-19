package com.example.drugstore_vnc.fragment.home

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.home.ApdapterHashTag
import com.example.drugstore_vnc.adapter.home.ApdapterProduct
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.DialogaddtocartBinding
import com.example.drugstore_vnc.databinding.FragmentHomeBinding
import com.example.drugstore_vnc.local.SharedPreferencesToken
import com.example.drugstore_vnc.model.home.HomepageData
import com.example.drugstore_vnc.model.home.ProductDemo
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.AddImageSignUpGeneral.isUrlReachable
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
import com.example.drugstore_vnc.viewModelFatory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ViewModelProductAPI
    private var checkAdd = true
    private lateinit var job: Job
    private lateinit var apiService: TakeProductInCart
    private var Language: String? = null

    @SuppressLint("SuspiciousIndentation", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Language = SharedPreferencesToken(requireContext()).getLanguage()
        val configuration = resources.configuration
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.VISIBLE

        viewModel = ViewModelProvider(
            this, ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java]
        viewModel.fetchDataInCart()
        viewModel.fetchDataDemo()
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        if (Language != "vi") {
            binding.language.setImageResource(R.drawable.usa)
            configuration.setLocale(Locale("vi"))
        } else {
            binding.language.setImageResource(R.drawable.vietnam)
            configuration.setLocale(Locale.getDefault())
            requireContext().resources.updateConfiguration(
                configuration,
                requireContext().resources.displayMetrics
            )
        }

        binding.language.setOnClickListener {

            if (Language != "vi") {
                SharedPreferencesToken(requireContext()).saveDataLanguage(requireContext(), "vi")
                binding.language.setImageResource(R.drawable.usa)
                configuration.setLocale(Locale.getDefault())
                requireContext().resources.updateConfiguration(
                    configuration,
                    requireContext().resources.displayMetrics
                )
            } else {
                binding.language.setImageResource(R.drawable.vietnam)
                SharedPreferencesToken(requireContext()).saveDataLanguage(requireContext(), "en")
                configuration.setLocale(Locale("vi")) // Đặt ngôn ngữ thành Tiếng Việt
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            requireActivity().recreate()
        }
        binding.alertAmongCart.visibility = View.GONE
        apiService =
            ClientAPI.getClientProduct(requireContext()).create(TakeProductInCart::class.java)
        binding.cartInFragmentHome.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.listCartFragment)
        }
        viewModel.responseDataDemo.observe(viewLifecycleOwner) { products ->
            alertAmong(products.response.total_cart)
            Picasso.get().load(Uri.parse(products.response.thu_hang_icon)).into(binding.avata)
            SharedPreferencesToken(requireContext()).saveImageRank(products.response.thu_hang_icon)
            CheckToPay.checkAccount(checkAdd)
            if (products.response.member_status == 1) {
                checkAdd = false
                CheckToPay.checkAccount(checkAdd)
                binding.accessHome.gravity = Gravity.CENTER_VERTICAL
                binding.accessHome.visibility = View.VISIBLE
            }

            binding.nameHome.text = products.response.member_name
            products.response.let {
                val imageList = ArrayList<SlideModel>()
                it.banners.map { banner ->
                    SlideModel(banner.value)
                }.let { nonNullSlideModels ->
                    imageList.addAll(nonNullSlideModels)
                }
                binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                val productItemSelling = takeListData(it, "ban_chay", 2)
                val productItemNew = takeListData(it, "moi", 4)
                val productItemPromotional = takeListData(it, "khuyen_mai", 2)
                val productItemAll = takeListData(it, "all", 4)
                recycleView(binding.recycViewProducts, productItemAll, checkAdd)
                recycleView(binding.recycViewSellingProducts, productItemSelling, checkAdd)
                recycleView(binding.recycViewNewProducts, productItemNew, checkAdd)
                recycleView(binding.recycViewPromotionalProducts, productItemPromotional, checkAdd)
            }
        }
        binding.iconImageSellingProducts.setOnClickListener {
            changeFragment("ban_chay", R.id.selectedFragment)
        }
        binding.iconImageNewProducts.setOnClickListener {
            changeFragment("moi", R.id.selectedFragment)
        }
        binding.iconImagePromotionalProducts.setOnClickListener {
            changeFragment(
                "khuyen_mai", R.id.selectedFragment
            )
        }
        binding.iconImageProducts.setOnClickListener {
            changeFragment(
                "all", R.id.selectedFragment
            )
        }
        binding.searchTextInputLayout.setOnTouchListener { v, event ->
            Navigation.findNavController(requireView()).navigate(R.id.searchFragment);
            v?.onTouchEvent(event) ?: true
        }
        return binding.root
    }
    private fun takeListData(it: HomepageData, value: Any, among: Int): List<ProductDemo> {
        return it.products.flatMap { typeProduct ->
            typeProduct.data.filter {
                typeProduct.value == value
            }.take(among)
        }
    }

    private fun changeFragment(value: String, fragment: Int) {
        val bundle = bundleOf("key" to value)
        view?.findNavController()?.navigate(fragment, bundle)
    }

    private fun recycleView(recycle: RecyclerView, list: List<ProductDemo>, check: Boolean) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                // Check if the fragment is still attached before UI operations
                if (isAdded) {
                    binding.progressBarHome1.visibility = View.GONE
                    binding.progressBarHome2.visibility = View.GONE
                    binding.progressBarHome3.visibility = View.GONE
                    binding.progressBarHome4.visibility = View.GONE
                    binding.progressBarHome5.visibility = View.GONE

                    delay(2000)
                    if (isAdded) {
                        recycle.layoutManager = layoutManager
                        val myListAdapter = ApdapterProduct(requireContext())
                        myListAdapter.setAdd(check)
                        myListAdapter.setList(list)
                        myListAdapter.setOnItemClickListener(object :
                            ApdapterProduct.OnItemClickListener {
                            override fun onItemClick(position: Int, item: ProductDemo?) {
                                showDialog(item)
                            }
                        })
                        recycle.adapter = myListAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog(item: ProductDemo?) {
        var amongnow: Int = 1
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding: DialogaddtocartBinding = DialogaddtocartBinding.inflate(layoutInflater)
        var amongCart = 0
        viewModel.amongcart.observe(viewLifecycleOwner) { among ->
            val checkAmong = among.products.data.filter { data -> data.id == item!!.id }
                .filter { data -> data.so_luong > 0 }
            checkAmong.forEach { it ->
                amongCart = it.so_luong
            }

        }

        bottomSheetDialog.setContentView(binding.root)
        GlobalScope.launch(Dispatchers.IO) {
            val isUrlReachable = isUrlReachable(item?.img_url ?: "")

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
            binding.amongMinItemProduct.text = getString(R.string.minimumquantity)+" ${item.so_luong_toi_thieu}"
        } else if (item.so_luong_toi_thieu > 1) {
            amongnow = item.so_luong_toi_thieu
            binding.edtAmong.setText("$amongnow")
            binding.amongMinItemProduct.text = getString(R.string.maximumquantity)+" ${item.so_luong_toi_thieu}"
        } else {

            binding.amongMinItemProduct.visibility = View.GONE
        }
        binding.edtAmong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.sos.visibility = View.INVISIBLE


            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isEmpty() || s.toString().toInt()==0){
                        binding.sos.text = getString(R.string.addFailCheck)
                        binding.sos.visibility = View.VISIBLE
                        amongnow =0
                    }
                   else{
                        amongnow = s.toString().toInt()
                    }
                }
            }
        })
        if (item.bonus_coins > 0) {
            binding.bonusCoinsItemProduct.text = getString(R.string.bonus_Coins)+"${item.bonus_coins}"
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
                if (binding.edtAmong.text.isEmpty() || binding.edtAmong.text.toString().toInt()==0){
                    StyleableToast.makeText(
                        requireContext(),
                        getString(R.string.addFailCheck),
                        R.style.failImage
                    ).show()
                }else{
                    val call = apiService.fetchAddCartCustomersUser(
                        item.id,
                        binding.edtAmong.text.toString().toInt() + amongCart,
                    )
                    CallAPI().callRetrofitPostCart(call, object : CallAPI.AuthCallbackAddToCard {
                        override fun onAddToCart(check: Int) {
                            if (check > 0) {
                                StyleableToast.makeText(
                                    requireContext(),
                                    getString(R.string.addFail),
                                    R.style.failImage
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
            }

        }

        bottomSheetDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun alertAmong(among: Int) {

        if (among > 0) {
            binding.alertAmongCart.visibility = View.VISIBLE
            if (among > 98) binding.alertAmongCart.text = "99+"
            else binding.alertAmongCart.text = among.toString()

        } else {
            binding.alertAmongCart.visibility = View.GONE
        }
    }
}