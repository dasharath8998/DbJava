package com.dasharath.hatisamaj.ui.business

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.view.isVisible
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_business_and_other_form.*
import kotlinx.android.synthetic.main.activity_business_and_other_form.aviLoading
import kotlinx.android.synthetic.main.activity_business_and_other_form.btnEmployeeSubmit
import kotlinx.android.synthetic.main.activity_business_and_other_form.etEducation
import kotlinx.android.synthetic.main.activity_business_and_other_form.toolbar
import kotlinx.android.synthetic.main.activity_employee_form.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class BusinessAndOtherFormActivity : AppCompatActivity(),ConnectivityReceiver.ConnectivityReceiverListener {

    var title: String = ""
    var personData: HashMap<String,String?>? = null

    var db: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    var currentUserId: String? = null
    var docId: String? = ""
    private var snackBar: Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_and_other_form)
        init()
        listener()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        toolbar.ivBack.visibility = View.VISIBLE
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        personData = intent.getSerializableExtra(CommonUtils.PERSONAL) as HashMap<String, String?>
        title = intent.getStringExtra(CommonUtils.TITLE)!!

        if(personData?.contains(CommonUtils.DOC_ID)!!){
            docId = personData!![CommonUtils.DOC_ID]
            etEducation.setText(personData!![CommonUtils.EDUCATION]).toString()
            etMNumberBusinessOther.setText(personData!![CommonUtils.MOBILE_NO]).toString()
            if(title == CommonUtils.OTHER){
                etOtherBusinessType.setText(personData!![CommonUtils.OTHER])
            } else {
                etBusinessName.setText(personData!![CommonUtils.BUSINESS_NAME])
                etOtherBusinessType.setText(personData!![CommonUtils.BUSINESS_DETAIL])
            }
        } else {
            docId = db?.collection(CommonUtils.PEOPLE)?.document()?.id.toString()
        }

        if (title == CommonUtils.OTHER) {
            linearBusinessName.visibility = View.GONE
            tvBusinessOrOther.setText(R.string.describe_yourself)
            toolbar.tvTitle.text = "Other Details"
        } else if(title == CommonUtils.AS_SOCIAL_WORKER){
            linearBusinessName.visibility = View.GONE
            toolbar.tvTitle.text = "Social Details"
            tvBusinessOrOther.text = "Describe your social jobs in brief"
        } else {
            toolbar.tvTitle.text = "Business Details"
        }
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

        toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun storeDataToDB() {

        personData?.put(CommonUtils.JOB_TYPE, "")
        personData?.put(CommonUtils.EDUCATION, etEducation.text.toString())
        personData?.put(CommonUtils.CLASS, "")
        personData?.put(CommonUtils.DESIGNATION, "")
        personData?.put(CommonUtils.COMPANY_NAME, "")
        personData?.put(CommonUtils.MOBILE_NO, etMNumberBusinessOther.text.toString())
        personData?.put(CommonUtils.UID, currentUserId)
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

        db?.collection(CommonUtils.PEOPLE)?.document(docId!!)?.set(personData!!)
            ?.addOnSuccessListener { documentReference ->
                aviLoading.hide()
                setResult(Activity.RESULT_OK, Intent())
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
                findViewById(R.id.mainBusiness),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            btnEmployeeSubmit.isEnabled = true
            snackBar?.dismiss()
        }
    }
}
