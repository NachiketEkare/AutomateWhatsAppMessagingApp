package com.example.testapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var etPhoneNumber: EditText
    private lateinit var etMessage: EditText
    private lateinit var btnSendMessage: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPhoneNumber = findViewById(R.id.CNEditTxt)
        etMessage = findViewById(R.id.editMsg)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        btnSendMessage.setOnClickListener {
            val phoneNumber = "+91" + etPhoneNumber.text.toString()
            val message = etMessage.text.toString()

            if (phoneNumber.isNotBlank() && message.isNotBlank()) {
                if (!isAccessibilityOn(this, WhatsappaccessibilityService::class.java)) {
                    showEnableAccessibilityServiceDialog()
                } else {
                    sendMessage(phoneNumber, message)
                    etPhoneNumber.text.clear()
                    etMessage.text.clear()
                }

            } else {
                Toast.makeText(
                    this,
                    "Please enter both phone number and message",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun showEnableAccessibilityServiceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enable Accessibility Service")
        builder.setMessage("Please enable the accessibility service to allow the app to send messages via WhatsApp.")
        builder.setPositiveButton("Enable") { _, _ ->
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun isAccessibilityOn(
        context: Context,
        clazz: Class<out WhatsappaccessibilityService?>
    ): Boolean {
        var accessibilityEnabled = 0
        val service = context.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (ignored: Settings.SettingNotFoundException) {
        }

        //string splitter to parse the list of enabled accessibility services
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            colonSplitter.setString(settingValue)
            while (colonSplitter.hasNext()) {
                val accessibilityService = colonSplitter.next()
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    private fun sendMessage(phoneNumber: String, message: String) {
        val uri =
            Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT)
                .show()
        }
    }
}