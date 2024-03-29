package com.example.drugstore_vnc.model.portfolio.item

import com.example.drugstore_vnc.model.home.Tagse
import com.google.gson.annotations.SerializedName

data class DataCategory(
    @SerializedName("bonus_coins") var bonus_coins: Int,
    val detail_url: String,
    val discount_price: Int,
    val don_gia: Int,
    val ban_chay: Int?,
    val trang_thai: Int?,
    val gia_uu_dai: Any,
    val hoatchat_san_phams: List<HoatchatSanPham>,
    val id: Int,
    val img_san_phams: List<ImgSanPham>,
    val img_url: String,
    val khuyen_mai: Int,
    val product_tags: List<Any>,
    val quy_cach_dong_goi: String,
    val so_luong: Int,
    val so_luong_toi_da: Int,
    val so_luong_toi_thieu: Int,
    val tags: List<Tagse>?,
    val ten_san_pham: String
)