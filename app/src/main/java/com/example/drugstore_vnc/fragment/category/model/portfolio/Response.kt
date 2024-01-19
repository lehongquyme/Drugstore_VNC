package com.example.drugstore_vnc.model.portfolio

data class ResponseCategory(
    val category: List<Category>,
    val icon: String,
    val key: String,
    val name: String
)