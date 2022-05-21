package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Profile : AppCompatActivity(), AllOrdersInterface {
    lateinit var imageView: CircleImageView
    lateinit var pfpUri : Uri
    var bitmap1: Bitmap? = null
    private lateinit var fEncoded: String
    var userN = ""
    var shp: SharedPreferences? = null
    lateinit var n: TextView
    lateinit var nameUser: TextView
    lateinit var recyclerViewOrders: RecyclerView
    lateinit var allOrdersAdapter: AllOrdersAdapter
    lateinit var arrayOrders: ArrayList<PlacedOrdersModel>
    lateinit var recycler: RecyclerView
    lateinit var rewardAdapter: RewardAdapter
    lateinit var rewardList: ArrayList<RewardsModel>
    var customerOrders: Int? = null
    lateinit var profilebtn: ImageView
    lateinit var searchbtn: ImageView
    lateinit var basketbtn: ImageView
    lateinit var homebtn: ImageView
    lateinit var settings: ImageView
    lateinit var logout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageView = findViewById(R.id.pfp)
        n = findViewById(R.id.name)
        nameUser = findViewById(R.id.user)
        recyclerViewOrders = findViewById(R.id.reyclerborrowedbooks)
        recyclerViewOrders.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewOrders.setHasFixedSize(true)

        settings = findViewById(R.id.settings)
        homebtn = findViewById(R.id.home)
        searchbtn = findViewById(R.id.search)
        profilebtn = findViewById(R.id.profile)
        basketbtn = findViewById(R.id.basket)
        logout = findViewById(R.id.logout)

        logout.setOnClickListener {
            logout()
        }

        settings.setOnClickListener {
            val intent = Intent(applicationContext, Settings::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }

        recycler = findViewById(R.id.recyclerRewards)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.setHasFixedSize(true)

        rewardList = ArrayList()
        rewardAdapter = RewardAdapter(rewardList)

        arrayOrders = ArrayList()

        allOrdersAdapter = AllOrdersAdapter(arrayOrders, this)

        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        var Name = shp!!.getString("name", "") as String
        userN = Name

        if (userN.isEmpty()){
            logout.visibility = View.GONE
        }

        pfpUri = Uri.EMPTY
        fEncoded = ""

        homebtn = findViewById(R.id.home)
        searchbtn = findViewById(R.id.search)
        profilebtn = findViewById(R.id.profile)
        basketbtn = findViewById(R.id.basket)

        homebtn.setImageResource(R.drawable.ic_baseline_home_24)
        basketbtn.setImageResource(R.drawable.ic_baseline_shopping_cart_24)
        profilebtn.setImageResource(R.drawable.ic_twotone_person_24)

        homebtn.setOnClickListener {
            val intent = Intent(applicationContext, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }

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

        imageView.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1000)
        }

        fetchOrders()
        fetchUserInfo()
        countCustomerOrders()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //checking for the right activity for image
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(applicationContext, "Front Cover Fetched!", Toast.LENGTH_LONG).show()
                pfpUri = data!!.data!!
                try {
                    var inputStream: InputStream = contentResolver.openInputStream(pfpUri) as InputStream
                    bitmap1 = BitmapFactory.decodeStream(inputStream)

                    imageFDecode(bitmap1)


                } catch (ex: Exception) {

                }
            }
        }
    }

    private fun logout() {
        try {
            if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
            var shpEditor: SharedPreferences.Editor = shp!!.edit()
            shpEditor.putString("name", "")
            shpEditor.commit()
            val i = Intent(applicationContext, Login::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, ex.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun imageFDecode(bitmap1: Bitmap?) {
        var byteOutputStream1: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap1!!.compress(Bitmap.CompressFormat.JPEG,60,byteOutputStream1)
        var imageBytes1 = byteOutputStream1.toByteArray()

        fEncoded = android.util.Base64.encodeToString(imageBytes1, android.util.Base64.DEFAULT)

        uploadprofile(fEncoded)
    }

    private fun uploadprofile(fEncoded: String?) {
        val myReq: StringRequest = object : StringRequest(
            Method.POST,
            "https://usmansorion.000webhostapp.com/uploadprofile.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    fetchUserInfo()
                }

            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()

                params["profilepic"] = fEncoded.toString()
                params["username"] = userN

                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)
    }

    private fun fetchUserInfo() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            "https://usmansorion.000webhostapp.com/fetchUserData.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            val user = array.getJSONObject(i)

                            var profile = user.getString("profilepic")
                            var url = "https://usmansorion.000webhostapp.com/profilepic/"+profile


                            if (user.getString("username").toString() == userN){
                                n.setText(user.getString("fullname").toString())
                                nameUser.setText("@"+user.getString("username").toString())
                                Glide.with(applicationContext).load(url).into(imageView)
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

    private fun fetchOrders() {
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
                            if (orders.getString("buyer") == userN) {
                                arrayOrders.add(
                                    PlacedOrdersModel(
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

                            }
                        }
                        recyclerViewOrders.adapter = allOrdersAdapter
                        allOrdersAdapter.notifyDataSetChanged()

                        if (arrayOrders.isEmpty()){
                            val text: TextView = findViewById(R.id.bookstext)
                            text.visibility = View.VISIBLE
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

    private fun countCustomerOrders() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            "https://usmansorion.000webhostapp.com/countDataOrders.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    customerOrders = response.toString()!!.toInt()

//                    fetchRewards()
                    if (customerOrders!!.toInt() > 5){
                        fetchRewards()
                    }
                    else{
                        Toast.makeText(applicationContext, "Borrow more books, to be eligible for rewards", Toast.LENGTH_LONG).show()
                    }
                }

            },
            Response.ErrorListener {

            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                var shp: SharedPreferences? = null

                params["buyer"] = userN
                return params
            }
        }
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(stringRequest)
    }

    private fun fetchRewards() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            "https://usmansorion.000webhostapp.com/fetchRewards.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        rewardList.clear()
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val r = array.getJSONObject(i)

                            //adding the product to product list
                            rewardList.add(
                                RewardsModel(
                                    r.getInt("id"),
                                    r.getString("reward"),
                                )
                            )
                        }

                        //creating adapter object and setting it to recyclerview
                        recycler.adapter = rewardAdapter
                        rewardAdapter.notifyDataSetChanged()
                        Toast.makeText(applicationContext, "Show rewards to our representative, to avail the offer!", Toast.LENGTH_LONG).show()

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

    override fun OnItemClick(currentItem: PlacedOrdersModel, position: Int) {
        var yes = true
        var no = false
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val factory: LayoutInflater = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.dialogreturn, null);
        builder.setView(view)

        if (currentItem.order_status.toString() != "Processing" && currentItem.order_status != "Shipped" && !currentItem.order_status.equals("Returned")){
            val alertDialog = builder.create()
            alertDialog.show()

            var checkYes = view.findViewById<AppCompatCheckBox>(R.id.checkRYesItem)
            var checkNo = view.findViewById<AppCompatCheckBox>(R.id.checkRNoItem)
            var rate = view.findViewById<EditText>(R.id.rating)
            var btn = view.findViewById<AppCompatButton>(R.id.returnBtn)

            checkYes.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    checkNo.isChecked = false
                    yes = true
                }
                else if (!isChecked){
                    yes = false
                    no = true
                    checkNo.isChecked = true
                }
            }

            checkNo.setOnCheckedChangeListener{ buttonView, isChecked ->
                if (isChecked){
                    checkYes.isChecked = false
                    no = true
                }
                else if (!isChecked){
                    no = false
                    yes = true
                    checkYes.isChecked = true
                }
            }

            btn.setOnClickListener {
                if (yes == true && no == false && rate.text.toString().trim() != ""){
                    val myReq: StringRequest = object : StringRequest(Method.POST,
                        "https://usmansorion.000webhostapp.com/updateSInOrders.php",
                        object : Response.Listener<String> {
                            override fun onResponse(response: String?) {
                                Toast.makeText(applicationContext, response,Toast.LENGTH_LONG).show()
                            }
                        },
                        Response.ErrorListener {

                        }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String>? {
                            val params: MutableMap<String, String> = HashMap()
                            params["ref_number"] = currentItem.ref_number.toString()
                            params["order_status"] = "Returned"
                            return params
                        }
                    }

                    var req: RequestQueue = Volley.newRequestQueue(this)
                    req.add(myReq)
                    alertDialog.dismiss()
                    fetchOrders()
                    updateAllbooks(currentItem.ref_number.toString(), rate.text.toString().trim())
                }

            }
        }
        else{
            Toast.makeText(applicationContext, "Can't Return Undelivered or Already Returned Books!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateAllbooks(toString: String, trim: String) {
        val myReq: StringRequest = object : StringRequest(Method.POST,
            "https://usmansorion.000webhostapp.com/updateStatus.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    Toast.makeText(applicationContext, response,Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["ref_number"] = toString
                params["loan_status"] = "Available"
                params["rating"] = trim
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(this)
        req.add(myReq)
    }
}