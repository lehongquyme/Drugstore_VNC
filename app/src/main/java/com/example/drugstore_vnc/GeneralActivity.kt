package com.example.drugstore_vnc

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.drugstore_vnc.databinding.ActivityGeneralBinding
import com.example.drugstore_vnc.util.CheckToPay.bindingGeneral
import io.github.muddz.styleabletoast.StyleableToast


class GeneralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralBinding
    private var navController: NavController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindingGeneral(binding)
        navController = findNavController(this@GeneralActivity, R.id.flFragment)

        setupWithNavController(binding.bottomNavigationView, navController!!)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId != navController?.currentDestination?.id) {

                when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController?.navigate(R.id.homeFragment)
                }

                R.id.categoryFragment -> {
                    navController?.navigate(R.id.categoryFragment)
                }

                R.id.historyFragment -> {
                    navController?.navigate(R.id.historyFragment)
                }

                R.id.manageFragment -> {
                    navController?.navigate(R.id.manageFragment)
                }
            }}
            true
        }
    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
            StyleableToast.makeText(
            this@GeneralActivity,
            getString(R.string.pressBack),
            R.style.failImage
        ).show()
        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000) // Set the time window for the double click (in milliseconds)
    }
}
