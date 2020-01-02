package com.dasharath.hatisamaj.ui.search


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemPersonInfoBinding
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.model.PersonModel
import com.dasharath.hatisamaj.ui.personinfo.PersonInfoActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class SearchFragment : Fragment() {

    var personList: ArrayList<PersonModel>? = null
    var peopleList: ArrayList<PersonDetailModel>? = null

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var userData: DocumentReference? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listener(view)
        db?.collection(CommonUtils.PEOPLE)?.get()?.addOnSuccessListener {
            if(!it.isEmpty){
                val peopleId = it.documents
                peopleList = ArrayList()
                for (id in peopleId){
                    val data = id.toObject(PersonDetailModel::class.java)
                    peopleList?.add(data!!)
                }
                Log.d("TAG",peopleList.toString())
                setAdapter(view)
            }
        }
        personList = ArrayList()
        for (i in 0..30){
            if(i % 2 == 0) {
                personList!!.add(PersonModel("", "Name $i", "1$i", "Male", "ABCD"))
            } else {
                personList!!.add(PersonModel("", "Name $i", "1$i", "Female", "ABCD"))
            }
        }
        if(this@SearchFragment.isVisible) {
        }
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth?.currentUser
        toolbar.ivAddPeople.visibility = View.VISIBLE
    }

    private fun listener(view: View) {
        view.ivFilter.setOnClickListener {
            Toast.makeText(context,"Filter Screen",Toast.LENGTH_LONG).show()
        }

        toolbar.ivAddPeople.setOnClickListener {

        }
    }

    private fun setAdapter(view: View) {
        view.rvSearch.layoutManager = GridLayoutManager(context,2)
        LastAdapter(peopleList!!, BR.personData).map<PersonDetailModel,ItemPersonInfoBinding>(R.layout.item_person_info){
            onBind {
//                if(personList!![it.adapterPosition].gender == "Male") {
//                    Glide.with(context!!).load(personList!![it.adapterPosition].image)
//                        .placeholder(R.drawable.ic_person_male).into(it.binding.ivPerson)
//                } else {
//                    Glide.with(context!!).load(personList!![it.adapterPosition].image)
//                        .placeholder(R.drawable.ic_person_female).into(it.binding.ivPerson)
//                }
            }

            onClick {
                if(it.adapterPosition % 2 == 0){
                    startActivity(Intent(activity, PersonInfoActivity::class.java).putExtra(CommonUtils.CATEGORY,CommonUtils.AS_BUSINESS))
                    return@onClick
                }
                if(it.adapterPosition % 3 == 0){
                    startActivity(Intent(activity,PersonInfoActivity::class.java).putExtra(CommonUtils.CATEGORY,CommonUtils.AS_EMPLOYEE))
                    return@onClick
                }
                if(it.adapterPosition % 4 == 0)
                    startActivity(Intent(activity,PersonInfoActivity::class.java).putExtra(CommonUtils.CATEGORY,CommonUtils.AS_STUDENT))
            }
        }.into(view.rvSearch)
    }

}
