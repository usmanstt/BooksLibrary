package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hmomeni.progresscircula.ProgressCircula
import org.json.JSONArray
import org.json.JSONException

class AllBooksAdmin : AppCompatActivity(), BookInterfaceAdmin {

    private val URL_PRODUCTS = "https://usmansorion.000webhostapp.com/fetchBooks.php"
    private lateinit var booksList: ArrayList<BooksModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksAdapter: BooksAdapterAdmin
    private lateinit var btnAllbooks: FloatingActionButton
    lateinit var backBtn: ImageView
    lateinit var progressCircula: ProgressCircula

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_books_admin)

        recyclerView = findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        btnAllbooks = findViewById(R.id.bookADD)
        backBtn = findViewById(R.id.backBtn)
        progressCircula = findViewById(R.id.progressBarC)

        booksList = ArrayList()
        booksAdapter = BooksAdapterAdmin(booksList, applicationContext, this)

        btnAllbooks.setOnClickListener {
            startActivity(Intent(applicationContext, UploadBooks::class.java))
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }

        backBtn.setOnClickListener {
//            finish()
            val intent = Intent(applicationContext, AdminPortal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
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
                            var imageBack = books.getString("imageBack")
                            var url = "https://usmansorion.000webhostapp.com/front_cover/"+imageFront
                            var urlBack = "https://usmansorion.000webhostapp.com/back_cover/" + imageBack


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
                                    urlBack,
                                    url,
                                    books.getString("rating"),
                                    books.getString("loan_status")
                                )
                            )
                        }

                        //creating adapter object and setting it to recyclerview
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()
                        progressCircula.visibility = View.GONE

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