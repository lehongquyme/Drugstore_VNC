package com.example.drugstore_vnc.model.pay

data class ResponsePay(
    val message: List<Any>,
    val response: List<ResponseVoucher>
)
data class ResponsePayAdd(
    val message: List<Any>?,
    val response:ResponseX
)