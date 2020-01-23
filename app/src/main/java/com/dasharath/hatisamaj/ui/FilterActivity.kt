package com.dasharath.hatisamaj.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class FilterActivity : AppCompatActivity() {

    private var filterList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        init()
        listeners()
    }

    private fun init() {
        toolbar.tvTitle.text = "Filters"
        toolbar.ivBack.visibility = View.VISIBLE
        filterList = intent.getStringArrayListExtra("List")
        if (filterList.contains(CommonUtils.AS_BUSINESS)) {
            checkboxBusiness.isChecked = true
        }
        if (filterList.contains(CommonUtils.AS_EMPLOYEE)) {
            checkboxEmployee.isChecked = true
        }
        if (filterList.contains(CommonUtils.AS_SOCIAL_WORKER)) {
            checkboxSocial.isChecked = true
        }
        if (filterList.contains(CommonUtils.OTHER)) {
            checkboxOther.isChecked = true
        }
    }

    private fun listeners() {

        toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        tvApply.setOnClickListener {
            checkboxEmployee.isCheckedOrNot(CommonUtils.AS_EMPLOYEE)
            checkboxBusiness.isCheckedOrNot(CommonUtils.AS_BUSINESS)
            checkboxSocial.isCheckedOrNot(CommonUtils.AS_SOCIAL_WORKER)
            checkboxOther.isCheckedOrNot(CommonUtils.OTHER)
            setReturnIntent()
        }

        tvClear.setOnClickListener {
            checkboxEmployee.isChecked = false
            checkboxBusiness.isChecked = false
            checkboxSocial.isChecked = false
            checkboxOther.isChecked = false
            filterList.clear()
            setReturnIntent()
        }
    }

    private fun setReturnIntent() {
        val intent = Intent()
        intent.putExtra("List", filterList)
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

    fun CheckBox.isCheckedOrNot(value: String) {
        if (isChecked) {
            if (!filterList.contains(value))
                filterList.add(value)
        } else
            filterList.remove(value)

    }
}
