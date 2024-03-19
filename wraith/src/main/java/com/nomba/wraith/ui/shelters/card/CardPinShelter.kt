package com.nomba.wraith.ui.shelters.card

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.databinding.CardPinViewBinding


class CardPinShelter(private var manager: NombaManager, activityCardPinViewBinding: CardPinViewBinding) : Shelter(activityCardPinViewBinding) {

    override fun layout(): CardPinViewBinding {
        return super.layout() as CardPinViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        setOnClickListeners()
        manager.displayViewState = DisplayViewState.CARD_PIN
        layout().pinView.text?.clear()
        layout().pinView.requestFocus()
        manager.showKeyboard(layout().pinView)
    }

    private fun setOnClickListeners(){
        layout().pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (layout().pinView.text!!.length == 4){
                    manager.cardObject.cardPin = layout().pinView.text.toString()
                    manager.submitCardDetails()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}