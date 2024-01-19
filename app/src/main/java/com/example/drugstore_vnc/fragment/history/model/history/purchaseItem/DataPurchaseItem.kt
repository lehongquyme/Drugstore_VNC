package com.example.drugstore_vnc.model.history.purchaseItem

data class DataPurchaseItem(
    val bonus_coins: Int,
    val detail_url: String,
    val discount_price: Int,
    val don_gia: Int,
    val id: Int,
    val img_url: String,
    val khuyen_mai: Int,
    val so_luong: Int,
    val tags: List<tagHistoryItem>,
    val ten_san_pham: String
)