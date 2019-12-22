package com.dasharath.hatisamaj.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasharath.hatisamaj.BR
import com.dasharath.hatisamaj.R
import com.dasharath.hatisamaj.databinding.ItemPostLayoutBinding
import com.dasharath.hatisamaj.model.PostModel
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    var arrayList: ArrayList<PostModel>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setAdapter(view)
    }

    private fun init(view: View) {
        view.rvPostData.layoutManager = LinearLayoutManager(context)
        arrayList = ArrayList()
        for (i in 0..10) {
            arrayList?.add(PostModel("", "", "Dasharath$i", "Description $i"))
        }
    }

    private fun setAdapter(view: View) {
        LastAdapter(arrayList!!,BR.postData).map<PostModel,ItemPostLayoutBinding>(R.layout.item_post_layout){

        }.into(view.rvPostData)
    }
}
