package com.example.JanMuhammadKnowledgeOasis

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class Rewards : AppCompatActivity() {
    lateinit var recycler: RecyclerView
    lateinit var rewardAdapter: RewardAdapter
    lateinit var rewardList: ArrayList<RewardsModel>
    var customerOrders: Int? = null
    var shp: SharedPreferences? = null
    lateinit var orderList: ArrayList<PlacedOrdersModel>
    var userOrders: ArrayList<String> = ArrayList()
    lateinit var backBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        recycler = findViewById(R.id.recyclerRewards)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        rewardList = ArrayList()
        orderList = ArrayList()
        rewardAdapter = RewardAdapter(rewardList)

        backBtn = findViewById(R.id.text)

        backBtn.setOnClickListener {
            finish()
        }

        countCustomerOrders()


    }

    private fun countCustomerOrders() {
        var stringRequest: StringRequest = object : StringRequest(
                Request.Method.GET,
                "https://usmansorion.000webhostapp.com/countDataOrders.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        customerOrders = response.toString()!!.toInt()

                        if (customerOrders!!.toInt() > 5){
                            fetchRewards()
                        }
                        else{
                            Toast.makeText(applicationContext, "Borrow more books, to be eligible for rewards", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }

                },
                Response.ErrorListener {

                }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                var shp: SharedPreferences? = null
                if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                var userName = shp!!.getString("name", "") as String

                params["buyer"] = userName
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
                            Toast.makeText(applicationContext, "Show this to our representative, to avail the offer!", Toast.LENGTH_LONG).show()

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