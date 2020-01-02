package com.dasharath.hatisamaj.ui.commonform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.listeners.DateSetListener
import com.dasharath.hatisamaj.ui.business.BusinessAndOtherFormActivity
import com.dasharath.hatisamaj.ui.employeeform.EmployeeFormActivity
import com.dasharath.hatisamaj.ui.studentform.StudentFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap


class CommonFormActivity : AppCompatActivity() {

    var formTitle: String? = null
    var ps: Pattern? = null

    var cYear = 0
    var cMonth = 0
    var cDay = 0

    var db: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null
    private var userPfofileImagesRef: StorageReference? = null
    var currentUserId: String? = null
    var peopleImageUrl = ""

    var personalData: HashMap<String, String?>? = null
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
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        userPfofileImagesRef = FirebaseStorage.getInstance().reference.child(CommonUtils.PEOPLE_IMAGE)
    }

    private fun listeners() {
        btnNextCommon.setOnClickListener {
            if (formIsValid()) {
                storeFormData()
                navigateUserToAnotherForm()
            }
        }

        etBirthday.setOnClickListener {
            openCalender()
        }

        btnReset.setOnClickListener {
            onBackPressed()
        }

        btnCalender.setOnClickListener {
            openCalender()
        }

        imgProfile.setOnClickListener {
            val statusRead = ContextCompat.checkSelfPermission(this@CommonFormActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            val statusCamera = ContextCompat.checkSelfPermission(this@CommonFormActivity,android.Manifest.permission.CAMERA)
            if (statusRead == PackageManager.PERMISSION_GRANTED && statusCamera == PackageManager.PERMISSION_GRANTED) {
//                setImage()
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            }
            else {
                ActivityCompat.requestPermissions(this@CommonFormActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA), 101)
            }
        }
    }

    private fun storeFormData() {
         personalData = hashMapOf(
            CommonUtils.NAME to etName.text.toString(),
            CommonUtils.FNAME to etFName.text.toString(),
            CommonUtils.SNAME to etSName.text.toString(),
            CommonUtils.EMAIL to etEmail.text.toString(),
            CommonUtils.AGE to tvYear.text.toString(),
            CommonUtils.PR to etPResidence.text.toString(),
            CommonUtils.CL to etCLocationo.text.toString(),
            CommonUtils.ADDRESS to etAddress.text.toString(),
            CommonUtils.REGISTER_AS to formTitle,
            CommonUtils.STATUS to CommonUtils.PENDING,
            CommonUtils.IMAGE_URL to peopleImageUrl
        )

//

    }

//    private fun setImage(){
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, 1)
//    }

    private fun navigateUserToAnotherForm() {
        var i: Intent? = null
        when (formTitle) {
            CommonUtils.AS_STUDENT -> i = Intent(this@CommonFormActivity, StudentFormActivity::class.java)
            CommonUtils.AS_EMPLOYEE -> i = Intent(this@CommonFormActivity, EmployeeFormActivity::class.java)
            CommonUtils.AS_BUSINESS -> i =  Intent(this@CommonFormActivity,BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle)
            CommonUtils.OTHER -> i = Intent(this@CommonFormActivity, BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle)
        }
        startActivity(i?.putExtra(CommonUtils.PERSONAL,personalData))
        finish()
    }

    private fun formIsValid(): Boolean {
//        if(peopleImageUrl == "") {
//            toast("Please select image first")
//            return false
//        }
        if (!etName.checkTextValue(checkForValidText = true)) return false
        if (!etFName.checkTextValue(checkForValidText = true)) return false
        if (!etSName.checkTextValue(checkForValidText = true)) return false
        if (!etEmail.checkTextValue(isEmail = true)) return false
        if (!etBirthday.checkTextValue()) return false
        if (!etPResidence.checkTextValue()) return false
        if (!etCLocationo.checkTextValue()) return false
        if (!etAddress.checkTextValue()) return false

        return true
    }

    private fun EditText.checkTextValue(
        checkForValidText: Boolean = false,
        isEmail: Boolean = false
    ): Boolean {

        val value = this.text.toString()
        if (value == "") {
            this.error = "This field can't be blank"
            return false
        }

        if (checkForValidText) {
            if (!ps?.matcher(value)?.matches()!!) {
                this.error = "Enter valid text"
                return false
            }
        }

        if (isEmail) {
            if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                this.error = "Enter valid email"
                return false
            }
        }

        return true
    }

    private fun openCalender() {
        Utils.openCalender(this@CommonFormActivity, cYear, cMonth, cDay, object : DateSetListener {
            @SuppressLint("SetTextI18n")
            override fun onDateSelected(year: Int, month: Int, day: Int) {
                cYear = year
                cMonth = month
                cDay = day
                etBirthday.setText("${day}, ${month + 1}, $year")
                tvYear.text = Utils.getAge(year, month, day)
            }
        })
    }

    private fun putFileToFirebase(data: CropImage.ActivityResult) {
        val resultUri = data.uri
        imgProfile.setImageURI(resultUri)
        val filePath = userPfofileImagesRef?.child(System.currentTimeMillis().toString() + ".jpg")
        filePath?.putFile(resultUri!!)?.addOnCompleteListener {
            if (it.isSuccessful) {
                storeDownloadUrl(filePath)
            } else {
                aviLoading.hide()
                toast( "Error: " + it.exception.toString())
            }
        }
    }

    private fun storeDownloadUrl(filePath: StorageReference) {
        filePath.downloadUrl.addOnSuccessListener {
            aviLoading.hide()
            toast("Image uploaded successfully")
            peopleImageUrl = it.toString()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        } else {
            toast("Grant require permissions")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                aviLoading.show()
                putFileToFirebase(result)
            }
        }
    }

}
