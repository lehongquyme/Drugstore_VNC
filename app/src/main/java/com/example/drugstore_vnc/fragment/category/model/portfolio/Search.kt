package com.example.drugstore_vnc.model.portfolio

data class Search(
    val code: Int,
    val message: List<Any>,
    val response: List<ResponseSearch>
)