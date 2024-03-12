package com.nomba.wraith.core.textwatchers

import android.os.Handler
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import com.nomba.wraith.core.enums.CardMonthYearError
import com.nomba.wraith.core.textwatchers.CardMonthYearFormatter.SLASH
import java.util.Calendar

open class CardMonthYearTextWatcher(
    private val completedCheckDelayMills: Long = 1000L,
    //var onTextChanged: () -> Unit
) : TextWatcher {

    private var isChangingText = false
    private var beforeText = ""
    private var oldBeforeText = ""

    private val handler = Handler()
    private val runnable = Runnable {
        onCardMonthYearCompleted()
    }

    open fun onCardMonthYearErrorChanged(error: CardMonthYearError) {}

    open fun onCardMonthYearCompleted() {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        oldBeforeText = beforeText
        beforeText = s.toString()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!isChangingText) {
            isChangingText = true

            val formattedText = if (isDeleted(s)) {
                CardMonthYearFormatter.formatForDelete(s, beforeText)
            } else {
                CardMonthYearFormatter.formatForInsert(s)
            }

            if (formattedText != beforeText) {
                s.replace(0, s.length, formattedText)
                val error = CardMonthYearValidator.validateOnTextChanged(formattedText)
                onCardMonthYearErrorChanged(error)

                val cursorPos = CardMonthYearFormatter.calculateCursorPos(formattedText, beforeText, oldBeforeText)
                Selection.setSelection(s, cursorPos)

                checkEditCompleted(formattedText)
            }

            isChangingText = false
        }
    }

    private fun isDeleted(s: CharSequence) = s.length < beforeText.length

    private fun checkEditCompleted(formattedText: String) {
        handler.removeCallbacks(runnable)
        val error = CardMonthYearValidator.validateOnFocusChanged(formattedText)
        if (error == CardMonthYearError.NONE) {
            handler.postDelayed(runnable, completedCheckDelayMills)
        }
    }
}

object CardMonthYearFormatter {
    const val SLASH = "/"

    fun formatForInsert(monthYear: CharSequence): String {
        val checkedMonthYear = removeUnnecessarySlash(monthYear.digitsAndSlash())
        if (checkedMonthYear.isEmpty()) {
            return ""
        }

        val monthYearArray = checkedMonthYear.split(SLASH)

        return when (monthYearArray.size) {
            1 -> { // Slash is not included
                when (checkedMonthYear.length) {
                    1 -> {
                        "$checkedMonthYear$SLASH"
                    }
                    2 -> {
                        return if (checkedMonthYear.toInt() <= 12) {
                            "$checkedMonthYear$SLASH"
                        } else {
                            StringBuilder(checkedMonthYear).insert(1, SLASH).toString()
                        }
                    }
                    else -> {
                        val first2Chars = checkedMonthYear.substring(0, 2)
                        return if (first2Chars.toInt() <= 12) {
                            val year = checkedMonthYear.substring(2, (checkedMonthYear.length).coerceAtMost(4))
                            "$first2Chars$SLASH$year"
                        } else {
                            val month = checkedMonthYear.substring(0, 1)
                            val year = checkedMonthYear.substring(1, (checkedMonthYear.length).coerceAtMost(3))
                            "$month$SLASH$year"
                        }
                    }
                }
            }
            else -> { // Slash is included
                val month = monthYearArray.first()
                val year = monthYearArray.last()

                when (month.length) {
                    0 -> {
                        return if (year.isEmpty()) { // "/"
                            ""
                        } else {
                            val yearEndIndex = year.length.coerceAtMost(2)
                            return "$SLASH${year.substring(0, yearEndIndex)}"
                        }
                    }
                    1 -> {
                        val yearEndIndex = year.length.coerceAtMost(2)
                        return "$month$SLASH${year.substring(0, yearEndIndex)}"
                    }
                    2 -> {
                        return if (month.toInt() <= 12) {
                            val yearEndIndex = year.length.coerceAtMost(2)
                            "$month$SLASH${year.substring(0, yearEndIndex)}"
                        } else {
                            val modifiedMonth = month.substring(0, 1)
                            val extraMonth = month.substring(1, 2)
                            val modifiedYear = if (year.isEmpty()) extraMonth else year
                            val yearEndIndex = modifiedYear.length.coerceAtMost(2)
                            "$modifiedMonth$SLASH${modifiedYear.substring(0, yearEndIndex)}"
                        }
                    }
                    else -> {
                        val month2Chars = month.substring(0, 2)
                        return if (month2Chars.toInt() <= 12) {
                            val modifiedYear = month.substring(2, month.length) + year
                            val yearEndIndex = modifiedYear.length.coerceAtMost(2)
                            "$month2Chars$SLASH${modifiedYear.substring(0, yearEndIndex)}"
                        } else {
                            val modifiedMonth = month.substring(0, 1)
                            val yearEndIndex = year.length.coerceAtMost(2)
                            "$modifiedMonth$SLASH${year.substring(0, yearEndIndex)}"
                        }
                    }
                }
            }
        }
    }

    fun formatForDelete(monthYear: CharSequence, beforeText: String): String {
        if (monthYear.isEmpty() || monthYear.toString() == SLASH) {
            return ""
        }

        val isSlashDeleted = beforeText.contains(SLASH) && !monthYear.contains(SLASH)
        if (isSlashDeleted) {
            val indexBeforeSlash = (beforeText.indexOf(SLASH) - 1)
            return if (indexBeforeSlash < 0) {
                "$SLASH$monthYear"
            } else {
                val modified = monthYear.replaceRange(indexBeforeSlash, indexBeforeSlash + 1, SLASH).toString()
                if (modified == SLASH) {
                    ""
                } else {
                    modified
                }
            }
        }

        return monthYear.toString()
    }

    fun calculateCursorPos(formatted: CharSequence, originalAfter: CharSequence, originalBefore: CharSequence): Int {
        if (formatted.isEmpty()) {
            return 0
        }

        val isPasted = originalAfter.length - originalBefore.length > 1
        if (isPasted) {
            return formatted.length - 1
        }

        val originalCursorPos = getCurrentCursorPos(originalAfter, originalBefore)

        val isInserted = originalBefore.length < originalAfter.length
        if (isInserted) {
            val isSlashInserted = !originalAfter.contains(SLASH) && formatted.contains(SLASH)
            if (isSlashInserted) {
                val month = formatted.split(SLASH).first()
                return if (month == "0" || month == "1") {
                    formatted.indexOf(SLASH)
                } else {
                    formatted.length
                }
            }

            if (originalAfter[originalCursorPos].toString() == SLASH) {
                if (originalCursorPos == 0) {
                    return 0
                }
                return originalCursorPos
            }

            val originalSlashPos = originalAfter.indexOf(SLASH)
            val isMonthInserted = originalCursorPos < originalSlashPos
            val month = originalAfter.split(SLASH).first()
            if (isMonthInserted) {
                val year = originalAfter.split(SLASH).last()
                if (year.isEmpty()) {
                    if (month.toInt() <= 12) {
                        if (month == "0" || month == "1") {
                            return formatted.indexOf(SLASH)
                        }
                        return formatted.indexOf(SLASH) + 1
                    } else {
                        return formatted.indexOf(SLASH) + 2
                    }
                } else {
                    return originalCursorPos + 1
                }
            }

            return (originalCursorPos + 1).coerceAtMost(formatted.length)
        }

        val isDeleted = originalBefore.length > formatted.length
        if (isDeleted) {
            val isSlashModified = !originalAfter.contains(SLASH) && formatted.contains(SLASH)
            if (isSlashModified) {
                return formatted.indexOf(SLASH)
            }
            return originalCursorPos
        }

        return formatted.length
    }

    private fun getCurrentCursorPos(originalAfter: CharSequence, originalBefore: CharSequence): Int {
        if (originalAfter.length <= originalBefore.length) {
            for ((idx, c) in originalAfter.withIndex()) {
                if (c != originalBefore[idx]) {
                    return idx
                }
            }
            return originalAfter.length
        } else {
            for ((idx, c) in originalBefore.withIndex()) {
                if (c != originalAfter[idx]) {
                    return idx
                }
            }
            return originalAfter.length - 1
        }
    }

    private fun hasMultipleSlash(text: String): Boolean {
        val firstSlashIndex = text.indexOf(SLASH)
        val lastSlashIndex = text.lastIndexOf(SLASH)
        return firstSlashIndex >= 0 && lastSlashIndex >= 0 && firstSlashIndex != lastSlashIndex
    }

    private fun removeUnnecessarySlash(text: String): String {
        if (hasMultipleSlash(text)) {
            val firstSlashIndex = text.indexOf(SLASH)
            val modified = text.replace(SLASH, "")
            return StringBuilder(modified).insert(firstSlashIndex, SLASH).toString()
        }
        return text
    }
}

object CardMonthYearValidator {

    fun validateOnFocusChanged(monthYear: CharSequence): CardMonthYearError {
        val monthYearArray = monthYear.split(SLASH)
        if (monthYearArray.size == 1) {
            return CardMonthYearError.EMPTY
        }

        val month = monthYearArray.first()
        val year = monthYearArray.last()

        checkMonthYear(month, year)?.let {
            return it
        }

        return CardMonthYearError.NONE
    }

    fun validateOnTextChanged(monthYear: CharSequence): CardMonthYearError {
        val monthYearArray = monthYear.split(SLASH)
        if (monthYearArray.size == 2) {
            val month = monthYearArray.first()
            val year = monthYearArray.last()
            if (month.isNotEmpty() && year.isNotEmpty() && year.length == 2) {
                checkMonthYear(month, year)?.let {
                    return it
                }
            }
        }

        return CardMonthYearError.NONE
    }

    private fun checkMonthYear(month: String, year: String): CardMonthYearError? {
        if (month.isEmpty()) return CardMonthYearError.MONTH_REQUIRED
        val monthInt = month.toIntOrNull()
        if (monthInt == null || monthInt > 12 || monthInt <= 0) return CardMonthYearError.MONTH_INVALID

        if (year.isEmpty()) return CardMonthYearError.YEAR_REQUIRED

        val yearInt = year.toIntOrNull() ?: return CardMonthYearError.YEAR_INVALID

        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        if (yearInt > currentYear + 20) return CardMonthYearError.YEAR_OVER_20_YEARS_LATER
        if (yearInt < currentYear || (yearInt == currentYear && monthInt < currentMonth)) return CardMonthYearError.EXPIRED

        return null
    }
}

fun CharSequence.digits(): String {
    val builder = StringBuilder()
    for (c in this) {
        if (c.isDigit()) {
            builder.append(c)
        }
    }
    return builder.toString()
}

fun CharSequence.digitsAndSlash(): String {
    val builder = StringBuilder()
    for (c in this) {
        if (c.isDigit() || c == "/".toCharArray().first()) {
            builder.append(c)
        }
    }
    return builder.toString()
}