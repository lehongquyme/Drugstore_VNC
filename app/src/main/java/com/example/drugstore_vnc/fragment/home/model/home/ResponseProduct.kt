package com.example.drugstore_vnc.model.home

data class ResponseProduct(
    var products: Products,
    var ti_le_giam: String,
    var total_number: Int,
    var total_price: Int
)