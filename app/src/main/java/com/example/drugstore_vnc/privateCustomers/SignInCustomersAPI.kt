package com.example.drugstore_vnc.privateCustomers
import com.example.drugstore_vnc.privateCustomers.model.InforCustomer
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignInCustomersAPI {
    @FormUrlEncoded
    @POST("v2/customer/login")
    fun fetchloginCustomersUser(
        @Field("username") userDrugstore: String,
        @Field("password") passwordDrugstore: String,
    ): Call<InforCustomer>
}