package com.dasharath.hatisamaj.ui.commonform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.ui.business.BusinessAndOtherFormActivity
import com.dasharath.hatisamaj.ui.employeeform.EmployeeFormActivity
import com.dasharath.hatisamaj.ui.studentform.StudentFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class CommonFormActivity : AppCompatActivity() {

    var formTitle: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_from)
        init()
        onClick()
    }

    private fun onClick() {
        btnNextCommon.setOnClickListener {
            when (formTitle) {
                CommonUtils.AS_STUDENT -> startActivity(Intent(this@CommonFormActivity,StudentFormActivity::class.java))
                CommonUtils.AS_EMPLOYEE -> startActivity(Intent(this@CommonFormActivity,EmployeeFormActivity::class.java))
                CommonUtils.AS_BUSINESS -> startActivity(Intent(this@CommonFormActivity,BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE,formTitle))
            }
        }
    }

    private fun init() {
        formTitle = intent.getStringExtra("Title")
        toolbar.tvTitle.text = formTitle
    }
}
