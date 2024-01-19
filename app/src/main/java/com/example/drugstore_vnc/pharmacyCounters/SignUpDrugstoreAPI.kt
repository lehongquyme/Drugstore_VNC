package com.example.drugstore_vnc.pharmacyCounters

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SignUpDrugstoreAPI {

    @Multipart
    @POST("v2/member/register")
    fun fetchregisterDrugstoreUser(
        @Part("ten") nameRegister: RequestBody,
        @Part("ten_nha_thuoc") nameDrugstoreRegister: RequestBody,
        @Part("dia_chi") addressRegister: RequestBody,
        @Part("tinh") provinceRegister: RequestBody,
        @Part("sdt") numberPhoneRegister: RequestBody,
        @Part("email") emailRegister: RequestBody,
        @Part("password") passwordRegister: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<ResponseBody>
    @Multipart
    @POST("v2/member/profile")
    fun fetchregisterDrugstoreUpdate(
        @Part("ten") nameRegister: RequestBody,
        @Part("ten_nha_thuoc") nameDrugstoreRegister: RequestBody,
        @Part("dia_chi") addressRegister: RequestBody,
        @Part("tinh") provinceRegister: RequestBody,
        @Part("ma_so_thue") taxRegister: RequestBody,
        @Part("email") emailRegister: RequestBody,
        @Part("password") passwordRegister: RequestBody,
        @Part img: MultipartBody.Part?
    ): Call<ResponseBody>
}
