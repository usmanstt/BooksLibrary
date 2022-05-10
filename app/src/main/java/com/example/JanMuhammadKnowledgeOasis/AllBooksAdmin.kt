package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class AllBooksAdmin : AppCompatActivity(), BookInterfaceAdmin {

    private val URL_PRODUCTS = "https://usmansorion.000webhostapp.com/fetchBooks.php"
    private lateinit var booksList: ArrayList<BooksModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksAdapter: BooksAdapterAdmin
    private lateinit var btnAllbooks: Button
    lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_books_admin)

        recyclerView = findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        btnAllbooks = findViewById(R.id.bookADD)
        backBtn = findViewById(R.id.text)

        booksList = ArrayList()
        booksAdapter = BooksAdapterAdmin(booksList, applicationContext, this)

        btnAllbooks.setOnClickListener {
            startActivity(Intent(applicationContext, UploadBooks::class.java))
        }

        backBtn.setOnClickListener {
            finish()
        }

        loadBooks()
    }

    override fun OnBookClicked(currentItem: BooksModel, position: Int) {


    }

    private fun loadBooks() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)
                            var imageFront = books.getString("imageFront")
                            var url = "https://usmansorion.000webhostapp.com/front_cover/"+imageFront


                            //adding the product to product list
                            booksList.add(
                                BooksModel(
                                    books.getInt("id"),
                                    books.getString("book_name"),
                                    books.getString("ref_number"),
                                    books.getString("book_type"),
                                    books.getString("book_level"),
                                    books.getString("author"),
                                    books.getString("publisher"),
                                    books.getString("imageBack"),
                                    url,
                                    books.getString("rating"),
                                    books.getString("loan_status")
                                )
                            )
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            },
            Response.ErrorListener {

            }){

        }
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(stringRequest)
    }

}