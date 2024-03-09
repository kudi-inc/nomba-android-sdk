package com.nomba.wraith.ui.shelters.transfer

import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import com.nomba.wraith.R
import com.nomba.wraith.core.DisplayViewState
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.ConfirmingTransferViewBinding
import com.nomba.wraith.databinding.GetHelpViewBinding
import java.util.Locale

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

        layout().tryAgainBtn.setOnClickListener {
            hideShelter()
        }
    }


    override fun hideShelter() {
        super.hideShelter()
    }

}