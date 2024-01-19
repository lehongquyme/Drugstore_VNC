package com.example.drugstore_vnc.model.portfolio

data class ProductPortfolio(
    val code: Int,
    val message: List<Any>,
    val response: List<ResponseCategory>
)