package com.example.drugstore_vnc.model.home

import com.google.gson.annotations.SerializedName

data class ProductToCart(
    @SerializedName("response") var response: ResponseProduct,
    @SerializedName("message") var message: List<String>?
)
