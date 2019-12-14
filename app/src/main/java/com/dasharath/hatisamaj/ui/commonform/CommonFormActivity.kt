package com.dasharath.hatisamaj.ui.commonform

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.listeners.DateSetListener
import com.dasharath.hatisamaj.ui.business.BusinessAndOtherFormActivity
import com.dasharath.hatisamaj.ui.employeeform.EmployeeFormActivity
import com.dasharath.hatisamaj.ui.studentform.StudentFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import java.util.*
import java.util.regex.Pattern


class CommonFormActivity : AppCompatActivity() {

    var formTitle: String? = null
    var ps: Pattern? = null

    var cYear = 0
    var cMonth = 0
    var cDay = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_from)
        init()
        listeners()
    }

    private fun init() {
        formTitle = intent.getStringExtra(CommonUtils.TITLE)
        toolbar.tvTitle.text = formTitle
        ps = Pattern.compile("^[a-zA-Z ]+$")
        val c = Calendar.getInstance()
        cYear = c.get(Calendar.YEAR)
        cMonth = c.get(Calendar.MONTH)
        cDay = c.get(Calendar.DAY_OF_MONTH)
    }

    private fun listeners() {
        btnNextCommon.setOnClickListener {
            if(formIsValid()) {
                when (formTitle) {
                    CommonUtils.AS_STUDENT -> startActivity(Intent(this@CommonFormActivity, StudentFormActivity::class.java))
                    CommonUtils.AS_EMPLOYEE -> startActivity(Intent(this@CommonFormActivity, EmployeeFormActivity::class.java))
                    CommonUtils.AS_BUSINESS -> startActivity(Intent(this@CommonFormActivity, BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle))
                    CommonUtils.OTHER -> startActivity(Intent(this@CommonFormActivity, BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle))
                }
            }
        }

        etBirthday.setOnClickListener {
            openCalender()
        }

        btnReset.setOnClickListener {
            onBackPressed()
        }
    }

    private fun formIsValid(): Boolean{
        if(!etName.checkTextValue(true)) return false
        if(!etFName.checkTextValue(true)) return false
        if(!etSName.checkTextValue(true)) return false
        if(!etEmail.checkTextValue(checkForValidText = false, isEmail = true)) return false
        if(!etBirthday.checkTextValue()) return false
        if(!etPResidence.checkTextValue()) return false
        if(!etCLocationo.checkTextValue()) return false
        if(!etAddress.checkTextValue()) return false

        return true
    }

    private fun EditText.checkTextValue(checkForValidText: Boolean = false, isEmail: Boolean = false): Boolean{

        val value = this.text.toString()
        if(value == "") {
            this.error = "Enter value please"
            return false
        }

        if(checkForValidText){
            if(!ps?.matcher(value)?.matches()!!){
                this.error = "Enter valid text"
                return false
            }
        }

        if(isEmail){
            if(!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
                this.error = "Enter valid email"
                return false
            }
        }

        return true
    }

    private fun openCalender(){
        Utils.openCalender(this@CommonFormActivity,cYear,cMonth,cDay, object : DateSetListener {
            @SuppressLint("SetTextI18n")
            override fun onDateSelected(year: Int, month: Int, day: Int) {
                cYear=year
                cMonth=month
                cDay=day
                etBirthday.setText("${day}, ${month+1}, $year")
                tvYear.text= Utils.getAge(year,month,day)
            }
        })
    }

}
