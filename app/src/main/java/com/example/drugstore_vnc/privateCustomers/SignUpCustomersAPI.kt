package com.example.drugstore_vnc.privateCustomers

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SignUpCustomersAPI {
    @Multipart
    @POST("v2/customer/register")
    fun fetchregisterCustomersUser(
        @Part("ten") nameCustomers: RequestBody,
        @Part("id_agency") idAgencyCustomers: RequestBody,
        @Part("dia_chi") addressCustomers: RequestBody,
        @Part("tinh") provinceCustomers: RequestBody,
        @Part("sdt") numberPhoneCustomers: RequestBody,
        @Part("email") emailCustomers: RequestBody,
        @Part("password") passwordCustomers: RequestBody,
        @Part img: MultipartBody.Part?=null
    ): Call<ResponseBody>
}