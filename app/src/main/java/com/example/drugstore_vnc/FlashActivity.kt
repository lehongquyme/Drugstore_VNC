package com.example.drugstore_vnc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.drugstore_vnc.local.SharedPreferencesToken
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class FlashActivity : AppCompatActivity() {
    private lateinit var changeZoom: CardView
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)
        val configuration = resources.configuration
        configuration.setLocale(Locale("vi")) // Đặt ngôn ngữ thành Tiếng Việt
        resources.updateConfiguration(configuration, resources.displayMetrics)
        changeZoom = findViewById(R.id.cardImageFlash)
        val anim = ValueAnimator.ofFloat(1f, 2f)
        val anim2 = ValueAnimator.ofFloat(0f, 360f)
        val anim3 = ValueAnimator.ofFloat(2f, 1f)
        anim.duration = 1000
        anim2.duration = 500
        anim3.duration = 1000
        anim.repeatCount = 0
        anim.repeatMode = ValueAnimator.REVERSE
        anim2.repeatCount = 0
        anim2.repeatMode = ValueAnimator.RESTART
        anim3.repeatCount = 0
        anim3.repeatMode = ValueAnimator.REVERSE
        anim.addUpdateListener { animation ->
            val scaleValue = animation.animatedValue as Float
            changeZoom.scaleX = scaleValue
            changeZoom.scaleY = scaleValue
        }
        anim3.addUpdateListener { animation ->
            val scaleValue = animation.animatedValue as Float
            changeZoom.scaleX = scaleValue
            changeZoom.scaleY = scaleValue
        }

        anim2.addUpdateListener { animation ->
            changeZoom.rotation = animation.animatedValue as Float
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(anim, anim2, anim3)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animatorSet.start()
            }
        })
        animatorSet.start()
        val task = object : TimerTask() {
            override fun run() {

                this@FlashActivity.runOnUiThread {
                    animatorSet.start()

                    val connectivityManager =
                        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkInfo = connectivityManager.activeNetworkInfo

                    if (networkInfo != null && networkInfo.isConnected) {
                        val token =
                            SharedPreferencesToken(this@FlashActivity).getToken()?.token ?: ""

                        startActivity(
                            Intent(
                                this@FlashActivity,
                                if (token.isNotEmpty()) GeneralActivity::class.java else MainActivity::class.java
                            )
                        )
                        this@FlashActivity.finish()
                        timer.cancel()
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(task, 0, 2500L)
    }
}