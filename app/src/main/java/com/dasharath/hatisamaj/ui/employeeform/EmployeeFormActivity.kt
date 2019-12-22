package com.dasharath.hatisamaj.ui.employeeform

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import kotlinx.android.synthetic.main.activity_employee_form.*
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

        btnEmployeeSubmit.setOnClickListener {
            if(isValid()){
                toast("valid")
            }
        }

        btnEmployeeCancel.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
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

    private fun isValid(): Boolean{
        if(spinnerEmployeeType.selectedItem.toString().trim() == "Select"){
            toast("Please select your employment type")
            return false
        }

        if(linearOther.isVisible){
            if(!etOtherJobType.checkTextValue()) return false
        }
        if(!etEducation.checkTextValue()) return false
        if(!etDesignation.checkTextValue()) return false
        if(linearCompany.isVisible){
            if(!etCompanyName.checkTextValue()) return false
        }
        if(!etMNumber.checkTextValue()) return false


        return true
    }

    private fun EditText.checkTextValue(): Boolean{
        val value = text.toString()
        if(value == "") {
            error = "This field can't be blank"
            return false
        }
        return true
    }
}
