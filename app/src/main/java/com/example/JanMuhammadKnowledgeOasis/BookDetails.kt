package com.example.JanMuhammadKnowledgeOasis

import android.R.attr.bitmap
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import io.paperdb.Paper
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class BookDetails : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var front: ImageView
    private lateinit var btnAddtoCart: Button
    private lateinit var btnCheckContent: Button
    var shp: SharedPreferences? = null
    private lateinit var cartList: ArrayList<CartItemsModel>
    private lateinit var cartBookRef :ArrayList<String>
    private lateinit var cartBookbuyer: ArrayList<String>
    var check: Boolean = true
    private lateinit var backBtn: ImageView
    var id: Int? = null
    lateinit var imageFCart: Bitmap
    var imageString = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        back = findViewById(R.id.imageBookBack)
        front = findViewById(R.id.imageBookFront)
        btnAddtoCart = findViewById(R.id.btnAddCart)
        btnCheckContent = findViewById(R.id.btnCheckContent)

        Paper.init(this)


        cartList = ArrayList()
        cartBookRef = ArrayList()
        cartBookbuyer = ArrayList()

        var fronturl = intent.getStringExtra("frontImage")
        var backurl = intent.getStringExtra("backImage")
        var book = intent.getStringExtra("name")
        var author = intent.getStringExtra("author")
        var type = intent.getStringExtra("type")
        var level = intent.getStringExtra("level")
        var rating = intent.getStringExtra("rating")
        var status = intent.getStringExtra("loan_status")
        var ref = intent.getStringExtra("ref")
        var imageContent = intent.getStringExtra("imageContent")


        backBtn = findViewById(R.id.backBtn)

        btnCheckContent.setOnClickListener {
            if (imageContent.toString().isNotEmpty()){
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                val factory: LayoutInflater = LayoutInflater.from(this)
                val view: View = factory.inflate(R.layout.contentdialog, null);
                builder.setView(view)

                val image: ImageView = view.findViewById(R.id.content)
                val btncancel: Button = view.findViewById(R.id.cancel)

                val alertDialog = builder.create()
                alertDialog.show()

                try {
                    Glide.with(applicationContext).load(imageContent).into(image)
                }
                catch (ex: Exception){

                }

                btncancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            else{
                Toast.makeText(applicationContext, "Content Image for current book is not available!", Toast.LENGTH_LONG).show()
            }

        }

        backBtn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }

        try {
            Glide.with(applicationContext).load(fronturl).into(front)
        }
        catch (ex: Exception){

        }
        try {
            Glide.with(applicationContext).load(backurl).into(back)
        }
        catch (ex: Exception){

        }

        back.setOnClickListener {
            finish()
        }


        if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)
            var userName = shp!!.getString("name", "") as String

        var bookN = findViewById<TextView>(R.id.book)
        bookN.setText(book)

        var authorN = findViewById<TextView>(R.id.author)
        authorN.setText(author)

        var typeN = findViewById<TextView>(R.id.type)
        typeN.setText(type)

        var levelN = findViewById<TextView>(R.id.level)
        levelN.setText(level)

        var ratingN = findViewById<TextView>(R.id.rating)
        ratingN.setText(rating)

        var quantityN = findViewById<TextView>(R.id.quantity)
        quantityN.setText(status.toString())

        loadBooksInCart()
        fetchUserID()



        btnAddtoCart.setOnClickListener {
            if (shp == null){
                shp = getSharedPreferences("myPreferences", MODE_PRIVATE)
            }


            val u: String = shp!!.getString("name", "") as String

//            if (u != null && u != "") {
////                Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show()
//                if (status == "Available") {
//                    val myReq: StringRequest = object : StringRequest(Method.POST,
//                            "https://usmansorion.000webhostapp.com/insertCart.php",
//                            object : Response.Listener<String> {
//                                override fun onResponse(response: String?) {
//                                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
//                                }
//
//                            },
//                            Response.ErrorListener {
//                                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
//                            }) {
//                        @Throws(AuthFailureError::class)
//                        override fun getParams(): Map<String, String>? {
//                            val params: MutableMap<String, String> = HashMap()
//                            params["book_name"] = book.toString()
//                            params["ref_number"] = ref.toString()
//                            params["buyer"] = userName.toString()
//                            params["imageFront"] = fronturl.toString()
//                            params["userID"] = id.toString()
//                            params["book_type"] = type.toString()
//
//
////                    params["image_linkB"] = bEncoded.toString()
////                    params["image_link"] = fEncoded.toString()
////                    params["rating"] = "0"
////                    params["quantity"] = quan.text.toString().trim()
//                            return params
//                        }
//                    }
//
//
//                    var x: Int = 0
//                    while (x < cartList.size) {
//                        if (cartList[x].buyer.toString().equals(userName) && cartList[x].ref_number.toString().equals(ref.toString())) {
//                            check = false
//                            break
//                        } else {
//                            check = true
//                        }
//                        x = x + 1
//                    }
//
//                    if (!check) {
//                        Toast.makeText(applicationContext, "Book already added!", Toast.LENGTH_LONG).show()
//                    } else if (check) {
//                        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
//                        req.add(myReq)
//                    }
//
//                }
//                else{
//                    Toast.makeText(applicationContext, "Book not available at the moment", Toast.LENGTH_LONG).show()
//                }
//            } else {
//                Toast.makeText(applicationContext, "Please Log in/Sign up to borrow the books.", Toast.LENGTH_LONG).show()
//                val intent = Intent(this, Login::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
//            }

            var list: ArrayList<CartItemsModel>? = ArrayList()
            if (Paper.book().contains("cartItems")){
                list = Paper.book().read<ArrayList<CartItemsModel>>("cartItems")
                if (list!!.isNotEmpty()){
                    for (i in 0..list.size - 1){
                        if (list[i].book_name == book.toString()){
                            Toast.makeText(applicationContext, "Book Already Added In The Cart!", Toast.LENGTH_LONG).show()
                        }
                        else{
                            val cartItems: CartItemsModel = CartItemsModel(id,book.toString(),ref.toString(),userName.toString(),fronturl.toString(),type.toString())
                            list.add(cartItems)
                            Paper.book().write("cartItems", list)
                            Toast.makeText(applicationContext, "Added to the basket!", Toast.LENGTH_LONG).show()
                        }
                    }
                }

//            Paper.book().destroy()
            }
            else if (status.toString() == "Loaned"){
                Toast.makeText(applicationContext, "Book Not Available!", Toast.LENGTH_LONG).show()
            }
            else {
                val cartItems: CartItemsModel = CartItemsModel(id,book.toString(),ref.toString(),userName.toString(),fronturl.toString(),type.toString())
                list!!.add(cartItems)
                Paper.book().write("cartItems", list)
                Toast.makeText(applicationContext, "Added to the basket!", Toast.LENGTH_LONG).show()
            }




        }


    }

    private fun fetchUserID() {
        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchUserData.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)

                            if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                            var userName = shp!!.getString("name", "") as String
                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                val user = array.getJSONObject(i)

                                if (user.getString("username") == userName) {
                                    id = user.getInt("id")
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

    private fun loadBooksInCart() {
            var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                    "https://usmansorion.000webhostapp.com/fetchCartItems.php",
                    object : Response.Listener<String> {
                        override fun onResponse(response: String?) {
                            try {
                                //converting the string to json array object
                                val array = JSONArray(response)
                                cartList.clear()
                                cartBookbuyer.clear()
                                cartBookRef.clear()
                                //traversing through all the object
                                for (i in 0 until array.length()) {

                                    //getting product object from json array
                                    val books = array.getJSONObject(i)

                                    //adding the product to product list
                                    cartList.add(
                                            CartItemsModel(
                                                    books.getInt("id"),
                                                    books.getString("book_name"),
                                                    books.getString("ref_number"),
                                                    books.getString("buyer")
                                            )
                                    )
                                    cartBookRef.add(books.getString("ref_number"))
                                    cartBookbuyer.add(books.getString("buyer"))
                                }

//                                //creating adapter object and setting it to recyclerview
//                                recyclerView.adapter = booksAdapter
//                                booksAdapter.notifyDataSetChanged()

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