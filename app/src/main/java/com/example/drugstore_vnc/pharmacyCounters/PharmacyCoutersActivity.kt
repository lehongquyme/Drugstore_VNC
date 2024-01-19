package com.example.drugstore_vnc.pharmacyCounters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.drugstore_vnc.ForgotActivity
import com.example.drugstore_vnc.GeneralActivity
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.ActivityPharmacyCoutersBinding
import com.example.drugstore_vnc.local.SharedPreferencesToken
import com.example.drugstore_vnc.privateCustomers.model.ResponseInforCustomer
import com.example.drugstore_vnc.util.funGeneralCheck
import io.github.muddz.styleabletoast.StyleableToast

//check data biding
class PharmacyCoutersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPharmacyCoutersBinding
    private val apiService = ClientAPI.getClient().create(SignInDrugstoreAPI::class.java)
    private var check: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacyCoutersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.passwordEditText.setOnTouchListener { _, event ->
            val drawableRight = 2 // Chỉ số 2 ứng với drawableEnd
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.passwordEditText.right - binding.passwordEditText.compoundDrawables[drawableRight].bounds.width()) {
                    // Xác định xem người dùng chạm vào phần drawableEnd hay không
                    check = !check
                    if (check) {
                        binding.passwordEditText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.eyes_open, 0
                        )
                    } else {
                        binding.passwordEditText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.eyes_close, 0
                        )
                    }
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        binding.txtForgot.setOnClickListener {
            val intent = Intent(this@PharmacyCoutersActivity, ForgotActivity::class.java)
            val myBooleanValue = true
            intent.putExtra("Forgot", myBooleanValue)
            startActivity(intent)        }
        binding.txtSignUpPharmacy.setOnClickListener {
            startActivity(Intent(this, SignUpPharmacyActivity::class.java))
        }



        binding.btnLogin.setOnClickListener {
            signInPharmacy(
                binding.phoneNumberEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )

        }

    }


    private fun signInPharmacy(
        username: String,
        password: String,
    ) {
        val checkPhone = funGeneralCheck().checkNullDataPhone(this@PharmacyCoutersActivity,
            binding.phoneNumberEditText, binding.layoutInputPhone
        )
        val checkPass = funGeneralCheck().checkNullDataPassword(this@PharmacyCoutersActivity,
            binding.passwordEditText, binding.layoutInputPass
        )
        if (checkPhone && checkPass) {
            val call = apiService.fetchloginDrugstoreUser(
                username,
                password,
            )
            CallAPI().callRetrofitSignIn(call,

                object : CallAPI.AuthCallback {
                    override fun onTokenReceived(token: ResponseInforCustomer, pharmacy: String?) {
                        SharedPreferencesToken(this@PharmacyCoutersActivity).saveToken(token,pharmacy)
                    }

                    override fun onSignInSuccess(checkSignIn: Int) {
                        if (checkSignIn == 1) {
                            StyleableToast.makeText(this@PharmacyCoutersActivity,
                                getString(R.string.success),
                                R.style.mytoast).show()
                            startActivity(
                                Intent(
                                    this@PharmacyCoutersActivity,
                                    GeneralActivity::class.java
                                )
                            )
                            this@PharmacyCoutersActivity.finish()
                        }

                    }

                    override fun onSignInFail(check:String) {
                        binding.layoutInputPhone.error = null
                        binding.layoutInputPass.error = null
                        if (check == "Tài khoản không tồn tại") {

                            binding.layoutInputPhone.error =getString(R.string.numberFail)
                        }
                        if (check == "Sai mật khẩu") {
                            binding.layoutInputPass.error =getString(R.string.failPass)
                        }

                    }

                })

        }}

}