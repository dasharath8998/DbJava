package com.dasharath.hatisamaj.ui.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
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
import com.dasharath.hatisamaj.utils.SharedPrefUtils
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

    val requestCodePersonDetail = 111
    val requestCodeFilter = 123
    var globalView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        globalView = inflater.inflate(R.layout.fragment_search, container, false)
        return globalView
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
        toolbar.tvTitle.text = "Search"
        if(SharedPrefUtils().getUserType(context!!) == CommonUtils.ADMIN)
            toolbar.radioPendingStatus.visibility = View.VISIBLE
        tempPeopleList = ArrayList()
    }

    private fun listener(view: View) {

        view.ivFilter.setOnClickListener {
            startActivityForResult(
                Intent(context, FilterActivity::class.java).putExtra(
                    "List",
                    filterList
                ), requestCodeFilter
            )
        }

        toolbar.radioPendingStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                adminStatus = true
                filterList.clear()
                getDataFirstTime(CommonUtils.PENDING_STATUS)
                context!!.toast("Pending")
            } else {
                adminStatus = false
                filterList.clear()
                getDataFirstTime(CommonUtils.PASS_STATUS)
                context!!.toast("Pass")
            }
        }

        toolbar.ivAddPeople.setOnClickListener {
            if (!adminStatus) {
                adminStatus = true
                filterList.clear()
                getDataFirstTime(CommonUtils.PENDING_STATUS)
                toolbar.ivAddPeople.setColorFilter(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorPrimary
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
                context!!.toast("Pending")
            } else {
                adminStatus = false
                filterList.clear()
                getDataFirstTime(CommonUtils.PASS_STATUS)
                toolbar.ivAddPeople.setColorFilter(
                    ContextCompat.getColor(context!!, R.color.dark),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
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
                    if (personDetailModel.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        if (!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if (personDetailModel.sName.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        if (!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if (personDetailModel.fName.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        if (!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if (personDetailModel.cl.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        if (!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if (personDetailModel.pr.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        if (!peopleList?.contains(personDetailModel)!!) {
                            peopleList?.add(personDetailModel)
                        }
                    }

                    if (personDetailModel.job_type.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        if (!peopleList?.contains(personDetailModel)!!) {
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
                    tempPeopleList = ArrayList()
                    for (id in peopleId) {
                        val data = id.toObject(PersonDetailModel::class.java)
                        peopleList?.add(data!!)
                    }
                    tempPeopleList?.addAll(peopleList!!)
                    Log.d("TAG", peopleList.toString())
                    if (this@SearchFragment.isVisible) setAdapter()

                } else {
                    globalView?.aviLoading?.hide()
                    if(peopleList == null){
                        peopleList = ArrayList()
                    } else {
                        peopleList?.clear()
                    }
                    if (this@SearchFragment.isVisible) setAdapter()
                    context?.toast("No data found")
                }
            }
            ?.addOnFailureListener {
                globalView?.aviLoading?.hide()
                if(peopleList == null){
                    peopleList = ArrayList()
                } else {
                    peopleList?.clear()
                }
                if (this@SearchFragment.isVisible) setAdapter()
                Log.d("Failed", "Failed SearchFragment : $it")
            }
    }

    private fun getDataByFilter(status: String) {
        aviLoading.show()
        db?.collection(CommonUtils.PEOPLE)?.whereIn(CommonUtils.REGISTER_AS, filterList)
            ?.whereEqualTo(CommonUtils.STATUS, status)
            ?.get()?.addOnSuccessListener {
                aviLoading.hide()
                if (!it.isEmpty) {
                    val peopleId = it.documents
                    peopleList = ArrayList()
                    tempPeopleList = ArrayList()
                    for (id in peopleId) {
                        val data = id.toObject(PersonDetailModel::class.java)
                        peopleList?.add(data!!)
                    }
                    tempPeopleList?.addAll(peopleList!!)
                    Log.d("TAG", peopleList.toString())
                    if (this@SearchFragment.isVisible) setAdapter()
                } else {
                    aviLoading.hide()
                    if(peopleList == null){
                        peopleList = ArrayList()
                    } else {
                        peopleList?.clear()
                    }
                    if (this@SearchFragment.isVisible) setAdapter()
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
                    CommonUtils.AS_SOCIAL_WORKER -> it.binding.tvRegisterAsSearch.text = "Social worker"
                    else -> it.binding.tvRegisterAsSearch.text = "Other"
                }
                aviLoading.hide()
            }

            onClick {
                val intent = Intent(activity, PersonInfoActivity::class.java)
                intent.putExtra(CommonUtils.PERSONAL, peopleList!![it.adapterPosition])
                startActivityForResult(intent, requestCodePersonDetail)
            }
        }.into(rvSearch)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeFilter) {
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
        if (requestCode == requestCodePersonDetail) {
            if (resultCode == Activity.RESULT_OK)
                if (adminStatus)
                    getDataFirstTime(CommonUtils.PENDING_STATUS)
                else
                    getDataFirstTime(CommonUtils.PASS_STATUS)

        }
    }
}
