package com.dasharath.hatisamaj.ui.business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import kotlinx.android.synthetic.main.activity_business_and_other_form.*
import kotlinx.android.synthetic.main.activity_business_and_other_form.toolbar
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

        btnEmployeeSubmit.setOnClickListener {
            if (isValidForm()) {
                toast("Valid")
            }
        }
    }

    private fun isValidForm(): Boolean {
        if (linearBusinessName.isVisible)
            if (!etBusinessName.checkTextValue()) return false

        if (!etEducation.checkTextValue()) return false
        if (!etOtherBusinessType.checkTextValue()) return false
        if (!etMNumberBusinessOther.checkTextValue()) return false
        return true
    }

    private fun init() {
        title = intent.getStringExtra(CommonUtils.TITLE)!!
        if (title == CommonUtils.OTHER) {
            linearBusinessName.visibility = View.GONE
            tvBusinessOrOther.setText(R.string.describe_yourself)
            toolbar.tvTitle.text = "Other Details"
        } else {
            toolbar.tvTitle.text = "Business Details"
        }
    }

    private fun EditText.checkTextValue(): Boolean {
        val value = text.toString()
        if (value == "") {
            error = "This field can't be blank"
            return false
        }
        return true
    }
}
