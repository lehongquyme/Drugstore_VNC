package com.example.drugstore_vnc.getAPI

import com.example.drugstore_vnc.fragment.manager.model.contact.Contact
import com.example.drugstore_vnc.fragment.manager.model.infor.Profile
import com.example.drugstore_vnc.fragment.manager.model.news.News
import com.example.drugstore_vnc.model.pay.ResponsePay
import com.example.drugstore_vnc.model.portfolio.ProductPortfolio
import com.example.drugstore_vnc.pharmacyCounters.model.DrugstoreResponse
import com.example.drugstore_vnc.pharmacyCounters.model.ProvinceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpinnerProvinceAPI {
    @GET("v2/system/provinces")
    suspend fun fetchDataProvince(): ProvinceResponse
    @GET("v2/system/provinces/agency_list")
    suspend fun fetchDataDrugstore(@Query("province_id") provinceId: Int): DrugstoreResponse
    @GET("v2/cart/list_voucher")
    suspend fun  fetchTakeVoucher(
        @Query("data_id[]") idCart:List<Int>
    ): ResponsePay
    @GET("v2/system/category")
    suspend fun  fetchTakeCategory(): ProductPortfolio
    @GET("v2/agency/category")
    suspend fun  fetchTakeManagerShop(): ProductPortfolio
    @GET("v2/member/profile")
    suspend fun  fetchTakeProfile(): Profile
    @GET("v2/system/contact")
    suspend fun  fetchTakeContact(): Contact
    @GET("v2/system/list_news")
    suspend fun  fetchTakeNews(): News

}