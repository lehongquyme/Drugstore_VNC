package com.example.drugstore_vnc.fragment.manager.model.infor

data class ResponseProfile(
    val coins: Int,
    val description: String,
    val dia_chi: String,
    val email: String,
    val img: String,
    val ma_so_thue: Any,
    val provinces: List<Province>,
    val sdt: String,
    val ten: String,
    val ten_nha_thuoc: String,
    val thu_hang: String,
    val thu_hang_icon: String,
    val tinh: Int,
    val trang_thai: Int
)