package com.example.JanMuhammadKnowledgeOasis

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class CheckOut : AppCompatActivity() {

    private lateinit var checkedItemsFinal: ArrayList<CartItemsModel>
    private lateinit var recyclerView: RecyclerView
    var orderNumber: Int = 0
    private lateinit var txtOrder: TextView
    private lateinit var txtaddressHeader :TextView
    private lateinit var txtAddress: TextView
    private lateinit var checkDel: AppCompatCheckBox
    private lateinit var arrayLoc: ArrayList<String>
    private lateinit var btnCheckOut: Button
    var shp: SharedPreferences? = null
    var combinedAddress = ""
    private lateinit var phone: String
    private lateinit var addressuser: String
    private lateinit var fullName: String
    lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)


        txtOrder = findViewById(R.id.numorder)
        txtaddressHeader = findViewById(R.id.addressHeader)
        txtAddress = findViewById(R.id.address)
        checkDel = findViewById(R.id.deliveryCheck)
        btnCheckOut = findViewById(R.id.btnCheckOut)
        arrayLoc = arrayListOf<String>("Jasrota", "Ara", "Barar", "Maira", "Khurd", "Borian", "Muftian", "Ranga")
        backBtn = findViewById(R.id.backBtn)

        addressuser = ""

        backBtn.setOnClickListener {
            finish()
        }


        fetchUserInfo()


        btnCheckOut.setOnClickListener {

            for (i in 0..checkedItemsFinal.size - 1){
                savedOrder(i)
//                changeStatus(i)
            }

            val myReq: StringRequest = object : StringRequest(
                    Method.POST,
                    "https://www.hajanaone.com/api/sendsms.php?apikey=UAbQCTmSZbAAMclGKcLH&phone=$phone&sender=SmartSMS&message=Your%20order%20for%20${checkedItemsFinal.size.toString()}%20books%20is%20placed%20at%20Jan%20Muhammad%20Knowledge%20Oasis.%20Your%20order%20number%20is%20${orderNumber.toString()}.",
                    object : Response.Listener<String> {
                        override fun onResponse(response: String?) {
                            Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                    }) {
            }

            var req: RequestQueue = Volley.newRequestQueue(applicationContext)
            req.add(myReq)

            startActivity(Intent(applicationContext, Home::class.java))
            finish()

        }

        checkDel.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked){
                txtaddressHeader.setText("Address")
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                val factory: LayoutInflater = LayoutInflater.from(this)
                val view: View = factory.inflate(R.layout.addadressdialog, null);
                builder.setView(view)
                var alertDialog = builder.create()

                if (txtAddress.text != addressuser){
                    alertDialog.show()
                }

                var streetAdd = view.findViewById<EditText>(R.id.streetAddress)
                var btnSubmit = view.findViewById<Button>(R.id.submitAddress)
                var clicktext = view.findViewById<TextView>(R.id.savedAddress)

                clicktext.setOnClickListener {
                    txtAddress.setText(addressuser)
                    alertDialog.dismiss()
                }

//                var adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, arrayLoc)
//                spinLoc.adapter = adapter
//                spinLoc.prompt = "Select Location"

                btnSubmit.setOnClickListener {
//                    var Loc = spinLoc.selectedItem.toString()
                    var add = streetAdd.text.toString().trim()

                    addressuser = add

                    if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                    var userName = shp!!.getString("name", "") as String

                    val myReq: StringRequest = object : StringRequest(Method.POST,
                            "https://usmansorion.000webhostapp.com/insertAddress.php",
                            object : Response.Listener<String> {
                                override fun onResponse(response: String?) {
                                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                                    txtAddress.setText(addressuser)
                                    alertDialog.dismiss()
                                }

                            },
                            Response.ErrorListener {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                                alertDialog.dismiss()
                            }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String>? {
                            val params: MutableMap<String, String> = HashMap()
                            params["address"] = addressuser
                            params["username"] = userName
                            return params
                        }
                    }

                    var req: RequestQueue = Volley.newRequestQueue(this)
                    req.add(myReq)

                }

            }
            else if (!isChecked){
                txtaddressHeader.setText("Pick Up Address")
                txtAddress.setText("Some Address")
            }

        }


        countOrders()

        checkedItemsFinal = ArrayList()

        checkedItemsFinal = intent.getSerializableExtra("itemsCheckedList") as ArrayList<CartItemsModel>



        recyclerView = findViewById(R.id.checkedRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = CheckOutAdapter(checkedItemsFinal)
        CheckOutAdapter(checkedItemsFinal).notifyDataSetChanged()

    }

    private fun changeStatus(i : Int) {
        val myReq: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/updateStatus.php",
                object : Response.Listener<String>{
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()

//                if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                var userName = shp!!.getString("name", "") as String

                params["loan_status"] = "Loaned"
                params["ref_number"] = checkedItemsFinal[i].ref_number.toString()
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)
    }


    private fun fetchUserInfo() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchUser.php",
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

                                if (user.getString("username") == userName){
                                    phone = user.getString("phone").toString().trim()
                                    addressuser = user.getString("address").toString().trim()
                                    fullName = user.getString("fullname").toString().trim()
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

    private fun countOrders() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
            "https://usmansorion.000webhostapp.com/countDataOrders.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    orderNumber = response.toString().toInt() + 1
                    txtOrder.setText(orderNumber.toString())
                }

            },
            Response.ErrorListener {

            }){

        }
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(stringRequest)
    }

//    val checkoutItems: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//
//            checkedItemsFinal = intent.getSerializableExtra("itemsCheckedList") as ArrayList<CartItemsModel>
//
//            if (checkedItemsFinal.isEmpty()){
//                Toast.makeText(applicationContext, "No data recieved", Toast.LENGTH_LONG).show()
//            }
//            else{
//                Toast.makeText(applicationContext, "data recieved", Toast.LENGTH_LONG).show()
//            }
//
//        }
//    }

    fun savedOrder(i :Int){

        var f = checkedItemsFinal[i].ref_number.toString()

        val myReq1: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/updateStatus.php",
                object : Response.Listener<String>{
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()

//                if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                var userName = shp!!.getString("name", "") as String

                params["loan_status"] = "Loaned"
                params["ref_number"] = f
                return params
            }
        }

        var req1: RequestQueue = Volley.newRequestQueue(applicationContext)
        req1.add(myReq1)


        val myReq: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/placeOrders.php",
                object : Response.Listener<String>{
                    override fun onResponse(response: String?) {
//                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        Log.d("TAG","TAG")
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()

                if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                var userName = shp!!.getString("name", "") as String

                    params["book_name"] = checkedItemsFinal[i].book_name.toString()
                    params["ref_number"] = checkedItemsFinal[i].ref_number.toString()
                    params["order_number"] = orderNumber.toString()
                    params["buyer"] = userName
                    if (txtAddress.text == addressuser){
                        params["delivery_address"] = addressuser
                        params["pick_address"] = ""
                    }
                    else if (txtAddress.text != addressuser){
                        params["delivery_address"] = ""
                        params["pick_address"] = txtAddress.text.toString()
                    }
                    params["delievery_boy"] = ""
                    params["contact_num"] = phone
                    params["expected_delDate"] = ""
                    params["buyer_fullname"] = fullName
                    params["order_status"] = "Processing"
                    params["book_type"] = checkedItemsFinal[i].book_type.toString()
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)


    }
}