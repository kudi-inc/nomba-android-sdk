package com.nomba.wraith.ui.shelters.transfer

import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.SuccessTransferViewBinding
import com.nomba.wraith.R

class SuccessShelter(private var manager: NombaManager, activitySuccessTransferViewBinding: SuccessTransferViewBinding) : Shelter(activitySuccessTransferViewBinding) {

    override fun layout(): SuccessTransferViewBinding {
        return super.layout() as SuccessTransferViewBinding
    }

    //@SuppressLint("StringFormatMatches")
    override fun showShelter() {
        super.showShelter()
        layout().successContentText.text = manager.activity.get()?.getString(R.string.your_payment_of_80_400_to_kurukuru_sweets_has_been_confirmed_you_will_now_be_redirected_to_your_merchant_s_site_thank_you, manager.paymentAmount, manager.customerName)
    }

}