package com.nomba.wraith.ui.shelters.transfer

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.nomba.wraith.R
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.api.models.transationstatus.CheckTransactionStatusResponse
import com.nomba.wraith.core.enums.PaymentOption
import com.nomba.wraith.databinding.ConfirmingTransferViewBinding
import java.util.Locale

class ConfirmingTransferShelter(private var manager: NombaManager, activityConfirmingTransferViewBinding: ConfirmingTransferViewBinding) : Shelter(activityConfirmingTransferViewBinding) {

    private lateinit var confirmationTimer: CountDownTimer
    private val confirmationTime : Long = 600000

    override fun layout(): ConfirmingTransferViewBinding {
        return super.layout() as ConfirmingTransferViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        manager.displayViewState = DisplayViewState.TRANSFER_CONFIRMATION
        confirmationTimer = manager.utils.createTimer(confirmationTime, ::onConfirmationTransferTick, ::onConfirmationTransferEnd)
        confirmationTimer.start()
        layout().waitingForConfirmationProgress.max = confirmationTime.toInt()
        layout().tryAgainBtn.setOnClickListener {
            if (manager.displayViewState == DisplayViewState.TRANSFER_CONFIRMATION) {
                manager.displayViewState = DisplayViewState.TRANSFER_CONFIRMATION_INNER_ONE
                setUpSecondConfirmationScreen()
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            } else {
                manager.displayViewState = DisplayViewState.TRANSFER_CONFIRMATION_INNER_TWO
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
        }

        layout().getHelpBtn.setOnClickListener {
            manager.hideTransferConfirmingView()
            manager.showGetHelpView()
        }

        layout().needHelpWithThisTransactionButton.setOnClickListener {
            manager.hideTransferConfirmingView()
            manager.showGetHelpView()
        }

        resetViews()

    }



    private fun onConfirmationTransferTick(millisUntilFinished: Long){
        val totalSeconds = millisUntilFinished / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        val textCount = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds,
        )

        layout().waitingForConfirmationTimerView.text = textCount
        layout().waitingForConfirmationProgress.progress = millisUntilFinished.toInt()


        // check the transfer status at different times
        when (textCount) {
            "09:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "08:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "07:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "06:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "05:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "04:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "03:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "02:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
            "01:00" -> {
                manager.checkOrderDetails(paymentOption = PaymentOption.TRANSFER, ::endConfirmationTimer)
            }
        }

    }

    private fun endConfirmationTimer(){
        confirmationTimer.cancel()
        if (manager.transactionResponse!=null) {
            manager.transactionCallback(manager.transactionResponse!!)
        }
    }

    private fun onConfirmationTransferEnd(){
        firstConfirmationEnded()
    }

    private fun firstConfirmationEnded(){
        layout().timeElaspedLabel.text = manager.activity.get()?.getString(R.string.time_elapsed)
        layout().waitingForConfirmationProgress.progress = 0
        layout().waitingForConfirmationTimerView.visibility = View.GONE

        layout().topContentHolder.background =
            manager.activity.get()?.let {
                ContextCompat.getDrawable(it, R.drawable.red_warning_bg) }

        layout().staticProgress.visibility = View.GONE
        layout().contentLabel.text = manager.activity.get()?.let {
            ContextCompat.getString(it, R.string.transaction_taking_long)
        }

        layout().needHelpWithThisTransactionButton.visibility = View.GONE
        layout().buttonHolder.visibility = View.VISIBLE
    }

    private fun resetViews(){
        layout().timeElaspedLabel.text = manager.activity.get()?.getString(R.string.wait_time)
        layout().waitingForConfirmationTimerView.visibility = View.VISIBLE

        layout().topContentHolder.background =
            manager.activity.get()?.let {
                ContextCompat.getDrawable(it, R.drawable.warning_bg) }

        layout().staticProgress.visibility = View.VISIBLE
        layout().contentLabel.text = manager.activity.get()?.let {
            ContextCompat.getString(it, R.string.please_wait_we_are_confirming_your_payment_it_might_take_a_few_minutes)
        }

        layout().needHelpWithThisTransactionButton.visibility = View.VISIBLE
        layout().buttonHolder.visibility = View.GONE
        layout().waitingHolder.visibility = View.VISIBLE
        layout().waitingForConfirmationTimerView.visibility = View.VISIBLE
    }

    private fun setUpSecondConfirmationScreen(){
        layout().waitingHolder.visibility = View.GONE
        layout().waitingForConfirmationProgress.progress = 0
        layout().waitingForConfirmationTimerView.visibility = View.GONE

        layout().topContentHolder.background =
            manager.activity.get()?.let {
                ContextCompat.getDrawable(it, R.drawable.warning_bg) }

        layout().staticProgress.visibility = View.VISIBLE
        layout().contentLabel.text = manager.activity.get()?.let {
            ContextCompat.getString(it, R.string.longer_than_expected)
        }
    }

    override fun hideShelter() {
        super.hideShelter()
        if (this::confirmationTimer.isInitialized){
            confirmationTimer.cancel()
        }
    }

}