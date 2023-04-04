package com.weather.demo.common


import android.util.Log
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.weather.demo.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

open class BindingMethods {

    companion object {

        @JvmStatic
        @BindingAdapter("setImage")
        fun setImage(imageview: CircleImageView, b: String) {
            Glide.with(imageview.context).load(b)
                .placeholder(R.drawable.ic_user_profile)
                .error(R.drawable.ic_user_profile).skipMemoryCache(true).into(imageview)
        }

        @JvmStatic
        @BindingAdapter("setWind")
        fun setWind(textView: MaterialTextView, b: String?) {
            Log.e("wind",b.toString())
            if(b != null && b.isNotEmpty()){
                textView.isVisible = true
                textView.text = StringBuilder(textView.context.getString(R.string.wind) + " " + b)
            }else {
                textView.isVisible = false
            }
        }

    }
}