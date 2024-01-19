package com.example.drugstore_vnc.fragment.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.history.ApdapterPurchaseHistory
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.model.history.purchase.DataPurchaseHistory
import com.example.drugstore_vnc.model.history.purchase.PurchaseHistory
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchaseHistoryFragment : Fragment() {
    private lateinit var apiServiceCart: TakeProductInCart
    private  var  newItems:List<DataPurchaseHistory>?=null
    private lateinit var  itemAdapter:ApdapterPurchaseHistory
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_purchase_history, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerViewPurchaseHistory)
        val progressBar = rootView.findViewById<ProgressBar>(R.id.progressBarPurchaseHistory)
        apiServiceCart.fetchTakeHistoryPurchase(null)
            .enqueue(object : Callback<PurchaseHistory> {
                override fun onResponse(
                    call: Call<PurchaseHistory>,
                    response: Response<PurchaseHistory>
                ) {

                    if (response.isSuccessful) {
                       newItems  = response.body()?.response?.data
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                         itemAdapter = ApdapterPurchaseHistory(requireContext())
                        if (newItems?.isNotEmpty() == true) {
                            // Introduce a 2-second delay before hiding the progress bar
                            job = CoroutineScope(Dispatchers.Main).launch {
                                delay(500) // 2 seconds delay
                                progressBar.visibility = View.GONE
                                itemAdapter.setList(newItems)

                            }
                        } else {
                            // Data is not available, show the progress bar
                            progressBar.visibility = View.VISIBLE
                        }
                        itemAdapter.setOnItemClickListener(object :
                            ApdapterPurchaseHistory.OnItemClickListener {
                            override fun onItemClick(id: Int) {
                                val bundle = bundleOf("idDetail" to id)
                                view?.findNavController()?.navigate(R.id.detailFragment,bundle)
                            }
                        })
                        recyclerView.adapter = itemAdapter
                    }
                }

                override fun onFailure(call: Call<PurchaseHistory>, t: Throwable) {

                }
            })
        return rootView
    }
}