package com.dasharath.hatisamaj.utils

import android.content.Context
import android.content.SharedPreferences


class SharedPrefUtils{
    fun storeUserType(context: Context,usetType: String){
        val editor: SharedPreferences.Editor = context.getSharedPreferences(CommonUtils.HATI_SAMAJ, Context.MODE_PRIVATE).edit()
        editor.putString(CommonUtils.USERTYPE, usetType)
        editor.apply()
    }

    fun getUserType(context: Context): String? {
        val pref  = context.getSharedPreferences(CommonUtils.HATI_SAMAJ, Context.MODE_PRIVATE)
        return pref.getString(CommonUtils.USERTYPE, CommonUtils.USER_PREF)
    }
}