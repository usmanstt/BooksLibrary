package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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


class pageHome : Fragment(), BookInterface {

    companion object {
        fun newInstance() = pageHome()
    }

    private lateinit var viewModel: PageHomeViewModel
    private val URL_PRODUCTS = "https://usmansorion.000webhostapp.com/fetchBooks.php"
    private lateinit var booksList: ArrayList<BooksModel>
    private lateinit var userbooksList: ArrayList<BooksModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var userbooksAdapter: BooksAdapter
    lateinit var arrayOrders: ArrayList<String>
    var shp: SharedPreferences? = null
    lateinit var strcompare : String
    var booktype: String = ""
    var userName = ""
    lateinit var fab: FloatingActionButton
    lateinit var progressCircula: ProgressCircula
    lateinit var linear: LinearLayout
    lateinit var drop: ImageView
    lateinit var profile: ImageView
    lateinit var search: ImageView
    lateinit var basket: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.page_home_fragment, container, false)

//        spinnerFilter = view.findViewById(R.id.filterLevel)
//        spinnerFiltertype = view.findViewById(R.id.filterType)
        recyclerView = view.findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView2 = view.findViewById(R.id.recbooksRecycler)
        recyclerView2.layoutManager = LinearLayoutManager(activity)
        recyclerView2.setHasFixedSize(true)
        progressCircula = view.findViewById(R.id.progressBarC)
        fab = view.findViewById(R.id.fab)
        linear = view.findViewById(R.id.linear)
        drop =view.findViewById(R.id.drop)

        booksList = ArrayList()
        userbooksList = ArrayList()
        arrayOrders = ArrayList()
        booksAdapter = BooksAdapter(booksList, requireContext(), this)
        userbooksAdapter = BooksAdapter(userbooksList, requireContext(), this)

        if (shp == null) shp = requireActivity().getSharedPreferences(
                "myPreferences",
                AppCompatActivity.MODE_PRIVATE
        )
        userName = shp!!.getString("name", "") as String


//        filterItemsLevel = arrayListOf("All", "Primary", "Middle", "High", "FA/FSc", "Graduation")
//        filterItemsType = arrayListOf(
//            "All", "Science", "Fiction/Story/Novels", "Math", "Language",
//            "Religious", "History", "Information Technology", "Magazines & News", "School Books"
//        )
//
//
//        var arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            filterItemsLevel
//        )
//        spinnerFilter.adapter = arrayAdapter
//        spinnerFilter.prompt = "Select Level"
//
//        var arrayAdapterType: ArrayAdapter<String> = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            filterItemsType
//        )
//        spinnerFiltertype.adapter = arrayAdapterType
//        spinnerFiltertype.prompt = "Select Type"

        loadBooksInOrders()

//        loadUsersFavorits()

        fab.setOnClickListener {
            linear.visibility = View.VISIBLE
            linear.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_down));
        }

        drop.setOnClickListener {
            linear.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_up));
            linear.visibility = View.GONE
        }


        return view
    }

    private fun loadUsersFavorits() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
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
                            var url =
                                "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


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
                                    books.getString("loan_status"),
                                        books.getString("imageContent")
                                )
                            )
                        }

                        //creating adapter object and setting it to recyclerview
//                        recyclerView.adapter = booksAdapter
//                        booksAdapter.notifyDataSetChanged()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            },
            Response.ErrorListener {

            }){

        }
        var req: RequestQueue = Volley.newRequestQueue(activity)
        req.add(stringRequest)
    }

    private fun loadBooksInOrders() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            "https://usmansorion.000webhostapp.com/fetchAllOrders.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        arrayOrders.clear()



                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val orders = array.getJSONObject(i)

                            //adding the product to product list
                            if (orders.getString("buyer") == userName) {
                                arrayOrders.add(orders.getString("book_type"))
                            }
                        }

                        strcompare = arrayOrders.joinToString(separator = ",")
                        fetchBookType()



                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            },
            Response.ErrorListener {

            }){
        }
        var req: RequestQueue = Volley.newRequestQueue(activity)
        req.add(stringRequest)
    }

    private fun fetchBookType() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)
                            if (arrayOrders.isNotEmpty()){
                                if (books.getString("book_type") == arrayOrders[arrayOrders.size - 1]){
                                    booktype = books.getString("book_type").toString()
                                }
                                else{
                                    Log.d("TAG", "TAG")
                                }
                            }

                            //creating adapter object and setting it to recyclerview
                        }
                        loadBooks()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            },
            Response.ErrorListener {

            }){

        }
        var req: RequestQueue = Volley.newRequestQueue(activity)
        req.add(stringRequest)
    }

    private fun loadBooks() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            URL_PRODUCTS,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        booksList.clear()
                        userbooksList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val books = array.getJSONObject(i)
                            var imageFront = books.getString("imageFront")
                            var url =
                                "https://usmansorion.000webhostapp.com/front_cover/" + imageFront


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
                                    books.getString("loan_status"),
                                        books.getString("imageContent")
                                )
                            )
                        }

                        for (x in 0..booksList.size - 1){
                            if (booksList[x].book_type == booktype){
                                userbooksList.add(booksList[x])
                            }
                        }
                        if (userbooksList.isNotEmpty()){
                            recyclerView2.adapter = userbooksAdapter
                            userbooksAdapter.notifyDataSetChanged()
                            progressCircula.visibility = View.GONE
//                            Toast.makeText(activity, "User", Toast.LENGTH_LONG).show()
                        }
                        else{
                            recyclerView2.adapter = booksAdapter
                            booksAdapter.notifyDataSetChanged()
                            progressCircula.visibility = View.GONE

//                            Toast.makeText(activity, userbooksList.size.toString(), Toast.LENGTH_LONG).show()
                        }
                        recyclerView.adapter = booksAdapter
                        booksAdapter.notifyDataSetChanged()

                        //creating adapter object and setting it to recyclerview

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            },
            Response.ErrorListener {

            }){

        }
        var req: RequestQueue = Volley.newRequestQueue(activity)
        req.add(stringRequest)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PageHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun OnBookClicked(currentItem: BooksModel, position: Int) {
        var intent = Intent(activity, BookDetails::class.java)
        intent.putExtra("name", currentItem.book_name.toString())
        intent.putExtra("type", currentItem.book_type.toString())
        intent.putExtra("level", currentItem.book_level.toString())
        intent.putExtra(
            "backImage",
            "https://usmansorion.000webhostapp.com/back_cover/" + currentItem.imageBack.toString()
        )
        intent.putExtra("frontImage", currentItem.imageFront.toString())
        intent.putExtra("rating", currentItem.rating.toString())
        intent.putExtra("loan_status", currentItem.loan_status.toString())
        intent.putExtra("author", currentItem.author)
        intent.putExtra("ref", currentItem.ref_number.toString())
        intent.putExtra("imageContent","https://usmansorion.000webhostapp.com/content_cover/" +  currentItem.imageContent.toString())

        startActivity(intent)
    }

}