package com.example.drugstore_vnc.fragment.manager.model.news

import com.google.gson.annotations.SerializedName

data class News(
    val code: Long,
    val message: List<Any?>,
    val response: ResponseNews,
)

data class ResponseNews(
    @SerializedName("current_page")
    val currentPage: Long,
    val data: List<DataNews>,
    @SerializedName("first_page_url")
    val firstPageUrl: String,
    val from: Long,
    @SerializedName("last_page")
    val lastPage: Long,
    @SerializedName("last_page_url")
    val lastPageUrl: String,
    @SerializedName("next_page_url")
    val nextPageUrl: Any?,
    val path: String,
    @SerializedName("per_page")
    val perPage: Long,
    @SerializedName("prev_page_url")
    val prevPageUrl: Any?,
    val to: Long,
    val total: Long,
)

data class DataNews(
    val id: Long,
    @SerializedName("tieu_de")
    val tieuDe: String,
    @SerializedName("mo_ta")
    val moTa: String,
    val img: String,
    @SerializedName("ngay_cong_khai")
    val ngayCongKhai: String,
    val url: String,
)
