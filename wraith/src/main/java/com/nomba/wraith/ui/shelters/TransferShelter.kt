package com.nomba.wraith.ui.shelters

import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.TransferViewBinding

class TransferShelter(activityTransferViewBinding: TransferViewBinding) : Shelter(activityTransferViewBinding) {

    override fun layout(): TransferViewBinding {
        return super.layout() as TransferViewBinding
    }

}