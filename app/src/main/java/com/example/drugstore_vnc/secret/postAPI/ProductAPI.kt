package com.example.drugstore_vnc.postAPI

import com.example.drugstore_vnc.fragment.home.model.select.Select
import com.example.drugstore_vnc.fragment.search.model.ReturnDataProduct
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProductAPI {
    @FormUrlEncoded
    @POST("v2/product/index")
     fun fetchDataProduct(
        @Field("search") search: String,
    ): Call<ReturnDataProduct>
    @FormUrlEncoded
    @POST("v2/agency/product/change_of_status")
    fun fetchDataAgency(
        @Field("id") id: Int,
    ): Call<Select>
    @FormUrlEncoded
    @POST("v2/agency/product/bestseller")
    fun fetchDataBestseller(
        @Field("id") id: Int,
    ): Call<Select>
    @FormUrlEncoded
    @POST("v2/agency/product/delete")
    fun fetchDataDelete(
        @Field("id") id: Int,
    ): Call<Select>

}
