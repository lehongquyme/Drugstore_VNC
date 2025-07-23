package com.example.drugstore_vnc.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.fragment.manager.model.contact.ResponseContact
import com.example.drugstore_vnc.fragment.manager.model.infor.ResponseProfile
import com.example.drugstore_vnc.fragment.manager.model.news.ResponseNews
import com.example.drugstore_vnc.getAPI.SpinnerProvinceAPI
import com.example.drugstore_vnc.model.pay.ResponsePay
import com.example.drugstore_vnc.model.portfolio.ResponseCategory
import com.example.drugstore_vnc.pharmacyCounters.model.Drugstore
import com.example.drugstore_vnc.pharmacyCounters.model.Province
import kotlinx.coroutines.launch

class ViewModelSpinnerAPI() : ViewModel() {

    private val _provinces = MutableLiveData<List<Province>>()
    private val _drugstore = MutableLiveData<List<Drugstore>>()
    private val _profile= MutableLiveData<ResponseProfile>()
    private val _pay= MutableLiveData<ResponsePay>()
    private val _category= MutableLiveData<List<ResponseCategory>>()
    private val _contact= MutableLiveData<List<ResponseContact>>()
    private val _news= MutableLiveData<ResponseNews>()
    val provinces: LiveData<List<Province>> = _provinces
    val drugstore: LiveData<List<Drugstore>> = _drugstore
    val  profile:LiveData<ResponseProfile> =_profile
    val  news:LiveData<ResponseNews> =_news
    val  pay:LiveData<ResponsePay> =_pay
    val  category:LiveData<List<ResponseCategory>> =_category
    val  contact:LiveData<List<ResponseContact>> =_contact
    private val api = ClientAPI.getClient().create(SpinnerProvinceAPI::class.java)

    fun fetchProvinces() {

        viewModelScope.launch {
            try {
                val response = api.fetchDataProvince()
                _provinces.value = response.response

            } catch (e: Exception) {
                // Handle errors
                Log.e("ProvinceViewModel", "Error fetching provinces: ${e.message}")
            }
        }
    }
    fun fetchProfile(){
        viewModelScope.launch {
            try {
                val response = api.fetchTakeProfile()
                _profile.value = response.response

            } catch (e: Exception) {
                // Handle errors
                Log.e("ProvinceViewModel", "Error fetching provinces: ${e.message}")
            }
        }
    }
    fun fetchDrugstores(provinceId: Int) {
        viewModelScope.launch {
            try {
                val response = api.fetchDataDrugstore(provinceId)
                _drugstore.value = response.response1
            } catch (e: Exception) {
                // Handle errors
                Log.e("ApiService", "Error fetching drugstores: ${e.message}")
            }
        }
    }
    fun fetchDataPay(idCart: List<Int>) {
        viewModelScope.launch {
            try {
                val response = api.fetchTakeVoucher(idCart)
                _pay.value = response
            } catch (e: Exception) {
                // Handle errors
                Log.e("ApiService", "Error fetching drugstores: ${e.message}")
            }
        }
    }
    fun fetchCategory() {

        viewModelScope.launch {
            try {
                val response = api.fetchTakeCategory()
                _category.value = response.response

            } catch (e: Exception) {
                // Handle errors
                Log.e("ProvinceViewModel", "Error fetching provinces: ${e.message}")
            }
        }
    }
    fun fetchManagerShop() {

        viewModelScope.launch {
            try {
                val response = api.fetchTakeManagerShop()
                _category.value = response.response

            } catch (e: Exception) {
                // Handle errors
                Log.e("ProvinceViewModel", "Error fetching provinces: ${e.message}")
            }
        }
    }
    fun fetchContact() {

        viewModelScope.launch {
            try {
                val response = api.fetchTakeContact()
                _contact.value = response.response

            } catch (e: Exception) {
                // Handle errors
                Log.e("ContactViewModel", "Error fetching contact: ${e.message}")
            }
        }
    }
    fun fetchNews() {

        viewModelScope.launch {
            try {
                val response = api.fetchTakeNews()
                _news.value = response.response

            } catch (e: Exception) {
                // Handle errors
                Log.e("ContactViewModel", "Error fetching contact: ${e.message}")
            }
        }
    }
}