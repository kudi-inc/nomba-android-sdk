package com.nomba.wraith.ui.shelters.transfer

import android.annotation.SuppressLint
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.TransferViewBinding
import java.util.Locale

class TransferShelter(private var manager: NombaManager, activityTransferViewBinding: TransferViewBinding) : Shelter(activityTransferViewBinding) {

    private lateinit var waitingForTransferTimer: CountDownTimer
    private val waitingForTransferTime : Long = 1800000
    private var accountNumber : String = ""
    private var bankName : String = ""
    private var accountName : String = ""

    override fun layout(): TransferViewBinding {
        return super.layout() as TransferViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        setOnClickListeners()
        layout().amountLabel.text = manager.formatPaymentAmount()
        layout().accountNumberText.text = accountNumber
        layout().accountNameText.text = accountName
        layout().bankNameText.text = bankName

        manager.displayViewState = DisplayViewState.TRANSFER
        waitingForTransferTimer = manager.utils.createTimer(waitingForTransferTime, ::onWaitingForTransferTick, ::onWaitingForTransferEnd)

        layout().waitingForTransferProgress.max = waitingForTransferTime.toInt()
        waitingForTransferTimer.start()


    }

    fun setBankDetails(accountNumber: String, bankName : String, accountName : String){
        this.bankName = bankName
        this.accountName = accountName
        this.accountNumber = accountNumber
    }

    private fun setOnClickListeners(){
        //Change Payment Button In Transfer View
        layout().transferChangePaymentMtdBtn.setOnClickListener {
            manager.changePaymentFromTransfer()
        }

        layout().cancelButton.setOnClickListener {
            manager.showExitDialog()
        }

        layout().accountNumber.setOnClickListener {
            val accountNumber = layout().accountNumberText.text
            manager.addToClipboard(accountNumber)
            Snackbar.make(layout().root, "Account Number copied to clipboard", Snackbar.LENGTH_SHORT).show()
        }

        layout().accountNumberCopyBtn.setOnClickListener {
            val accountNumber = layout().accountNumberText.text
            manager.addToClipboard(accountNumber)
            Snackbar.make(layout().root, "Account Number copied to clipboard", Snackbar.LENGTH_SHORT).show()
        }



        layout().sentMnyBtn.setOnClickListener{
            manager.showTransferConfirmationView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onWaitingForTransferTick(millisUntilFinished: Long){
        val totalSeconds = millisUntilFinished / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        layout().waitingForTransferTimerView.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds,
        )
        layout().waitingForTransferProgress.progress = millisUntilFinished.toInt()
    }

    private fun onWaitingForTransferEnd() {
        manager.waitingForTransferExpired()
    }


    override fun hideShelter() {
        super.hideShelter()
        waitingForTransferTimer.cancel()

    }

}