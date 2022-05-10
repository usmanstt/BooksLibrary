package com.example.JanMuhammadKnowledgeOasis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RewardAdapter(var rewardList: ArrayList<RewardsModel>) : RecyclerView.Adapter<RewardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.rewards, parent, false)
        return RewardAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RewardAdapter.ViewHolder, position: Int) {
        val currentItem = rewardList[position]
        holder.rText.text = currentItem.reward.toString()
    }

    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var rText: TextView = itemView.findViewById(R.id.rewardText)
    }

    fun RewardAdapter(rList: ArrayList<RewardsModel>) {
        rewardList = rList
    }
}