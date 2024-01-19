package com.example.drugstore_vnc.privateCustomers

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
import com.example.drugstore_vnc.databinding.ActivitySignupPrivateCustomersBinding
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.funGeneralCheck
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import com.google.android.material.snackbar.Snackbar
import io.github.muddz.styleabletoast.StyleableToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class SignupPrivateCustomersActivity : AppCompatActivity() {
    private lateinit var provinceViewModel: ViewModelSpinnerAPI
    private lateinit var binding: ActivitySignupPrivateCustomersBinding
    private var provincePost: Int = 1
    private var drugstorePost: Int = 1
    private val apiService = ClientAPI.getClient().create(SignUpCustomersAPI::class.java)
    private var uriPart: String? = ""
    private var idprovince = listOf<Int>()
    private var idDrugstore = listOf<Int>()
    var checkImage = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var check = false
        binding = ActivitySignupPrivateCustomersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val underline = binding.textViewTermSignUp
        val spannableString = SpannableString(underline.text)
        val underlineSpan = UnderlineSpan()

        spannableString.setSpan(
            underlineSpan, 0, underline.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        underline.text = spannableString
        binding.iconImage.setOnClickListener {
            checkImage = true
            AddImageSignUpGeneral.openImageDialog(this,this, null)
        }
        provinceViewModel =
            ViewModelProvider(this@SignupPrivateCustomersActivity)[ViewModelSpinnerAPI::class.java]
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
        provinceViewModel.fetchDrugstores(provincePost)

        provinceViewModel.drugstore.observe(this) { provinces ->
            val drugstore = provinces.map { it.nameDrugstore }
            idDrugstore = provinces.map { it.id }
            binding.spinerDrugstore.setItems(drugstore)

        }
        binding.spinerDrugstore.setOnItemSelectedListener { view, position, id, item ->
            Snackbar.make(
                view, "Clicked $item $position $id", Snackbar.LENGTH_LONG
            ).show()
            drugstorePost = idDrugstore[position]
        }
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
        binding.btnLogin.setOnClickListener {
            if (!checkImage) {
                StyleableToast.makeText(
                    this@SignupPrivateCustomersActivity,
                    getString(R.string.failAddImage),
                    R.style.failImage
                ).show()
            } else {
                val checkPhone = funGeneralCheck().checkNullDataPhone(this@SignupPrivateCustomersActivity,
                    binding.phoneNumberSignUpEditText, binding.phoneNumberSignUpTextInputLayout
                )
                val checkPass = funGeneralCheck().checkNullDataPassword(this@SignupPrivateCustomersActivity,
                    binding.passwordEditText,
                    binding.layoutInputPass
                )
                val checkName = funGeneralCheck().checkNullData(this@SignupPrivateCustomersActivity,
                    binding.nameSignUpEditText,
                    binding.nameSignUpTextInputLayout
                )
                val checkAddress = funGeneralCheck().checkNullData(this@SignupPrivateCustomersActivity,
                    binding.addressSignUpNumberEditText, binding.addressSignUpNumberTextInputLayout
                )
                val checkEmail = funGeneralCheck().checkEmail(this@SignupPrivateCustomersActivity,
                    binding.emailSignUpNumberEditText, binding.emailSignUpNumberTextInputLayout
                )
                if (checkPhone && checkPass && checkAddress && checkName && checkEmail) {
                    signUpRegisterCustomers(
                        binding.nameSignUpEditText.text.toString(),
                        drugstorePost,
                        binding.addressSignUpNumberEditText.text.toString(),
                        provincePost,
                        binding.phoneNumberSignUpEditText.text.toString(),
                        binding.emailSignUpNumberEditText.text.toString(),
                        binding.passwordEditText.text.toString(),
                        uriPart
                    )
                }
            }
        }


    }

    private fun signUpRegisterCustomers(
        nameRegister: String,
        idDrugstore: Int,
        addressRegister: String,
        provinceRegister: Int,
        numberPhoneRegister: String,
        emailRegister: String,
        passwordRegister: String,
        fileUri: String?
    ) {
        val nameRequestBody = nameRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val idDrugstoreRequestBody =
            idDrugstore.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val addressRequestBody = addressRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val provinceRequestBody =
            provinceRegister.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val numberPhoneRequestBody =
            numberPhoneRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailRequestBody = emailRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordRequestBody = passwordRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val call: Any

        if (fileUri == null || fileUri == "") {
            call = apiService.fetchregisterCustomersUser(
                nameRequestBody,
                idDrugstoreRequestBody,
                addressRequestBody,
                provinceRequestBody,
                numberPhoneRequestBody,
                emailRequestBody,
                passwordRequestBody,

                )
        } else {
            val imageRequestBody =
                AddImageSignUpGeneral.getRequestBodyFromFile(Uri.parse(fileUri))
            val imagePart = MultipartBody.Part.createFormData(
                "img", "image_file",
                imageRequestBody!!
            )

            call = apiService.fetchregisterCustomersUser(
                nameRequestBody,
                idDrugstoreRequestBody,
                addressRequestBody,
                provinceRequestBody,
                numberPhoneRequestBody,
                emailRequestBody,
                passwordRequestBody,
                imagePart
            )


        }
        CallAPI().callRetrofitSignUp(
            call,
            this@SignupPrivateCustomersActivity,
            Intent(this@SignupPrivateCustomersActivity, PrivateCustomersActivity::class.java)
        )


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
                uriPart = uri.toString()
            } ?: run {
                // If data.data is null, check if imageUri is not null
                AddImageSignUpGeneral.imageUri?.let { uri ->
                    uriPart = uri.toString()
                }
            }
        }

    }

}