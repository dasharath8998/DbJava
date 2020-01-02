package com.dasharath.hatisamaj.ui.employeeform

import android.annotation.SuppressLint
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
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_employee_form.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class EmployeeFormActivity : AppCompatActivity() {

    var personData: HashMap<String,String?>? = null

    var db: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var classText: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_form)
        init()
        listeners()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        personData = intent.getSerializableExtra(CommonUtils.PERSONAL) as HashMap<String, String?>
        Log.d("personData",personData.toString())
        toolbar.tvTitle.text = "Employment Details"
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
                aviLoading.show()
                storeDataToDB()
            }
        }

        btnEmployeeCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun storeDataToDB() {
        if(rgGClass.isVisible) {
            val selectedId: Int = rgGClass.checkedRadioButtonId
            val selectedButton = findViewById<View>(selectedId) as RadioButton
            classText = selectedButton.text.toString()
        }

        personData?.put(CommonUtils.JOB_TYPE, spinnerEmployeeType.selectedItem.toString())
        personData?.put(CommonUtils.EDUCATION, etEducation.text.toString())
        personData?.put(CommonUtils.CLASS, classText)
        personData?.put(CommonUtils.DESIGNATION, etDesignation.text.toString())
        personData?.put(CommonUtils.COMPANY_NAME, etCompanyName.text.toString())
        personData?.put(CommonUtils.MOBILE_NO, etMNumber.text.toString())
        personData?.put(CommonUtils.BUSINESS_NAME, "")
        personData?.put(CommonUtils.BUSINESS_DETAIL, "")
        personData?.put(CommonUtils.UID, currentUserId)
        personData?.put(CommonUtils.OTHER, etOtherJobType.text.toString())

        db?.collection(CommonUtils.PEOPLE)?.add(personData!!)
            ?.addOnSuccessListener {
                aviLoading.hide()
                onBackPressed()
                toast("Detail submitted successfully")
            }
            ?.addOnFailureListener { e ->
                aviLoading.hide()
                Log.w("TAG", "Error adding document", e)
            }

//        db?.collection(CommonUtils.PEOPLE)?.document(currentUserId!!)?.collection(CommonUtils.REGISTER_PEOPLE)?.add(personData!!)
//            ?.addOnSuccessListener { documentReference ->
//                aviLoading.hide()
//                onBackPressed()
//                toast("Detail submitted successfully")
//                Log.d("TAG", "DocumentSnapshot added with $documentReference")
//            }
//            ?.addOnFailureListener { e ->
//                aviLoading.hide()
//                Log.w("TAG", "Error adding document", e)
//            }
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
