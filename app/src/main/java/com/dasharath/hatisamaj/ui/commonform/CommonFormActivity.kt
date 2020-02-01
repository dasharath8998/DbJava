package com.dasharath.hatisamaj.ui.commonform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.listeners.DateSetListener
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.ui.business.BusinessAndOtherFormActivity
import com.dasharath.hatisamaj.ui.employeeform.EmployeeFormActivity
import com.dasharath.hatisamaj.ui.studentform.StudentFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.Utils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_common_from.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class CommonFormActivity : AppCompatActivity(),ConnectivityReceiver.ConnectivityReceiverListener {

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

    var personalData: HashMap<String, String?>? = hashMapOf()
    var isFromRegisterPeople = false
    var personData: PersonDetailModel? = null
    private var snackBar: Snackbar? = null

    var requestCodeForm = 123

    var firebaseStorage: FirebaseStorage? = null
    var oldImagePath: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_from)
        init()
        listeners()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun init() {
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth?.currentUser?.uid
        firebaseStorage = FirebaseStorage.getInstance()
        userPfofileImagesRef = FirebaseStorage.getInstance().reference.child(CommonUtils.PEOPLE_IMAGE)

        toolbar.ivBack.visibility = View.VISIBLE
        formTitle = intent.getStringExtra(CommonUtils.TITLE)
        isFromRegisterPeople = intent.getBooleanExtra(CommonUtils.IS_FROM_REGISTER_PEOPLE,false)

        if(isFromRegisterPeople) {
            initalizViews()
        }

        toolbar.tvTitle.text = formTitle
        ps = Pattern.compile("^[a-zA-Z ]+$")
        val c = Calendar.getInstance()
        cYear = c.get(Calendar.YEAR)
        cMonth = c.get(Calendar.MONTH)
        cDay = c.get(Calendar.DAY_OF_MONTH)

    }

    @SuppressLint("SimpleDateFormat")
    private fun initalizViews() {
        personData = intent.getSerializableExtra(CommonUtils.PERSON_DATA) as PersonDetailModel

        personalData?.put(CommonUtils.DOC_ID,personData?.doc_id)
        personalData?.put(CommonUtils.BUSINESS_DETAIL,personData?.business_detail)
        personalData?.put(CommonUtils.BUSINESS_NAME,personData?.business_name)
        personalData?.put(CommonUtils.OTHER,personData?.Other)
        personalData?.put(CommonUtils.MOBILE_NO,personData?.mobile_no)
        personalData?.put(CommonUtils.EDUCATION,personData?.education)
        personalData?.put(CommonUtils.CLASS,personData?.job_class)
        personalData?.put(CommonUtils.JOB_TYPE,personData?.job_type)
        personalData?.put(CommonUtils.DESIGNATION,personData?.designation)
        personalData?.put(CommonUtils.COMPANY_NAME,personData?.company_name)
        personalData?.put(CommonUtils.IMAGE_URL,personData?.image_url)

        etName.setText(personData?.name)
        etSName.setText(personData?.sName)
        etFName.setText(personData?.fName)
        etEmail.setText(personData?.email)
        etBirthday.setText(personData?.age)
        if (personData?.gender == "Male")
            rBMale.isChecked = true
        else
            rBFemale.isChecked = true
        etPResidence.setText(personData?.pr)
        etCLocationo.setText(personData?.cl)
        etAddress.setText(personData?.address)
        Glide.with(this@CommonFormActivity).load(personData?.image_url).into(imgProfile)

        val simpleDate = SimpleDateFormat("dd, mm, yyyy")
        val parseDate = simpleDate.parse(personData?.age!!)
        val day = Integer.parseInt(DateFormat.format("dd",parseDate).toString())
        val month = Integer.parseInt(DateFormat.format("mm",parseDate).toString())
        val year = Integer.parseInt(DateFormat.format("yyyy",parseDate).toString())
        tvYear.text = Utils.getAge(year, month-1, day)

        if(personData?.image_url!! != "")
            oldImagePath = firebaseStorage?.getReferenceFromUrl(personData?.image_url!!)
    }

    private fun listeners() {
        btnNextCommon.setOnClickListener {
            if (formIsValid()) {
                storeFormData()
                navigateUserToAnotherForm()
            } else {
                toast("Please fill require fields")
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
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            }
            else {
                ActivityCompat.requestPermissions(this@CommonFormActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA), 101)
            }
        }

        toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun storeFormData() {
        val selectedId: Int = rGroup.checkedRadioButtonId
        val selectedButton = findViewById<View>(selectedId) as RadioButton

        personalData?.put(CommonUtils.NAME,etName.text.toString())
        personalData?.put(CommonUtils.FNAME,etFName.text.toString())
        personalData?.put(CommonUtils.SNAME,etSName.text.toString())
        personalData?.put(CommonUtils.EMAIL,etEmail.text.toString())
        personalData?.put(CommonUtils.AGE,etBirthday.text.toString())
        personalData?.put(CommonUtils.GENDER,selectedButton.text.toString())
        personalData?.put(CommonUtils.PR,etPResidence.text.toString())
        personalData?.put(CommonUtils.CL,etCLocationo.text.toString())
        personalData?.put(CommonUtils.ADDRESS,etAddress.text.toString())
        personalData?.put(CommonUtils.REGISTER_AS,formTitle)
        personalData?.put(CommonUtils.STATUS,CommonUtils.PENDING)
        personalData?.put(CommonUtils.IMAGE_URL,peopleImageUrl)
    }

    private fun navigateUserToAnotherForm() {
        var i: Intent? = null
        when (formTitle) {
            CommonUtils.AS_STUDENT -> i = Intent(this@CommonFormActivity, StudentFormActivity::class.java)
            CommonUtils.AS_EMPLOYEE -> i = Intent(this@CommonFormActivity, EmployeeFormActivity::class.java)
            CommonUtils.AS_BUSINESS -> i =  Intent(this@CommonFormActivity,BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle)
            CommonUtils.AS_SOCIAL_WORKER -> i =  Intent(this@CommonFormActivity,BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle)
            CommonUtils.OTHER -> i = Intent(this@CommonFormActivity, BusinessAndOtherFormActivity::class.java).putExtra(CommonUtils.TITLE, formTitle)
        }
        i?.putExtra(CommonUtils.PERSONAL,personalData)
        startActivityForResult(i,requestCodeForm)
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

    private fun EditText.checkTextValue(checkForValidText: Boolean = false, isEmail: Boolean = false): Boolean {

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
                if(oldImagePath != null){
                    oldImagePath!!.delete().addOnSuccessListener {
                        setResult(Activity.RESULT_OK, Intent())
                    }
                }
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                findViewById(R.id.mainCommon),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                aviLoading.show()
                putFileToFirebase(result)
            }
        }

        if (requestCode == requestCodeForm) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK, Intent())
                onBackPressed()
            }
        }
    }

}
