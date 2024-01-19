package com.example.drugstore_vnc.fragment.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drugstore_vnc.adapter.manager.ApdapterNews
import com.example.drugstore_vnc.databinding.FragmentNewsBinding
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI


class NewsFragment : Fragment() {
    private lateinit var newsViewModel: ViewModelSpinnerAPI
    private lateinit var binding: FragmentNewsBinding
    private lateinit var myListAdapter: ApdapterNews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel =
            ViewModelProvider(this@NewsFragment)[ViewModelSpinnerAPI::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentNewsBinding.inflate(layoutInflater)
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewNews.layoutManager = layoutManager
       newsViewModel.news.observe(viewLifecycleOwner) { data ->
           myListAdapter = ApdapterNews(data.data)
           binding.recycleViewNews.adapter = myListAdapter

       }

        newsViewModel.fetchNews()

        binding.backManagerFragment.setOnClickListener {
            requireFragmentManager().popBackStack()
        }
        return binding.root
    }


}