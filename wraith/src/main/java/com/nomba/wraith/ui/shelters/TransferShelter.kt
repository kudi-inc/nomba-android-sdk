package com.nomba.wraith.ui.shelters

import android.annotation.SuppressLint
import android.os.CountDownTimer
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.TransferViewBinding
import kotlin.reflect.KFunction1

class TransferShelter(activityTransferViewBinding: TransferViewBinding) : Shelter(activityTransferViewBinding) {

    private lateinit var waitingForTransferTimer: CountDownTimer
    private lateinit var confirmationTimer: CountDownTimer
    override fun layout(): TransferViewBinding {
        return super.layout() as TransferViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        waitingForTransferTimer = createTimer(1800000, ::onWaitingForTransferTick, ::onWaitingForTransferEnd)
        confirmationTimer = createTimer(1000, ::onConfirmationTransferTick, ::onConfirmationTransferEnd)
        waitingForTransferTimer.start()
    }

    @SuppressLint("SetTextI18n")
    private fun onWaitingForTransferTick(millisUntilFinished: Long){
        val totalSeconds = millisUntilFinished / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        layout().waitingForTransferTimerView.text = "$minutes:$seconds"
    }

    private fun onConfirmationTransferTick(millisUntilFinished: Long){

    }

    private fun onWaitingForTransferEnd() {

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