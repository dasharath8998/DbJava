package com.dasharath.hatisamaj.ui.business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.view.isVisible
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_business_and_other_form.*
import kotlinx.android.synthetic.main.activity_business_and_other_form.aviLoading
import kotlinx.android.synthetic.main.activity_business_and_other_form.btnEmployeeSubmit
import kotlinx.android.synthetic.main.activity_business_and_other_form.etEducation
import kotlinx.android.synthetic.main.activity_business_and_other_form.toolbar
import kotlinx.android.synthetic.main.activity_employee_form.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class BusinessAndOtherFormActivity : AppCompatActivity() {

    var title: String = ""
    var personData: HashMap<String,String?>? = null

    var db: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    var currentUserId: String? = null
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
                aviLoading.show()
                storeDataToDB()
            }
        }
    }

    private fun storeDataToDB() {

        val docId = db?.collection(CommonUtils.PEOPLE)?.document()?.id.toString()
        personData?.put(CommonUtils.JOB_TYPE, "")
        personData?.put(CommonUtils.EDUCATION, etEducation.text.toString())
        personData?.put(CommonUtils.CLASS, "")
        personData?.put(CommonUtils.DESIGNATION, "")
        personData?.put(CommonUtils.COMPANY_NAME, "")
        personData?.put(CommonUtils.MOBILE_NO, etMNumberBusinessOther.text.toString())
        personData?.put(CommonUtils.DOC_ID, docId)

        if (title == CommonUtils.OTHER) {
            personData?.put(CommonUtils.BUSINESS_NAME, "")
            personData?.put(CommonUtils.BUSINESS_DETAIL, "")
            personData?.put(CommonUtils.OTHER, etOtherBusinessType.text.toString())
        } else {
            personData?.put(CommonUtils.BUSINESS_NAME, etBusinessName.text.toString())
            personData?.put(CommonUtils.BUSINESS_DETAIL, etOtherBusinessType.text.toString())
            personData?.put(CommonUtils.OTHER, "")
        }

        db?.collection(CommonUtils.PEOPLE)?.document(docId)?.set(personData!!)
            ?.addOnSuccessListener { documentReference ->
                aviLoading.hide()
                onBackPressed()
                toast("Detail submitted successfully")
                Log.d("TAG", "DocumentSnapshot added with $documentReference")
            }
            ?.addOnFailureListener { e ->
                aviLoading.hide()
                Log.w("TAG", "Error adding document", e)
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
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        personData = intent.getSerializableExtra(CommonUtils.PERSONAL) as HashMap<String, String?>
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
