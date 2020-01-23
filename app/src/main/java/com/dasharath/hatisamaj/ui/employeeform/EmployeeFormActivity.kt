package com.dasharath.hatisamaj.ui.employeeform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_employee_form.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class EmployeeFormActivity : AppCompatActivity(),
    ConnectivityReceiver.ConnectivityReceiverListener {

    var personData: HashMap<String, String?>? = null

    var db: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var classText: String? = ""
    var docId: String? = ""
    var jobType = ""

    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_form)
        init()
        listeners()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        toolbar.ivBack.visibility = View.VISIBLE
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        personData = intent.getSerializableExtra(CommonUtils.PERSONAL) as HashMap<String, String?>



        if (personData?.contains(CommonUtils.DOC_ID)!!) {
            docId = personData!![CommonUtils.DOC_ID]
            jobType = personData!![CommonUtils.JOB_TYPE].toString()
            initializViews()
        } else {
            docId = db?.collection(CommonUtils.PEOPLE)?.document()?.id.toString()
        }

        Log.d("personData", personData.toString())
        toolbar.tvTitle.text = "Employment Details"
    }

    private fun initializViews() {
        for (i in 0 until spinnerEmployeeType.count) {
            if (spinnerEmployeeType.getItemAtPosition(i).toString() == personData!![CommonUtils.JOB_TYPE]) {
                spinnerEmployeeType.setSelection(i)
            }
        }
        etOtherJobType.setText(personData!![CommonUtils.OTHER])
        if (personData!![CommonUtils.CLASS] == rbC1.text.toString()) rbC1.isChecked = true
        if (personData!![CommonUtils.CLASS] == rbC2.text.toString()) rbC2.isChecked = true
        if (personData!![CommonUtils.CLASS] == rbC3.text.toString()) rbC3.isChecked = true
        etCompanyName.setText(personData!![CommonUtils.COMPANY_NAME])

        etEducation.setText(personData!![CommonUtils.EDUCATION])
        etMNumber.setText(personData!![CommonUtils.MOBILE_NO])
        etDesignation.setText(personData!![CommonUtils.DESIGNATION])
    }

    private fun listeners() {

        toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        spinnerEmployeeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                jobType = parent?.getItemAtPosition(position).toString()
                when (jobType) {
                    CommonUtils.OTHER -> showOther()
                    CommonUtils.GOVR_JOB -> showGoverment()
                    CommonUtils.SELF_EMPLOYEE -> showCompanyName()
                    CommonUtils.PRIVATE_JOB -> showCompanyName()
                    else -> hideAll()
                }
            }
        }

        btnEmployeeSubmit.setOnClickListener {
            if (isValid()) {
                aviLoading.show()
                storeDataToDB()
            }
        }

        btnEmployeeCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun storeDataToDB() {
        if (rgGClass.isVisible) {
            val selectedId: Int = rgGClass.checkedRadioButtonId
            val selectedButton = findViewById<View>(selectedId) as RadioButton
            classText = selectedButton.text.toString()
        }

        personData?.put(CommonUtils.JOB_TYPE, jobType)
        personData?.put(CommonUtils.EDUCATION, etEducation.text.toString())
        personData?.put(CommonUtils.CLASS, classText)
        personData?.put(CommonUtils.DESIGNATION, etDesignation.text.toString())
        personData?.put(CommonUtils.COMPANY_NAME, etCompanyName.text.toString())
        personData?.put(CommonUtils.MOBILE_NO, etMNumber.text.toString())
        personData?.put(CommonUtils.BUSINESS_NAME, "")
        personData?.put(CommonUtils.BUSINESS_DETAIL, "")
        personData?.put(CommonUtils.UID, currentUserId)
        personData?.put(CommonUtils.DOC_ID, docId)
        personData?.put(CommonUtils.OTHER, etOtherJobType.text.toString())

        db?.collection(CommonUtils.PEOPLE)?.document(docId!!)?.set(personData!!)
            ?.addOnSuccessListener {
                aviLoading.hide()
                setResult(Activity.RESULT_OK, Intent())
                onBackPressed()
                toast("Detail submitted successfully")
            }
            ?.addOnFailureListener { e ->
                aviLoading.hide()
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun showCompanyName() {
        etOtherJobType.setText("")
        classText = ""

        rgGClass.visibility = View.GONE
        linearOther.visibility = View.GONE
        linearCompany.visibility = View.VISIBLE
    }

    private fun showGoverment() {
        etOtherJobType.setText("")
        etCompanyName.setText("")

        linearOther.visibility = View.GONE
        linearCompany.visibility = View.GONE
        rgGClass.visibility = View.VISIBLE
    }

    private fun showOther() {
        classText = ""
        etCompanyName.setText("")

        rgGClass.visibility = View.GONE
        linearCompany.visibility = View.GONE
        linearOther.visibility = View.VISIBLE
    }

    private fun hideAll() {
        classText = ""
        etCompanyName.setText("")
        etOtherJobType.setText("")

        linearOther.visibility = View.GONE
        rgGClass.visibility = View.GONE
        linearCompany.visibility = View.GONE
    }

    private fun isValid(): Boolean {
        if (spinnerEmployeeType.selectedItem.toString().trim() == "Select") {
            toast("Please select your employment type")
            return false
        }

        if (linearOther.isVisible) {
            if (!etOtherJobType.checkTextValue()) return false
        }
        if (!etEducation.checkTextValue()) return false
        if (!etDesignation.checkTextValue()) return false
        if (linearCompany.isVisible) {
            if (!etCompanyName.checkTextValue()) return false
        }
        if (!etMNumber.checkTextValue()) return false


        return true
    }

    private fun EditText.checkTextValue(): Boolean {
        val value = text.toString()
        if (value == "") {
            error = "This field can't be blank"
            return false
        }
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            btnEmployeeSubmit.isEnabled = false
            snackBar = Snackbar.make(
                findViewById(R.id.mainEmployee),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            if (!btnEmployeeSubmit.isEnabled)
                btnEmployeeSubmit.isEnabled = true
            snackBar?.dismiss()
        }
    }
}
