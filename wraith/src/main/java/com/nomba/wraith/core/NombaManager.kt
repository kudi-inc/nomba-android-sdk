package com.nomba.wraith.core

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter
import com.nomba.wraith.ui.shelters.transfer.ConfirmingTransferShelter
import com.nomba.wraith.ui.shelters.transfer.TransferExpiredShelter
import com.nomba.wraith.ui.shelters.transfer.TransferShelter
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.Currency


// pass the activity and parentGroup as weakreferences to avoid memory leak
open class NombaManager private constructor (var activity: WeakReference<Activity>, var clientKey: String, private var parentGroup: WeakReference<ConstraintLayout>) {

    init {
        setUpMainPaymentView()
        createAllShelters()
        setOnClickListeners()
    }

    private val format: NumberFormat = NumberFormat.getCurrencyInstance()
    var paymentAmount : Double = 0.0
    var displayViewState : DisplayViewState = DisplayViewState.PAYMENTOPTIONS
    var customerEmail : String = "customer-email@gmail.com"
    private val clipboardManager = activity.get()?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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
    private lateinit var confirmingTransferShelter: ConfirmingTransferShelter
    var utils = Utils()

    fun showExitDialog(){
        activityMainViewBinding.dialogView.root.visibility = View.VISIBLE
    }

    fun hideExitDialog(){
        activityMainViewBinding.dialogView.root.visibility = View.GONE
    }

    fun showLoader(){
        activityMainViewBinding.loadingView.root.visibility = View.VISIBLE
    }

    fun hideLoader(){
        activityMainViewBinding.loadingView.root.visibility = View.GONE
    }

    fun addToClipboard(textToAdd: CharSequence){
        val clipData = ClipData.newPlainText("text", textToAdd)
        clipboardManager.setPrimaryClip(clipData)
    }

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
        }
        parentGroup.get()?.addView(activityMainViewBinding.root)
    }

    private fun setOnClickListeners(){
        activityMainViewBinding.root.isFocusableInTouchMode = true
        activityMainViewBinding.root.requestFocus()
        Log.e("Bruh", "SET ERE")
        activityMainViewBinding.root.setOnKeyListener { _, keyCode, event ->
            Log.e("Bruh", "SET ERE TY")
            if (event.action === KeyEvent.ACTION_UP) {
                // Add your code here
                Log.e("Bru", "SET")
                handleBackStack()
                true
            } else false
        }

        activityMainViewBinding.dialogView.dialogCloseBtn.setOnClickListener {
            hideExitDialog()
        }

        activityMainViewBinding.dialogView.dialogCancelBtn.setOnClickListener {
            hideExitDialog()
        }

        activityMainViewBinding.dialogView.dialogConfirmBtn.setOnClickListener {
            hideExitDialog()
            hidePaymentView()
        }
    }

    fun handleBackStack(){
        when (displayViewState){
            DisplayViewState.PAYMENTOPTIONS -> activity.get()?.onBackPressed()

            DisplayViewState.TRANSFER -> {
                transferShelter.hideShelter()
                paymentOptionsShelter.showShelter()
                displayViewState = DisplayViewState.PAYMENTOPTIONS
            }

            DisplayViewState.TRANSFER_CONFIRMATION -> {

            }

            DisplayViewState.DIALOG -> TODO()
        }
    }

    private fun createAllShelters(){
        paymentOptionsShelter = PaymentOptionsShelter(this, activityMainViewBinding.paymentOptions)
        transferShelter = TransferShelter(this, activityMainViewBinding.transferView)
        transferExpiredShelter = TransferExpiredShelter(this, activityMainViewBinding.transferExpiredView)
        confirmingTransferShelter = ConfirmingTransferShelter(this, activityMainViewBinding.confirmingTransferView)
    }

    fun showPaymentView(){
        setPaymentValues()
        paymentOptionsShelter.showShelter()
        activityMainViewBinding.root.visibility = View.VISIBLE
    }

    fun hidePaymentView(){
        paymentOptionsShelter.hideShelter()
        activityMainViewBinding.root.visibility = View.GONE
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

    fun showTransferConfirmationView(){
        transferShelter.hideShelter()
        confirmingTransferShelter.showShelter()
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