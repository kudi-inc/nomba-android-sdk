package com.nomba.wraith.ui.shelters.card

import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.AppCompatEditText
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.CardOtpViewBinding

class CardOTPShelter(var manager: NombaManager, activityCardOtpViewBinding: CardOtpViewBinding) : Shelter(activityCardOtpViewBinding) {

    override fun layout(): CardOtpViewBinding {
        return super.layout() as CardOtpViewBinding
    }

    override fun showShelter() {
        super.showShelter()
        setOnClickListeners()
        layout().contentLabel.text = manager.otpMessage
        layout().otpView.text?.clear()
    }

    private fun setOnClickListeners(){
        layout().otpView.disableCopyPaste()
        layout().otpView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (layout().otpView.text!!.length == 6){
                    manager.submitOTPDetails(layout().otpView.text.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun AppCompatEditText.disableCopyPaste() {
        isLongClickable = false
        setTextIsSelectable(false)
        customSelectionActionModeCallback = object : android.view.ActionMode.Callback {
            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(
                mode: android.view.ActionMode?,
                item: MenuItem?
            ): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {}
        }
    }

}