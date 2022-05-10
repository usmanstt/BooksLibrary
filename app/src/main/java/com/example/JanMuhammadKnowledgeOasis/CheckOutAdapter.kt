package com.example.JanMuhammadKnowledgeOasis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckOutAdapter(var cartList: ArrayList<CartItemsModel>) : RecyclerView.Adapter<CheckOutAdapter.ViewHolder>() {

    fun BooksAdapter(cartsList: ArrayList<CartItemsModel>, contexts: Context, cbookInterfaces: CBookInterface) {
        cartList = cartsList
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckOutAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.checked_books, parent, false)
        return CheckOutAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckOutAdapter.ViewHolder, position: Int) {
        val currentItem = cartList[position]

        holder.bookName.text = currentItem.book_name.toString()
        holder.ref.text = currentItem.ref_number.toString()
    }

    override fun getItemCount(): Int {
        return cartList.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookName = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.nameBook)
        var ref = itemView.findViewById<TextView>(R.id.refNum)
    }

}