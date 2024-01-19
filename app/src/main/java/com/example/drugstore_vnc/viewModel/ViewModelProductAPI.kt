package com.example.drugstore_vnc.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.getAPI.ProductAPIDemo
import com.example.drugstore_vnc.model.home.Data
import com.example.drugstore_vnc.model.home.HomepageResponse
import com.example.drugstore_vnc.model.home.ProductInCart
import com.example.drugstore_vnc.model.home.ResponseProduct
import com.example.drugstore_vnc.model.home.totalprice.TotalPrice
import com.example.drugstore_vnc.model.pay.ResponsePayAdd
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModelProductAPI(context: Context) : ViewModel() {
    private val apiServiceDemo: ProductAPIDemo
    private val apiServiceCart: TakeProductInCart
    val memberName = MutableLiveData<String>()
    val memberStatus = MutableLiveData<Int>()
    val amongCart = MutableLiveData<ResponseProduct>()
    val amongcart = MutableLiveData<ResponseProduct>()  // Renamed to amongcart
    val thuHangIcon = MutableLiveData<String>()
    val responseDataDemo = MutableLiveData<HomepageResponse>()
    val responseDataPay = MutableLiveData<ResponsePayAdd>()

    private val selectedItems = MutableLiveData<MutableList<Boolean>>()
    private var yourItemList: List<Data> = emptyList()
    private val _totalPrice = MutableLiveData<TotalPrice>()

    init {
        val retrofit = ClientAPI.getClientProduct(context)
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        selectedItems.value = mutableListOf()
        apiServiceDemo = retrofit.create(ProductAPIDemo::class.java)
    }

    val totalPrice: LiveData<TotalPrice>
        get() = _totalPrice


    fun updateCheckBoxState(position: Int, isChecked: Boolean) {
        if (position != RecyclerView.NO_POSITION && position < yourItemList.size) {
            val newList = yourItemList.toMutableList()
            newList[position].check = isChecked
            yourItemList = newList
            selectedItems.value = selectedItems.value?.toMutableList()?.apply {
                set(position, isChecked)
            }
        }
    }

    fun fetchDataDemo() {
        apiServiceDemo.fetchHomepageData().enqueue(object : Callback<HomepageResponse> {
            override fun onResponse(call: Call<HomepageResponse>, response: Response<HomepageResponse>) {
                if (response.isSuccessful) {
                    responseDataDemo.value = response.body()
                    thuHangIcon.value = response.body()?.response?.thu_hang_icon
                    memberName.value = response.body()?.response?.member_name
                    memberStatus.value = response.body()?.response?.member_status
                }
            }

            override fun onFailure(call: Call<HomepageResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    fun updateQuantityInCart(itemId: Int, newQuantity: Int) {
        val call = apiServiceCart.fetchAddCartCustomersUser(itemId, newQuantity)
        CallAPI().callRetrofitPostCart(call, object : CallAPI.AuthCallbackAddToCard {
            override fun onAddToCart(check: Int) {
                if (check >0) {

                } else {
                    fetchDataInCart()
                    fetchDataCart()
                }
            }
        })
    }

    fun fetchDataPayAdd(idCart: List<Int>, coin: Int, voucher: String) {
        apiServiceCart.fetchTakeVoucherAdd(idCart, coin, voucher).enqueue(object : Callback<ResponsePayAdd> {
            override fun onResponse(call: Call<ResponsePayAdd>, response: Response<ResponsePayAdd>) {
                if (response.isSuccessful) {
                    responseDataPay.value = response.body()
                }
            }

            override fun onFailure(call: Call<ResponsePayAdd>, t: Throwable) {
            }
        })
    }

    fun fetchDataCart() {
        apiServiceCart.fetchTakeCartCustomersUser().enqueue(object : Callback<ProductInCart> {
            override fun onResponse(call: Call<ProductInCart>, response: Response<ProductInCart>) {
                if (response.isSuccessful) {
                    amongCart.value = response.body()?.response
                }
            }

            override fun onFailure(call: Call<ProductInCart>, t: Throwable) {

            }
        })
    }

    fun fetchDataInCart() {
        apiServiceCart.fetchTakeCartCustomersUser().enqueue(object : Callback<ProductInCart> {
            override fun onResponse(call: Call<ProductInCart>, response: Response<ProductInCart>) {
                if (response.isSuccessful) {
                    amongcart.value = response.body()?.response  // Updated to amongcart
                }
            }

            override fun onFailure(call: Call<ProductInCart>, t: Throwable) {
            }
        })
    }

}
