package com.nomba.wraith.core

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter
import com.nomba.wraith.ui.shelters.transfer.TransferExpiredShelter
import com.nomba.wraith.ui.shelters.transfer.TransferShelter
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.Currency

// pass the activity and parentGroup as weakreferences to avoid memory leak
open class NombaManager private constructor (private var activity: WeakReference<Activity>, var clientKey: String, private var parentGroup: WeakReference<ConstraintLayout>) {

    init {
        setUpMainPaymentView()
        createAllShelters()
    }

    private val format: NumberFormat = NumberFormat.getCurrencyInstance()
    var paymentAmount : Double = 0.0
    var customerEmail : String = "customer-email@gmail.com"

    companion object {
        @Volatile
        private var instance: NombaManager? = null

        @Synchronized
        fun getInstance(activity: Activity, clientKey: String, parentGroup: ConstraintLayout): NombaManager {
            if (instance == null) {
                instance = NombaManager(WeakReference(activity), clientKey, WeakReference(parentGroup))
            }

            return instance!!
        }
    }

    //private lateinit var clientKey : String
    private lateinit var activityMainViewBinding : MainViewBinding
    private lateinit var paymentOptionsShelter: PaymentOptionsShelter
    private lateinit var transferShelter: TransferShelter
    private lateinit var transferExpiredShelter: TransferExpiredShelter



    private fun setUpMainPaymentView()  {
        val inflater: LayoutInflater = LayoutInflater.from(activity.get()).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        activityMainViewBinding = MainViewBinding.inflate(inflater)
        activityMainViewBinding.root.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,  ConstraintLayout.LayoutParams.MATCH_PARENT)
        activityMainViewBinding.root.visibility = View.GONE
        activityMainViewBinding.root.setOnApplyWindowInsetsListener { _, windowInsets ->
            val statusBarSize = windowInsets.systemWindowInsetTop
            val navBarSize = windowInsets.systemWindowInsetBottom
            val param = activityMainViewBinding.topView.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, statusBarSize,0, 0)
            activityMainViewBinding.topView.layoutParams = param

            val bottomParam = activityMainViewBinding.attribution.layoutParams as ViewGroup.MarginLayoutParams
            bottomParam.setMargins(0, 0,0, navBarSize + 40)
            activityMainViewBinding.attribution.layoutParams = bottomParam

            //val statusHeightParam = activityMainViewBinding.statusBar.layoutParams as ViewGroup.MarginLayoutParams
            activityMainViewBinding.statusBar.layoutParams = ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, statusBarSize)

            windowInsets

            //activityMainViewBinding.statusBar.he
        }
        parentGroup.get()?.addView(activityMainViewBinding.root)
    }

    private fun createAllShelters(){
        paymentOptionsShelter = PaymentOptionsShelter(this, activityMainViewBinding.paymentOptions)
        transferShelter = TransferShelter(this, activityMainViewBinding.transferView)
        transferExpiredShelter = TransferExpiredShelter(this, activityMainViewBinding.transferExpiredView)
    }

    fun showPaymentView(){
        setPaymentValues()
        paymentOptionsShelter.showShelter()
        activityMainViewBinding.root.visibility = View.VISIBLE
    }

    private fun setPaymentValues(){
        activityMainViewBinding.amountLabel.text = formatPaymentAmount()
        activityMainViewBinding.emailLabel.text = customerEmail
    }

    fun formatPaymentAmount() : String{
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("NGN")
        return format.format(paymentAmount)
    }

    fun showTransferView(){
        transferExpiredShelter.hideShelter()
        paymentOptionsShelter.hideShelter()
        transferShelter.showShelter()

    }

    fun changePaymentFromTransfer(){
        transferShelter.hideShelter()
        paymentOptionsShelter.showShelter()
    }

    fun waitingForTransferExpired(){
        transferShelter.hideShelter()
        transferExpiredShelter.showShelter()
    }




}