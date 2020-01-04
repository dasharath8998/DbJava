package com.dasharath.hatisamaj.ui.personinfotab


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_other_info.*


class OtherInfo : Fragment() {

    private var db: FirebaseFirestore? = null
    var personData: PersonDetailModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_other_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
    }

    private fun listeners() {
        spinnerStatus.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = parent?.getItemAtPosition(position).toString()
                if(status != "Select") {
                    db?.collection(CommonUtils.PEOPLE)?.document(personData?.doc_id!!)
                        ?.update(CommonUtils.STATUS, status)
                        ?.addOnSuccessListener {
                            context?.toast("Status changed")
                        }
                }
            }
        }
    }

    private fun init() {
        db = FirebaseFirestore.getInstance()
        personData = arguments?.getSerializable(CommonUtils.PERSONAL) as PersonDetailModel
        cardDegree.hideOrDisplayData(personData?.education!!, tvEducation)
        cardPercentage.hideOrDisplayData("",tvEducation)
        cardJobType.hideOrDisplayData(personData?.job_type!!, tvJobType)
        cardClass.hideOrDisplayData(personData?.job_class!!, tvClass)
        cardDesignation.hideOrDisplayData(personData?.designation!!, tvDesignation)
        cardCompanyName.hideOrDisplayData(personData?.company_name!!, tvCompanyName)
        cardBusinessName.hideOrDisplayData(personData?.business_name!!, tvBusinessName)
        cardOther.hideOrDisplayData(personData?.Other!!, tvOther)
        for (i in 0 until spinnerStatus.count) {
            if (spinnerStatus.getItemAtPosition(i).toString() == personData?.status){
                spinnerStatus.setSelection(i)
            }
        }

        if (personData?.business_detail!! != "") {
            cardOther.visibility = View.VISIBLE
            tvOther.text = personData?.business_detail
        }
    }

    private fun View.hideOrDisplayData(value: String, field: TextView){
        if(value != ""){
            field.text = value
        } else {
            visibility = View.GONE
        }
    }
}
