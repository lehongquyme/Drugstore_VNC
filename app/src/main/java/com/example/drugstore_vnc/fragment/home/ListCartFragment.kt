package com.example.drugstore_vnc.fragment.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.home.AdapterProductCart
import com.example.drugstore_vnc.databinding.FragmentListCartBinding
import com.example.drugstore_vnc.model.home.ProductInCartCustomer
import com.example.drugstore_vnc.model.home.totalprice.TotalPrice
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
import com.example.drugstore_vnc.viewModelFatory.ViewModelFactory
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.overlay.BalloonOverlayAnimation
import com.skydoves.balloon.overlay.BalloonOverlayCircle

class ListCartFragment : Fragment() {
    private lateinit var binding: FragmentListCartBinding
    private lateinit var viewModel: ViewModelProductAPI
    private lateinit var myListAdapter: AdapterProductCart
    private lateinit var listCart: MutableList<ProductInCartCustomer>
    private var oder: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCartBinding.inflate(inflater, container, false)
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        initializeViewModel()
        observeViewModel()
        setupViews()
        Handler(Looper.getMainLooper()).postDelayed({
            showTooltip()
        }, 1000)
        binding.imgAlertNote.setOnClickListener {
            showTooltip()        }
        return binding.root
    }

    private fun showTooltip() {
        val balloon = Balloon.Builder(requireContext())
            .setText(getString(R.string.donePriceListCard))
            .setOverlayColorResource(R.color.gray)
            .setIsVisibleOverlay(true)
            .setOverlayPadding(6f)
            .setArrowSize(10)
            .setWidth(200)
            .setPadding(10)
            .setArrowOrientation(ArrowOrientation.START)
            .setArrowPosition(0.5f) // Set arrow position at the middle of the Balloon
            .setOverlayShape(BalloonOverlayCircle(radius = 36f))
            .setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
            .setBackgroundColorResource(R.color.white)
            .setTextColorResource(R.color.red)
            .setDismissWhenOverlayClicked(false)
            .build()

        balloon.showAlignRight(binding.imgAlertNote)

        Handler(Looper.getMainLooper()).postDelayed({
            balloon.dismiss()
        }, 2000)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val allItemsSelected = myListAdapter.areAllItemsSelected()
        binding.checkboxAll.isChecked = allItemsSelected
        myListAdapter.selectAllItems()

        binding.btnToPay.setOnClickListener {
            if (oder > 0) {
                Navigation.findNavController(requireView()).navigate(R.id.payFragment)
            } else {
                Toast.makeText(requireContext(), getString(R.string.pleaseAddProduct), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.fetchDataCart()
        myListAdapter.setOnItemCheckListener(object : AdapterProductCart.OnItemCheckListener {
            override fun onItemCheckChanged() {
                updateCheckboxAllState()
            }
        })
    }
    private fun updateCheckboxAllState() {
        val allItemsSelected = myListAdapter.areAllItemsSelected()
        binding.checkboxAll.isChecked = allItemsSelected
    }
    private fun initializeViewModel() {
        viewModel = ViewModelProvider(
            this@ListCartFragment,
            ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java]
    }

    private fun setupViews() {
        binding.backHome.setOnClickListener {
            findNavController().popBackStack()
            CheckToPay.listPay.clear()
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSelect.layoutManager = layoutManager
        myListAdapter = AdapterProductCart(requireContext(), viewModel, binding)
        binding.recyclerViewSelect.adapter = myListAdapter


        binding.checkboxAll.setOnClickListener {
            val allItemsSelected = myListAdapter.areAllItemsSelected()
            binding.checkboxAll.isChecked = !allItemsSelected
            if (binding.checkboxAll.isChecked) {
                myListAdapter.selectAllItems()
            } else {
                myListAdapter.deselectAllItems()
            }
            calculateTotalForSelectedItems()
        }
    }
    private fun calculateTotalForSelectedItems() {
        val total = myListAdapter.totalPriceForSelectedItems()
        updateTotalViews(total)
    }
    private fun updateTotalViews(total: TotalPrice) {
        binding.priceAmongTotalProduct.text = total.price.toString()
        binding.amongTotalProduct.text = total.among.toString()
        binding.priceAmongTotalOders.text = total.oder.toString()
        oder = total.oder
    }

    private fun observeViewModel() {
        viewModel.amongCart.observe(viewLifecycleOwner) { products ->
            products?.let {
                val productItem = it.products.data
                listCart= productItem
                myListAdapter.setList(productItem)
                var totalPrice = 0
                var among = 0

                listCart.forEach {  data ->
                        totalPrice += data.so_luong * data.discount_price
                        among += data.so_luong

                }
                binding.priceAmongTotalProduct.text = totalPrice.toString()
                binding.amongTotalProduct.text =among.toString()
                binding.priceAmongTotalOders.text = listCart.size.toString()
                oder = listCart.size
            }
        }



        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            total?.let {
                updateTotalViews(total)
            }
        }


    }

}
