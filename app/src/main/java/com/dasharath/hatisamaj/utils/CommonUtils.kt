package com.dasharath.hatisamaj.utils

import android.app.DatePickerDialog
import android.content.Context
import android.text.format.DateFormat
import com.dasharath.hatisamaj.listeners.DateSetListener
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
}