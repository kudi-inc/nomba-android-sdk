package com.nomba.wraith.ui.shelters.card

import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.databinding.CardLoadingViewBinding

class CardLoadingShelter(var manager: NombaManager, activityCardLoadingViewBinding: CardLoadingViewBinding) : Shelter(activityCardLoadingViewBinding) {

    override fun layout(): CardLoadingViewBinding {
        return super.layout() as CardLoadingViewBinding
    }

    override fun showShelter() {
        manager.displayViewState = DisplayViewState.CARD_LOADING
        super.showShelter()
    }


}