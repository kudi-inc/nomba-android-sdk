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

        val button = findViewById<Button>(R.id.sample_pay_button)
        val main = findViewById<ConstraintLayout>(R.id.main)

        // initialise the Nomba Android SDK aka Wraith
        val nombaManager = NombaManager.getInstance(this, "293bb9a0-dc25-428d-8f63-d828b9420cd5", clientId = "2e43173e-3e69-4fa2-8168-f4fedbf9a962", clientKey = "GF3G1qY9f2TNZ64Jsin9QY4WJ5FnlCPyu23y716StxUsMR6jXNB0zcZHQEZ1avU1Y+CdgdrzW5zHefMlblXGmQ==", main)

        // set up payment values
        nombaManager.paymentAmount = 10.0
        nombaManager.customerEmail = "knightbenax@gmail.com"
        nombaManager.customerName = "Emeka Bond"

        // show the SDK when you want to make a purchase
        button.setOnClickListener {

            nombaManager.showPaymentView()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    // call this in your backstack management handling code,
                    // to enable the back button response to Wraith's DisplayStates
                    nombaManager.handleBackStack()
                }
            }


        onBackPressedDispatcher.addCallback(callback)
    }




}