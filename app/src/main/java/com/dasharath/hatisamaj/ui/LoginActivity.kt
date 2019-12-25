package com.dasharath.hatisamaj.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dasharath.hatisamaj.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        listeners()
    }

    private fun init() {

    }

    private fun listeners() {
        tvNeedNewAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
        }
        btnLoginButton.setOnClickListener {
            onBackPressed()
        }
    }
}
