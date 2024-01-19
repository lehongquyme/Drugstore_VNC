package com.example.drugstore_vnc.model.portfolio

data class SelectCategory(
    val code: Int,
    val message: List<Any>,
    val response: ResponseSelectCategory
)