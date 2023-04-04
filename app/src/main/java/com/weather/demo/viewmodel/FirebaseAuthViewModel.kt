package com.weather.demo.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.weather.demo.Controller
import com.weather.demo.R
import com.weather.demo.utils.Constants

class FirebaseAuthViewModel : ViewModel() {

    fun loginApi(
        email: String,
        password: String
    ) {
        Controller.mAuth?.signInWithEmailAndPassword(email, password)!!
            .addOnCompleteListener { task ->
                Controller.instance.context?.hideProgress()
                if (task.isSuccessful) {
                    Controller.instance.context?.openDashBoardActivity()
                } else {
                    Controller.instance.context?.makeToast(task.exception?.message.toString())
                }
            }
    }

    fun signUpApi(
        email: String, password: String,
        userName: String, bio: String, uri: Uri
    ) {
        val base = Controller.instance.context
        base?.showProgress()
        Controller.mAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(
                base!!
            ) { task ->

                if (task.isSuccessful) {

                    base.hideProgress()
                    val userID = Controller.mAuth?.currentUser?.uid

                    var ref = FirebaseDatabase.getInstance().reference
                    ref = ref.child(Constants.user_profile).child(userID!!)

                    ref.child(Constants.name).setValue(userName)
                    ref.child(Constants.email)
                        .setValue(email)
                    ref.child(Constants.password).setValue(password)
                    ref.child(Constants.bio).setValue(bio)
                    ref.child(Constants.photoUri).setValue(uri.toString())

                    base.makeToast(base.getString(R.string.register))

                    base.openLoginActivity()
                } else {
                    base.hideProgress()
                    base.makeToast(task.exception!!.message.toString())
                }
            }
    }
}