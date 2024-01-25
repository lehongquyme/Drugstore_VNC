package com.example.drugstore_vnc.fragment.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.history.ApdapterItemPurchase
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.BottomsheetBinding
import com.example.drugstore_vnc.databinding.FragmentDetailBinding
import com.example.drugstore_vnc.fragment.history.model.history.DataType
import com.example.drugstore_vnc.fragment.history.model.history.Detail
import com.example.drugstore_vnc.fragment.manager.ItemRecyclerAdapter
import com.example.drugstore_vnc.model.history.purchaseItem.DataPurchaseItem
import com.example.drugstore_vnc.model.history.purchaseItem.PurchaseItem
import com.example.drugstore_vnc.model.history.purchaseItem.ResponsePurchaseItem
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.CheckToPay
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private lateinit var itemAdapter: ApdapterItemPurchase
    private lateinit var itemRecyclerAdapter: ItemRecyclerAdapter
    private lateinit var apiServiceCart: TakeProductInCart
    private val binding get() = _binding!!
    private lateinit var filter: String
    private var newItems: List<DataPurchaseItem>? = null
    private var newItemsCategory: List<DataType>? = null
    private var inforCustomer: ResponsePurchaseItem? = null
    private var idDetail: Int? = null
    private lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        CheckToPay.binding?.bottomNavigationView?.visibility = View.GONE
        filter = arguments?.getString("Portfolio").toString()
        if (filter != "null") {
            binding.cardTotal.visibility = View.GONE
            when (filter) {

                "hoat_chat" -> {
                    callCategory(filter)

                    binding.textView.text = getString(R.string.active)
                }

                "nhom_thuoc" -> {
                    callCategory(filter)
                    binding.textView.text = getString(R.string.drugGroup)
                }

                "nha_san_xuat" -> {
                    callCategory(filter)
                    binding.textView.text = getString(R.string.producer)
                }
            }
        } else {
            idDetail = arguments?.getInt("idDetail")
            apiServiceCart.fetchTakeHistoryPurchaseDetail(idDetail!!, null)
                .enqueue(object : Callback<PurchaseItem> {
                    override fun onResponse(
                        call: Call<PurchaseItem>,
                        response: Response<PurchaseItem>
                    ) {
                        if (response.isSuccessful) {
                            newItems = response.body()?.response?.products?.data
                            binding.recyclerViewPayHistory.layoutManager =
                                LinearLayoutManager(requireContext())
                            itemAdapter = ApdapterItemPurchase(requireContext())
                            if (newItems?.isNotEmpty() == true) {
                                job = CoroutineScope(Dispatchers.Main).launch {
                                    delay(500)
                                    binding.progressBarDetail.visibility = View.GONE
                                    newItems?.let { itemAdapter.setList(it) }
                                }
                            } else {
                                binding.progressBarDetail.visibility = View.VISIBLE
                            }
                            binding.recyclerViewPayHistory.adapter = itemAdapter
                            binding.priceAmongTotalProduct.text =
                                response.body()?.response?.total_products.toString()
                            binding.amongTotalProduct.text =
                                response.body()?.response?.total_price.toString()
                            inforCustomer = response.body()?.response
                        }
                    }

                    override fun onFailure(call: Call<PurchaseItem>, t: Throwable) {
                    }
                })
            binding.backHistory.setOnClickListener {
                val fragmentManager = requireFragmentManager()
                fragmentManager.popBackStack()
            }
            binding.btnContinueInPayHistory.setOnClickListener {
                showDialog()
            }
        }
        return binding.root
    }

    private fun callCategory(key: String) {
        apiServiceCart.fetchDataCategoryType(key, null, null)
            .enqueue(object : Callback<Detail> {
                override fun onResponse(
                    call: Call<Detail>,
                    response: Response<Detail>
                ) {
                    if (response.isSuccessful) {
                        newItemsCategory = response.body()?.response?.data
                        binding.recyclerViewPayHistory.layoutManager =
                            LinearLayoutManager(requireContext())
                        itemRecyclerAdapter = ItemRecyclerAdapter()
                        if (newItemsCategory?.isNotEmpty() == true) {
                            job = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                binding.progressBarDetail.visibility = View.GONE
                                newItemsCategory?.let { itemRecyclerAdapter.setList(it) }
                                binding.recyclerViewPayHistory.adapter = itemRecyclerAdapter

                            }
                        } else {
                            binding.progressBarDetail.visibility = View.VISIBLE
                        }

                    }
                }

                override fun onFailure(call: Call<Detail>, t: Throwable) {
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding: BottomsheetBinding = BottomsheetBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(binding.root)
        binding.oderNumberValue.text = idDetail.toString()
        binding.oderDateValue.text = inforCustomer?.date_time
        binding.totalPriceValue.text = inforCustomer?.total_price.toString()
        if ((inforCustomer?.coin_value ?: 0) > 0) {
            binding.useCoin.visibility = View.VISIBLE
            binding.useCoinValue.visibility = View.VISIBLE
            binding.useCoinValue.text = "-${inforCustomer?.coin_value}"
        }
        if ((inforCustomer?.voucher_value ?: 0) > 0) {
            binding.useVocher.visibility = View.VISIBLE
            binding.useVocherValue.visibility = View.VISIBLE
            binding.useCoinValue.text = "-${inforCustomer?.voucher_value}"
        }
        binding.TotalEndValue.text = inforCustomer?.price.toString()
        binding.coinGiftValue.text = inforCustomer?.coin_bonus.toString()
        bottomSheetDialog.show()
    }
}