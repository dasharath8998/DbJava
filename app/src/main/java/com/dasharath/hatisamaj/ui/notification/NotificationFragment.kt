package com.dasharath.hatisamaj.ui.notification


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemNotificationBinding
import com.dasharath.hatisamaj.model.NotificationModel
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.ui.LoginActivity
import com.dasharath.hatisamaj.ui.RegisteredPeopleActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.view.*
import kotlinx.android.synthetic.main.toolbar_app.view.*

class NotificationFragment : Fragment() {

    var notificationList: ArrayList<NotificationModel>? = null
    var peopleList: ArrayList<PersonDetailModel>? = null

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var db: FirebaseFirestore? = null

    private val requestCodeRegistration = 123
    private var viewGlobal: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewGlobal = view
        init(view)
        listeners()
        notificationList = ArrayList()
        for (i in 0..20){
            if(i % 2 == 0) {
                notificationList!!.add(NotificationModel("", "", "Name Male $i", "This is description for male version and also test for multiline text so test test teest test test", "male"))
            } else {
                notificationList!!.add(NotificationModel("", "", "Name Female $i", "Female testing", "female"))
            }
        }
        if(this@NotificationFragment.isVisible) {
            setAdapter()
        }
    }

    private fun init(view: View) {
        peopleList = ArrayList()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth?.currentUser
        view.toolbar.tvTitle.text = "Activity"
        if(currentUser != null) {
            view.linearSignIn.visibility = View.GONE
            view.nestedNotification.visibility =  View.VISIBLE
            if (this@NotificationFragment.isVisible) getDataFirstTime(view)
        } else {
            view.linearSignIn.visibility = View.VISIBLE
            view.nestedNotification.visibility =  View.GONE
        }
    }

    private fun listeners() {

        btnAccountLogin.setOnClickListener {
            activity?.startActivity(Intent(activity, LoginActivity::class.java))
        }

        linearStatus.setOnClickListener {

            if(peopleList?.size == 0)
                context?.toast("Currently you don't have any registration")
            else {
                val intent = Intent(context,RegisteredPeopleActivity::class.java)
                intent.putExtra(CommonUtils.LIST,peopleList)
                startActivityForResult(intent,requestCodeRegistration)
            }

        }
    }


    private fun getDataFirstTime(view: View) {
        aviLoading.show()
        db?.collection(CommonUtils.PEOPLE)
            ?.whereEqualTo(CommonUtils.UID, currentUser?.uid.toString())?.get()
            ?.addOnSuccessListener {
                if (!it.isEmpty) {
                    peopleList?.clear()
                    val peopleId = it.documents
                    for (id in peopleId) {
                        val data = id.toObject(PersonDetailModel::class.java)
                        peopleList?.add(data!!)
                    }
                    Log.d("TAG", peopleList.toString())
                    view.tvNewMessage.text = peopleList?.size.toString()
                    view.aviLoading.hide()
                } else {
                    peopleList?.clear()
                    view.tvNewMessage.text = peopleList?.size.toString()
                    view.aviLoading.hide()
                    context?.toast("No data found")
                }
            }
            ?.addOnFailureListener {
                view.aviLoading.hide()
                Log.d("Failed", "Failed SearchFragment : $it")
            }
    }

    private fun setAdapter() {
        rvNotification.layoutManager = LinearLayoutManager(context)
        LastAdapter(notificationList!!, BR.notificationData).map<NotificationModel,ItemNotificationBinding>(R.layout.item_notification){
            onBind {
                if(notificationList!![it.adapterPosition].gender == "male") {
                    Glide.with(context!!).load(notificationList!![it.adapterPosition].imagePerson)
                        .placeholder(R.drawable.ic_person_male).into(it.binding.ivItemNotificationPerson)
                } else {
                    Glide.with(context!!).load(notificationList!![it.adapterPosition].imagePerson)
                        .placeholder(R.drawable.ic_person_female).into(it.binding.ivItemNotificationPerson)
                }

                val name = "<b>${notificationList!![it.adapterPosition].name}: </b> ${notificationList!![it.adapterPosition].description}"
                it.binding.tvNotificationDescription.text = HtmlCompat.fromHtml(name,HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }.into(rvNotification)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestCodeRegistration){
            if (resultCode == Activity.RESULT_OK) {
                getDataFirstTime(viewGlobal!!)
            }
        }
    }

}
