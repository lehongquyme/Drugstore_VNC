package com.example.drugstore_vnc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.ActivityForgotBinding
import com.example.drugstore_vnc.local.SharedPreferencesToken
import com.example.drugstore_vnc.pharmacyCounters.SignInDrugstoreAPI
import com.example.drugstore_vnc.privateCustomers.SignInCustomersAPI
import com.example.drugstore_vnc.privateCustomers.model.ResponseInforCustomer
import com.example.drugstore_vnc.util.funGeneralCheck
import io.github.muddz.styleabletoast.StyleableToast

class ForgotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotBinding
    private var forGot = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var receivedIntent = intent

        forGot = receivedIntent.getBooleanExtra("Forgot", false)
        binding.btnLogin.setOnClickListener {
            signInPharmacy(binding.phoneNumberEditText.text.toString())
        }
    }

    private fun signInPharmacy(
        username: String,
    ) {
        val checkPhone = funGeneralCheck().checkNullDataPhone(this@ForgotActivity,

            binding.phoneNumberEditText,
            binding.layoutInputPhone
        )

        if (checkPhone) {
            if (!forGot) {
                val apiService = ClientAPI.getClient().create(SignInCustomersAPI::class.java)

                val call = apiService.fetchloginCustomersUser(
                    username,
                    "12345678",
                )


                CallAPI().callRetrofitSignIn(
                    call,
                    object : CallAPI.AuthCallback {
                        override fun onTokenReceived(
                            token: ResponseInforCustomer,
                            pharmacy: String?
                        ) {
                            SharedPreferencesToken(this@ForgotActivity).saveToken(token, pharmacy)
                        }

                        override fun onSignInSuccess(checkSignIn: Int) {


                        }

                        override fun onSignInFail(check: String) {
                            if (check == "Tài khoản không tồn tại") {

                                binding.layoutInputPhone.error = null
                                StyleableToast.makeText(
                                    this@ForgotActivity,
                                    getString(R.string.numberFail),
                                    R.style.failImage
                                ).show()
                            }
                        }
                    }
                )
            }
            else {
                val apiService = ClientAPI.getClient().create(SignInDrugstoreAPI::class.java)

                val call = apiService.fetchloginDrugstoreUser(
                    username,
                    "12345678",
                )
                CallAPI().callRetrofitSignIn(call,

                    object : CallAPI.AuthCallback {
                        override fun onTokenReceived(
                            token: ResponseInforCustomer,
                            pharmacy: String?
                        ) {
                            SharedPreferencesToken(this@ForgotActivity).saveToken(token, pharmacy)
                        }

                        override fun onSignInSuccess(checkSignIn: Int) {

                        }

                        override fun onSignInFail(check: String) {
                            if (check == "Tài khoản không tồn tại") {

                                binding.layoutInputPhone.error = null
                                StyleableToast.makeText(
                                    this@ForgotActivity,
                                    getString(R.string.numberFail),
                                    R.style.failImage
                                ).show()

                            }
                        }

                    })


            }


        }
    }
}