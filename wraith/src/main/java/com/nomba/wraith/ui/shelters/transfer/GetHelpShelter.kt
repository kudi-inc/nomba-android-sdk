package com.nomba.wraith.ui.shelters.transfer

import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.GetHelpViewBinding

class GetHelpShelter(private var manager: NombaManager, activityGetHelpViewBinding: GetHelpViewBinding) : Shelter(activityGetHelpViewBinding) {

    override fun layout(): GetHelpViewBinding {
        return super.layout() as GetHelpViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        manager.displayViewState = DisplayViewState.GET_HELP
        layout().closeCheckoutBtn.setOnClickListener {
            hideShelter()
           manager.hidePaymentView()
        }

        layout().keepWaitingBtn.setOnClickListener {
            hideShelter()
            manager.showTransferConfirmationView()
        }
    }


    override fun hideShelter() {
        super.hideShelter()
    }

}