package com.nomba.wraith.core

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.nomba.wraith.R
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter

open class NombaManager(private var activity: Activity, var clientKey: String) {

    //private lateinit var clientKey : String
    private lateinit var activityMainViewBinding : MainViewBinding

    private lateinit var paymentOptionsShelter: PaymentOptionsShelter
    init {
        createAllShelters()
    }

    fun shared(activity: Activity, clientKey: String) = NombaManager(activity, clientKey)

    private fun createAllShelters(){
        val inflater: LayoutInflater = LayoutInflater.from(activity).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //MainViewBinding.inflate()
        activityMainViewBinding = MainViewBinding.inflate(inflater)
        paymentOptionsShelter = PaymentOptionsShelter(activityMainViewBinding.paymentOptions)
    }
}