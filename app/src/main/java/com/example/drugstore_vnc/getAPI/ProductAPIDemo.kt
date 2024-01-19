package com.example.drugstore_vnc.getAPI

import com.example.drugstore_vnc.model.home.HomepageResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductAPIDemo {
    @GET("v2/system/homepage")
     fun fetchHomepageData(): Call<HomepageResponse>
}