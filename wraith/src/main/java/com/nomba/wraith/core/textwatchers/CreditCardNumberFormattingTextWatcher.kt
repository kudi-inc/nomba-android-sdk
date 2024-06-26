package com.nomba.wraith.core.textwatchers

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher

class CreditCardNumberFormattingTextWatcher(var onTextChanged: () -> Unit) : TextWatcher {
    private var current = ""

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (s.toString() != current) {
            val userInput = s.toString().replace(nonDigits,"")
            if (userInput.length <= 19) {
                current = userInput.chunked(4).joinToString(" ")
                s.filters = arrayOfNulls<InputFilter>(0)
            }
            s.replace(0, s.length, current, 0, current.length)
        }
        onTextChanged()
    }

    companion object {
        private val nonDigits = Regex("[^\\d]")
    }
}