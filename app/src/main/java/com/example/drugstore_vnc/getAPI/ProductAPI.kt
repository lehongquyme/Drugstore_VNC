package com.example.drugstore_vnc.getAPI

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

}