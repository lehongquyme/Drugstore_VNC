package com.example.drugstore_vnc.pharmacyCounters

import com.example.drugstore_vnc.privateCustomers.model.InforCustomer
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignInDrugstoreAPI {
    @FormUrlEncoded
    @POST("v2/member/login")
    fun fetchloginDrugstoreUser(
        @Field("username") userDrugstore: String,
        @Field("password") passwordDrugstore: String,
    ): Call<InforCustomer>
}