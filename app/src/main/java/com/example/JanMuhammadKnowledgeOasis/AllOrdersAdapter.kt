package com.example.JanMuhammadKnowledgeOasis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllOrdersAdapter(var ordersList: ArrayList<PlacedOrdersModel>, var allOrdersInterface: AllOrdersInterface) : RecyclerView.Adapter<AllOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrdersAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.allorderscust, parent, false)
        return AllOrdersAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AllOrdersAdapter.ViewHolder, position: Int) {
        val currentItem = ordersList[position]
        holder.bookTitle.text = currentItem.book_name.toString()
        holder.bookStatus.text = currentItem.order_status.toString()
        holder.lay.setOnClickListener {
            allOrdersInterface.OnItemClick(currentItem, position)
        }
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var bookTitle = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.bookName)
        var bookStatus = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.bookStatus)

        var lay = itemView.findViewById<LinearLayout>(R.id.bookLay)
    }

    fun AllOrdersAdapter(oList: ArrayList<PlacedOrdersModel>, interfaces: AllOrdersInterface) {
        ordersList = oList
        allOrdersInterface = interfaces
    }

}