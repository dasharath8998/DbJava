package com.dasharath.hatisamaj.ui.search


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemPersonInfoBinding
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.ui.FilterActivity
import com.dasharath.hatisamaj.ui.personinfo.PersonInfoActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.getAgeDiff
import com.dasharath.hatisamaj.utils.Utils.toast
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class SearchFragment : Fragment() {

    var peopleList: ArrayList<PersonDetailModel>? = null
    var tempPeopleList: ArrayList<PersonDetailModel>? = null

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    var settings: FirebaseFirestoreSettings? = null

    var filterList = arrayListOf<String>()
    var adminStatus: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listener(view)
        getDataFirstTime(CommonUtils.PASS_STATUS)
    }

    private fun init() {
        settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        db?.firestoreSettings = settings!!
        currentUser = mAuth?.currentUser
        toolbar.ivAddPeople.visibility = View.VISIBLE
        tempPeopleList = ArrayList()
    }

    private fun listener(view: View) {
        view.ivFilter.setOnClickListener {
            startActivityForResult(
                Intent(context, FilterActivity::class.java).putExtra(
                    "List",
                    filterList
                ), 123
            )
        }

        toolbar.ivAddPeople.setOnClickListener {
            if (!adminStatus) {
                adminStatus = true
                filterList.clear()
                getDataFirstTime(CommonUtils.PENDING_STATUS)
                toolbar.ivAddPeople.setColorFilter(ContextCompat.getColor(context!!, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN)
                context!!.toast("Pending")
            } else {
                adminStatus = false
                filterList.clear()
                getDataFirstTime(CommonUtils.PASS_STATUS)
                toolbar.ivAddPeople.setColorFilter(ContextCompat.getColor(context!!, R.color.dark), android.graphics.PorterDuff.Mode.SRC_IN)
                context!!.toast("Pass")
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            }

            @SuppressLint("DefaultLocale")
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
                peopleList?.clear()
                tempPeopleList?.forEachIndexed { index, personDetailModel ->
                    if(personDetailModel.name.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        if(!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if(personDetailModel.sName.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        if(!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if(personDetailModel.fName.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        if(!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if(personDetailModel.cl.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        if(!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if(personDetailModel.pr.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        if(!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if(personDetailModel.job_type.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        if(!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }
                }
                rvSearch.adapter?.notifyDataSetChanged()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun getDataFirstTime(status: String) {
        aviLoading.show()
        db?.collection(CommonUtils.PEOPLE)
            ?.whereEqualTo(CommonUtils.STATUS, status)?.get()
            ?.addOnSuccessListener {
                if (!it.isEmpty) {
                    val peopleId = it.documents
                    peopleList = ArrayList()
                    for (id in peopleId) {
                        val data = id.toObject(PersonDetailModel::class.java)
                        peopleList?.add(data!!)
                    }
                    tempPeopleList?.addAll(peopleList!!)
                    Log.d("TAG", peopleList.toString())
                    if(this@SearchFragment.isVisible) setAdapter()

                } else {
                    aviLoading.hide()
                    context?.toast("No data found")
                }
            }
            ?.addOnFailureListener {
                aviLoading.hide()
                Log.d("Failed", "Failed SearchFragment : ${it.toString()}")
            }
    }

    private fun getDataByFilter(status: String) {
        aviLoading.show()
        db?.collection(CommonUtils.PEOPLE)
            ?.whereIn(CommonUtils.REGISTER_AS, filterList)
            ?.whereEqualTo(CommonUtils.STATUS,status)
            ?.get()
            ?.addOnSuccessListener {
                aviLoading.hide()
                if (!it.isEmpty) {
                    val peopleId = it.documents
                    peopleList = ArrayList()
                    for (id in peopleId) {
                        val data = id.toObject(PersonDetailModel::class.java)
                        peopleList?.add(data!!)
                    }
                    tempPeopleList?.addAll(peopleList!!)
                    Log.d("TAG", peopleList.toString())
                    if(this@SearchFragment.isVisible) setAdapter()
                } else {
                    aviLoading.hide()
                    context?.toast("No data found")
                }
            }
            ?.addOnFailureListener {
                aviLoading.hide()
                Log.d("Failed", "Failed SearchFragment : ${it.toString()}")
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setAdapter() {
        rvSearch.layoutManager = GridLayoutManager(context, 2)
        LastAdapter(peopleList!!, BR.personData).map<PersonDetailModel, ItemPersonInfoBinding>(R.layout.item_person_info) {
            onBind {
                val currentItem = peopleList?.get(it.adapterPosition)

                it.binding.tvAgeSearch.text = getAgeDiff(currentItem?.age!!)

                when (currentItem.register_as) {
                    CommonUtils.AS_EMPLOYEE -> it.binding.tvRegisterAsSearch.text = "Employee"
                    CommonUtils.AS_BUSINESS -> it.binding.tvRegisterAsSearch.text = "Business"
                    else -> it.binding.tvRegisterAsSearch.text = "Other"
                }
                aviLoading.hide()
            }

            onClick {
                startActivity(
                    Intent(
                        activity,
                        PersonInfoActivity::class.java
                    ).putExtra(CommonUtils.PERSONAL, peopleList!![it.adapterPosition])
                )
            }
        }.into(rvSearch)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                filterList = data?.getStringArrayListExtra("List")!!
                if (filterList.size != 0)
                    if (adminStatus)
                        getDataByFilter(CommonUtils.PENDING_STATUS)
                    else
                        getDataByFilter(CommonUtils.PASS_STATUS)
                else
                    if (adminStatus)
                        getDataFirstTime(CommonUtils.PENDING_STATUS)
                    else
                        getDataFirstTime(CommonUtils.PASS_STATUS)

            }
        }
    }
}
