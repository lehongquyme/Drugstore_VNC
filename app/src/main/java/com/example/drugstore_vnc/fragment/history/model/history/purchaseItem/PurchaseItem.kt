package com.example.drugstore_vnc.model.history.purchaseItem

data class PurchaseItem(
    val code: Int,
    val message: List<Any>,
    val response: ResponsePurchaseItem
)