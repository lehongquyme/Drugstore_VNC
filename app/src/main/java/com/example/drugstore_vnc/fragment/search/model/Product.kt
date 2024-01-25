package com.example.drugstore_vnc.fragment.search.model

import com.example.drugstore_vnc.model.portfolio.item.DataCategory
import com.google.gson.annotations.SerializedName


data class ListProduct(
    @SerializedName("data") val dataProduct : List<DataCategory>

)
data class  ReturnDataProduct(
    @SerializedName("response") var response: ListProduct
)
