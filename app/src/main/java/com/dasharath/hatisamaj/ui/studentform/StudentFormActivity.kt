package com.dasharath.hatisamaj.ui.studentform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import kotlinx.android.synthetic.main.activity_student_form.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import android.net.Uri
import java.io.File


class StudentFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_form)
        init()
        listeners()
    }

    @SuppressLint("SetTextI18n")
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

        ivUploadImage.setOnClickListener {
            val statusRead = ContextCompat.checkSelfPermission(this@StudentFormActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            val statusCamera = ContextCompat.checkSelfPermission(this@StudentFormActivity,android.Manifest.permission.CAMERA)
            if (statusRead == PackageManager.PERMISSION_GRANTED && statusCamera == PackageManager.PERMISSION_GRANTED) {
                setImage()
            }
            else {
                ActivityCompat.requestPermissions(this@StudentFormActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA), 101)
            }
        }

        btnStudentSubmit.setOnClickListener {
            if(isValid()) {
                Toast.makeText(this@StudentFormActivity, "Valid", Toast.LENGTH_LONG).show()
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

    private fun setImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    private fun isValid(): Boolean{
        if(spinnerStudiesType.selectedItem.toString().trim() == "Select"){
            toast("Please select your studies")
            return false
        }

        if(!etPercentage.checkTextValue(true)) return false

        if(linearOther.isVisible) if(!etOtherStudent.checkTextValue()) return false

        if(tvImageUpload.text.toString() == "Upload Here"){
            toast("Please upload your educational certificate")
            return false
        }
        return true
    }

    private fun EditText.checkTextValue(isNumberText: Boolean = false): Boolean{
        val value = this.text.toString()
        if(value == "") {
            this.error = "This field can't be blank"
            return false
        }

        if(isNumberText && value.toDouble() > 100){
            this.error = "Invalid"
            return false

        }
        return true
    }

    private fun hideAll() {
        linearOther.visibility = View.GONE
    }

    private fun showOther(){
        linearOther.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            setImage()
        } else {
            toast("Grant require permissions")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> {
                if (resultCode == Activity.RESULT_OK && data?.data != null) {
                    val selectedImage = data.data
                    val uri = Uri.parse(selectedImage.toString())
                    tvImageUpload.text = File("" + uri).name
                    ivUploadImage.setImageURI(selectedImage)
                }
            }
        }
    }
}

