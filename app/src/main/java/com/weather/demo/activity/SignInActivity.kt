package com.weather.demo.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.weather.demo.R
import com.weather.demo.databinding.ActivitySignInBinding
import com.weather.demo.common.NavigationActivity
import com.weather.demo.utils.TextUtil
import com.weather.demo.viewmodel.FirebaseAuthViewModel

class SignInActivity : NavigationActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignInBinding
    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clickListener = this

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnLogin -> {
                if (validation()) {
                    showProgress()
                    firebaseAuthViewModel.loginApi(
                        binding.edtEmail.text.toString(),
                        binding.edtPassword.text.toString()
                    )
                }
            }
            binding.txtCreateOne -> {
                openSignUpActivity()
            }
        }
    }

    private fun validation(): Boolean {
        return when {
            TextUtil.isNullOrEmpty(binding.edtEmail.text.toString().trim()) -> {
                binding.edtEmail.error = getString(R.string.error_email)
                binding.edtEmail.requestFocus()
                false
            }
            !TextUtil.isValidEmail(binding.edtEmail.text.toString().trim()) -> {
                binding.edtEmail.error = getString(R.string.error_validemail)
                binding.edtEmail.requestFocus()
                false
            }
            TextUtil.isNullOrEmpty(binding.edtPassword.text.toString().trim()) -> {
                binding.edtPassword.error = null
                binding.edtPassword.error = getString(R.string.error_password)
                binding.edtPassword.requestFocus()
                false
            }

            !TextUtil.isValidPassword(binding.edtPassword.text.toString().trim()) -> {
                binding.edtPassword.error = getString(R.string.error_valid_pass)
                binding.edtPassword.requestFocus()
                false
            }
            else -> true
        }
    }
}