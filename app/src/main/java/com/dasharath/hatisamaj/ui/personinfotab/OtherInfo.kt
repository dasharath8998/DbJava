package com.dasharath.hatisamaj.ui.personinfotab


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_other_info.*

class OtherInfo : Fragment() {

    var category: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_other_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            category = arguments?.getString(CommonUtils.CATEGORY).toString()
        }
        if(category == CommonUtils.AS_EMPLOYEE){
            showEmployee()
        }
    }

    private fun showEmployee(){
        cardPercentage.hide()
        cardBusinessName.hide()
    }

    private fun View.hide(){
        visibility = View.GONE
    }
}
