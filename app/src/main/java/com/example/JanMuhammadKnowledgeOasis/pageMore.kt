package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class pageMore : Fragment() {

    companion object {
        fun newInstance() = pageMore()
    }

    private lateinit var viewModel: PageMoreViewModel
    private lateinit var shp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.page_more_fragment, container, false)
        val btnLogOut = view.findViewById<AppCompatButton>(R.id.logout)
        val btnAllOrders = view.findViewById<AppCompatButton>(R.id.allOrders)
        val btnRewards = view.findViewById<AppCompatButton>(R.id.rewardBtn)
        val btnPI = view.findViewById<AppCompatButton>(R.id.personalInfo)
        btnLogOut.setOnClickListener {
            logout()
        }

        btnAllOrders.setOnClickListener {
            startActivity(Intent(activity, AllOrdersCust::class.java))
        }

        btnPI.setOnClickListener {
            startActivity(Intent(activity, PersonalInformation::class.java))
        }

        btnRewards.setOnClickListener {
            startActivity(Intent(activity, Rewards::class.java))
        }

        shp = this.requireActivity().getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        return view


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PageMoreViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun logout() {
        try {
            if (shp == null) shp = this.requireActivity().getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
            var shpEditor: SharedPreferences.Editor = shp.edit()
            shpEditor.putString("name", "")
            shpEditor.commit()
            val i = Intent(activity, Login::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

}