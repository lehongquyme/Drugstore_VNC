package com.example.drugstore_vnc.fragment.manager.model.contact

data class Contact(
    val code: Long,
    val message: List<Any?>,
    val response: List<ResponseContact>,
)

data class ResponseContact(
    val icon: String,
    val name: String,
    val value: String,
    val type: Long,
)
