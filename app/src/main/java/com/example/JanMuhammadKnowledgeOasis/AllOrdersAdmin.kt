package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AllOrdersAdmin : AppCompatActivity(), AllOrdersInterfaceAdmin {
    lateinit var recyclerView: RecyclerView
    lateinit var allOrdersAdapter: AllOrdersAdminAdapter
    lateinit var allOrdersAdapterP: AllOrdersAdminAdapter
    lateinit var allOrdersAdapterS: AllOrdersAdminAdapter
    lateinit var allOrdersAdapterD: AllOrdersAdminAdapter
    lateinit var allOrdersAdapterR: AllOrdersAdminAdapter
    lateinit var arrayOrders: ArrayList<PlacedOrdersModel>
    lateinit var arrayOrdersP: ArrayList<PlacedOrdersModel>
    lateinit var arrayOrdersS: ArrayList<PlacedOrdersModel>
    lateinit var arrayOrdersD: ArrayList<PlacedOrdersModel>
    lateinit var arrayOrdersR: ArrayList<PlacedOrdersModel>
    var shp: SharedPreferences? = null
    lateinit var arrayspinRid: ArrayList<String>
    lateinit var ridIDs: ArrayList<String>
    lateinit var spinner: Spinner
    lateinit var backBtn: ImageView
    var arrayStatus: ArrayList<String> = arrayListOf("All", "Processing", "Shipped", "Delivered", "Returned")
    var arrayStatus1: ArrayList<String> = arrayListOf("Processing", "Shipped", "Delivered", "Returned")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders_admin)

        recyclerView = findViewById(R.id.recyclerOrdersAdmin)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        backBtn = findViewById(R.id.backBtn)
        spinner = findViewById(R.id.spinnerStatusAdmin)

        var arrayAdapter: ArrayAdapter<String> = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, arrayStatus)
        spinner.adapter = arrayAdapter

        spinner.prompt = "Order Status"

        arrayOrders = ArrayList()
        arrayOrdersP = ArrayList()
        arrayOrdersS = ArrayList()
        arrayOrdersD = ArrayList()
        arrayOrdersR = ArrayList()

        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        var userName = shp!!.getString("name", "") as String


        allOrdersAdapter = AllOrdersAdminAdapter(arrayOrders, this)
        allOrdersAdapterP = AllOrdersAdminAdapter(arrayOrdersP, this)
        allOrdersAdapterS = AllOrdersAdminAdapter(arrayOrdersS, this)
        allOrdersAdapterD = AllOrdersAdminAdapter(arrayOrdersD, this)
        allOrdersAdapterR = AllOrdersAdminAdapter(arrayOrdersR, this)

        spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent!!.getItemAtPosition(position).toString() == "All"){
                    fetchOrders()
                }
                else if (parent!!.getItemAtPosition(position).toString() == "Processing"){
                    fetchOrdersP()
                }
                else if (parent!!.getItemAtPosition(position).toString() == "Shipped"){
                    fetchOrdersS()
                }
                else if (parent!!.getItemAtPosition(position).toString() == "Delivered"){
                    fetchOrdersD()
                }
                else if (parent!!.getItemAtPosition(position).toString() == "Returned"){
                    fetchOrdersR()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, AdminPortal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }


    }

    private fun fetchOrdersR() {
        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchAllOrders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            arrayOrdersR.clear()



                            //traversing through all the object

                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val orders = array.getJSONObject(i)

                                if (orders.getString("order_status") == "Returned") {

                                    //adding the product to product list
                                    arrayOrdersR.add(
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

                            recyclerView.adapter = allOrdersAdapterR
                            allOrdersAdapterR.notifyDataSetChanged()
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin)
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin).notifyDataSetChanged()

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

    private fun fetchOrdersD() {
        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchAllOrders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            arrayOrdersD.clear()


//                            if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                            var userName = shp!!.getString("name", "") as String
                            //traversing through all the object

                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val orders = array.getJSONObject(i)

                                if (orders.getString("order_status") == "Delivered") {

                                    //adding the product to product list
                                    arrayOrdersD.add(
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

                            recyclerView.adapter = allOrdersAdapterD
                            allOrdersAdapterD.notifyDataSetChanged()
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin)
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin).notifyDataSetChanged()

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

    private fun fetchOrdersS() {
        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchAllOrders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            arrayOrdersS.clear()


//                            if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                            var userName = shp!!.getString("name", "") as String
                            //traversing through all the object

                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val orders = array.getJSONObject(i)

                                if (orders.getString("order_status") == "Shipped") {

                                    //adding the product to product list
                                    arrayOrdersS.add(
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

                            recyclerView.adapter = allOrdersAdapterS
                            allOrdersAdapterS.notifyDataSetChanged()
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin)
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin).notifyDataSetChanged()

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

    private fun fetchOrdersP() {
        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchAllOrders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            arrayOrdersP.clear()


//                            if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                            var userName = shp!!.getString("name", "") as String
                            //traversing through all the object

                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val orders = array.getJSONObject(i)

                                if (orders.getString("order_status") == "Processing") {

                                    //adding the product to product list
                                    arrayOrdersP.add(
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

                            recyclerView.adapter = allOrdersAdapterP
                            allOrdersAdapterP.notifyDataSetChanged()
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin)
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin).notifyDataSetChanged()

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


//                            if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
//                            var userName = shp!!.getString("name", "") as String
                            //traversing through all the object

                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val orders = array.getJSONObject(i)

                                //adding the product to product list
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

                                recyclerView.adapter = allOrdersAdapter
                                allOrdersAdapter.notifyDataSetChanged()
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin)
//                                    AllOrdersAdminAdapter(arrayOrdersP,this@AllOrdersAdmin).notifyDataSetChanged()
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

    override fun OnItemClick(currentItem: PlacedOrdersModel, position: Int) {
        var ordernum = currentItem.order_number
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val factory: LayoutInflater = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.dialogadminchooser, null);
        builder.setView(view)

        val alertDialog = builder.create()
        alertDialog.show()

        var btnstat = view.findViewById<Button>(R.id.statusdialog)
        var btnRide = view.findViewById<Button>(R.id.riderDialog)

        btnRide.setOnClickListener {
            arrayspinRid = ArrayList()
            ridIDs = ArrayList()

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val factory: LayoutInflater = LayoutInflater.from(this)
            val view1: View = factory.inflate(R.layout.dialogassignrider, null);
            builder.setView(view1)

            val alertDialog1 = builder.create()
            alertDialog1.show()

            var spinnerRid: Spinner = view1.findViewById(R.id.it)
            var editID: TextView = view1.findViewById(R.id.newPass1)
            var btnsub = view1.findViewById<Button>(R.id.btnChangeP)

            var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                    "https://usmansorion.000webhostapp.com/fetchAllRiders.php",
                    object : Response.Listener<String> {
                        override fun onResponse(response: String?) {
                            try {
                                //converting the string to json array object
                                val array = JSONArray(response)
                                arrayspinRid.clear()
                                ridIDs.clear()
                                //traversing through all the object
                                for (i in 0 until array.length()) {

                                    //getting product object from json array
                                    val riders = array.getJSONObject(i)


                                    //adding the product to product list
                                    arrayspinRid.add((riders.getString("rider_name")) + " (" + riders.getString("rider_phone") + ")")
                                    ridIDs.add(riders.getInt("id").toString())

                                }


                                if (arrayspinRid.isNotEmpty()) {
                                    var arrayAdapter = ArrayAdapter(this@AllOrdersAdmin, android.R.layout.simple_spinner_dropdown_item, arrayspinRid)
                                    spinnerRid.adapter = arrayAdapter
                                }


                                spinnerRid.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        editID.setText(ridIDs[position].toString())
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
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

            btnsub.setOnClickListener {
                var rider = spinnerRid.selectedItem.toString()
                val myReq2: StringRequest = object : StringRequest(Method.POST,
                        "https://usmansorion.000webhostapp.com/updateRider.php",
                        object : Response.Listener<String> {
                            override fun onResponse(response: String?) {
                                val myReq: StringRequest = object : StringRequest(
                                        Method.POST,
                                        "https://www.hajanaone.com/api/sendsms.php?apikey=UAbQCTmSZbAAMclGKcLH&phone=${currentItem.contact_num.toString()}&sender=SmartSMS&message=Your%20Order%20Number%20$ordernum%20has%20been%20assigned%20to%20$rider%20.Your%20order%20will%20reach%20you%soon.Message%20from%20Jan%20Muhammad%20Knowledge%20Oasis.",
                                        object : Response.Listener<String> {
                                            override fun onResponse(response: String?) {
                                                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                                                alertDialog1.dismiss()
                                            }
                                        },
                                        Response.ErrorListener {
                                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                                        }) {
                                }

                                var req3: RequestQueue = Volley.newRequestQueue(applicationContext)
                                req3.add(myReq)
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                        }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["id"] = currentItem.id.toString()
                        params["delievery_boy"] = editID.text.toString() + " - " + spinnerRid.selectedItem.toString()
                        val c: Calendar = Calendar.getInstance()

                        // manipulate date

                        // manipulate date
                        var year: String = Calendar.YEAR.toString()
                        var month: String = Calendar.MONTH.toString()
                        var day: String = c.add(Calendar.DATE, 2).toString() //same with c.add(Calendar.DAY_OF_MONTH, 1);

                        params["expected_delDate"] = day + " " + month + " " + year
                        return params
                    }
                }

                var req1: RequestQueue = Volley.newRequestQueue(applicationContext)
                req1.add(myReq2)
            }



        }

        btnstat.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val factory: LayoutInflater = LayoutInflater.from(this)
            val view2: View = factory.inflate(R.layout.dialogassignstatus, null);
            builder.setView(view2)

            val alertDialog2 = builder.create()
            alertDialog2.show()

            var spinnerS: Spinner = view2.findViewById(R.id.it)
            var editID: TextView = view2.findViewById(R.id.newPass1)
            var btnsub = view2.findViewById<Button>(R.id.btnChangeS)

            var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayStatus1)
            spinnerS.adapter = arrayAdapter
            spinnerS.prompt = "Status"

            editID.setText(currentItem.ref_number.toString())

            btnsub.setOnClickListener {
                val myReq2: StringRequest = object : StringRequest(Method.POST,
                        "https://usmansorion.000webhostapp.com/updateStatusAdmin.php",
                        object : Response.Listener<String> {
                            override fun onResponse(response: String?) {
                                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                        }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["ref_number"] = currentItem.ref_number.toString()
                        if (spinnerS.selectedItem.toString() == "Returned"){
                            params["loan_status"] = "Available"
                        }
                        else{
                            params["loan_status"] = "Loaned"
                        }
                        return params
                    }
                }

                var req1: RequestQueue = Volley.newRequestQueue(applicationContext)
                req1.add(myReq2)

                val myReq3: StringRequest = object : StringRequest(Method.POST,
                        "https://usmansorion.000webhostapp.com/updateOrdersSAdmin.php",
                        object : Response.Listener<String> {
                            override fun onResponse(response: String?) {
                                Toast.makeText(applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                        }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["ref_number"] = currentItem.ref_number.toString()
                        params["order_status"] = spinnerS.selectedItem.toString()
                        return params
                    }
                }

                var req2: RequestQueue = Volley.newRequestQueue(applicationContext)
                req2.add(myReq3)


            }


        }

    }

}