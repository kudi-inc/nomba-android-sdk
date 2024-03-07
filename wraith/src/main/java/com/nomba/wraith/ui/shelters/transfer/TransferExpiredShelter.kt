package com.nomba.wraith.ui.shelters.transfer

import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.TransferExpiredViewBinding


class TransferExpiredShelter(var manager : NombaManager, activityTransferExpiredViewBinding: TransferExpiredViewBinding) : Shelter(activityTransferExpiredViewBinding) {

    override fun layout(): TransferExpiredViewBinding {
        return super.layout() as TransferExpiredViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        layout().tryAgainBtn.setOnClickListener {
            manager.showTransferView()
        }

        layout().transferExpiredChangePaymentMtdBtn.setOnClickListener {

        }

        layout().sentMnyBtn.setOnClickListener {

        }
    }

}