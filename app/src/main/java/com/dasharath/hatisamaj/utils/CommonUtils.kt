package com.dasharath.hatisamaj.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.Toast
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.listeners.DateSetListener
import com.dasharath.hatisamaj.ui.LoginActivity
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


object CommonUtils {
    const val AS_STUDENT = "As a student"
    const val AS_EMPLOYEE = "As an employee"
    const val AS_BUSINESS = "As a business"
    const val AS_SOCIAL_WORKER = "As a social worker"
    const val OTHER = "Other"
    const val TITLE = "Title"
    const val GOVR_JOB = "Government Job"
    const val PRIVATE_JOB = "Private Job"
    const val SELF_EMPLOYEE = "Self Employee"
    const val CATEGORY = "Category"
    const val IS_FROM_LOGIN = "is_from_login"

    const val USER: String = "users"
    const val USER_PREF: String = "user"
    const val EMAIL: String = "email"
    const val PASS: String = "password"
    const val USERTYPE: String = "userType"
    const val NAME: String = "name"
    const val DEVICE_TOKEN = "device_token"

    const val PEOPLE = "people"
    const val REGISTER_PEOPLE = "register_people"
    const val PEOPLE_IMAGE = "people_image"
    const val POST_IMAGE = "post_image"
    //personal
    const val IMAGE_URL = "image_url"
    const val REGISTER_AS = "register_as"
    const val PERSONAL = "personal"
    const val SNAME = "sName"
    const val FNAME = "fName"
    const val AGE = "age"
    const val GENDER = "gender"
    const val PR = "pr"
    const val CL = "cl"
    const val ADDRESS = "address"
    const val STATUS = "status"
    const val PENDING = "Pending"

    //other
    const val EDUCATION = "education"
    const val PERCENTAGE = "percentage"
    const val JOB_TYPE = "job_type"
    const val CLASS = "job_class"
    const val DESIGNATION = "designation"
    const val COMPANY_NAME = "company_name"
    const val BUSINESS_NAME = "business_name"
    const val BUSINESS_DETAIL = "business_detail"
    const val MOBILE_NO = "mobile_no"
    const val UID = "uid"
    const val DOC_ID = "doc_id"

    const val PASS_STATUS = "Pass"
    const val PENDING_STATUS = "Pending"
    const val REJECTED = "Rejected"
    const val LIST = "List"
    const val IS_FROM_REGISTER_PEOPLE = "isFromRegisterPeople"
    const val PERSON_DATA = "personData"
    const val DATA = "Data"
    const val IS_CHANGED = "is_changed"

    const val POSTS = "posts"
    const val POST_DATE = "post_date"
    const val POST_TIME = "post_time"
    const val DESCRIPTION = "description"
    const val LIKE_COUNT = "like_count"
    const val LIKED_USER = "liked_user"

    const val ADMIN = "admin"
    const val HATI_SAMAJ = "hati_samaj"

    const val UTILS = "utils"
    const val APP_RUNNIG = "appRunning"
    const val APP_VERSION = "appVersion"
    const val IS_FORCED = "isForced"
    const val UPDATE_MESSAGE = "updateMessage"

    const val POST_URL = "post_url"
    const val URL = "url"
    const val IS_FROM_URL = "is_from_url"
}

object Utils{
    fun openCalender(context: Context, cYear: Int, cMonth: Int, cDay: Int, dateSelectListener: DateSetListener){
        val dpd = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateSelectListener.onDateSelected(year,monthOfYear,dayOfMonth)
        },cYear,cMonth,cDay)

        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    fun getAge(year:Int,monthOfYear:Int,dayOfMonth:Int):String{

        val c = Calendar.getInstance()
        val cYear = c.get(Calendar.YEAR)
        val cMonth = c.get(Calendar.MONTH)
        val cDay = c.get(Calendar.DAY_OF_MONTH)
        var age:String=""
        if(cMonth < monthOfYear) {

            age = "${cYear - year - 1} Years, "
            if(dayOfMonth < cDay) {
                age+= (12 - (monthOfYear - cMonth)).toString() + " Months, "
                age+= "${cDay - dayOfMonth} Days"
            }else{
                age+= (11 - (monthOfYear - cMonth)).toString() + " Months, "
                age+= "${31 - (dayOfMonth - cDay)} Days"
            }

        }else if(cMonth > monthOfYear) {

            age+= (cYear - year).toString() + " Years, "
            if(dayOfMonth < cDay) {
                age+= (cMonth - monthOfYear).toString() + " Months, "
                age+= "${(cDay - dayOfMonth)} Days"
            }else{
                age+= (cMonth - monthOfYear - 1).toString() + " Months, "
                age+= "${31 - (dayOfMonth - cDay)} Days"
            }

        }else{
            age+= (cYear - year).toString() + " Years, "

            if(dayOfMonth < cDay) {
                age+= "0 Months, "
                age+= "${(cDay - dayOfMonth)} Days"
            }else{
                age+="11 Months, "
                age+= "${31 - (dayOfMonth - cDay)} Days"
            }
        }
        return age
    }

    fun getAgeDiff(date: String): String{
        val simpleDate = SimpleDateFormat("dd, mm, yyyy")
        val parseDate = simpleDate.parse(date)
        val day = Integer.parseInt(DateFormat.format("dd",parseDate).toString())
        val month = Integer.parseInt(DateFormat.format("mm",parseDate).toString())
        val year = Integer.parseInt(DateFormat.format("yyyy",parseDate).toString())

        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return "${age+1} years old"

//        return getAge(year,month-1,day)
    }

    fun g(year: Int, month: Int, day: Int): String {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        val ageInt = age + 1

        return ageInt.toString()
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun Context.toast(value: String){
        Toast.makeText(this,value,Toast.LENGTH_LONG).show()
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
    }

    fun showAlert(context: Context, msg: String){
        AlertDialog.Builder(context)
            .setTitle("Hati samaj")
            .setMessage(msg)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    fun sendVerificationLink(context: Context,mAuth: FirebaseAuth) {
        mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                AlertDialog.Builder(context)
                    .setTitle("Hati kshtriya samaj")
                    .setMessage("Email verification link send to your mail please verify than login")
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                context.toast("Something went wrong please try again later")
            }
        }
    }
}