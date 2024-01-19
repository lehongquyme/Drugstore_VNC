package com.example.drugstore_vnc.fragment.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.home.ApdapterProductPay
import com.example.drugstore_vnc.adapter.home.ApdapterVoucher
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.DialoginpayBinding
import com.example.drugstore_vnc.databinding.FragmentPayBinding
import com.example.drugstore_vnc.local.SharedPreferencesToken
import com.example.drugstore_vnc.model.home.ProductInCartCustomer
import com.example.drugstore_vnc.model.home.totalprice.Voucher
import com.example.drugstore_vnc.model.pay.PayProduct
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.util.funGeneralCheck
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import com.example.drugstore_vnc.viewModelFatory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.github.muddz.styleabletoast.StyleableToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PayFragment : Fragment() {
    private var _binding: FragmentPayBinding? = null
    private var voucher_available: Int = 1
    private lateinit var viewModel: ViewModelProductAPI
    private lateinit var viewModelGEt: ViewModelSpinnerAPI
    private lateinit var apiService: TakeProductInCart
    private var reductionRate = 0.0
    private var checkPayOnline = 0
    private lateinit var myListAdapter: ApdapterProductPay
    private var listVouccher = mutableListOf<Voucher>()
    private var listPay = mutableListOf<ProductInCartCustomer>()
    private var totalPricePay = 0
    private var totalAmongPay: Int = 0
    private var totalCoin: Int = 0
    private var priceVoucher = 0
    private val productIdCart = mutableListOf<Int>()
    private val productId = mutableListOf<Int>()
    private var checkVoucherDone: Boolean = false
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        listPay.clear()

        _binding = FragmentPayBinding.inflate(inflater, container, false)
        binding.backListCart.setOnClickListener {
            val fragmentManager = requireFragmentManager()
            fragmentManager.popBackStack()
        }

        viewModel = ViewModelProvider(
            this@PayFragment, ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java]
        viewModelGEt = ViewModelProvider(this@PayFragment)[ViewModelSpinnerAPI::class.java]

        viewModel.fetchDataInCart()
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewPay.layoutManager = layoutManager
        myListAdapter = ApdapterProductPay(requireContext())

        viewModel.amongcart.observe(viewLifecycleOwner) { products ->
            productIdCart.addAll(products.products.data.map { it.gio_hang_id })
            productId.addAll(products.products.data.map { it.id })
            viewModelGEt.fetchDataPay(productIdCart)

            products?.let { it ->
                val productItem = it.products.data

                val newItems = productItem.filter { newItem ->
                    CheckToPay.listPay.any { it.oder == newItem.id }
                }
                listPay.addAll(newItems)
                myListAdapter.setList(listPay)
                listPay.forEachIndexed { _, data ->
                    totalPricePay += if (data.don_gia > data.discount_price) {
                        data.so_luong * data.discount_price
                    } else {
                        data.so_luong * data.don_gia
                    }
                    totalAmongPay += data.so_luong
                    if (data.bonus_coins > 0) {
                        totalCoin += (data.bonus_coins * data.so_luong)
                    }
                }
            }
            binding.btnContinueInPay.setOnClickListener {
                listVouccher.clear()
                showDialog()
                viewModelGEt.pay.observe(viewLifecycleOwner) { voucherList ->
                    voucherList?.let { vouchers ->
                        listVouccher.addAll(vouchers.response.map { voucher ->
                            Voucher(voucher.value, voucher.title, voucher.content)
                        })
                    }
                }
            }
            binding.priceAmongTotalProduct.text = totalAmongPay.toString()
            binding.amongTotalProduct.text = totalPricePay.toString()
            binding.recyclerViewPay.adapter = myListAdapter

        }

        return binding.root

    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding: DialoginpayBinding = DialoginpayBinding.inflate(layoutInflater)
        val infor = SharedPreferencesToken(requireContext()).getToken()
        apiService =
            ClientAPI.getClientProduct(requireContext()).create(TakeProductInCart::class.java)

        bottomSheetDialog.setContentView(binding.root)
        val items = listVouccher
        binding.edtFullName.setText(infor!!.ten)
        binding.edtPhone.setText(infor.sdt)
        binding.edtEmail.setText(infor.email)
        binding.edtTaxCode.setText(infor.ma_so_thue)
        binding.edtAddress.setText(infor.dia_chi)
        binding.amongTotalProduct.text = totalAmongPay.toString()
        binding.priceAmongTotalProduct.text = totalPricePay.toString()
        binding.iconGit.visibility = View.GONE
        binding.vocherAdd.visibility = View.GONE

        if (totalCoin > 0) {
            binding.iconGit.visibility = View.VISIBLE
            binding.iconGit.text = getString(R.string.odersReceive) + " $totalCoin"
        }

        val adapter = ApdapterVoucher(requireContext(), items)

        viewModel.amongcart.observe(viewLifecycleOwner) { products ->
            reductionRate = products.ti_le_giam.toDouble()
            binding.reductionRate.text = "-${reductionRate}%"
        }

        binding.txtVorcher.setOnClickListener {
            checkVoucherDone = !checkVoucherDone
            if (binding.txtVorcher.text == requireContext().getString(R.string.delete)) {

                binding.edtVorcher.setText("")
                voucher_available = 1
                val price = totalPricePay
                if (checkPayOnline == 1) {
                    binding.priceAmongTotalProduct.text =
                        (price - (price * (reductionRate / 100)).toInt()).toString()
                } else {
                    binding.priceAmongTotalProduct.text = price.toString()
                }
                binding.vocherAdd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), android.R.color.holo_green_light
                    )
                )
            }
        }

        var switch = 0

        binding.switchIcon.setOnCheckedChangeListener { _, isChecked ->
            switch = 0
            binding.alertChangeCoin.visibility = View.GONE
            if (isChecked) {
                binding.alertChangeCoin.visibility = View.VISIBLE
                switch = 1
                binding.alertChangeCoin.text = getString(R.string.use1000)
            }
        }

        binding.checkBoxPay.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                checkPayOnline = 1

            } else {
                checkPayOnline = 0
            }
            checkVoucher(switch, binding.edtVorcher.text.toString(), binding)


        }
        binding.btnOder.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage(getString(R.string.doyouwantcreatethisorder))
                .setNegativeButton(getString(R.string.cancle)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    val checkName =
                        funGeneralCheck().checkNullData(
                            requireContext(),
                            binding.edtFullName,
                            binding.layoutEdtFullName
                        )
                    val checkPhone =
                        funGeneralCheck().checkNullDataPhone(
                            requireContext(),
                            binding.edtPhone,
                            binding.layoutEdtPhone
                        )
                    val checkEmail =
                        funGeneralCheck().checkEmail(
                            requireContext(),
                            binding.edtEmail,
                            binding.layoutEdtEmail
                        )
                    val checkAddress =
                        funGeneralCheck().checkNullData(
                            requireContext(),
                            binding.edtAddress,
                            binding.layoutEdtAddress
                        )

                    if (checkName && checkPhone && checkEmail && checkAddress) {
                        val call = apiService.fetchAddOder(
                            productIdCart,
                            1,
                            binding.edtFullName.text.toString(),
                            binding.edtPhone.text.toString(),
                            binding.edtEmail.text.toString(),
                            binding.edtAddress.text.toString(),
                            binding.edtTaxCode.text.toString(),
                            binding.edtNote.text.toString(),
                            checkPayOnline,
                            binding.edtVorcher.text.toString(),
                            switch,
                            binding.priceAmongTotalProduct.text.toString(),
                        )
                        call.enqueue(object : Callback<PayProduct> {
                            override fun onResponse(
                                call: Call<PayProduct>, response: Response<PayProduct>
                            ) {
                                if (checkPayOnline == 0) {
                                    val builder = AlertDialog.Builder(requireContext())
                                    if (response.message().length > 2) {
                                        StyleableToast.makeText(
                                            requireContext(),
                                            response.body()?.message.toString(),
                                            R.style.failImage
                                        ).show()
                                    }
                                    if ((response.body()?.response?.description?.length ?: 0) > 0) {
                                        builder.setTitle(
                                            response.body()?.response?.description ?: "lỗi"
                                        )
                                        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                            bottomSheetDialog.dismiss()
                                            val fragmentManager = parentFragmentManager
                                            fragmentManager.popBackStack(
                                                null,
                                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                                            )
                                            Navigation.findNavController(requireView())
                                                .navigate(R.id.homeFragment)
                                        }
                                        val dialog = builder.create()
                                        dialog.setCanceledOnTouchOutside(false)
                                        builder.show()
                                    }
                                } else {

                                    if (voucher_available == 1 || binding.edtVorcher.text.toString() == "" || binding.edtVorcher.text == null) {
                                        bottomSheetDialog.dismiss()
                                        val bundle = bundleOf(
                                            "URL" to response.body()?.response?.url_payment.toString(),
                                            "ListPay" to productId
                                        )
                                        val fragmentManager = parentFragmentManager
                                        fragmentManager.popBackStack(
                                            null,
                                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                                        )
                                        view?.findNavController()
                                            ?.navigate(R.id.webViewFragment, bundle)
                                    } else {

                                        StyleableToast.makeText(
                                            requireContext(),
                                            getString(R.string.warningVoucher),
                                            R.style.warningVoucher
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<PayProduct>, t: Throwable) {
                                Log.e("Error", t.message.toString())
                            }
                        })
                    }
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }


        binding.imgAddVocher.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val title = getString(R.string.listvoucher)
            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(
                StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableTitle.setSpan(
                ForegroundColorSpan(requireContext().resources.getColor(android.R.color.holo_green_light)),
                0,
                title.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val titleTextView = TextView(context)
            titleTextView.text = spannableTitle
            titleTextView.textSize = 20f
            titleTextView.gravity = Gravity.CENTER_HORIZONTAL
            builder.setCustomTitle(titleTextView)

            builder.setAdapter(adapter) { _, which ->
                val selectedItem = items[which]
                binding.txtVorcher.text = requireContext().getString(R.string.delete)
                checkVoucherDone = true
                binding.txtVorcher.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), android.R.color.holo_red_light
                    )
                )
                binding.vocherAdd.visibility = View.GONE
                if (binding.edtVorcher.text.isNotEmpty()) {
                    binding.vocherAdd.visibility = View.VISIBLE
                }
                binding.edtVorcher.setText(selectedItem.key)
                checkVoucher(switch, selectedItem.key, binding)

            }

            binding.edtVorcher.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                @SuppressLint("ResourceAsColor")
                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    checkVoucher(switch, binding.edtVorcher.text.toString(), binding)

                    binding.txtVorcher.text = getString(R.string.apply)
                    binding.txtVorcher.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), android.R.color.holo_green_dark
                        )
                    )
                    items.forEach {
                        if (it.key == s.toString()) {
                            checkVoucher(switch, binding.edtVorcher.text.toString(), binding)
                            binding.txtVorcher.text = requireContext().getString(R.string.delete)
                            binding.txtVorcher.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(), android.R.color.holo_red_light
                                )
                            )
                        }
                    }

                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            builder.setOnCancelListener {
                it.dismiss()
            }
            builder.setNegativeButton(getString(R.string.cancle)) { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(true)
            builder.show()
        }
        binding.edtTaxCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {
                val text = charSequence.toString()

                if (text.length == 10 && count == 1) {
                    val newText = StringBuilder(text).insert(10, "-").toString()
                    binding.edtTaxCode.setText(newText)
                    binding.edtTaxCode.setSelection(newText.length) // Move the cursor to the end
                } else if (text.length == 11 && count == 0 && text.endsWith("-")) {
                    val newText = text.substring(0, 10)
                    binding.edtTaxCode.setText(newText)
                    binding.edtTaxCode.setSelection(newText.length) // Move the cursor to the end
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        bottomSheetDialog.show()
    }

    private fun checkVoucher(switch: Int, voucherCode: String, binding: DialoginpayBinding) {

        viewModel.fetchDataPayAdd(listPay.map { it.gio_hang_id }, switch, voucherCode)
        binding.vocherAdd.visibility = View.GONE
        priceVoucher = 0
        if (binding.edtVorcher.text.isNotEmpty()) {
            binding.vocherAdd.visibility = View.VISIBLE
        }

        viewModel.responseDataPay.observe(viewLifecycleOwner) { voucherList ->
            voucherList?.let { vouchers ->
                binding.vocherAdd.text = vouchers.response.voucher_description
                voucher_available = vouchers.response.voucher_available


                binding.vocherAdd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), android.R.color.holo_red_light
                    )
                )
                priceVoucher=vouchers.response.money

                if (vouchers.response.voucher_available == 1) {
                    voucher_available = vouchers.response.voucher_available
                    binding.vocherAdd.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), android.R.color.holo_green_light
                        )
                    )
                    val total = totalPricePay - priceVoucher
                    binding.priceAmongTotalProduct.text = "$total"
                    if (checkPayOnline == 1) {
                        binding.priceAmongTotalProduct.text =
                            "${total - (total * (reductionRate / 100)).toInt()}"
                    }
                }else{
                    val total = totalPricePay
                    binding.priceAmongTotalProduct.text = "$total"
                    if (checkPayOnline == 1) {
                        binding.priceAmongTotalProduct.text =
                            "${total - (total * (reductionRate / 100)).toInt()}"
                    }
                }


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}