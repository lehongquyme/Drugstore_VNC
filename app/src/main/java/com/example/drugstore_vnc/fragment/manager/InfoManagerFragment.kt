@file:Suppress("DEPRECATION")

package com.example.drugstore_vnc.fragment.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drugstore_vnc.MainActivity
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.client.ClientAPI
import com.example.drugstore_vnc.databinding.FragmentInfoManagerBinding
import com.example.drugstore_vnc.fragment.manager.model.logout.Logout
import com.example.drugstore_vnc.local.SharedPreferencesToken
import com.example.drugstore_vnc.pharmacyCounters.SignUpDrugstoreAPI
import com.example.drugstore_vnc.postAPI.TakeProductInCart
import com.example.drugstore_vnc.util.AddImageSignUpGeneral
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InfoManagerFragment : Fragment() {
    private lateinit var  apiServiceCart: TakeProductInCart
    private lateinit var binding: FragmentInfoManagerBinding
    private lateinit var provinceViewModel: ViewModelSpinnerAPI
    private var idprovince = listOf<Int>()
    private var provincePost: Int = 1
    private val apiService = ClientAPI.getClient().create(SignUpDrugstoreAPI::class.java)
    private var uriPath = ""
    var check = false
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoManagerBinding.inflate(inflater, container, false)
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        binding.backManagerFragment.setOnClickListener {
            requireFragmentManager().popBackStack()
        }
        binding.logOutManager.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage(getString(R.string.doyouwantlogoutthisaccount))
                .setNegativeButton(getString(R.string.cancle)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    Logout()
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()

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
        val imgRank = SharedPreferencesToken(requireContext()).getImageRank()

        Picasso.get().load(imgRank).into(binding.imageRank)
        val retrofit = ClientAPI.getClientProduct(requireContext())
        apiServiceCart = retrofit.create(TakeProductInCart::class.java)
        provinceViewModel =
            ViewModelProvider(this@InfoManagerFragment)[ViewModelSpinnerAPI::class.java]
        provinceViewModel.profile.observe(viewLifecycleOwner) { profile ->
            val province = profile.provinces.map { it.ten }
            idprovince = profile.provinces.map { it.id }
            binding.nameRank.text= profile.thu_hang
            binding.coinRank.text= "${profile.coins} "+ getString(R.string.coins)
            binding.spinerProvince.setItems(province)
            binding.spinerProvince.selectedIndex = profile.tinh - 1
            Picasso.get().load(profile.img).into(binding.noImage)
            binding.nameInforEditText.setText(profile.ten)
            binding.phoneNumberInforEditText.setText(profile.sdt)
            binding.emailInforNumberEditText.setText(profile.email)
            binding.namePharmaInforEditText.setText(profile.ten_nha_thuoc)
            binding.addressInforNumberEditText.setText(profile.dia_chi)
            binding.taxEditText.setText(profile.ma_so_thue.toString())

        }
        binding.btnLogin.setOnClickListener {

            signUpRegisterPharmacy(
                binding.nameInforEditText.text.toString(),
                binding.namePharmaInforEditText.text.toString(),
                binding.addressInforNumberEditText.text.toString(),
                provincePost,
                binding.taxEditText.text.toString(),
                binding.emailInforNumberEditText.text.toString(),
                binding.passwordEditText.text.toString(),
                Uri.parse(uriPath)
            )
        }
        binding.iconImage.setOnClickListener {
            AddImageSignUpGeneral.openImageDialog(requireContext(),requireActivity(), this@InfoManagerFragment)
        }
        provinceViewModel.fetchProfile()
        binding.spinerProvince.setOnItemSelectedListener { view, position, _, item ->
            Snackbar.make(
                view, "$item", Snackbar.LENGTH_LONG
            ).show()
            provincePost = idprovince[position]
        }
        return binding.root
    }
    private  fun Logout(){
        apiServiceCart.fetchPostLogout().enqueue(object : Callback<Logout> {
            override fun onResponse(call: Call<Logout>, response: Response<Logout>) {
                if (response.isSuccessful) {
                     if ( response.body()?.code==0){
                    SharedPreferencesToken(requireContext()).deleteToken()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }else{
                         StyleableToast.makeText(requireContext(),
                             getString(R.string.logoutFail),
                             R.style.failImage).show()
                     }
                }
            }

            override fun onFailure(call: Call<Logout>, t: Throwable) {
            }
        })
    }
    @SuppressLint("SuspiciousIndentation")
    private fun signUpRegisterPharmacy(
        nameRegister: String,
        nameDrugstoreRegister: String,
        addressRegister: String,
        provinceRegister: Int,
        taxRegister: String,
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
        val taxRequestBody =
            taxRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailRequestBody = emailRegister.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordRequestBody = passwordRegister.toRequestBody("text/plain".toMediaTypeOrNull())

        val imageRequestBody =
            AddImageSignUpGeneral.getRequestBodyFromFile(
                fileUri ?: Uri.parse("")
            )

        val imagePart =
            imageRequestBody?.let { MultipartBody.Part.createFormData("img", "image_file", it) }
        val call = apiService.fetchregisterDrugstoreUpdate(
            nameRequestBody,
            nameDrugstoreRequestBody,
            addressRequestBody,
            provinceRequestBody,
            taxRequestBody,
            emailRequestBody,
            passwordRequestBody,
            imagePart
        )
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        fragmentManager?.popBackStack()
                        StyleableToast.makeText(requireContext(),
                            getString(R.string.updateInfor),
                            R.style.creatProduct).show()
                    }
                    else{
                        StyleableToast.makeText(requireContext(),
                            getString(R.string.addFail),
                            R.style.failImage).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                }
            })


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
