package com.example.JanMuhammadKnowledgeOasis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllOrdersAdminAdapter(var ordersList: ArrayList<PlacedOrdersModel>, var allOrdersInterface: AllOrdersInterfaceAdmin) : RecyclerView.Adapter<AllOrdersAdminAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrdersAdminAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.allordersadmin, parent, false)
        return AllOrdersAdminAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AllOrdersAdminAdapter.ViewHolder, position: Int) {
        val currentItem = ordersList[position]
        holder.bookTitle.text = currentItem.book_name.toString()
        holder.bookRef.text = currentItem.ref_number.toString()
        holder.bookStatus.text = currentItem.order_status.toString()
        holder.order.text = currentItem.order_number.toString()
        holder.deliverTo.text = currentItem.buyer + " / " + currentItem.buyer_fullname
        if (holder.rider.text != ""){
            holder.rider.text = currentItem.delievery_boy
        }
        else{
            holder.rider.text = "None Assigned"
        }

        if (currentItem.delivery_address == ""){
            holder.address.text = "Self Pick Up"
            holder.rider.visibility = View.GONE
            holder.rider1.visibility = View.GONE
        }
        else{
            holder.address.text = currentItem.delivery_address
        }
        holder.lay.setOnClickListener {
            allOrdersInterface.OnItemClick(currentItem, position)
        }
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var bookTitle = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.bookName)
        var bookRef = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.refBook)
        var bookStatus = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.orderStatus)
        var order = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.orderNum)
        var deliverTo = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.deliverTo)
        var rider = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.riderName)
        var rider1 = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.riderHead)
        var address = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.addressAd)
        var lay = itemView.findViewById<LinearLayout>(R.id.abookLay)
    }

    fun AllOrdersAdapter(oList: ArrayList<PlacedOrdersModel>, interfaces: AllOrdersInterfaceAdmin) {
        ordersList = oList
        allOrdersInterface = interfaces
    }


}