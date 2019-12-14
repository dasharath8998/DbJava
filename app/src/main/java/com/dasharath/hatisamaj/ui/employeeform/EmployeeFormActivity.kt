package com.dasharath.hatisamaj.ui.employeeform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_employee_form.*
import kotlinx.android.synthetic.main.activity_employee_form.linearOther
import kotlinx.android.synthetic.main.activity_employee_form.toolbar
import kotlinx.android.synthetic.main.activity_student_form.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class EmployeeFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_form)
        init()
        listeners()
    }

    private fun listeners() {
        spinnerEmployeeType.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(parent?.getItemAtPosition(position).toString()){
                    CommonUtils.OTHER -> showOther()
                    CommonUtils.GOVR_JOB -> showGoverment()
                    CommonUtils.SELF_EMPLOYEE -> showCompanyName()
                    CommonUtils.PRIVATE_JOB -> showCompanyName()
                    else -> hideAll()
                }
            }
        }

        btnEmployeeCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun init() {
        toolbar.tvTitle.text = "Employment Details"
    }

    private fun showCompanyName(){
        rgGClass.visibility = View.GONE
        linearOther.visibility =View.GONE
        linearCompany.visibility = View.VISIBLE
    }

    private fun showGoverment(){
        rgGClass.visibility = View.VISIBLE
        linearOther.visibility =View.GONE
        linearCompany.visibility = View.GONE
    }

    private fun showOther(){
        linearOther.visibility = View.VISIBLE
        rgGClass.visibility = View.GONE
        linearCompany.visibility = View.GONE
    }

    private fun hideAll(){
        linearOther.visibility = View.GONE
        rgGClass.visibility = View.GONE
        linearCompany.visibility = View.GONE
    }
}
