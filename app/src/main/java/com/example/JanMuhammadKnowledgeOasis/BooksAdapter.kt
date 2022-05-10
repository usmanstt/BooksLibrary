package com.example.JanMuhammadKnowledgeOasis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class BooksAdapter(var booksList: ArrayList<BooksModel>, var context: Context, var bookInterface: BookInterface) :RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.JanMuhammadKnowledgeOasis.R.layout.books, parent, false)
        return BooksAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BooksAdapter.ViewHolder, position: Int) {
        val currentItem = booksList[position]
        Glide.with(context).load(currentItem.imageFront).into(holder.bookImage)
        holder.bookTitle.text = currentItem.book_name.toString()
        holder.bookAuthor.text = currentItem.author.toString()
//        holder.bookType.text = currentItem.book_type.toString()
//        holder.bookRating.text = currentItem.rating.toString()

        holder.lay.setOnClickListener {
            bookInterface.OnBookClicked(currentItem, position)
        }
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    fun BooksAdapter(bookList: ArrayList<BooksModel>, contexts: Context, bookInterfaces: BookInterface) {
        booksList = bookList
        context = contexts
        bookInterface = bookInterfaces
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var bookImage = itemView.findViewById<ImageView>(com.example.JanMuhammadKnowledgeOasis.R.id.bookfrontCover)
        var bookTitle = itemView.findViewById<TextView>(R.id.bookName)
        var bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthor)
//        var bookType = itemView.findViewById<TextView>(com.example.testproject.R.id.bookType)
//        var bookRating = itemView.findViewById<TextView>(com.example.testproject.R.id.bookRating)
        var lay = itemView.findViewById<LinearLayout>(com.example.JanMuhammadKnowledgeOasis.R.id.bookLay)
    }


}