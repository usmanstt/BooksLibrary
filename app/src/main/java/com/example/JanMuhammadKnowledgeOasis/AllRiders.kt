package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
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

class AllRiders : AppCompatActivity(), RiderInterface {
    lateinit var add: Button
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RiderAdapter
    lateinit var riderList: ArrayList<RiderModel>
    lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_riders)

        add = findViewById(R.id.riderADD)
        recyclerView = findViewById(R.id.recyclerRiders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        backBtn = findViewById(R.id.text)

        backBtn.setOnClickListener {
            finish()
        }

        Toast.makeText(applicationContext, "Click on the item to delete!", Toast.LENGTH_LONG).show()

        riderList = ArrayList()

        adapter = RiderAdapter(riderList, this)

        add.setOnClickListener {
            startActivity(Intent(applicationContext, UploadRiders::class.java))
        }

        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchAllRiders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            riderList.clear()
                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val riders = array.getJSONObject(i)


                                //adding the product to product list
                                riderList.add(
                                        RiderModel(
                                                riders.getInt("id"),
                                                riders.getString("rider_name"),
                                                riders.getString("rider_phone"),
                                                riders.getString("rider_cnic"),
                                                riders.getString("rider_email")
                                        )
                                )

                            }

                            if (riderList.isNotEmpty()){
                                recyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()
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

    private fun fetchRiders() {
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchAllRiders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            riderList.clear()
                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val riders = array.getJSONObject(i)


                                //adding the product to product list
                                riderList.add(
                                        RiderModel(
                                                riders.getInt("id"),
                                                riders.getString("rider_name"),
                                                riders.getString("rider_phone"),
                                                riders.getString("rider_cnic"),
                                                riders.getString("rider_email")
                                        )
                                )

                            }

                            if (riderList.isNotEmpty()){
                                recyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()
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

    override fun onDeleteClicked(currentItem: RiderModel, position: Int) {
        val myReq: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/deleteRider.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        riderList.removeAt(position)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = currentItem.id.toString()
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)

    }
}