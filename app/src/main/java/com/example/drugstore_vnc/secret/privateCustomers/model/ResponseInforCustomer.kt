package com.example.drugstore_vnc.privateCustomers.model

data class ResponseInforCustomer(
    val agency: Int,
    val description: String,
    val dia_chi: String,
    val email: String,
    val id: Int,
    val id_agency: Int,
    val ma_so_thue: String,
    val sdt: String,
    val ten: String,
    val token: String,
    val ten_nha_thuoc: String,
    val trang_thai: Int
)