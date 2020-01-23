package com.dasharath.hatisamaj.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.RegisterLayoutBinding
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.ui.commonform.CommonFormActivity
import com.dasharath.hatisamaj.utils.CommonUtils
import com.dasharath.hatisamaj.utils.ConnectivityReceiver
import com.dasharath.hatisamaj.utils.Utils.toast
import com.github.nitrico.lastadapter.LastAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registered_people.*
import kotlinx.android.synthetic.main.toolbar_app.view.*


class RegisteredPeopleActivity : AppCompatActivity(),ConnectivityReceiver.ConnectivityReceiverListener {

    private var peopleList: ArrayList<PersonDetailModel>? = null
    private var db: FirebaseFirestore? = null

    val requestCodeForm = 123
    var firebaseStorage: FirebaseStorage? = null
    private var snackBar: Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registered_people)
        init()
        listeners()
        setAdapter()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }


    private fun listeners() {
        toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun init() {
        toolbar.ivBack.visibility = View.VISIBLE
        toolbar.tvTitle.text = "Your registration status"
        db = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        peopleList = ArrayList()
        peopleList = intent.getSerializableExtra(CommonUtils.LIST) as ArrayList<PersonDetailModel>
        rvRegisterPeople.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun setAdapter() {
        LastAdapter(peopleList!!,BR.registerPeople).map<PersonDetailModel,RegisterLayoutBinding>(R.layout.register_layout){
            onBind {
                val currentItem = peopleList?.get(it.adapterPosition)
                it.binding.tvRegisterName.text = "${currentItem?.name} ${currentItem?.fName} ${currentItem?.sName}"
                if(currentItem?.status == CommonUtils.PENDING_STATUS)
                    it.binding.itemCard.setCardBackgroundColor(Color.parseColor("#FF5722"))
                if(currentItem?.status == CommonUtils.REJECTED) {
                    it.binding.itemCard.setCardBackgroundColor(Color.RED)
                    it.binding.linearEditDelete.visibility = View.VISIBLE
                }

                it.binding.ivDelete.setOnClickListener {view ->
                    showAlertOnDelete(currentItem,it.adapterPosition)
                }

                it.binding.ivEdit.setOnClickListener {
                    showAlertOnEdit(currentItem)
                }
            }
        }.into(rvRegisterPeople)
    }

    private fun showAlertOnEdit(currentItem: PersonDetailModel?) {
        AlertDialog.Builder(this@RegisteredPeopleActivity)
            .setTitle("Hati samaj")
            .setMessage("Please update and provide proper detail for verification")
            .setPositiveButton(
                R.string.update
            ) { dialog, which ->
                val intent = Intent(this@RegisteredPeopleActivity, CommonFormActivity::class.java)
                intent.putExtra(CommonUtils.TITLE, currentItem?.register_as)
                intent.putExtra(CommonUtils.IS_FROM_REGISTER_PEOPLE, true)
                intent.putExtra(CommonUtils.PERSON_DATA, currentItem)
                startActivityForResult(intent,requestCodeForm)
            }
            .setNegativeButton(R.string.no) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAlertOnDelete(currentItem: PersonDetailModel?, position: Int) {
        AlertDialog.Builder(this@RegisteredPeopleActivity)
            .setTitle("Hati samaj")
            .setMessage("Are you sure you want to delete this?")
            .setPositiveButton(
                R.string.delete
            ) { dialog, which ->
                aviLoading.show()
                db?.collection(CommonUtils.PEOPLE)?.document(currentItem?.doc_id!!)
                    ?.delete()
                    ?.addOnSuccessListener {void ->
                        firebaseStorage?.getReferenceFromUrl(currentItem.image_url)!!.delete().addOnSuccessListener {
                            aviLoading.hide()
                            toast("Successfully deleted!")
                            peopleList?.remove(currentItem)
                            rvRegisterPeople.adapter?.notifyItemRemoved(position)
                            setResult(Activity.RESULT_OK, Intent())
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar = Snackbar.make(
                findViewById(R.id.mainRegister),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestCodeForm){
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK,Intent())
                onBackPressed()
            }
        }
    }
}
