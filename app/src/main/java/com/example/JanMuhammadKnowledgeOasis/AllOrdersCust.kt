package com.example.JanMuhammadKnowledgeOasis

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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

class AllOrdersCust : AppCompatActivity(), AllOrdersInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var allOrdersAdapter: AllOrdersAdapter
    lateinit var arrayOrders: ArrayList<PlacedOrdersModel>
    var shp: SharedPreferences? = null
    lateinit var backBtn: ImageView
    var userName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_orders_cust)

        recyclerView = findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        backBtn = findViewById(R.id.text)

        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        userName = shp!!.getString("name", "") as String

        arrayOrders = ArrayList()

        allOrdersAdapter = AllOrdersAdapter(arrayOrders, this)

        fetchOrders()

        backBtn.setOnClickListener {
            finish()
        }

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
                            if (orders.getString("buyer") == userName) {
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
                        recyclerView.adapter = allOrdersAdapter
                        allOrdersAdapter.notifyDataSetChanged()

                        if (arrayOrders.isEmpty()){
                            finish()
                            Toast.makeText(applicationContext, "You have No Orders placed yet!",Toast.LENGTH_LONG).show()
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
            var btn = view.findViewById<Button>(R.id.returnBtn)

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