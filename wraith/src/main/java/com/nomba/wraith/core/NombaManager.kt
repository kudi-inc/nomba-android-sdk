package com.nomba.wraith.core

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.nomba.wraith.R
import com.nomba.wraith.core.api.models.CardObject
import com.nomba.wraith.core.api.models.createorder.CreateOrderRequest
import com.nomba.wraith.core.api.models.createorder.CreateOrderResponse
import com.nomba.wraith.core.api.models.createorder.Order
import com.nomba.wraith.core.api.models.flashaccount.FlashAccountResponse
import com.nomba.wraith.core.api.models.savecard.SaveCardOtpRequest
import com.nomba.wraith.core.api.models.savecard.SaveCardOtpResponse
import com.nomba.wraith.core.api.models.savecardsubmitotp.SaveCardSubmitOtpRequest
import com.nomba.wraith.core.api.models.savecardsubmitotp.SaveCardSubmitOtpResponse
import com.nomba.wraith.core.api.models.submitcard.DeviceInformation
import com.nomba.wraith.core.api.models.submitcard.SubmitCardDetailsRequest
import com.nomba.wraith.core.api.models.submitcard.SubmitCardDetailsResponse
import com.nomba.wraith.core.api.models.submitotp.SubmitOTPRequest
import com.nomba.wraith.core.api.models.submitotp.SubmitOTPResponse
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusRequest
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusResponse
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.enums.PaymentOption
import com.nomba.wraith.core.managers.NetworkManager
import com.nomba.wraith.databinding.MainViewBinding
import com.nomba.wraith.ui.shelters.FailureShelter
import com.nomba.wraith.ui.shelters.PaymentOptionsShelter
import com.nomba.wraith.ui.shelters.SaveCardOtpShelter
import com.nomba.wraith.ui.shelters.SaveCardSuccessShelter
import com.nomba.wraith.ui.shelters.SuccessShelter
import com.nomba.wraith.ui.shelters.card.CardLoadingShelter
import com.nomba.wraith.ui.shelters.card.CardOTPShelter
import com.nomba.wraith.ui.shelters.card.CardPinShelter
import com.nomba.wraith.ui.shelters.card.CardShelter
import com.nomba.wraith.ui.shelters.card.ThreeDSShelter
import com.nomba.wraith.ui.shelters.transfer.ConfirmingTransferShelter
import com.nomba.wraith.ui.shelters.transfer.GetHelpShelter
import com.nomba.wraith.ui.shelters.transfer.TransferExpiredShelter
import com.nomba.wraith.ui.shelters.transfer.TransferShelter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.UUID

var dev_mode = false;
// pass the activity and parentGroup as weakreferences to avoid memory leak
open class NombaManager private constructor(
    var activity: WeakReference<Activity>,
    private var accountId: String,
    var clientId: String,
    private var parentGroup: WeakReference<ConstraintLayout>
) {

    init {
        setUpMainPaymentView()
        createAllShelters()
        setOnClickListeners()
    }

    private val format: NumberFormat = NumberFormat.getInstance()
    var paymentAmount: Double = 0.0
    var orderReference: String = UUID.randomUUID().toString()
    var displayViewState: DisplayViewState = DisplayViewState.PAYMENTOPTIONS
    var customerEmail: String = "customer-email@gmail.com"
    var customerId: String = UUID.randomUUID().toString()
    var customerName: String = "Wasiu Jackson"
    var source: String = "android-sdk"
    var logo: Bitmap? = null
    var shouldSaveCard: Boolean = false
    var otpPhoneNumber: String = ""
    var transactionCallback: (CheckTransactionStatusResponse) -> Unit =
        fun(CheckTransactionStatusResponse) {}


    private var callbackURL: String = "https://wraith/android.sdk/callback"
    private var networkManager = NetworkManager()
    var otpMessage: String = ""
    var transactionID: String = ""
    private val clipboardManager =
        activity.get()?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    lateinit var cardObject: CardObject

    companion object {
        @Volatile
        private var instance: NombaManager? = null

        fun getInstance(
            activity: Activity,
            accountId: String,
            clientId: String,
            parentGroup: ConstraintLayout
        ): NombaManager {
            return instance ?: synchronized(this) {
                instance ?: NombaManager(
                    WeakReference(activity),
                    accountId,
                    clientId,
                    WeakReference(parentGroup)
                ).also { instance = it }
            }
        }
    }

    fun endInstance() {
        Log.d("Manager-Instance", "$instance")
        instance = null
    }


    //Shelters
    private lateinit var activityMainViewBinding: MainViewBinding
    private lateinit var paymentOptionsShelter: PaymentOptionsShelter
    private lateinit var transferShelter: TransferShelter
    private lateinit var cardShelter: CardShelter
    private lateinit var transferExpiredShelter: TransferExpiredShelter
    private lateinit var confirmingTransferShelter: ConfirmingTransferShelter
    private lateinit var getHelpShelter: GetHelpShelter
    private lateinit var successShelter: SuccessShelter
    private lateinit var cardPinShelter: CardPinShelter
    private lateinit var cardLoadingShelter: CardLoadingShelter
    private lateinit var cardOTPShelter: CardOTPShelter
    private lateinit var threeDSShelter: ThreeDSShelter
    private lateinit var failureShelter: FailureShelter
    private lateinit var saveCardSuccessShelter: SaveCardSuccessShelter
    private lateinit var saveCardOtpShelter: SaveCardOtpShelter

    var utils = Utils()
    private val displayMetrics = DisplayMetrics()
    private val windowManager: WindowManager =
        activity.get()?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private fun createAllShelters() {
        paymentOptionsShelter = PaymentOptionsShelter(this, activityMainViewBinding.paymentOptions)
        transferShelter = TransferShelter(this, activityMainViewBinding.transferView)
        transferExpiredShelter =
            TransferExpiredShelter(this, activityMainViewBinding.transferExpiredView)
        confirmingTransferShelter =
            ConfirmingTransferShelter(this, activityMainViewBinding.confirmingTransferView)
        getHelpShelter = GetHelpShelter(this, activityMainViewBinding.getHelpView)
        successShelter = SuccessShelter(this, activityMainViewBinding.successView)
        failureShelter = FailureShelter(this, activityMainViewBinding.failureView)
        cardShelter = CardShelter(this, activityMainViewBinding.cardView)
        cardPinShelter = CardPinShelter(this, activityMainViewBinding.cardPinView)
        cardLoadingShelter = CardLoadingShelter(this, activityMainViewBinding.cardLoadingView)
        cardOTPShelter = CardOTPShelter(this, activityMainViewBinding.cardOtpView)
        threeDSShelter = ThreeDSShelter(this, activityMainViewBinding.threedsView)
        saveCardSuccessShelter =
            SaveCardSuccessShelter(this, activityMainViewBinding.saveCardSuccessView)
        saveCardOtpShelter = SaveCardOtpShelter(this, activityMainViewBinding.saveCardOtpView)
    }

    private fun setUpMainPaymentView() {
        val inflater: LayoutInflater =
            LayoutInflater.from(activity.get()).context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        activityMainViewBinding = MainViewBinding.inflate(inflater)
        activityMainViewBinding.root.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        activityMainViewBinding.root.visibility = View.GONE
        activityMainViewBinding.root.setOnApplyWindowInsetsListener { _, windowInsets ->
            val statusBarSize = windowInsets.systemWindowInsetTop
            val navBarSize = windowInsets.systemWindowInsetBottom
            val param = activityMainViewBinding.topView.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, statusBarSize, 0, 0)
            activityMainViewBinding.topView.layoutParams = param
            val bottomParam =
                activityMainViewBinding.attribution.layoutParams as ViewGroup.MarginLayoutParams
            bottomParam.setMargins(0, 0, 0, navBarSize + 40)
            activityMainViewBinding.attribution.layoutParams = bottomParam
            //val statusHeightParam = activityMainViewBinding.statusBar.layoutParams as ViewGroup.MarginLayoutParams
            activityMainViewBinding.statusBar.layoutParams =
                ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, statusBarSize)
            windowInsets
        }

        parentGroup.get()?.addView(activityMainViewBinding.root)
    }

    fun showExitDialog() {
        activityMainViewBinding.dialogView.root.visibility = View.VISIBLE
    }

    fun hideExitDialog() {
        activityMainViewBinding.dialogView.root.visibility = View.GONE
    }

    fun showLoader() {
        activityMainViewBinding.loadingView.root.visibility = View.VISIBLE
    }

    fun hideLoader() {
        activityMainViewBinding.loadingView.root.visibility = View.GONE
    }

    fun addToClipboard(textToAdd: CharSequence) {
        val clipData = ClipData.newPlainText("text", textToAdd)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun setOnClickListeners() {
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

        //hide attribution when soft keyboard shown. Also add margin to success screen
        //shelter when the keyboard is shown and remove when it's hidden
        //this allows the user to scroll the view freely when the keyboard is visible
        val activityRootView: View = activityMainViewBinding.root
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            activityRootView.getWindowVisibleDisplayFrame(r)
            //Get keyboard height
            val insets = activity.get()?.window?.decorView
                ?.let { ViewCompat.getRootWindowInsets(it) }
            val statusBarSize = insets?.systemWindowInsetTop ?: 0
            val navBarSize = insets?.systemWindowInsetBottom ?: 0
            val keyboardHeight = insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 500
            var heightDiff: Int = activityRootView.rootView.height - (r.bottom - r.top)
            heightDiff = heightDiff - statusBarSize - navBarSize
            if (heightDiff >= keyboardHeight) {
                //keyboard is visible
                showAttribution()
                if (displayViewState == DisplayViewState.PAYMENT_SUCCESS) {
                    val bottomParam =
                        successShelter.layout().parent.layoutParams as ViewGroup.MarginLayoutParams
                    bottomParam.setMargins(0, 0, 0, 0)
                    successShelter.layout().parent.layoutParams = bottomParam
                }
            } else {
                //keyboard is hidden
                hideAttribution()
                if (displayViewState == DisplayViewState.PAYMENT_SUCCESS) {
                    val bottomParam =
                        successShelter.layout().parent.layoutParams as ViewGroup.MarginLayoutParams
                    bottomParam.setMargins(0, 0, 0, keyboardHeight + 300)
                    successShelter.layout().parent.layoutParams = bottomParam
                }

            }
        }


    }

    fun hideAttribution() {
        activityMainViewBinding.attribution.visibility = View.GONE
    }

    fun showAttribution() {
        activityMainViewBinding.attribution.visibility = View.VISIBLE
    }

    fun handleBackStack() {
        when (displayViewState) {
            DisplayViewState.PAYMENTOPTIONS -> {
                hidePaymentView()
            }

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
            DisplayViewState.PAYMENT_SUCCESS, DisplayViewState.SAVE_CARD_SUCCESS -> restartAllStates()
            DisplayViewState.CARD -> {
                cardShelter.hideShelter()
                cardLoadingShelter.hideShelter()
                paymentOptionsShelter.showShelter()
                displayViewState = DisplayViewState.PAYMENTOPTIONS
            }

            DisplayViewState.CARD_PIN -> {
                cardLoadingShelter.hideShelter()
                cardPinShelter.hideShelter()
                cardShelter.showShelter()
                displayViewState = DisplayViewState.CARD
            }

            DisplayViewState.CARD_OTP -> {
                cardLoadingShelter.hideShelter()
                cardOTPShelter.hideShelter()
                cardShelter.showShelter()
                displayViewState = DisplayViewState.CARD
            }

            DisplayViewState.CARD_LOADING -> {}
            DisplayViewState.PAYMENT_FAILTURE -> {
                cardShelter.hideShelter()
                failureShelter.hideShelter()
                paymentOptionsShelter.showShelter()
                displayViewState = DisplayViewState.PAYMENTOPTIONS
            }

            DisplayViewState.SECURE3DS -> {
                threeDSShelter.hideShelter()
                cardOTPShelter.hideShelter()
                cardLoadingShelter.hideShelter()
                cardShelter.showShelter()
                displayViewState = DisplayViewState.CARD
            }

            DisplayViewState.SAVE_CARD_OTP -> {
                saveCardOtpShelter.hideShelter()
                successShelter.showShelter()
            }
        }
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(activityMainViewBinding.root, message, Snackbar.LENGTH_LONG).show()
    }

    fun showPaymentView() {
        setPaymentValues()
        paymentOptionsShelter.showShelter()
        activityMainViewBinding.root.visibility = View.VISIBLE
    }

    fun showGetHelpView() {
        getHelpShelter.showShelter()
    }

    fun changePaymentMethodFromFailureShelter() {
        failureShelter.hideShelter()
        cardOTPShelter.hideShelter()
        cardLoadingShelter.hideShelter()
        cardPinShelter.hideShelter()
        cardShelter.hideShelter()
        paymentOptionsShelter.showShelter()
        displayViewState = DisplayViewState.PAYMENTOPTIONS
    }

    fun tryAnotherCardFromFailureShelter() {
        failureShelter.hideShelter()
        cardOTPShelter.hideShelter()
        cardLoadingShelter.hideShelter()
        cardPinShelter.hideShelter()
        cardShelter.showShelter()
        displayViewState = DisplayViewState.CARD
    }

    fun showPaymentOptionsView() {
        transferExpiredShelter.hideShelter()
        paymentOptionsShelter.showShelter()
    }

    fun hideTransferConfirmingView() {
        confirmingTransferShelter.hideShelter()
    }

    fun hidePaymentView() {
        transferShelter.hideShelter()
        cardShelter.hideShelter()
        paymentOptionsShelter.hideShelter()
        activityMainViewBinding.root.visibility = View.GONE
    }

    private fun restartAllStates() {
        successShelter.hideShelter()
        saveCardSuccessShelter.hideShelter()
        transferShelter.hideShelter()
        cardShelter.hideShelter()
        paymentOptionsShelter.hideShelter()
        activityMainViewBinding.root.visibility = View.GONE
    }

    private fun setPaymentValues() {
        //orderReference = UUID.randomUUID().toString()
        activityMainViewBinding.amountLabel.text = formatPaymentAmount()
        activityMainViewBinding.emailLabel.text = customerEmail
        logo?.let {
            activityMainViewBinding.logo.setImageBitmap(it)
            activityMainViewBinding.logoHolder.background = null
        }
    }

    fun formatPaymentAmount(): String {
        return activity.get()!!.getString(
            R.string.pay_label,
            doFormattingAmount()
        )
    }


    fun doFormattingAmount(): String {
        format.maximumFractionDigits = 2
        format.minimumFractionDigits = 2
        return format.format(paymentAmount)
    }

    fun showTransferView() {
        shouldSaveCard = false
        showLoader()
        createOrder(PaymentOption.TRANSFER)
    }


    fun showCardView() {
        shouldSaveCard = false
        showLoader()
        createOrder(PaymentOption.CARD)
    }

    fun showCardPinView() {
        cardShelter.hideShelter()
        cardPinShelter.showShelter()
    }


    fun saveCard() {

    }

    fun showCardLoadingShelter() {
        cardLoadingShelter.showShelter()
    }


    fun goBackToChangeNumberForSaveCardOtp() {
        saveCardOtpShelter.hideShelter()
        successShelter.showShelter()
    }

    fun hideCardLoadingShelter() {
        cardLoadingShelter.hideShelter()
    }


    fun hideKeyboard() {
        val inputMethodManager =
            activity.get()?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activityMainViewBinding.root.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        val inputMethodManager =
            activity.get()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
        //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun showTransferConfirmationView() {
        confirmingTransferShelter.showShelter()
        transferShelter.hideShelter()
    }

    fun changePaymentFromTransfer() {
        transferShelter.hideShelter()
        paymentOptionsShelter.showShelter()
    }

    fun changePaymentFromCard() {
        cardShelter.hideShelter()
        paymentOptionsShelter.showShelter()
    }

    fun waitingForTransferExpired() {
        transferShelter.hideShelter()
        transferExpiredShelter.showShelter()
    }

    fun getHeight(): Int {
        return displayMetrics.heightPixels
    }

    fun getWidth(): Int {
        return displayMetrics.widthPixels
    }


    ///Network Funs
    private fun fetchBanksForTransfer() {
        networkManager.getFlashAccount(orderReference)
            .enqueue(object : Callback<FlashAccountResponse> {
                override fun onResponse(
                    call: Call<FlashAccountResponse>,
                    response: Response<FlashAccountResponse>
                ) {
                    if (response.isSuccessful) {
                        // Handle the retrieved post data
                        val post = response.body()
                        hideLoader()
                        if (post?.code == "00") {
                            transferShelter.setBankDetails(
                                post.data.accountNumber,
                                post.data.bankName,
                                post.data.accountName
                            )
                            transferExpiredShelter.hideShelter()
                            paymentOptionsShelter.hideShelter()
                            transferShelter.showShelter()
                        }
                    } else {
                        // Handle error
                        showSnackbar(response.errorBody().toString() + " Try Again")
                        hideLoader()
                    }
                }

                override fun onFailure(call: Call<FlashAccountResponse>, t: Throwable) {
                    // Handle failure
                    showSnackbar(t.message + " Try Again")
                    hideLoader()
                }
            })
    }


    fun requestOTPForCardSaving() {
        hideKeyboard()
        successShelter.hideShelter()
        saveCardOtpShelter.hideShelter()
        showLoader()

        networkManager.requestOTPForCardSaving(SaveCardOtpRequest(orderReference, otpPhoneNumber))
            .enqueue(object : Callback<SaveCardOtpResponse> {
                override fun onResponse(
                    call: Call<SaveCardOtpResponse>,
                    response: Response<SaveCardOtpResponse>
                ) {
                    if (response.isSuccessful) {
                        hideLoader()
                        val post = response.body()
                        Log.e("Success Response", post.toString())
                        if (post?.code == "00") {
                            //show OTP entry view
                            saveCardOtpShelter.showShelter()
                        } else {
                            successShelter.showShelter()
                            showSnackbar(post?.data?.message + "Try Again")
                        }
                    } else {
                        showSnackbar(response.errorBody().toString() + " Try Again")
                        hideLoader()
                    }
                }

                override fun onFailure(call: Call<SaveCardOtpResponse>, t: Throwable) {
                    // Handle failure
                    showSnackbar(t.message + " Try Again")
                    hideLoader()
                }
            })
    }

    fun submitOtpForCardSaving(otpText: String) {
        hideKeyboard()
        saveCardOtpShelter.hideShelter()
        showLoader()

        networkManager.submitOTPForCardSaving(
            SaveCardSubmitOtpRequest(
                orderReference,
                otpText,
                otpPhoneNumber
            )
        ).enqueue(object : Callback<SaveCardSubmitOtpResponse> {
            override fun onResponse(
                call: Call<SaveCardSubmitOtpResponse>,
                response: Response<SaveCardSubmitOtpResponse>
            ) {
                if (response.isSuccessful) {
                    hideLoader()
                    val post = response.body()
                    Log.e("Success Response", post.toString())
                    if (post?.code == "00") {
                        //show OTP entry view
                        saveCardSuccessShelter.showShelter()
                    } else {
                        saveCardOtpShelter.showShelter()
                        showSnackbar(post?.data?.message + "Try Again")

                    }
                } else {
                    showSnackbar(response.errorBody().toString() + " Try Again")
                    hideLoader()
                }
            }

            override fun onFailure(call: Call<SaveCardSubmitOtpResponse>, t: Throwable) {
                // Handle failure
                showSnackbar(t.message + " Try Again")
                hideLoader()
            }
        })
    }


    private fun createOrder(selectedPaymentOption: PaymentOption) {
        // the order hasn't been created so create it
        networkManager.createOrder(
            accountId,
            clientId,
            source,
            CreateOrderRequest(
                Order(
                    paymentAmount.toString(), callbackUrl = callbackURL,
                    currency = "NGN", customerEmail = customerEmail,
                    orderReference = orderReference, customerId = customerId
                ),
                tokenizeCard = "true"
            )
        ).enqueue(object : Callback<CreateOrderResponse> {
            override fun onResponse(
                call: Call<CreateOrderResponse>,
                response: Response<CreateOrderResponse>
            ) {
                if (response.isSuccessful) {
                    val post = response.body()
                    orderReference = post?.data?.orderReference ?: orderReference
                    if (post?.code == "00") {
                        when (selectedPaymentOption) {
                            PaymentOption.TRANSFER -> fetchBanksForTransfer()
                            PaymentOption.CARD -> {
                                hideLoader()
                                paymentOptionsShelter.hideShelter()
                                cardShelter.showShelter()
                            }
                        }
                    } else if (post?.code == "400") {
                        //order already exists, move on
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
                    showSnackbar(response.errorBody().toString() + "Try Again")
                    hideLoader()
                }
            }

            override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                // Handle failure
                showSnackbar(t.message + "Try Again")
                hideLoader()
            }
        })
    }

    fun submitOTPDetails(otpText: String) {
        hideKeyboard()
        cardOTPShelter.hideShelter()
        cardLoadingShelter.showShelter()
        networkManager.submitOTPDetails(SubmitOTPRequest(orderReference, otpText, transactionID))
            .enqueue(object : Callback<SubmitOTPResponse> {
                override fun onResponse(
                    call: Call<SubmitOTPResponse>,
                    response: Response<SubmitOTPResponse>
                ) {
                    if (response.isSuccessful) {
                        val post = response.body()
                        Log.e("Success Response", post.toString())
                        if (post?.code == "00") {
                            if (post.data.message == "Token Authorization Not Successful. Incorrect Token Supplied") {
                                showSnackbar(post.data.message + "Try Again")
                                cardLoadingShelter.hideShelter()
                                cardPinShelter.hideShelter()
                                cardOTPShelter.showShelter()
                            } else if (post.data.status == "true") {
                                cardLoadingShelter.hideShelter()
                                successShelter.showShelter()
                            } else {
                                showSnackbar(post.data.message + "Try Again")
                                cardLoadingShelter.hideShelter()
                                cardPinShelter.hideShelter()
                                cardOTPShelter.showShelter()
                            }
                        } else if (post?.code == "400") {
                            if (post.data.message.contains("already completed.")) {
                                cardLoadingShelter.hideShelter()
                                successShelter.showShelter()
                            }
                        }
                    } else {
                        showSnackbar(response.errorBody().toString() + "Try Again")
                        cardPinShelter.hideShelter()
                        cardOTPShelter.hideShelter()
                        cardLoadingShelter.hideShelter()
                        cardShelter.showShelter()
                    }
                }

                override fun onFailure(call: Call<SubmitOTPResponse>, t: Throwable) {
                    // Handle failure
                    showSnackbar(t.message + "Try Again")
                    cardPinShelter.hideShelter()
                    cardOTPShelter.hideShelter()
                    cardLoadingShelter.hideShelter()
                    cardShelter.showShelter()
                }
            })
    }


    fun submitCardDetails() {
        hideKeyboard()
        cardPinShelter.hideShelter()
        cardLoadingShelter.showShelter()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val deviceInformation = DeviceInformation(
            "Browser",
            "24",
            "true",
            "true",
            "en",
            getHeight().toString(),
            getWidth().toString(),
            "-60",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        )
        val stringCardDetails =
            "{\"cardCVV\": " + cardObject.cardCVV + ",\"cardExpiryMonth\": " + cardObject.cardMonth + ",\"cardExpiryYear\": " + cardObject.cardYear + ",\"cardNumber\": \"" + cardObject.cardNumber + "\",\"cardPin\": " + cardObject.cardPin + "}"

        val submitCardDetailsRequest = SubmitCardDetailsRequest(
            cardDetails = stringCardDetails,
            key = "", orderReference = orderReference,
            saveCard = cardObject.saveCard.toString(),
            deviceInformation = deviceInformation
        )
        networkManager.submitCardDetails(submitCardDetailsRequest)
            .enqueue(object : Callback<SubmitCardDetailsResponse> {
                override fun onResponse(
                    call: Call<SubmitCardDetailsResponse>,
                    response: Response<SubmitCardDetailsResponse>
                ) {
                    //Log.e("Error Response", response.toString())
                    if (response.isSuccessful) {
                        val post = response.body()
                        Log.e("Success Response", post.toString())
                        if (post?.code == "00") {
                            when (post.data.responseCode) {
                                "T0" -> {
                                    //show OTP screen
                                    otpMessage = post.data.message
                                    transactionID = post.data.transactionId
                                    cardLoadingShelter.hideShelter()
                                    cardOTPShelter.showShelter()
                                }

                                "T1", "500" -> {
                                    showSnackbar(post.data.message + " Try Again")
                                    cardOTPShelter.hideShelter()
                                    cardLoadingShelter.hideShelter()
                                    cardPinShelter.hideShelter()
                                    cardShelter.showShelter()
                                }

                                "S0" -> {
                                    //show 3Ds screen
                                    cardLoadingShelter.hideShelter()
                                    threeDSShelter.acsUrl =
                                        post.data.secureAuthenticationData.acsUrl
                                    threeDSShelter.jwtToken = post.data.secureAuthenticationData.jwt
                                    threeDSShelter.md = post.data.secureAuthenticationData.md
                                    threeDSShelter.termUrl =
                                        post.data.secureAuthenticationData.termUrl
                                    threeDSShelter.showShelter()
                                }

                                "00" -> {
                                    cardLoadingShelter.hideShelter()
                                    successShelter.showShelter()
                                }

                                else -> {
                                    showSnackbar(post.data.message + "Try Again")
                                    cardLoadingShelter.hideShelter()
                                    cardPinShelter.hideShelter()
                                    cardShelter.showShelter()
                                }
                            }
                        }
                    } else {
                        showSnackbar(response.errorBody().toString() + "Try Again")
                        cardLoadingShelter.hideShelter()
                        cardPinShelter.hideShelter()
                        cardShelter.showShelter()
                    }
                }

                override fun onFailure(call: Call<SubmitCardDetailsResponse>, t: Throwable) {
                    // Handle failure
                    showSnackbar(t.message + "Try Again")
                    cardLoadingShelter.hideShelter()
                    cardPinShelter.hideShelter()
                    cardShelter.showShelter()
                }
            })
    }

    var transactionResponse: CheckTransactionStatusResponse? = null

    fun checkOrderDetails(
        paymentOption: PaymentOption = PaymentOption.TRANSFER,
        onSuccessFun: () -> Unit
    ) {
        networkManager.checkTransactionOrderStatus(CheckTransactionStatusRequest(orderReference))
            .enqueue(object : Callback<CheckTransactionStatusResponse> {
                override fun onResponse(
                    call: Call<CheckTransactionStatusResponse>,
                    response: Response<CheckTransactionStatusResponse>
                ) {
                    transactionResponse = response.body()
                    if (response.isSuccessful) {
                        val post = response.body()
                        Log.e("Error Response", post.toString())
                        if (post?.code == "00") {
                            if (post.data.status == "true") {
                                onSuccessFun()
                                cardLoadingShelter.hideShelter()
                                confirmingTransferShelter.hideShelter()
                                successShelter.showShelter()
                            } else if (post.data.status == "false" && paymentOption == PaymentOption.CARD) {
                                confirmingTransferShelter.hideShelter()
                                failureShelter.failureMessage = post.data.message
                                failureShelter.showShelter()
                                cardLoadingShelter.hideShelter()
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