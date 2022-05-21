package com.example.JanMuhammadKnowledgeOasis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiderAdapter(var riderList: ArrayList<RiderModel>, var riderInterface: RiderInterface) : RecyclerView.Adapter<RiderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var riderName = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.riderName)
        var riderPhone = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.riderPhone)
        var riderEmail = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.riderEmail)
        var riderCnic = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.riderCNIC)
        var riderLay = itemView.findViewById<Button>(com.example.JanMuhammadKnowledgeOasis.R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.allriders, parent, false)
        return RiderAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = riderList[position]
        holder.riderName.text = currentItem.rider_name.toString()
        holder.riderEmail.text = currentItem.rider_email.toString()
        holder.riderPhone.text = currentItem.rider_phone.toString()
        holder.riderCnic.text = currentItem.rider_cnic.toString()
        holder.riderLay.setOnClickListener {
            riderInterface.onDeleteClicked(currentItem, position)
        }
    }

    override fun getItemCount(): Int {
        return riderList.size
    }

    fun RiderAdapter(rList: ArrayList<RiderModel>, rinterface: RiderInterface) {
        riderList = rList
        riderInterface = rinterface
    }

}