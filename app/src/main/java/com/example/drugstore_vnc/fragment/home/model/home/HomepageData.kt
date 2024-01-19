package com.example.drugstore_vnc.model.home

import com.example.drugstore_vnc.fragment.home.model.home.ImgProduct
import com.google.gson.annotations.SerializedName


data class ProductDemo(
    @SerializedName("bonus_coins") var bonus_coins: Int,
    @SerializedName("detail_url") var detail_url: String,
    @SerializedName("discount_price") var discount_price: Double,
    @SerializedName("don_gia") var don_gia: Int,
    @SerializedName("id") var id: Int,
    @SerializedName("img_url") var img_url: String,
    @SerializedName("khuyen_mai") var khuyen_mai: Int,
    @SerializedName("quy_cach_dong_goi") var quy_cach_dong_goi: String,
    @SerializedName("so_luong") var so_luong: Int,
    @SerializedName("so_luong_toi_da") var so_luong_toi_da: Int,
    @SerializedName("so_luong_toi_thieu") var so_luong_toi_thieu: Int,
    @SerializedName("tags") var tags: MutableList<Tagse>,
    val gia_uu_dai: String,
    val img_san_phams: List<ImgProduct>,
    val product_tags: List<Any>,
    @SerializedName("ten_san_pham") var ten_san_pham: String
)


data class Tagse(
    @SerializedName("key") var key: String,
    @SerializedName("name") var name: String,
    @SerializedName("value") var value: Int
)

data class HomepageData(
    @SerializedName("banners") var banners: List<Banner>,
    @SerializedName("products") var products: List<TypeProduct>,
    @SerializedName("member_name") var member_name: String,
    @SerializedName("member_status") var member_status: Int,
    @SerializedName("total_cart") var total_cart: Int,
    @SerializedName("thu_hang_icon") var thu_hang_icon: String

)

data class TypeProduct(
    @SerializedName("key") var key: String,
    @SerializedName("name") var name: String,
    @SerializedName("value") var value: String,
    @SerializedName("data") var data: List<ProductDemo>
)

data class Banner(@SerializedName("value") var value: String?)
