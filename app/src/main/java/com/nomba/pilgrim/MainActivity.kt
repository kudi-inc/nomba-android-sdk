package com.nomba.pilgrim

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nomba.wraith.core.NombaManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val button = findViewById<Button>(R.id.sample_pay_button)
        val main = findViewById<ConstraintLayout>(R.id.main)
        val nombaManager = NombaManager.getInstance(this, "", main)
        nombaManager.paymentAmount = 69000.0
        nombaManager.customerEmail = "knightbenax@gmail.com"

        button.setOnClickListener {
            nombaManager.showPaymentView()
        }
    }


}