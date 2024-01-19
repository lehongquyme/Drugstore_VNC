package com.example.drugstore_vnc.model.pay

data class ResponseX(
    val coin_available: Int,
    val coin_description: String,
    val money: Int,
    val voucher_available: Int,
    val voucher_description: String
)