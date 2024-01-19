package com.example.drugstore_vnc.util

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import com.example.drugstore_vnc.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class funGeneralCheck() {

     fun checkNullDataPhone(context: Context,edtData: TextInputEditText,layoutData: TextInputLayout):Boolean{

         return if ((edtData.text?.length ?: 0) < 10) {
             layoutData.error = context.getString(R.string.phone10)
             false
         } else {
             layoutData.error = null
             true
         }

        }
        fun checkNullDataPassword(context: Context,edtData: TextInputEditText,layoutData: TextInputLayout):Boolean{

            return if ((edtData.text?.length ?: 0) < 8) {
                layoutData.error = context.getString(R.string.pass8)
                false
            } else {
                layoutData.error = null
                true
            }
                }

    fun checkNullData(context: Context,edtData: TextInputEditText,layoutData: TextInputLayout):Boolean{

                return if ((edtData.text?.length ?: 0) == 0) {
                    layoutData.error =context.getString(R.string.please)+" ${edtData.hint}"
                    false
                } else {
                    layoutData.error = null
                    true
                }

    }
    fun checkEmail(context: Context,edtData: TextInputEditText,layoutData: TextInputLayout):Boolean{

        return if ((edtData.text?.length ?: 0) == 0) {
            layoutData.error = context.getString(R.string.please)+" "+context.getString(R.string._email)
            false
        } else  if (!isValidEmail(edtData.text)){
            layoutData.error = context.getString(R.string.pleaseEmail)
            false
        } else {
            layoutData.error = null
            true
        }


    }
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

