package com.nomba.wraith.core

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.nomba.wraith.core.api.models.createorder.CreateOrderRequest
import com.nomba.wraith.core.api.models.createorder.CreateOrderResponse
import com.nomba.wraith.core.api.models.createorder.Order
import com.nomba.wraith.core.api.models.flashaccount.FlashAccountResponse
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusRequest
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusResponse
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.enums.PaymentOption
import com.nomba.wraith.core.managers.NetworkManager
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter
import com.nomba.wraith.ui.shelters.card.CardShelter
import com.nomba.wraith.ui.shelters.transfer.ConfirmingTransferShelter
import com.nomba.wraith.ui.shelters.transfer.GetHelpShelter
import com.nomba.wraith.ui.shelters.transfer.SuccessShelter
import com.nomba.wraith.ui.shelters.transfer.TransferExpiredShelter
import com.nomba.wraith.ui.shelters.transfer.TransferShelter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.Currency
import java.util.UUID


// pass the activity and parentGroup as weakreferences to avoid memory leak
open class NombaManager private constructor (var activity: WeakReference<Activity>, private var accountId: String, var clientId: String, var clientKey: String, private var parentGroup: WeakReference<ConstraintLayout>) {

    init {
        setUpMainPaymentView()
        createAllShelters()
        setOnClickListeners()
    }

    private val format: NumberFormat = NumberFormat.getCurrencyInstance()
    var paymentAmount : Double = 0.0
    var orderReference : String = UUID.randomUUID().toString()
    var displayViewState : DisplayViewState = DisplayViewState.PAYMENTOPTIONS
    var customerEmail : String = "customer-email@gmail.com"
    var customerId : String = UUID.randomUUID().toString()
    var customerName : String = "Wasiu Jackson"

    private var networkManager = NetworkManager()
    private val clipboardManager = activity.get()?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    companion object {
        @Volatile
        private var instance: NombaManager? = null

        @Synchronized
        fun getInstance(activity: Activity, accountId: String, clientId: String, clientKey: String, parentGroup: ConstraintLayout): NombaManager {
            if (instance == null) {
                instance = NombaManager(WeakReference(activity), accountId, clientId, clientKey, WeakReference(parentGroup))
            }

            return instance!!
        }
    }

    //private lateinit var clientKey : String
    private lateinit var activityMainViewBinding : MainViewBinding
    private lateinit var paymentOptionsShelter: PaymentOptionsShelter
    private lateinit var transferShelter: TransferShelter
    private lateinit var cardShelter: CardShelter
    private lateinit var transferExpiredShelter: TransferExpiredShelter
    private lateinit var confirmingTransferShelter: ConfirmingTransferShelter
    private lateinit var getHelpShelter: GetHelpShelter
    private lateinit var successShelter: SuccessShelter

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

            DisplayViewState.TRANSFER_CONFIRMATION_INNER_ONE -> TODO()
            DisplayViewState.TRANSFER_CONFIRMATION_INNER_TWO -> TODO()
            DisplayViewState.GET_HELP -> TODO()
            DisplayViewState.PAYMENT_SUCCESS -> activity.get()?.onBackPressed()
        }
    }

    private fun createAllShelters(){
        paymentOptionsShelter = PaymentOptionsShelter(this, activityMainViewBinding.paymentOptions)
        transferShelter = TransferShelter(this, activityMainViewBinding.transferView)
        transferExpiredShelter = TransferExpiredShelter(this, activityMainViewBinding.transferExpiredView)
        confirmingTransferShelter = ConfirmingTransferShelter(this, activityMainViewBinding.confirmingTransferView)
        getHelpShelter = GetHelpShelter(this, activityMainViewBinding.getHelpView)
        successShelter = SuccessShelter(this, activityMainViewBinding.successTransferView)
        cardShelter = CardShelter(this, activityMainViewBinding.cardView)
    }

    fun showPaymentView(){
        setPaymentValues()
        paymentOptionsShelter.showShelter()
        activityMainViewBinding.root.visibility = View.VISIBLE
    }

    fun showGetHelpView(){
        getHelpShelter.showShelter()
    }

    fun hideTransferConfirmingView(){
        confirmingTransferShelter.hideShelter()
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
        showLoader()
        networkManager.getAccessToken(accountId = accountId, clientId = clientId, clientKey = clientKey, PaymentOption.TRANSFER, ::createOrder)
    }

    fun showCardView(){
        showLoader()
        networkManager.getAccessToken(accountId = accountId, clientId = clientId, clientKey = clientKey, PaymentOption.CARD, ::createOrder)
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


///Network Funs
private fun fetchBanksForTransfer(){
    networkManager.getFlashAccount(orderReference).enqueue(object : Callback<FlashAccountResponse> {
        override fun onResponse(call: Call<FlashAccountResponse>, response: Response<FlashAccountResponse>) {
            if (response.isSuccessful) {
                // Handle the retrieved post data
                val post = response.body()
                Log.e("Error Response", post.toString())
                hideLoader()
                if (post?.code == "00"){
                    transferShelter.setBankDetails(post.data.accountNumber, post.data.bankName, post.data.accountName)
                    transferExpiredShelter.hideShelter()
                    paymentOptionsShelter.hideShelter()
                    transferShelter.showShelter()
                }
//                    else if (post?.code == "400") {
//                        Log.e("Error Response", post.toString())
//                        val description = post.description
//                        if (description.contains("INTERNAL: Unable to find Order for", ignoreCase = true)){
//                            createOrder()
//                        }
//                    }
            } else {
//                    Log.e("Error", "Error Transfer")
//                    Log.e("Error", response.toString())
//                    Log.e("Error", response.code().toString())
                // Handle error
                hideLoader()
            }
        }
        override fun onFailure(call: Call<FlashAccountResponse>, t: Throwable) {
            // Handle failure
            hideLoader()
        }
    })
}



    private fun createOrder(selectedPaymentOption : PaymentOption){
        // the order hasn't been created so create it
        networkManager.createOrder(accountId,
            CreateOrderRequest(Order(paymentAmount.toString(), callbackUrl = "",
                currency = "NGN", customerEmail = customerEmail,
                orderReference = orderReference, customerId = customerId),
                tokenizeCard = "true")).enqueue(object : Callback<CreateOrderResponse> {
            override fun onResponse(call: Call<CreateOrderResponse>, response: Response<CreateOrderResponse>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    Log.e("Error Response", post.toString())
                    if (post?.code == "00"){
                        orderReference = post.data.orderReference
                        when (selectedPaymentOption) {
                            PaymentOption.TRANSFER -> fetchBanksForTransfer()
                            PaymentOption.CARD -> {
                                hideLoader()
                                    paymentOptionsShelter.hideShelter()
                                    cardShelter.showShelter()
                            }
                        }
                    }
                } else {
                    hideLoader()
                }
            }
            override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                // Handle failure
                hideLoader()
            }
        })
    }


    fun checkOrderDetails(){
        networkManager.checkTransactionOrderStatus(CheckTransactionStatusRequest(orderReference)).enqueue(object : Callback<CheckTransactionStatusResponse> {
            override fun onResponse(call: Call<CheckTransactionStatusResponse>, response: Response<CheckTransactionStatusResponse>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    Log.e("Error Response", post.toString())
                    if (post?.code == "00"){
                        if (post.data.status == "true") {
                            confirmingTransferShelter.hideShelter()
                            successShelter.showShelter()
                        }
                    }
                } else {
                    hideLoader()
                }
            }
            override fun onFailure(call: Call<CheckTransactionStatusResponse>, t: Throwable) {
                // Handle failure
                hideLoader()
            }
        })
    }


}