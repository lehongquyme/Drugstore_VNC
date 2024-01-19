package com.example.drugstore_vnc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codebyashish.autoimageslider.Enums.ImageActionTypes
import com.codebyashish.autoimageslider.Enums.ImageScaleType
import com.codebyashish.autoimageslider.Interfaces.ItemsListener
import com.codebyashish.autoimageslider.Models.ImageSlidesModel
import com.example.drugstore_vnc.databinding.ActivityMainBinding
import com.example.drugstore_vnc.pharmacyCounters.PharmacyCoutersActivity
import com.example.drugstore_vnc.privateCustomers.PrivateCustomersActivity
import io.github.muddz.styleabletoast.StyleableToast


class MainActivity : AppCompatActivity(), ItemsListener {
    private lateinit var binding: ActivityMainBinding
    var listImageSlider: ArrayList<ImageSlidesModel> = ArrayList()
    private var listener: ItemsListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listener = this
        var autoImage = binding.autoImageSlider
        listImageSlider.add(ImageSlidesModel(R.drawable.onboarding_1));
        listImageSlider.add(ImageSlidesModel(R.drawable.onboarding_2));
        listImageSlider.add(ImageSlidesModel(R.drawable.onboarding_3));
        listImageSlider.add(ImageSlidesModel(R.drawable.onboarding_4));
        listImageSlider.add(ImageSlidesModel(R.drawable.onboarding_5));
        autoImage.setImageList(listImageSlider, ImageScaleType.FIT)
        autoImage.setDefaultAnimation()
        autoImage.onItemClickListener(listener)
        binding.constraintLayoutPrivate.setOnClickListener {
            startActivity(Intent(this, PrivateCustomersActivity()::class.java))
        }
        binding.constraintLayoutPharmacy.setOnClickListener {
            startActivity(Intent(this, PharmacyCoutersActivity()::class.java))

        }
    }

    override fun onItemChanged(position: Int) {

    }

    override fun onTouched(actionTypes: ImageActionTypes?, position: Int) {
    }

    override fun onItemClicked(position: Int) {
    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        StyleableToast.makeText(
            this@MainActivity,
            getString(R.string.pressBack),
            R.style.failImage
        ).show()
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000) // Set the time window for the double click (in milliseconds)
    }
}

