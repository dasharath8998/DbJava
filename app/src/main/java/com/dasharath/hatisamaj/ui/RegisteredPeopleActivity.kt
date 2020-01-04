package com.dasharath.hatisamaj.ui

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.RegisterLayoutBinding
import com.dasharath.hatisamaj.model.PersonDetailModel
import com.dasharath.hatisamaj.utils.CommonUtils
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_registered_people.*

class RegisteredPeopleActivity : AppCompatActivity() {

    var peopleList: ArrayList<PersonDetailModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registered_people)
        init()
        setAdapter()
    }

    private fun init() {
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

            }
        }.into(rvRegisterPeople)
    }
}
