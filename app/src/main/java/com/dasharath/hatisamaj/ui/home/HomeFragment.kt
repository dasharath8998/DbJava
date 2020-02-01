package com.dasharath.hatisamaj.ui.home


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.utils.Utils.toast
import com.dasharath.hatisamaj.CreatePostActivity
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemPostLayoutBinding
import com.dasharath.hatisamaj.model.PostModel
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.SharedPrefUtils
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.toolbar_app.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    var postList: ArrayList<PostModel>? = null

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var db: FirebaseFirestore? = null
    var adminStatus: Boolean = false
    val format = SimpleDateFormat("hh:mm a - MMM d")

    var globleView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        globleView = inflater.inflate(R.layout.fragment_home, container, false)
        return  globleView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getPostData(view)
        listeners(view)
    }

    private fun getPostData(view: View) {
        if(!view.swiperefresh.isRefreshing) {
            view.aviLoading.show()
        }
        db?.collection(CommonUtils.POSTS)?.get()?.addOnSuccessListener {
            if (!it.isEmpty) {
                val postId = it.documents
                postList = ArrayList()
                for(id in postId){
                    val postData = id.toObject(PostModel::class.java)
                    postData?.postDate = format.parse(postData?.post_date)
                    postList?.add(postData!!)
                    setAdapter(view)
                }
            } else {
                postList = ArrayList()
                setAdapter(view)
                context?.toast("No data found")
            }
        }
    }

    private fun init(view: View) {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        currentUser = mAuth?.currentUser

        toolbar.tvTitle.visibility = View.GONE
        toolbar.imgHomeLogo.visibility = View.VISIBLE

        if(SharedPrefUtils().getUserType(context!!) == CommonUtils.ADMIN)
            toolbar.ivAddPeople.visibility = View.VISIBLE

        view.rvPostData.layoutManager = LinearLayoutManager(context)

    }

    private fun listeners(view: View) {
        toolbar.ivAddPeople.setOnClickListener {
            startActivityForResult(Intent(context, CreatePostActivity::class.java),123)
        }

        view.swiperefresh.setOnRefreshListener {
            view.swiperefresh.isRefreshing = true
            getPostData(globleView!!)
        }
    }

    private fun setAdapter(view: View) {
        if(view.swiperefresh.isRefreshing){
            view.swiperefresh.isRefreshing = false
        } else {
            view.aviLoading.hide()
        }

        postList?.sortByDescending { it.postDate }

        LastAdapter(postList!!,BR.postData).map<PostModel,ItemPostLayoutBinding>(R.layout.item_post_layout){
            onBind {
                val currentItem = postList!![it.adapterPosition]
                if(postList!![it.adapterPosition].description == "") it.binding.tvCaption.visibility = View.GONE

                if(SharedPrefUtils().getUserType(context!!) == CommonUtils.ADMIN)
                    it.binding.ivDelete.visibility = View.VISIBLE

//                it.binding.ivLike.setOnClickListener {view ->
//                    it.binding.ivLike.setImageResource(R.drawable.ic_like)
//                    it.binding.tvLikeCount.text = (it.binding.tvLikeCount.text.toString().toInt() + 1).toString()
//                }

                it.binding.ivDelete.setOnClickListener { view ->
                    showAlertOnDelete(currentItem,it.adapterPosition)
                }
            }
        }.into(view.rvPostData)
    }

    private fun showAlertOnDelete(currentItem: PostModel?, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hati samaj")
            .setMessage("Are you sure you want to delete this?")
            .setPositiveButton(
                R.string.delete
            ) { dialog, which ->
                globleView?.aviLoading?.show()
                db?.collection(CommonUtils.POSTS)?.document(currentItem?.doc_id!!)
                    ?.delete()
                    ?.addOnSuccessListener { void ->
                        firebaseStorage?.getReferenceFromUrl(currentItem.image_url)!!.delete().addOnSuccessListener {
                            globleView?.aviLoading?.hide()
                            context?.toast("Successfully deleted!")
                            postList?.remove(currentItem)
                            rvPostData.adapter?.notifyItemRemoved(position)
                            Log.d("TAG", "DocumentSnapshot successfully deleted!")
                        }
                    }
                    ?.addOnFailureListener { e -> Log.d("TAG", "Error deleting document", e) }
            }
            .setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                getPostData(globleView!!)
            }
        }

    }
}
