package com.dasharath.hatisamaj.ui.search


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemPersonInfoBinding
import com.dasharath.hatisamaj.model.PersonModel
import com.dasharath.hatisamaj.ui.personinfo.PersonInfoActivity
import com.dasharath.hatisamaj.ui.personinfotab.PersonalInfo
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    var personList: ArrayList<PersonModel>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener(view)
        personList = ArrayList()
        for (i in 0..30){
            if(i % 2 == 0) {
                personList!!.add(PersonModel("", "Name $i", "1$i", "male", "ABCD"))
            } else {
                personList!!.add(PersonModel("", "Name $i", "1$i", "female", "ABCD"))
            }
        }
        if(this@SearchFragment.isVisible) {
            setAdapter(view)
        }
    }

    private fun listener(view: View) {
        view.ivFilter.setOnClickListener {
            Toast.makeText(context,"Filter Screen",Toast.LENGTH_LONG).show()
        }
    }

    private fun setAdapter(view: View) {
        view.rvSearch.layoutManager = GridLayoutManager(context,2)
        LastAdapter(personList!!, BR.personData).map<PersonModel,ItemPersonInfoBinding>(R.layout.item_person_info){
            onBind {
                if(personList!![it.adapterPosition].gender == "male") {
                    Glide.with(context!!).load(personList!![it.adapterPosition].image)
                        .placeholder(R.drawable.ic_person_male).into(it.binding.ivPerson)
                } else {
                    Glide.with(context!!).load(personList!![it.adapterPosition].image)
                        .placeholder(R.drawable.ic_person_female).into(it.binding.ivPerson)
                }
            }

            onClick {
                startActivity(Intent(activity,PersonInfoActivity::class.java))
            }
        }.into(view.rvSearch)
    }

}
