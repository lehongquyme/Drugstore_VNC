package com.example.drugstore_vnc.fragment.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.category.ApddapterPortfolio
import com.example.drugstore_vnc.model.pay.ResponseXX
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() , ApddapterPortfolio.OnItemClickListener{
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var myListAdapter: ApddapterPortfolio
    private lateinit var categoryViewModel: ViewModelSpinnerAPI
    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryViewModel =
            ViewModelProvider(this@CategoryFragment)[ViewModelSpinnerAPI::class.java]
    }


    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_category, container, false)
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.VISIBLE
                recyclerView=rootView.findViewById(R.id.recyclerViewCategory)
        progressBar= rootView.findViewById<ProgressBar>(R.id.progressBarcategory)
        edtSearch=rootView.findViewById(R.id.searchTextInputLayout)
        edtSearch.setOnTouchListener { v, event ->
            view?.findNavController()?.navigate(R.id.searchFragment)
            v?.onTouchEvent(event) ?: true
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        myListAdapter = ApddapterPortfolio(this,requireContext(),0)
        recyclerView.adapter = myListAdapter
        categoryViewModel.category.observe(viewLifecycleOwner) { data ->
            val list= data.map { it }
            if (list.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                job = CoroutineScope(Dispatchers.Main).launch {
                    delay(500) // 2 seconds delay
                    progressBar.visibility = View.GONE
                    myListAdapter.setList(list.toMutableList())
                }
            } else {
                // Data is not available, show the progress bar
                progressBar.visibility = View.VISIBLE
            }
        }

        categoryViewModel.fetchCategory()


        return rootView
    }

    override fun onItemClick(position: Int,value:ResponseXX) {
        val gson = Gson()
        val jsonString = gson.toJson(value)
        val bundle = bundleOf("Category" to jsonString)
        view?.findNavController()?.navigate(R.id.selectCategoryFragment, bundle)
    }

}