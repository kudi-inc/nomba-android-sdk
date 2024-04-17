package com.nomba.wraith.ui.shelters.card

import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.nomba.wraith.R
import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.core.api.models.CardObject
import com.nomba.wraith.core.enums.CardMonthYearError
import com.nomba.wraith.core.enums.DisplayViewState
import com.nomba.wraith.core.textwatchers.CardMonthYearTextWatcher
import com.nomba.wraith.core.textwatchers.CreditCardNumberFormattingTextWatcher
import com.nomba.wraith.core.textwatchers.ExpiryDateTextWatcher
import com.nomba.wraith.databinding.CardViewBinding
import java.time.YearMonth
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

class CardShelter(private var manager: NombaManager, activityCardViewBinding: CardViewBinding) : Shelter(activityCardViewBinding) {

    override fun layout(): CardViewBinding {
        return super.layout() as CardViewBinding
    }

    var expiryDateRight : Boolean = false

    override fun showShelter() {
        super.showShelter()
        setOnClickListeners()
        manager.displayViewState = DisplayViewState.CARD
        layout().payBtn.text = manager.activity.get()?.getString(
            R.string.pay_button_label,
            manager.doFormattingAmount()
        )
        layout().cardNumberText.text.clear()
        layout().expiryDateText.text.clear()
        layout().cvvText.text.clear()
    }


    private fun setOnClickListeners(){
        //Change Payment Button In Transfer View
        layout().cardChangePaymentMtdBtn.setOnClickListener {
            manager.changePaymentFromCard()
        }

        layout().cancelButton.setOnClickListener {
            manager.showExitDialog()
        }

        layout().cardNumberText.addTextChangedListener(CreditCardNumberFormattingTextWatcher(::checkIfFillsEmpty))
        layout().expiryDateText.addTextChangedListener(object : CardMonthYearTextWatcher(1000L){
            override fun onCardMonthYearErrorChanged(error: CardMonthYearError) {
                super.onCardMonthYearErrorChanged(error)
                expiryDateRight = error != CardMonthYearError.NONE
                checkIfFillsEmpty()
            }

            override fun onCardMonthYearCompleted() {
                super.onCardMonthYearCompleted()
                expiryDateRight = true
                checkIfFillsEmpty()
            }
        })
        layout().cvvText.addTextChangedListener {
            checkIfFillsEmpty()
        }
        layout().payBtn.setOnClickListener {
            val cardNumber = layout().cardNumberText.text.trim().replace("\\s".toRegex(), "")
            val cardDate = layout().expiryDateText.text
            val cvvText = layout().cvvText.text

            val monthYearFormatter = DateTimeFormatterBuilder()
                .appendPattern("MM/yy")
                .toFormatter(Locale.ENGLISH)

          try {
              val month = YearMonth.parse(cardDate, monthYearFormatter).month.value
              val year = YearMonth.parse(cardDate, monthYearFormatter).year
              manager.cardObject = CardObject(cardNumber, month.toString(), year.toString(), cvvText.toString(), "", layout().saveCardCheckbox.isChecked)
              manager.hideKeyboard()
              manager.showCardPinView()
          } catch (_: Exception){
              expiryDateRight = false
              checkIfFillsEmpty()
          }
        }

        layout().saveCardCheckbox.setOnCheckedChangeListener { _, isChecked ->
            manager.shouldSaveCard = isChecked
        }
    }

    private fun checkIfFillsEmpty(){
        println(layout().cardNumberText.text.length)
        if ((layout().cardNumberText.text.length == 23 || layout().cardNumberText.text.length == 19) && expiryDateRight && layout().cvvText.text.length == 3){
            layout().payBtn.alpha = 1.0F
            layout().payBtn.isEnabled = true
        } else {
            layout().payBtn.alpha = .5F
            layout().payBtn.isEnabled = false
        }
    }
}