package com.example.drugstore_vnc.pharmacyCounters.model

import com.google.gson.annotations.SerializedName

data class DataSignInDrugstore(
    val description: String,
    val dia_chi: String,
    val email: String,
    val id: Int,
    val sdt: String,
    val  ma_so_thue:String,
    val ten: String,
    val ten_nha_thuoc: String,
    val token: String,
    val trang_thai: Int
)
data class  ReturnData(
    @SerializedName("response") var response: DataSignInDrugstore,
    @SerializedName("message") var message: List<String>?
)
