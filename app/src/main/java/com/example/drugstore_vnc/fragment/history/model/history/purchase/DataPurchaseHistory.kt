package com.example.drugstore_vnc.model.history.purchase

data class DataPurchaseHistory(
    val ck_truoc: Int,
    val coin_value: Int,
    val coins: Int,
    val created_at: Any,
    val dia_chi: String,
    val ghi_chu: Any,
    val id: Int,
    val ma_don_hang: String,
    val ti_le_giam: Any,
    val tong_tien: Int,
    val trang_thai: Int,
    val voucher: String,
    val voucher_value: Int
)