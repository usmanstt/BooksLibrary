package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hmomeni.progresscircula.ProgressCircula
import io.paperdb.Paper
import org.json.JSONArray
import org.json.JSONException


class Home : AppCompatActivity(), BookInterface{

    private val URL_PRODUCTS = "https://usmansorion.000webhostapp.com/fetchBooks.php"
    lateinit var search: EditText
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
    private lateinit var filteredlist: ArrayList<BooksModel>
    var list: ArrayList<CartItemsModel>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        val bottomMenu = findViewById<BottomNavigationView>(R.id.navigation)
//        val navController = findNavController(R.id.fragment)
//        bottomMenu.setupWithNavController(navController)

        Paper.init(this)

        if (Paper.book().contains("cartItems")){
            list = Paper.book().read<ArrayList<CartItemsModel>>("cartItems")
        }
//        Paper.book().destroy()
//        Toast.makeText(applicationContext,list!!.size.toString(),Toast.LENGTH_LONG).show()

        if (shp == null) shp = applicationContext.getSharedPreferences(
                "myPreferences",
                AppCompatActivity.MODE_PRIVATE
        )
        userName = shp!!.getString("name", "") as String

        recyclerView = findViewById(R.id.booksRecycler)
        search = findViewById(R.id.searchBooks)
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
        filteredlist = ArrayList()

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

        })

        homebtn = findViewById(R.id.home)
        searchbtn = findViewById(R.id.search)
        profilebtn = findViewById(R.id.profile)
        basketbtn = findViewById(R.id.basket)

        homebtn.setImageResource(R.drawable.ic_twotone_home_24)
        basketbtn.setImageResource(R.drawable.ic_baseline_shopping_cart_24)
        profilebtn.setImageResource(R.drawable.ic_baseline_person_24)

        profilebtn.setOnClickListener {
            if (userName.isEmpty()){
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                val factory: LayoutInflater = LayoutInflater.from(this)
                val view: View = factory.inflate(R.layout.dialoglogin, null);
                builder.setView(view)

                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val Lt = view.findViewById<TextView>(R.id.loginT)
                val St = view.findViewById<TextView>(R.id.signupT)

                Lt.setOnClickListener {
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                }

                St.setOnClickListener {
                    val intent = Intent(applicationContext, Signup::class.java)
                    startActivity(intent)
                }
            }
            else{
                val intent = Intent(applicationContext, Profile::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
            }

        }

        basketbtn.setOnClickListener {

//            if (userName.isEmpty()){
//                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//                val factory: LayoutInflater = LayoutInflater.from(this)
//                val view: View = factory.inflate(R.layout.dialoglogin, null);
//                builder.setView(view)
//
//                val alertDialog = builder.create()
//                alertDialog.show()
//
//                val Lt = view.findViewById<TextView>(R.id.loginT)
//                val St = view.findViewById<TextView>(R.id.signupT)
//
//                Lt.setOnClickListener {
//                    val intent = Intent(applicationContext, Login::class.java)
//                    startActivity(intent)
//                }
//
//                St.setOnClickListener {
//                    val intent = Intent(applicationContext, Signup::class.java)
//                    startActivity(intent)
//                }
//            }
//            else {
                val intent = Intent(applicationContext, Basket::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
//            }
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

//        if (shp == null) shp = applicationContext.getSharedPreferences(
//                "myPreferences",
//                AppCompatActivity.MODE_PRIVATE
//        )
//        userName = shp!!.getString("name", "") as String



        shp = getSharedPreferences("myPreferences", MODE_PRIVATE)

//        checkLogin()

        loadBooksInOrders()
        fetchCartItems()
        fetchOrderedItems()

//        loadUsersFavorits()

        fab.setOnClickListener {
            linear.visibility = View.VISIBLE
            linear.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down));
        }

        drop.setOnClickListener {
            linear.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up));
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
                                if (arrayOrders.isNotEmpty()) {
                                    if (books.getString("book_type") == arrayOrders[arrayOrders.size - 1]) {
                                        booktype = books.getString("book_type").toString()
                                    } else {
                                        Log.d("TAG_Orders", "TAG")
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

                            for (x in 0..booksList.size - 1) {
                                if (booksList[x].book_type == booktype) {
                                    userbooksList.add(booksList[x])
                                }
                            }
                            if (userbooksList.isNotEmpty()) {
                                recyclerView2.adapter = userbooksAdapter
                                userbooksAdapter.notifyDataSetChanged()
                                progressCircula.visibility = View.GONE
//                            Toast.makeText(activity, "User", Toast.LENGTH_LONG).show()
                            } else {
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


//                        if (shp == null) shp = applicationContext?.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                        var userName = shp!!.getString("name", "") as String
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
                                                orders.getString("buyer_fullname"),
                                                orders.getString("order_status")
                                        )
                                )
                                buyers.add(orders.getString("buyer"))
                                refs.add(orders.getString("ref_number"))
                            }
                            var LT: ArrayList<String> = ArrayList()


                            //sort order_list
                            var sortedList: ArrayList<OrderedBooks> = ArrayList()
                            if (list!!.isNotEmpty() && refs.size != 0 && buyers.contains(userName)) {
                                LT.clear()
                                for (h in 0..list!!.size - 1) {
                                    LT.add(list!![h].ref_number)
                                }
                                var x = 0
                                while (x < orderList!!.size) {
                                    if (LT!!.contains(orderList[x].ref_number) && orderList[x].order_status.toString() != "Returned" && orderList[x].buyer == userName) {

                                        var index = LT.indexOf(orderList[x].ref_number.toString())
//                                        list!!.removeAt(index)
                                        sortedList.add(orderList[x])
                                        Log.d("TAG_BOOK", list!!.get(index).book_name.toString())
//                                        Paper.book().destroy()
                                    } else {
//                                        Log.d("TAG","TAG")
                                        continue
                                    }
                                    x = x + 1
                                }

                                if (sortedList.isNotEmpty()) {
//                                Toast.makeText(applicationContext,sortedList.size.toString(),Toast.LENGTH_LONG).show()
                                    sortedList.sortByDescending {
                                        it.book_name
                                    }
                                    for (i in 0..sortedList.size - 1) {
                                        Log.d("TAG_SORT", sortedList[i].book_name)
                                    }
                                }

                                if (list!!.isNotEmpty()) {
                                    list!!.sortByDescending {
                                        it.book_name
                                    }
                                    for (i in 0..list!!.size - 1) {
                                        Log.d("TAG_SORT_LIST", list!![i].book_name)
                                    }
                                }


                                if (sortedList!!.isNotEmpty() && list!!.isNotEmpty()) {
                                    var s = sortedList.size - 1
                                    var i = 0
                                    while (s >= 0) {
                                        if (sortedList[s].book_name == list!![s].book_name) {
                                            Log.d("TAG_NAME", "YES " + list!![s].book_name)
//                                            list!!.remove(list!![s])
                                            Log.d("TAG_NAME_AFTER", "YES " + sortedList!![s].book_name)
//                                        Paper.book().destroy()
                                            i = s

                                        }
                                        else {
                                            continue
                                        }
                                        list!!.removeAt(i)
                                        s = s - 1
                                    }
                                    if (list!!.isNotEmpty()) {
                                        Paper.book().destroy()
                                        Paper.book().write("cartItems", list!!)
                                    } else {
                                        Paper.book().destroy()
                                    }
                                } else {
                                    Log.d("TAG_EMPTY_SORT", "TAG")
                                }

//                            if (list!!.isNotEmpty()){
//                                Paper.book().destroy()
//                                Paper.book().write("cartItems",list!!)
//                            }
//                            else{
//                                Log.d("TA_ET", "Empty")
//                            }
                            } else if (list!!.isEmpty()) {
//                            Toast.makeText(applicationContext,"No Toast",Toast.LENGTH_LONG).show()
                                Log.d("TAG_EMPTY_LIST", "TAG")
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

    private fun deleteOrderedItems(i: Int) {

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

    private fun filter(toString: String) {
        filteredlist.clear()
        var booksAdapter = BooksAdapter(filteredlist, applicationContext, this)
        for (i in 0..booksList.size - 1){
            if (booksList[i].book_name.toLowerCase().contains(toString.toLowerCase())){
                filteredlist.add(booksList[i])
            }
        }
        if (toString.isEmpty()){
            loadBooks()
        }
        recyclerView.adapter = booksAdapter
        booksAdapter.notifyDataSetChanged()
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
                                if (cart.getString("buyer") == userName) {
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
        intent.putExtra("imageContent", "https://usmansorion.000webhostapp.com/content_cover/" + currentItem.imageContent.toString())

        startActivity(intent)
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
    }
}