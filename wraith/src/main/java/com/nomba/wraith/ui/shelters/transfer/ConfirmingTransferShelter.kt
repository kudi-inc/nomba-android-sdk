package com.nomba.wraith.ui.shelters.transfer

import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.nomba.wraith.R
import com.nomba.wraith.core.DisplayViewState
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
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
    }

    private fun onConfirmationTransferTick(millisUntilFinished: Long){
        val totalSeconds = millisUntilFinished / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        layout().waitingForConfirmationTimerView.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds,
        )

        layout().waitingForConfirmationProgress.progress = millisUntilFinished.toInt()
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

        layout().helpButton.visibility = View.GONE
        layout().buttonHolder.visibility = View.VISIBLE
    }

    private fun secondConfirmationEnded(){
        layout().timeElaspedLabel.text = manager.activity.get()?.getString(R.string.time_elapsed)
        layout().waitingForConfirmationProgress.progress = 0
        layout().waitingForConfirmationTimerView.visibility = View.GONE
    }

    override fun hideShelter() {
        super.hideShelter()
        confirmationTimer.cancel()
    }

}