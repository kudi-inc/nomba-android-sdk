package com.nomba.wraith.ui.shelters

import com.nomba.wraith.R
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.databinding.SaveCardSuccessViewBinding

class SaveCardSuccessShelter(private var manager: NombaManager, activitySaveCardSuccessViewBinding: SaveCardSuccessViewBinding) : Shelter(activitySaveCardSuccessViewBinding) {

    override fun layout(): SaveCardSuccessViewBinding {
        return super.layout() as SaveCardSuccessViewBinding
    }

    //@SuppressLint("StringFormatMatches")
    override fun showShelter() {
        super.showShelter()
        manager.displayViewState = DisplayViewState.SAVE_CARD_SUCCESS
        layout().successContentText.text = manager.activity.get()?.getString(R.string.your_card_has_been_securely_saved_and_it_will_be_avaliable_for_your_next_checkout_with_1_s_you_will_now_be_redirected_to_your_merchant_s_app_thank_you, manager.customerName)
        layout().closeCheckoutBtn.setOnClickListener {
            hideShelter()
            manager.hidePaymentView()
        }

    }


}