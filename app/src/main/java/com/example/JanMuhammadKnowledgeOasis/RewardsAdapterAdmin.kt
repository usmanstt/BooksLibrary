package com.example.JanMuhammadKnowledgeOasis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RewardsAdapterAdmin(var rewardList: ArrayList<RewardsModel>) : RecyclerView.Adapter<RewardsAdapterAdmin.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsAdapterAdmin.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.rewardsadmin, parent, false)
        return RewardsAdapterAdmin.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RewardsAdapterAdmin.ViewHolder, position: Int) {
        val currentItem = rewardList[position]
        holder.rText.text = currentItem.reward.toString()
    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rText: TextView = itemView.findViewById(R.id.rewardText)
    }

    fun RewardsAdapterAdmin(rList: ArrayList<RewardsModel>) {
        rewardList = rList
    }
}