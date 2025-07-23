package com.example.drugstore_vnc.viewModelFatory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelProductAPI::class.java)) {
            return ViewModelProductAPI(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
