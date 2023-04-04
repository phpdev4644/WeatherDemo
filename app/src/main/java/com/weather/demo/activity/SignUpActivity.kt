package com.weather.demo.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.weather.demo.R
import com.weather.demo.databinding.ActivitySignUpBinding
import com.weather.demo.common.NavigationActivity
import com.weather.demo.dialog.DialogUtils
import com.weather.demo.utils.Constants
import com.weather.demo.utils.TextUtil
import com.weather.demo.viewmodel.FirebaseAuthViewModel
import java.io.File

class SignUpActivity : NavigationActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private var filePath: File? = null
    private var uri: Uri? = null
    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clickListener = this

        init()
    }

    private fun init() {
        touchListener()
        binding.header.txtLogout.isVisible = false
        binding.header.txtHeader.text = getString(R.string.create_account)
    }

    override fun onClick(v: View?) {
        hideKeyBoard()
        when (v) {
            binding.btnSignUp -> {
                if (checkFile() && validation()) {
                    firebaseAuthViewModel.signUpApi(
                        binding.edtEmail.text.toString(),
                        binding.edtPassword.text.toString(),
                        binding.edtperson.text.toString(),
                        binding.edtBio.text.toString(), uri!!
                    )
                }
            }
            binding.txtSignIn -> {
                openLoginActivity()
            }
            binding.ivProfile -> {
                DialogUtils.showChooseAppDialog(this, listener = { uri, s, which ->
                    when (which) {
                        Constants.CAMERA -> {
                            filePath = File(s)
                            this.uri = uri
                        }
                        else -> {
                            getFileFromUri(this, uri)?.let {
                                filePath = it
                                this.uri = uri
                            }
                        }
                    }

                    uploadImage(uri)
                })
            }
        }
    }

    private fun uploadImage(uri: Uri) {
        val mStorage: StorageReference =
            FirebaseStorage.getInstance().reference.child(Constants.users_photos)

        val imageFilePath = mStorage.child(uri.lastPathSegment!!)

        imageFilePath.putFile(uri).addOnSuccessListener {
            imageFilePath.downloadUrl.addOnSuccessListener {
                this.uri = it
            }
        }

        binding.ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this).load(uri).into(binding.ivProfile)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchListener() {
        binding.edtBio.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    private fun checkFile(): Boolean {
        return if (filePath == null) {
            makeToast(getString(R.string.select_profile))
            false
        } else {
            true
        }
    }

    private fun validation(): Boolean {
        return when {
            TextUtil.isNullOrEmpty(binding.edtperson.text.toString().trim()) -> {
                binding.edtperson.error = getString(R.string.error_username)
                binding.edtperson.requestFocus()
                false
            }
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
            TextUtil.isNullOrEmpty(binding.edtCnfPassword.text.toString().trim()) -> {
                binding.edtCnfPassword.error = getString(R.string.error_confirm_password)
                binding.edtCnfPassword.requestFocus()
                false
            }
            !TextUtil.isValidCPss(
                binding.edtPassword.text.toString().trim(),
                binding.edtCnfPassword.text.toString().trim()
            ) -> {
                binding.edtCnfPassword.error = getString(R.string.error_validnewcpassword)
                binding.edtCnfPassword.requestFocus()
                false
            }
            TextUtil.isNullOrEmpty(binding.edtBio.text.toString().trim()) -> {
                binding.edtBio.error = getString(R.string.error_description)
                binding.edtBio.requestFocus()
                false
            }
            else -> true
        }
    }

}