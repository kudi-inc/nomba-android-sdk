package com.nomba.wraith.ui.shelters

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import com.nomba.wraith.R
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.databinding.SaveCardOtpViewBinding
import java.util.Locale

class SaveCardOtpShelter(var manager: NombaManager, activitySaveCardOtpViewBinding: SaveCardOtpViewBinding) : Shelter(activitySaveCardOtpViewBinding) {

    override fun layout(): SaveCardOtpViewBinding {
        return super.layout() as SaveCardOtpViewBinding
    }

    private lateinit var waitingForResendTimer: CountDownTimer
    private val waitingForResendTime : Long = 60000

    override fun showShelter() {
        super.showShelter()
        layout().contentLabel.text = manager.activity.get()?.getString(R.string.save_opt_text, manager.otpPhoneNumber)
        manager.displayViewState = DisplayViewState.SAVE_CARD_OTP
        startTimer()
        setListeners()
    }

    private fun startTimer(){
        waitingForResendTimer = manager.utils.createTimer(waitingForResendTime, ::onWaitingForResendTick, ::onWaitingForResendEnd)
        waitingForResendTimer.start()
        layout().resendButton.isEnabled = false
    }
    private fun onWaitingForResendEnd(){
        layout().resendButton.text = "Resend OTP"
        layout().resendButton.isEnabled = true
    }

    private fun setListeners(){
        layout().resendButton.setOnClickListener {
            manager.requestOTPForCardSaving()
        }

        layout().changeNumberBtn.setOnClickListener {
manager.goBackToChangeNumberForSaveCardOtp()
        }

        layout().otpView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (layout().otpView.text!!.length == 4){

                    manager.submitOtpForCardSaving(layout().otpView.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun onWaitingForResendTick(millisUntilFinished: Long){
        val totalSeconds = millisUntilFinished / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val timeLeft  = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds,
        )

        layout().resendButton.text = manager.activity.get()?.getString(R.string.save_resend_otp, timeLeft)
    }

    override fun hideShelter() {
        super.hideShelter()
        if (this::waitingForResendTimer.isInitialized){
            waitingForResendTimer.cancel()
        }
    }


}