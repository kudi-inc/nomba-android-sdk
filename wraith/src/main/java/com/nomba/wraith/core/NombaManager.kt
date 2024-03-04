package com.nomba.wraith.core

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter

open class NombaManager(private var activity: Activity, var clientKey: String, private var parentGroup: ViewGroup) {

    //private lateinit var clientKey : String
    private lateinit var activityMainViewBinding : MainViewBinding

    private lateinit var paymentOptionsShelter: PaymentOptionsShelter
    init {
        createAllShelters()
    }

    open fun shared(activity: Activity, clientKey: String, parentGroup: ViewGroup) = NombaManager(activity, clientKey, parentGroup)

    private fun createAllShelters(){
        val inflater: LayoutInflater = LayoutInflater.from(activity).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        activityMainViewBinding = MainViewBinding.inflate(inflater)
        activityMainViewBinding.root.visibility = View.GONE
        parentGroup.addView(activityMainViewBinding.root)

        paymentOptionsShelter = PaymentOptionsShelter(activityMainViewBinding.paymentOptions)
    }

    fun showPaymentView(){
        paymentOptionsShelter.showShelter()
    }


}