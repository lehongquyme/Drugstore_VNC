package com.example.drugstore_vnc.pharmacyCounters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.client.CallAPI
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.ActivitySignUpPharmacyMainBinding
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.funGeneralCheck
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import com.google.android.material.snackbar.Snackbar
import io.github.muddz.styleabletoast.StyleableToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class SignUpPharmacyActivity : AppCompatActivity() {
    private lateinit var provinceViewModel: ViewModelSpinnerAPI
    private lateinit var binding: ActivitySignUpPharmacyMainBinding
    private var provincePost: Int = 1
    private val apiService = ClientAPI.getClient().create(SignUpDrugstoreAPI::class.java)
    private var idprovince = listOf<Int>()
    private lateinit var uriPath: String
    var checkImage = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        var check = false
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPharmacyMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val underline = binding.textViewTermSignUp
        val spannableString = SpannableString(underline.text)
        val underlineSpan = UnderlineSpan()


        binding.passwordEditText.setOnTouchListener { _, event ->
            val drawableRight = 2 // Chỉ số 2 ứng với drawableEnd
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.passwordEditText.right - binding.passwordEditText.compoundDrawables[drawableRight].bounds.width()) {
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

        provinceViewModel =
            ViewModelProvider(this@SignUpPharmacyActivity)[ViewModelSpinnerAPI::class.java]
        // Observe changes in the provinces LiveData
        provinceViewModel.provinces.observe(this) { provinces ->
            val province = provinces.map { it.nameDrugstore }
            idprovince = provinces.map { it.id }

            binding.spinerProvince.setItems(province)

        }
        provinceViewModel.fetchProvinces()
        binding.spinerProvince.setOnItemSelectedListener { view, position, _, item ->
            Snackbar.make(
                view, "Clicked $item", Snackbar.LENGTH_LONG
            ).show()
            provincePost = idprovince[position]
        }
        spannableString.setSpan(
            underlineSpan, 0, underline.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        underline.text = spannableString
        binding.iconImage.setOnClickListener {
            AddImageSignUpGeneral.openImageDialog(this,this, null)
            checkImage = true
        }
        binding.btnLogin.setOnClickListener {
            if (!checkImage) {
                StyleableToast.makeText(
                    this@SignUpPharmacyActivity,
                    getString(R.string.failAddImage),
                    R.style.failImage
                ).show()
            } else {
                val checkPhone = funGeneralCheck().checkNullDataPhone(this@SignUpPharmacyActivity,
                    binding.phoneNumberSignUpEditText, binding.phoneNumberSignUpTextInputLayout
                )
                val checkPass = funGeneralCheck().checkNullDataPassword(this@SignUpPharmacyActivity,
                    binding.passwordEditText,
                    binding.layoutInputPass
                )
                val checkName = funGeneralCheck().checkNullData(this@SignUpPharmacyActivity,
                    binding.nameSignUpEditText,
                    binding.nameSignUpTextInputLayout
                )
                val checkNamePharmacy = funGeneralCheck().checkNullData(this@SignUpPharmacyActivity,
                    binding.namePharmaSignUpEditText, binding.namePharmaSignUpTextInputLayout
                )
                val checkAddress = funGeneralCheck().checkNullData(this@SignUpPharmacyActivity,
                    binding.addressSignUpEditText, binding.addressSignUpTextInputLayout
                )
                val checkEmail = funGeneralCheck().checkEmail(this@SignUpPharmacyActivity,
                    binding.emailSignUpEditText, binding.emailSignUpTextInputLayout
                )
                if (checkPhone && checkPass && checkName && checkNamePharmacy && checkAddress && checkEmail) {
                    signUpRegisterPharmacy(
                        binding.nameSignUpEditText.text.toString(),
                        binding.namePharmaSignUpEditText.text.toString(),
                        binding.addressSignUpEditText.text.toString(),
                        provincePost,
                        binding.phoneNumberSignUpEditText.text.toString(),
                        binding.emailSignUpEditText.text.toString(),
                        binding.passwordEditText.text.toString(),
                        Uri.parse(uriPath)
                    )
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun signUpRegisterPharmacy(
        nameRegister: String,
        nameDrugstoreRegister: String,
        addressRegister: String,
        provinceRegister: Int,
        numberPhoneRegister: String,
        emailRegister: String,
        passwordRegister: String,
        fileUri: Uri?
    ) {
        val nameRequestBody = nameRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val nameDrugstoreRequestBody =
            nameDrugstoreRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressRequestBody = addressRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val provinceRequestBody =
            provinceRegister.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val numberPhoneRequestBody =
            numberPhoneRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailRequestBody = emailRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordRequestBody =
            passwordRegister.toRequestBody("text/plain".toMediaTypeOrNull())

        val imageRequestBody =
            AddImageSignUpGeneral.getRequestBodyFromFile(fileUri ?: Uri.parse(""))

        if (imageRequestBody != null) {
            val imagePart =
                MultipartBody.Part.createFormData("img", "image_file", imageRequestBody)

            val call = apiService.fetchregisterDrugstoreUser(
                nameRequestBody,
                nameDrugstoreRequestBody,
                addressRequestBody,
                provinceRequestBody,
                numberPhoneRequestBody,
                emailRequestBody,
                passwordRequestBody,
                imagePart
            )

            CallAPI().callRetrofitSignUp(
                call, this, Intent(this, PharmacyCoutersActivity::class.java)
            )
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            AddImageSignUpGeneral.handleImageSelectionResult(
                requestCode, resultCode, data, binding.noImage
            )

            // Check if data is not null before accessing data.data
            data?.data?.let { uri ->
                uriPath = uri.toString()
            } ?: run {
                // If data.data is null, check if imageUri is not null
                AddImageSignUpGeneral.imageUri?.let { uri ->
                    uriPath = uri.toString()
                }
            }
        }
    }

}