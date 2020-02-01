package com.dasharath.hatisamaj.ui.notification


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.CreatePostActivity
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemNotificationBinding
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.model.URLPostModel
import com.dasharath.hatisamaj.ui.LoginActivity
import com.dasharath.hatisamaj.ui.RegisteredPeopleActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.SharedPrefUtils
import com.dasharath.hatisamaj.utils.Utils.toast
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.view.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import java.text.SimpleDateFormat


class NotificationFragment : Fragment() {

    var peopleList: ArrayList<PersonDetailModel>? = null
    var urlPostList: ArrayList<URLPostModel>? = null

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var db: FirebaseFirestore? = null

    private val requestCodeRegistration = 123
    private var viewGlobal: View? = null

    val format = SimpleDateFormat("hh:mm a - MMM d")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewGlobal = view
        init(view)
        listeners()
    }

    private fun init(view: View) {
        peopleList = ArrayList()
        urlPostList = ArrayList()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth?.currentUser
        view.toolbar.tvTitle.text = "Activity"
        if(currentUser != null){
            if (this@NotificationFragment.isVisible) getDataFirstTime(view)
        }
        if(this@NotificationFragment.isVisible) getPostData(view)
        if (SharedPrefUtils().getUserType(context!!) == CommonUtils.ADMIN)
            view.toolbar.ivAddPeople.visibility = View.VISIBLE
    }

    private fun listeners() {

        linearStatus.setOnClickListener {

            if (currentUser == null) {
                activity?.startActivity(Intent(activity, LoginActivity::class.java))
                return@setOnClickListener
            }

            if (peopleList?.size == 0)
                context?.toast("Currently you don't have any registration")
            else {
                val intent = Intent(context, RegisteredPeopleActivity::class.java)
                intent.putExtra(CommonUtils.LIST, peopleList)
                startActivityForResult(intent, requestCodeRegistration)
            }

        }

        toolbar.ivAddPeople.setOnClickListener {
            val intent = Intent(context!!, CreatePostActivity::class.java)
            intent.putExtra(CommonUtils.IS_FROM_URL, true)
            startActivityForResult(intent, requestCodeRegistration)
        }
    }


    private fun getDataFirstTime(view: View) {
        view.aviLoading.show()
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
                    context?.toast("No registration found")
                }
            }
            ?.addOnFailureListener {
                view.aviLoading.hide()
                Log.d("Failed", "Failed SearchFragment : $it")
            }
    }

    private fun getPostData(view: View) {
        view.aviLoading.show()
        db?.collection(CommonUtils.POST_URL)?.get()?.addOnSuccessListener {
            urlPostList?.clear()
            val postId = it.documents
            for (id in postId) {
                val data = id.toObject(URLPostModel::class.java)
                data?.postDate = format.parse(data?.post_date)
                urlPostList?.add(data!!)
            }

            setAdapter()
        }
    }

    private fun setAdapter() {
        if(viewGlobal?.aviLoading?.isVisible!!){
            viewGlobal?.aviLoading?.hide()
        }
        urlPostList?.sortByDescending { it.postDate }

        viewGlobal?.rvNotification?.layoutManager = LinearLayoutManager(context)
        LastAdapter(
            urlPostList!!,
            BR.urlPost
        ).map<URLPostModel, ItemNotificationBinding>(R.layout.item_notification) {
            onBind {
                val currentItem = urlPostList!![it.adapterPosition]

                if (SharedPrefUtils().getUserType(context!!) == CommonUtils.ADMIN)
                    it.binding.imgDeletePost.visibility = View.VISIBLE

                it.binding.imgDeletePost.setOnClickListener {
                    showDeleteDialog(currentItem)
                }
            }
            onClick {
                var url = urlPostList!![it.adapterPosition].url
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://$url";
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context!!.startActivity(browserIntent)
            }
        }.into(viewGlobal?.rvNotification!!)
    }

    private fun showDeleteDialog(currentItem: URLPostModel) {
        AlertDialog.Builder(context)
            .setTitle("Hati samaj")
            .setMessage("Are you sure you want to delete this?")
            .setPositiveButton(
                R.string.delete
            ) { dialog, which ->
                viewGlobal?.aviLoading?.show()
                db?.collection(CommonUtils.POST_URL)?.document(currentItem.doc_id)
                    ?.delete()
                    ?.addOnSuccessListener { void ->
                        viewGlobal?.aviLoading?.hide()
                        context!!.toast("Post deleted successfully")
                        urlPostList?.remove(currentItem)
                        rvNotification.adapter?.notifyDataSetChanged()
                    }
            }
            .setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeRegistration) {
            if (resultCode == Activity.RESULT_OK) {
                getDataFirstTime(viewGlobal!!)
            }
        }
    }

}
