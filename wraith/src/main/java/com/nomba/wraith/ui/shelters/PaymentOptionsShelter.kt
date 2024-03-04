package com.nomba.wraith.ui.shelters

import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.PaymentOptionsViewBinding

class PaymentOptionsShelter(private var activityPaymentOptionsViewBinding: PaymentOptionsViewBinding): Shelter(activityPaymentOptionsViewBinding) {

    override fun layout(): PaymentOptionsViewBinding {

        return super.layout() as PaymentOptionsViewBinding
    }

    init {

    }

}