package com.example.drugstore_vnc.util

import com.example.drugstore_vnc.databinding.ActivityGeneralBinding
import com.example.drugstore_vnc.model.home.totalprice.TotalPrice

object CheckToPay {
    var check:Boolean=false
    var binding:ActivityGeneralBinding?=null
    var listPay = mutableListOf<TotalPrice>()

    fun checkAddPay(check: Int, cart: TotalPrice) {
        if (check == 1) {
            if (!listPay.any { it.oder == cart.oder }) {
                listPay.add(cart)
            }

        } else if (check == 0) {
            listPay.removeIf { it.oder == cart.oder }
        }
    }
    fun bindingGeneral(binding1: ActivityGeneralBinding){
        binding= binding1

    }


    fun checkAccount(checkAcc:Boolean): Boolean {
        check= checkAcc
        return check
    }
}