package com.nomba.wraith.ui.shelters

import com.nomba.wraith.R
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.databinding.SuccessViewBinding

class SuccessShelter(private var manager: NombaManager, activitySuccessTransferViewBinding: SuccessViewBinding) : Shelter(activitySuccessTransferViewBinding) {

    override fun layout(): SuccessViewBinding {
        return super.layout() as SuccessViewBinding
    }

    //@SuppressLint("StringFormatMatches")
    override fun showShelter() {
        super.showShelter()
        manager.displayViewState = DisplayViewState.PAYMENT_SUCCESS
        layout().successContentText.text = manager.activity.get()?.getString(R.string.your_payment_of_80_400_to_kurukuru_sweets_has_been_confirmed_you_will_now_be_redirected_to_your_merchant_s_site_thank_you, manager.doFormattingAmount(), manager.customerName)
        layout().closeCheckoutBtn.setOnClickListener {
            hideShelter()
            manager.hidePaymentView()
        }
    }

}