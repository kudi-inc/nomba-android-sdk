package com.nomba.wraith.ui.shelters

import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.databinding.FailureViewBinding

class FailureShelter(var manager: NombaManager, activityFailureViewBinding: FailureViewBinding) : Shelter(activityFailureViewBinding)  {

    override fun layout(): FailureViewBinding {
        return super.layout() as FailureViewBinding
    }

    var failureMessage : String = ""
    override fun showShelter() {
        super.showShelter()
        manager.displayViewState = DisplayViewState.PAYMENT_FAILTURE
        layout().contentText.text = failureMessage

        layout().failureChangePaymentMtdBtn.setOnClickListener {
            manager.changePaymentMethodFromFailureShelter()
        }

        layout().tryAnotherCardBtn.setOnClickListener {
            manager.tryAnotherCardFromFailureShelter()
        }
    }
}