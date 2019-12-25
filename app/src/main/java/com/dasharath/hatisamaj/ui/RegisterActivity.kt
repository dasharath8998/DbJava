package com.dasharath.hatisamaj.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dasharath.hatisamaj.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
        listeners()
    }

    private fun init() {

    }

    private fun listeners() {
        tvAlredyHaveAccountLink.setOnClickListener {
            onBackPressed()
        }
    }
}
