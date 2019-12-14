package com.dasharath.hatisamaj.ui.business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_business_and_other_form.*
import kotlinx.android.synthetic.main.activity_business_and_other_form.toolbar
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class BusinessAndOtherFormActivity : AppCompatActivity() {

    var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_and_other_form)
        init()
        listener()
    }

    private fun listener() {
        btnOtherCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun init() {
        title = intent.getStringExtra(CommonUtils.TITLE)
        if(title == CommonUtils.OTHER){
            linearBusinessName.visibility = View.GONE
            tvBusinessOrOther.setText(R.string.describe_yourself)
            toolbar.tvTitle.text = "Other Details"
        } else {
            toolbar.tvTitle.text = "Business Details"
        }
    }
}
