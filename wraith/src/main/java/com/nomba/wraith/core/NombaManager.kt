package com.nomba.wraith.core

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter
import java.lang.ref.WeakReference

// pass the activity and parentGroup as weakreferences to avoid memory leak
open class NombaManager private constructor (private var activity: WeakReference<Activity>, var clientKey: String, private var parentGroup: WeakReference<ViewGroup>) {

    init {
        createAllShelters()
    }

    companion object {
        @Volatile
        private var instance: NombaManager? = null

        init {

        }
        @Synchronized
        fun getInstance(activity: Activity, clientKey: String, parentGroup: ViewGroup): NombaManager {
            if (instance == null) {
                instance = NombaManager(WeakReference(activity), clientKey, WeakReference(parentGroup))
            }

            return instance!!
        }
    }

    //private lateinit var clientKey : String
    private lateinit var activityMainViewBinding : MainViewBinding

    private lateinit var paymentOptionsShelter: PaymentOptionsShelter


    //open fun shared(activity: Activity, clientKey: String, parentGroup: ViewGroup) = NombaManager(activity, clientKey, parentGroup)

    private fun createAllShelters(){
        val inflater: LayoutInflater = LayoutInflater.from(activity.get()).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        activityMainViewBinding = MainViewBinding.inflate(inflater)
        activityMainViewBinding.root.visibility = View.GONE
        parentGroup.get()?.addView(activityMainViewBinding.root)

        paymentOptionsShelter = PaymentOptionsShelter(activityMainViewBinding.paymentOptions)
    }

    fun showPaymentView(){
        paymentOptionsShelter.showShelter()
        activityMainViewBinding.root.visibility = View.VISIBLE
        Log.e("Yess", "Balls")
    }


}


//class Singleton {
//    companion object {
//        private var instance: NombaManager? = null
//        fun getInstance(activity: Activity, clientKey: String, parentGroup: ViewGroup): NombaManager {
//            if (instance == null) {
//                instance = NombaManager(activity, clientKey, parentGroup)
//            }
//
//            return instance!!
//        }
//    }
//}