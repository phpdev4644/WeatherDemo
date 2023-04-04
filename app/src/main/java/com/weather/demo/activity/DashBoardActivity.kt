package com.weather.demo.activity

import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.weather.demo.Controller
import com.weather.demo.model.UserData
import com.weather.demo.utils.Constants
import com.weather.demo.viewmodel.CityViewModel
import androidx.activity.viewModels
import com.weather.demo.R
import com.weather.demo.databinding.ActivityDashBoardBinding
import com.weather.demo.common.NavigationActivity

class DashBoardActivity : NavigationActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDashBoardBinding
    private var userListner: ValueEventListener? = null
    private var ref = FirebaseDatabase.getInstance().reference
    private val cityViewModel: CityViewModel by viewModels()
    private var name = ""
    private var bio = ""
    private var photoUri = ""
    private val cityList =
        arrayListOf(
            "Kolkata", "Ahmedabad", "Nagpur",
            "Jaipur", "Patna", "Delhi",
            "Sri Ganganagar", "Jhansi", "Bilaspur", "Agra"
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clickListener = this
        binding.header.clickListener = this

        init()

    }

    private fun init() {

        binding.edtCity.setText(getString(R.string.kolkata))
        val currentUser = Controller.mAuth?.currentUser

        currentUser.let {
            val ref = ref.child(Constants.user_profile).child(currentUser!!.uid)
            userListner = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.child(Constants.name).value?.let {
                        name = snapshot.child(Constants.name).value as String
                    }

                    snapshot.child(Constants.bio).value?.let {
                        bio = snapshot.child(Constants.bio).value as String
                    }

                    snapshot.child(Constants.photoUri).value?.let {
                        photoUri = snapshot.child(Constants.photoUri).value as String
                    }

                    val userData = UserData(name, bio, photoUri)
                    binding.getUser = userData
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            ref.addValueEventListener(userListner!!)
        }

        cityApi()
    }

    private fun cityApi() {
        showProgress()

        cityViewModel.cityApi(binding.edtCity.text.toString(), response = {
            hideProgress()
            binding.getWeather = it
        }, error = {
            makeToast(it)
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.edtCity -> {
                showPopup(binding.edtCity, cityList) {
                    if (binding.edtCity.text.toString().trim() != it) {
                        binding.edtCity.setText(it)
                        cityApi()
                    }
                }
            }
            binding.header.txtLogout -> {
                Controller.mAuth?.signOut()
                openLoginActivity()
            }
        }
    }
}