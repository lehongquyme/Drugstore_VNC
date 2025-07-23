package com.example.drugstore_vnc.pharmacyCounters.model

import com.google.gson.annotations.SerializedName

data class Province(@SerializedName("ten") val nameDrugstore: String,
    @SerializedName("id") val id: Int
)
data class ProvinceResponse(
    @SerializedName("response") val response: List<Province>
)
