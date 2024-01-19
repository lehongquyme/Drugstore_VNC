package com.example.drugstore_vnc.pharmacyCounters.model

import com.google.gson.annotations.SerializedName

data class Drugstore(@SerializedName("id") var id: Int,
                     @SerializedName("ten_nha_thuoc") var nameDrugstore: String,
                     @SerializedName("sdt") var  numberPhone : String,
                     @SerializedName("dia_chi") var addressDrugstore: String)
data class DrugstoreResponse(
    @SerializedName("response") val response1: List<Drugstore>
)
