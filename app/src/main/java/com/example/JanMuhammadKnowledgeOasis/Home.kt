package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hmomeni.progresscircula.ProgressCircula
import org.json.JSONArray
import org.json.JSONException


class Home : AppCompatActivity(), BookInterface{

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
    lateinit var profilebtn: ImageView
    lateinit var searchbtn: ImageView
    lateinit var basketbtn: ImageView
    lateinit var homebtn: ImageView
    private lateinit var orderList: ArrayList<OrderedBooks>
    private lateinit var buyers: ArrayList<String>
    private lateinit var refs: ArrayList<String>
    private lateinit var cartList: ArrayList<CartItemsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        val bottomMenu = findViewById<BottomNavigationView>(R.id.navigation)
//        val navController = findNavController(R.id.fragment)
//        bottomMenu.setupWithNavController(navController)

        recyclerView = findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView2 = findViewById(R.id.recbooksRecycler)
        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView2.setHasFixedSize(true)
        progressCircula = findViewById(R.id.progressBarC)
        fab = findViewById(R.id.fab)
        linear = findViewById(R.id.linear)
        drop = findViewById(R.id.drop)

        orderList = ArrayList()
        buyers = ArrayList()
        refs = ArrayList()
        cartList = ArrayList()

        homebtn = findViewById(R.id.home)
        searchbtn = findViewById(R.id.search)
        profilebtn = findViewById(R.id.profile)
        basketbtn = findViewById(R.id.basket)

        homebtn.setImageResource(R.drawable.ic_twotone_home_24)
        basketbtn.setImageResource(R.drawable.ic_baseline_shopping_cart_24)
        profilebtn.setImageResource(R.drawable.ic_baseline_person_24)

        basketbtn.setOnClickListener {
            val intent = Intent(applicationContext, Basket::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }
        searchbtn.setOnClickListener {
            val intent = Intent(applicationContext, Search::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }



        booksList = ArrayList()
        userbooksList = ArrayList()
        arrayOrders = ArrayList()
        booksAdapter = BooksAdapter(booksList, applicationContext, this)
        userbooksAdapter = BooksAdapter(userbooksList, applicationContext, this)

        if (shp == null) shp = applicationContext.getSharedPreferences(
                "myPreferences",
                AppCompatActivity.MODE_PRIVATE
        )
        userName = shp!!.getString("name", "") as String



        shp = getSharedPreferences("myPreferences", MODE_PRIVATE)

        checkLogin()

        loadBooksInOrders()
        fetchCartItems()
        fetchOrderedItems()

//        loadUsersFavorits()

        fab.setOnClickListener {
            linear.visibility = View.VISIBLE
            linear.startAnimation(AnimationUtils.loadAnimation(applicationContext,R.anim.slide_down));
        }

        drop.setOnClickListener {
            linear.startAnimation(AnimationUtils.loadAnimation(applicationContext,R.anim.slide_up));
            linear.visibility = View.GONE
        }

    }

    private fun logout() {
        try {
            if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)
            var shpEditor: SharedPreferences.Editor = shp!!.edit()
            shpEditor.putString("name", "")
            shpEditor.commit()
            val i = Intent(this@Home, Login::class.java)
            startActivity(i)
            finish()
        } catch (ex: Exception) {
            Toast.makeText(this@Home, ex.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun checkLogin() {

        if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)


        val userName: String = shp!!.getString("name", "") as String

        if (userName != null && userName != "") {
            Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show()
        } else {
            val i = Intent(this@Home, Login::class.java)
            startActivity(i)
            finish()
        }
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
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
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
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
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
                                var imageBack = books.getString("imageBack")
                                var url =
                                        "https://usmansorion.000webhostapp.com/front_cover/" + imageFront
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
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(stringRequest)
    }

    private fun fetchOrderedItems() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            "https://usmansorion.000webhostapp.com/fetchOrders.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        orderList.clear()
                        buyers.clear()
                        refs.clear()

                        if (shp == null) shp = applicationContext?.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                        var userName = shp!!.getString("name", "") as String
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val orders = array.getJSONObject(i)

                            //adding the product to product list
                            orderList.add(
                                OrderedBooks(
                                    orders.getInt("id"),
                                    orders.getString("book_name"),
                                    orders.getString("ref_number"),
                                    orders.getString("order_number"),
                                    orders.getString("buyer"),
                                    orders.getString("delivery_address"),
                                    orders.getString("pick_address"),
                                    orders.getString("delievery_boy"),
                                    orders.getString("contact_num"),
                                    orders.getString("expected_delDate"),
                                    orders.getString("buyer_fullname")
                                )
                            )
                            buyers.add(orders.getString("buyer"))
                            refs.add(orders.getString("ref_number"))
                        }
                        if (cartList.isNotEmpty()){
                            for (i in 0..cartList.size - 1){
                                if (buyers.contains(cartList[i].buyer) && refs.contains(cartList[i].ref_number)){
                                    deleteOrderedItems(i)
                                }
                            }
                        }

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

    private fun deleteOrderedItems(i : Int) {

        var Request: StringRequest = object : StringRequest(Request.Method.POST,
            "https://usmansorion.000webhostapp.com/delCItems.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    cartList.removeAt(i)
                }
            },
            Response.ErrorListener {

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["ref_number"] = cartList[i].ref_number.toString()
                params["buyer"] = cartList[i].buyer.toString()
                return params
            }
        }
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(Request)
    }

    private fun fetchCartItems() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            "https://usmansorion.000webhostapp.com/fetchCartItems.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        cartList.clear()


                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val cart = array.getJSONObject(i)

                            //adding the product to product list
                            if (cart.getString("buyer") == userName){
                                cartList.add(
                                    CartItemsModel(
                                        cart.getInt("id"),
                                        cart.getString("book_name"),
                                        cart.getString("ref_number"),
                                        cart.getString("buyer"),
                                        cart.getString("imageFront"),
                                        cart.getString("book_type")
                                    )
                                )
                            }
                        }

                        //creating adapter object and setting it to recyclerview


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

    override fun OnBookClicked(currentItem: BooksModel, position: Int) {
        var intent = Intent(applicationContext, BookDetails::class.java)
        intent.putExtra("name", currentItem.book_name.toString())
        intent.putExtra("type", currentItem.book_type.toString())
        intent.putExtra("level", currentItem.book_level.toString())
        intent.putExtra(
            "backImage",
            currentItem.imageBack.toString()
        )
        intent.putExtra("frontImage", currentItem.imageFront.toString())
        intent.putExtra("rating", currentItem.rating.toString())
        intent.putExtra("loan_status", currentItem.loan_status.toString())
        intent.putExtra("author", currentItem.author)
        intent.putExtra("ref", currentItem.ref_number.toString())
        intent.putExtra("imageContent","https://usmansorion.000webhostapp.com/content_cover/" +  currentItem.imageContent.toString())

        startActivity(intent)
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
    }
}