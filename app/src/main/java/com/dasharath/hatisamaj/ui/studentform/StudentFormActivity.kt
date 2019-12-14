package com.dasharath.hatisamaj.ui.studentform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.activity_student_form.*
import kotlinx.android.synthetic.main.activity_student_form.toolbar
import kotlinx.android.synthetic.main.toolbar_app.view.*


class StudentFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_form)
        init()
        listeners()
    }

    private fun init() {
        toolbar.tvTitle.text = "Educational Details"
    }

    private fun listeners() {
        spinnerStudiesType.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(parent?.getItemAtPosition(position).toString()){
                    CommonUtils.OTHER -> showOther()
                    else -> hideAll()
                }
            }
        }

        btnStudentCancel.setOnClickListener {
            onBackPressed()
        }

        rGroupPercentage.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbPercentage -> {
                    etPercentage.setHint(R.string.enter_your_percentage)
                }
                R.id.rbCGPA -> {
                    etPercentage.setHint(R.string.enter_your_CGPA)
                }
            }
        }
    }

    private fun hideAll() {
        linearOther.visibility = View.GONE
    }

    private fun showOther(){
        linearOther.visibility = View.VISIBLE
    }
}

