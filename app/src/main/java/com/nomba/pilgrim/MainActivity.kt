package com.nomba.pilgrim

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
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
        val nombaManager = NombaManager.getInstance(this, "293bb9a0-dc25-428d-8f63-d828b9420cd5", clientId = "2e43173e-3e69-4fa2-8168-f4fedbf9a962", clientKey = "GF3G1qY9f2TNZ64Jsin9QY4WJ5FnlCPyu23y716StxUsMR6jXNB0zcZHQEZ1avU1Y+CdgdrzW5zHefMlblXGmQ==", main)
        nombaManager.paymentAmount = 69000.0
        nombaManager.customerEmail = "knightbenax@gmail.com"

        button.setOnClickListener {
            nombaManager.showPaymentView()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    // Leave empty do disable back press or
                    // write your code which you want
                    nombaManager.handleBackStack()
                }
            }


        onBackPressedDispatcher.addCallback(callback)
    }




}