package com.weather.demo.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.weather.demo.databinding.ActivitySplashBinding
import com.weather.demo.Controller
import com.weather.demo.common.NavigationActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : NavigationActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Controller.mAuth?.currentUser != null){
            openDashBoardActivity()
        }else {
            openLoginActivity()
        }

    }
}