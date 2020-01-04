package com.dasharath.hatisamaj.ui.personinfotab


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_personal_info.*


class PersonalInfo : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        val personData = arguments?.getSerializable(CommonUtils.PERSONAL) as PersonDetailModel
        tvPersonName.text = "${personData.name} ${personData.fName} ${personData.sName}"
        tvPersonEmail.text = personData.email
        tvPersonBD.text = personData.age
        tvPersonGender.text = personData.gender
        tvPersonPR.text = personData.pr
        tvPersonCL.text = personData.cl
        tvPersonAddress.text = personData.address
    }


}
