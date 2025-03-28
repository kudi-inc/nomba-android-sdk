package com.nomba.pilgrim

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nomba.wraith.core.NombaManager

class MainActivity : AppCompatActivity() {
    private  var  nombaManager:NombaManager?=null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val main = findViewById<ConstraintLayout>(R.id.main)
        val amount = findViewById<EditText>(R.id.amount)
        val email = findViewById<EditText>(R.id.email)
        val customer = findViewById<EditText>(R.id.name)
        val button = findViewById<Button>(R.id.sample_pay_button)
        val cancelButton = findViewById<Button>(R.id.close_pay_button)



        amount.setText("10.0")
        email.setText("emeka@bond.com")
        customer.setText("Emeka Bond")
        // initialise the Nomba Android SDK aka Wraith
        nombaManager = NombaManager.getInstance(this, "293bb9a0-dc25-428d-8f63-d828b9420cd5", clientId = "2e43173e-3e69-4fa2-8168-f4fedbf9a962",main)
        nombaManager?.paymentAmount = amount.text.toString().toDouble()
        nombaManager?.customerEmail = email.text.toString()
        nombaManager?.customerName = customer.text.toString()

        nombaManager?.showPaymentView()
        nombaManager?.transactionCallback = fun(response){
            val resultIntent = Intent()
//            val jsonString = Gson().toJson(response)
            resultIntent.putExtra("result", "jsonString")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        button.setOnClickListener {
            // set up payment values
            // initialise the Nomba Android SDK aka Wraith

            nombaManager?.showPaymentView()
        }

        cancelButton.setOnClickListener {
            val resultIntent = this.intent
            resultIntent.putExtra("result", "")
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            setResult(Activity.RESULT_CANCELED, resultIntent)
            finish()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    // call this in your backstack management handling code,
                    // to enable the back button response to Wraith's DisplayStates
                    nombaManager?.handleBackStack()
                }
            }


        onBackPressedDispatcher.addCallback(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (nombaManager!=null){
            nombaManager?.endInstance()
        }

    }


}