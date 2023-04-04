package com.weather.demo.utils

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern

object TextUtil {

    fun isNullOrEmpty(string: String?): Boolean {
        return string?.isEmpty() ?: true
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(editText: String): Boolean {
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(editText.toString())
        return matcher.matches()
    }

    fun isValidCPss(target: CharSequence?, target2: CharSequence?): Boolean {
        return target!! == target2
    }
}