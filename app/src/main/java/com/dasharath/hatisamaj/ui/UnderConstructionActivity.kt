package com.dasharath.hatisamaj.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dasharath.hatisamaj.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_under_constroctor.*

class UnderConstructionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_under_constroctor)
        listeners()

    }

    private fun listeners() {
        btnCloseApp.setOnClickListener {
            finish()
        }
    }
}
