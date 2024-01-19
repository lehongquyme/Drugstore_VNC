package com.example.drugstore_vnc.model.history.purchaseItem

data class ResponsePurchaseItem(
    val coin: Any,
    val coin_bonus: Int,
    val coin_value: Int,
    val date_time: String,
    val ghi_chu: Any,
    val price: Int,
    val products: Products,
    val total_price: Int,
    val total_products: Int,
    val transferred: Any,
    val transferred_value: Any,
    val voucher: Any,
    val voucher_value: Int
)