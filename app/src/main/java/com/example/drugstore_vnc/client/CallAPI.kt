package com.example.drugstore_vnc.client

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.model.home.ProductToCart
import com.example.drugstore_vnc.privateCustomers.model.InforCustomer
import com.example.drugstore_vnc.privateCustomers.model.ResponseInforCustomer
import io.github.muddz.styleabletoast.StyleableToast
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallAPI() {
    interface AuthCallback {
        fun onTokenReceived(token: ResponseInforCustomer, pharmacy:String?)
        fun onSignInSuccess(checkSignIn: Int)
        fun onSignInFail(check: String)

    }

    interface AuthCallbackAddToCard {
        fun onAddToCart(check: Int)

    }


    fun callRetrofitSignUp(call: Call<ResponseBody>, activity: Activity, intent: Intent) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    StyleableToast.makeText(activity,
                    "Đăng ký thành công",
                    R.style.mytoast).show()
                    activity.startActivity(intent)
                    activity.finish()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }

    fun callRetrofitSignIn(
        call: Call<InforCustomer>,
        authCallback: AuthCallback? = null,
    ) {
        var checkSignIn = 0
        call.enqueue(object : Callback<InforCustomer> {
            override fun onResponse(
                call: Call<InforCustomer>,
                response: Response<InforCustomer>
            ) {
                if ((response.body()?.message?.size ?: 0) > 0) {
                    val check = response.body()?.message?.get(0).toString()
                    authCallback?.onSignInFail(check)
                } else {
                    val authToken = response.body()?.response
                    val pharmacy = response.body()?.response?.ten_nha_thuoc
                    authToken?.let {
                        authCallback?.onTokenReceived(it, pharmacy)
                        checkSignIn = 1
                        authCallback!!.onSignInSuccess(checkSignIn)
                    }
                }
            }

            override fun onFailure(call: Call<InforCustomer>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

        })
    }

    fun callRetrofitPostCart(
        call: Call<ProductToCart>,
        authCallback: AuthCallbackAddToCard? = null,

        ) {
        call.enqueue(object : Callback<ProductToCart> {
            override fun onResponse(
                call: Call<ProductToCart>,
                response: Response<ProductToCart>
            ) {
                authCallback?.onAddToCart(response.body()?.message?.size ?: 0
                )

            }

            override fun onFailure(call: Call<ProductToCart>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

        })
    }


}