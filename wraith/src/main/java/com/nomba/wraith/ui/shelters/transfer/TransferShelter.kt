package com.nomba.wraith.ui.shelters.transfer

import android.annotation.SuppressLint
import android.os.CountDownTimer
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.TransferViewBinding
import java.util.Locale

class TransferShelter(private var manager: NombaManager, activityTransferViewBinding: TransferViewBinding) : Shelter(activityTransferViewBinding) {

    private lateinit var waitingForTransferTimer: CountDownTimer
    private lateinit var confirmationTimer: CountDownTimer
    private val waitingForTransferTime : Long = 1800000
    private val confirmationTime : Long = 1800000
    override fun layout(): TransferViewBinding {
        return super.layout() as TransferViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        waitingForTransferTimer = createTimer(waitingForTransferTime, ::onWaitingForTransferTick, ::onWaitingForTransferEnd)
        confirmationTimer = createTimer(confirmationTime, ::onConfirmationTransferTick, ::onConfirmationTransferEnd)
        layout().waitingForTransferProgress.max = waitingForTransferTime.toInt()
        waitingForTransferTimer.start()

        //Change Payment Button In Transfer View
        layout().transferChangePaymentMtdBtn.setOnClickListener {
            manager.changePaymentFromTransfer()
        }

        layout().amountLabel.text = manager.formatPaymentAmount()
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

    private fun onConfirmationTransferTick(millisUntilFinished: Long){

    }

    private fun onWaitingForTransferEnd() {
        manager.waitingForTransferExpired()
    }

    private fun onConfirmationTransferEnd(){

    }

    private fun createTimer(duration: Long, onTickFun: (millisUntilFinished: Long) -> Unit, onFinishFun: () -> Unit): CountDownTimer{
        return object: CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTickFun(millisUntilFinished)
            }

            override fun onFinish() {
                onFinishFun()
            }
        }
    }


    override fun hideShelter() {
        super.hideShelter()
        waitingForTransferTimer.cancel()
        confirmationTimer.cancel()
    }

}