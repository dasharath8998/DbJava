package com.dasharath.hatisamaj.utils

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.Toast
import com.dasharath.hatisamaj.listeners.DateSetListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


object CommonUtils {
    const val AS_STUDENT = "As a student"
    const val AS_EMPLOYEE = "As an employee"
    const val AS_BUSINESS = "As a businessman"
    const val OTHER = "Other"
    const val TITLE = "Title"
    const val GOVR_JOB = "Government Job"
    const val PRIVATE_JOB = "Private Job"
    const val SELF_EMPLOYEE = "Self Employee"
    const val CATEGORY = "Category"
    const val IS_FROM_LOGIN = "is_from_login"

    const val USER: String = "users"
    const val EMAIL: String = "email"
    const val PASS: String = "password"
    const val USERTYPE: String = "userType"
    const val NAME: String = "name"
    const val DEVICE_TOKEN = "device_token"

    const val PEOPLE = "people"
    const val REGISTER_PEOPLE = "register_people"
    const val PEOPLE_IMAGE = "people_image"
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
    const val PENDING = "pending"

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

    fun stringToDate(date: String, format: String): String{
        val simpleDate = SimpleDateFormat(format)
        val parseDate = simpleDate.parse(date)
        val day = Integer.parseInt(DateFormat.format("dd",parseDate).toString())
        val month = Integer.parseInt(DateFormat.format("mm",parseDate).toString())
        val year = Integer.parseInt(DateFormat.format("yyyy",parseDate).toString())
        return getAge(year,month-1,day)
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
}