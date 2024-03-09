package com.nomba.wraith.ui.shelters

import com.google.android.material.snackbar.Snackbar
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.PaymentOptionsViewBinding

class PaymentOptionsShelter(private var manager: NombaManager, private var activityPaymentOptionsViewBinding: PaymentOptionsViewBinding): Shelter(activityPaymentOptionsViewBinding) {

    override fun layout(): PaymentOptionsViewBinding {
        return super.layout() as PaymentOptionsViewBinding

    }

    private fun setOnClickListeners(){
        //Change Payment Button In Transfer View
        layout().cancelButton.setOnClickListener {
            manager.hidePaymentView()
        }

        layout().payByTransferButton.setOnClickListener {
            manager.showTransferView()
        }
    }

    init {

    }

    override fun showShelter() {
        super.showShelter()
        setOnClickListeners()
    }



}