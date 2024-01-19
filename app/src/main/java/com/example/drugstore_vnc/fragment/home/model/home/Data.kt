package com.example.drugstore_vnc.model.home

data class Data(
    val bonus_coins: Int,
    val detail_url: String,
    val discount_price: Int,
    val don_gia: Int,
    val gia_uu_dai: String?,
    val gio_hang_id: Int,
    val id: Int,
    val id_member: Int,
    val id_sp_km: String,
    val img_sp_km: String,
    val img_url: String,
    val khuyen_mai: Int,
    val ngay_het_han: Any,
    val quy_cach_dong_goi: String,
    val so_luong: Int,
    val so_luong_km: Int,
    val so_luong_toi_da: Int,
    val so_luong_toi_thieu: Int,
    val tags: List<String>,
    val ten_san_pham: String,
    val totalPrice: Int,
    var check: Boolean= false
    )