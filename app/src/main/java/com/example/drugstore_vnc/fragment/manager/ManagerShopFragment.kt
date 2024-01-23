@file:Suppress("DEPRECATION")

package com.example.drugstore_vnc.fragment.manager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.category.ApddapterPortfolio
import com.example.drugstore_vnc.databinding.FragmentManagerShopBinding
import com.example.drugstore_vnc.model.pay.ResponseXX
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ManagerShopFragment : Fragment() , ApddapterPortfolio.OnItemClickListener {
    private lateinit var binding: FragmentManagerShopBinding
    private lateinit var myListAdapter: ApddapterPortfolio
    private lateinit var managerShopViewModel: ViewModelSpinnerAPI
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        managerShopViewModel = ViewModelProvider(this)[ViewModelSpinnerAPI::class.java]

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeUI() {
        binding.searchTextInputLayout.setOnTouchListener { v, event ->
            view?.findNavController()?.navigate(R.id.searchFragment)
            v?.onTouchEvent(event) ?: true
        }

        binding.recyclerViewSelectMangerShop.layoutManager = LinearLayoutManager(requireContext())

        myListAdapter = ApddapterPortfolio(this, requireContext(),1)
        binding.recyclerViewSelectMangerShop.adapter = myListAdapter
        managerShopViewModel.category.observe(viewLifecycleOwner) { data ->
            val list = data.map { it }
            if (list.isNotEmpty()) {
                binding.progressBarMangerShop.visibility = View.VISIBLE
                job = CoroutineScope(Dispatchers.Main).launch {
                    delay(500) // 2 seconds delay
                    binding.progressBarMangerShop.visibility = View.GONE
                    myListAdapter.setList(list.toMutableList())
                }
            } else {
                // Data is not available, show the progress bar
                binding.progressBarMangerShop.visibility = View.VISIBLE
            }
        }
        managerShopViewModel.fetchManagerShop()
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        CheckToPay.binding?.bottomNavigationView?.visibility = View.GONE
        binding = FragmentManagerShopBinding.inflate(inflater, container, false)
        binding.backHomeMangerShop.setOnClickListener {
            val fragmentManager = requireFragmentManager()
            fragmentManager.popBackStack()
        }
        initializeUI()
        binding.selling.setOnClickListener {
            val bundle = bundleOf("category" to "ban_chay")
            view?.findNavController()
                ?.navigate(R.id.selectedFragment, bundle)

        }
        binding.promotion.setOnClickListener {
            val bundle = bundleOf("category" to "khuyen_mai")
            view?.findNavController()
                ?.navigate(R.id.selectedFragment, bundle)
        }
        binding.all.setOnClickListener {
            val bundle = bundleOf("category" to "all")
            view?.findNavController()
                ?.navigate(R.id.selectedFragment, bundle)
        }
        return binding.root
    }
    override fun onItemClick(position: Int, value: ResponseXX) {
///
    }

}