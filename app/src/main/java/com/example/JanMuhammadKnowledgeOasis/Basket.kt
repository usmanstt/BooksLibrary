package com.example.JanMuhammadKnowledgeOasis

import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.paperdb.Paper
import org.json.JSONArray
import org.json.JSONException

class Basket : AppCompatActivity(), CBookInterface {
    lateinit var profilebtn: ImageView
    lateinit var searchbtn: ImageView
    lateinit var basketbtn: ImageView
    lateinit var homebtn: ImageView
    lateinit var text: TextView
    lateinit var cartList: ArrayList<CartItemsModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    var shp: SharedPreferences? = null
    lateinit var checkedBooks: ArrayList<CartItemsModel>
    var check = true
    private lateinit var checkedItems: ArrayList<CartItemsModel>
    private lateinit var btnCheckOut: FloatingActionButton
    private lateinit var orderList: ArrayList<OrderedBooks>
    private lateinit var buyers: ArrayList<String>
    private lateinit var refs: ArrayList<String>
    private lateinit var userName: String
    var items: ArrayList<CartItemsModel>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        Paper.init(this)

        homebtn = findViewById(R.id.home)
        searchbtn = findViewById(R.id.search)
        profilebtn = findViewById(R.id.profile)
        basketbtn = findViewById(R.id.basket)
        text = findViewById(R.id.textIn)

        if (shp == null) shp = applicationContext.getSharedPreferences(
            "myPreferences",
            AppCompatActivity.MODE_PRIVATE
        )
        userName = shp!!.getString("name", "") as String

        homebtn.setImageResource(R.drawable.ic_baseline_home_24)
        basketbtn.setImageResource(R.drawable.ic_twotone_shopping_cart_24)
        profilebtn.setImageResource(R.drawable.ic_baseline_person_24)

        homebtn.setOnClickListener {
            val intent = Intent(applicationContext, Home::class.java)
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

        recyclerView = findViewById(R.id.booksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        btnCheckOut = findViewById(R.id.checkOutBtn)



        cartList = ArrayList()
        refs = ArrayList()
        buyers = ArrayList()
        orderList = ArrayList()
        checkedBooks = ArrayList()
        checkedItems = ArrayList()
        cartAdapter = CartAdapter(cartList, applicationContext,this)

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            checkedItemsReciever,
            IntentFilter("items")
        )

        fetchCartItems()
//        fetchOrderedItems()

        btnCheckOut.setOnClickListener {
            if (checkedItems.isEmpty()){
                Toast.makeText(applicationContext, "No items selected", Toast.LENGTH_LONG).show()
            }
            else if (userName.isNotEmpty() && checkedItems.isNotEmpty()) {
//                val intent: Intent = Intent("itemsChecked")
//                intent.putExtra("itemsCheckedList", checkedItems)
//                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

                var intent2 = Intent(applicationContext, CheckOut::class.java)
                intent2.putExtra("itemsCheckedList", checkedItems)
                startActivity(intent2)
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);

            }
            else if (userName.isEmpty()){
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
        }

    }

//    private fun fetchOrderedItems() {
//        var stringRequest: StringRequest = object : StringRequest(
//            Request.Method.GET,
//            "https://usmansorion.000webhostapp.com/fetchOrders.php",
//            object : Response.Listener<String> {
//                override fun onResponse(response: String?) {
//                    try {
//                        //converting the string to json array object
//                        val array = JSONArray(response)
//                        orderList.clear()
//                        buyers.clear()
//                        refs.clear()
//
//                        if (shp == null) shp = applicationContext?.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                        var userName = shp!!.getString("name", "") as String
//                        //traversing through all the object
//                        for (i in 0 until array.length()) {
//
//                            //getting product object from json array
//                            val orders = array.getJSONObject(i)
//
//                            //adding the product to product list
//                            orderList.add(
//                                OrderedBooks(
//                                    orders.getInt("id"),
//                                    orders.getString("book_name"),
//                                    orders.getString("ref_number"),
//                                    orders.getString("order_number"),
//                                    orders.getString("buyer"),
//                                    orders.getString("delivery_address"),
//                                    orders.getString("pick_address"),
//                                    orders.getString("delievery_boy"),
//                                    orders.getString("contact_num"),
//                                    orders.getString("expected_delDate"),
//                                    orders.getString("buyer_fullname")
//                                )
//                            )
//                            buyers.add(orders.getString("buyer"))
//                            refs.add(orders.getString("ref_number"))
//                        }
//
//
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//
//            },
//            Response.ErrorListener {
//
//            }){
//        }
//        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
//        req.add(stringRequest)
//    }

    private fun fetchCartItems() {
//        var stringRequest: StringRequest = object : StringRequest(
//            Request.Method.GET,
//            "https://usmansorion.000webhostapp.com/fetchCartItems.php",
//            object : Response.Listener<String> {
//                override fun onResponse(response: String?) {
//                    try {
//                        //converting the string to json array object
//                        val array = JSONArray(response)
//                        cartList.clear()
//
//
//                        //traversing through all the object
//                        for (i in 0 until array.length()) {
//
//                            //getting product object from json array
//                            val cart = array.getJSONObject(i)
//
//                            //adding the product to product list
//                            if (cart.getString("buyer") == userName){
//                                cartList.add(
//                                    CartItemsModel(
//                                        cart.getInt("id"),
//                                        cart.getString("book_name"),
//                                        cart.getString("ref_number"),
//                                        cart.getString("buyer"),
//                                        cart.getString("imageFront"),
//                                        cart.getString("book_type")
//                                    )
//                                )
//                            }
//                        }
//
//                        //creating adapter object and setting it to recyclerview
//                        recyclerView.adapter = cartAdapter
//                        cartAdapter.notifyDataSetChanged()
//
//                        if (cartList.isEmpty()){
//                            text.visibility = View.VISIBLE
//                        }
//
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//
//            },
//            Response.ErrorListener {
//
//            }){
//
//        }
//        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
//        req.add(stringRequest)

        if (Paper.book().contains("cartItems")) {


            items = Paper.book().read<ArrayList<CartItemsModel>>("cartItems")

            if (items!!.isNotEmpty()) {
                for (i in 0..items!!.size - 1) {
                    cartList!!.add(items!![i])
                }

//            creating adapter object and setting it to recyclerview
                recyclerView.adapter = cartAdapter
                cartAdapter.notifyDataSetChanged()
            } else {
                text.visibility = View.VISIBLE
            }

        }
        else{
            text.visibility = View.VISIBLE
        }
    }

    override fun OnBookClicked(currentItem: CartItemsModel, position: Int) {

    }

    val checkedItemsReciever: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            checkedItems = intent.getSerializableExtra("itemList") as ArrayList<CartItemsModel>


        }
    }
}