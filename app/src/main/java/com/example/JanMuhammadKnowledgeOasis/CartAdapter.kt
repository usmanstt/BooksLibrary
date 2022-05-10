package com.example.JanMuhammadKnowledgeOasis

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(var cartList: ArrayList<CartItemsModel>, var context: Context, var cBookInterface: CBookInterface) : RecyclerView.Adapter<CartAdapter.ViewHolder> () {
    var check =true
    var checkedItemsList: ArrayList<CartItemsModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.cart_books, parent, false)
        return CartAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
        val currentItem = cartList[position]
        Glide.with(context).load(currentItem.imageFront).into(holder.bookImage)
        holder.bookTitle.text = currentItem.book_name.toString()
        holder.bookRef.text = currentItem.ref_number.toString()
//        holder.checkBooks.setOnClickListener {
//            if (check == true){
//                holder.checkBooks.setBackgroundColor(Color.RED)
//                check = false
//            }
//            else{
//                holder.checkBooks.background = ContextCompat.getDrawable(context, R.drawable.circle)
//                check = true
//            }
//            cBookInterface.OnBookClicked(currentItem, position)
//        }
        holder.checkItem.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                checkedItemsList.add(currentItem)
            }
            if (!isChecked){
                checkedItemsList.remove(currentItem)
            }
//            Toast.makeText(context, checkedItemsList.size.toString(), Toast.LENGTH_LONG).show()

            val intent: Intent = Intent("items")
            intent.putExtra("itemList", checkedItemsList)
//            intent.putStringArrayListExtra("partsPriceList", checkedPartsPrices)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun BooksAdapter(cartsList: ArrayList<CartItemsModel>, contexts: Context, cbookInterfaces: CBookInterface) {
        cartList = cartsList
        context = contexts
        cBookInterface = cbookInterfaces
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookImage = itemView.findViewById<ImageView>(com.example.JanMuhammadKnowledgeOasis.R.id.bookfrontCover)
        var bookTitle = itemView.findViewById<TextView>(com.example.JanMuhammadKnowledgeOasis.R.id.bookName)
        var bookRef = itemView.findViewById<TextView>(R.id.refBook)
        var checkBooks = itemView.findViewById<LinearLayout>(R.id.cbookLay)
        var checkItem = itemView.findViewById<AppCompatCheckBox>(R.id.checkCItem)

    }
}