package com.nomba.wraith.ui.shelters.card

import android.text.Editable
import android.text.TextWatcher
import androidx.viewbinding.ViewBinding
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.CardOtpViewBinding

class CardOTPShelter(var manager: NombaManager, activityCardOtpViewBinding: CardOtpViewBinding) : Shelter(activityCardOtpViewBinding) {

    override fun layout(): CardOtpViewBinding {
        return super.layout() as CardOtpViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        setOnClickListeners()
        layout().contentLabel.text = manager.otpMessage
    }

    private fun setOnClickListeners(){
        layout().otpView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (layout().otpView.text!!.length == 6){
                    manager.submitOTPDetails(layout().otpView.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

}