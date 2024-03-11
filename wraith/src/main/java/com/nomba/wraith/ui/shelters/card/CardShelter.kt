package com.nomba.wraith.ui.shelters.card

import com.nomba.wraith.core.CreditCardNumberFormattingTextWatcher
import com.nomba.wraith.core.ExpiryDateTextWatcher
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.CardViewBinding

class CardShelter(var manager: NombaManager, activityCardViewBinding: CardViewBinding) : Shelter(activityCardViewBinding) {

    override fun layout(): CardViewBinding {
        return super.layout() as CardViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        layout().cardNumberText.addTextChangedListener(CreditCardNumberFormattingTextWatcher())
        layout().expiryDateText.addTextChangedListener(ExpiryDateTextWatcher())
    }
}