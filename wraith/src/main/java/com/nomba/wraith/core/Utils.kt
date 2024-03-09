package com.nomba.wraith.core

import android.os.CountDownTimer

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