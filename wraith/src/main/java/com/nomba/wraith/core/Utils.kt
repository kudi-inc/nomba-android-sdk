package com.nomba.wraith.core

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ReplacementSpan


class Utils {
    fun createTimer(duration: Long, onTickFun: (millisUntilFinished: Long) -> Unit, onFinishFun: () -> Unit): CountDownTimer {
        return object: CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTickFun(millisUntilFinished)
            }

            override fun onFinish() {
                onFinishFun()
            }
        }
    }


}




