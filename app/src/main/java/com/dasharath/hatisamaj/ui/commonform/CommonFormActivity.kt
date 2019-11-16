package com.dasharath.hatisamaj.ui.commonform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dasharath.hatisamaj.R
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class CommonFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_from)
        init()
    }

    private fun init() {
        toolbar.tvTitle.text = intent.getStringExtra("Title")
//        window.statusBarColor = ContextCompat.getColor(this@CommonFormActivity, R.color.gray_status)
    }
}
