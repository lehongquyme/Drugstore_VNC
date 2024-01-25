package com.example.drugstore_vnc.fragment.history.model.history

data class ResponseDetail(
    val current_page: Int,
    val data: List<DataType>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)