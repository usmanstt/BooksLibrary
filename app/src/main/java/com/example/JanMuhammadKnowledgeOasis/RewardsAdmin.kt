package com.example.JanMuhammadKnowledgeOasis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

class RewardsAdmin : AppCompatActivity() {

    lateinit var recycler: RecyclerView
    lateinit var rewardAdapter: RewardAdapter
    lateinit var rewardList: ArrayList<RewardsModel>
    lateinit var backBtn: ImageView
    lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards_admin)

        recycler = findViewById(R.id.recyclerRewards)
        btnAdd = findViewById(R.id.rewardADD)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)

        rewardList = ArrayList()
        rewardAdapter = RewardAdapter(rewardList)

        backBtn = findViewById(R.id.text)

        backBtn.setOnClickListener {
            finish()
        }

        btnAdd.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val factory: LayoutInflater = LayoutInflater.from(this)
            val view: View = factory.inflate(R.layout.dialogaddreward, null);
            builder.setView(view)
            var alertDialog = builder.create()
            alertDialog.show()

            val rewardText: EditText = view.findViewById(R.id.it)
            val btnAdd: Button = view.findViewById(R.id.btnAddReward)

            btnAdd.setOnClickListener {
                val myReq: StringRequest = object : StringRequest(Method.POST,
                    "https://usmansorion.000webhostapp.com/insertRewards.php",
                    object : Response.Listener<String> {
                        override fun onResponse(response: String?) {
                            Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                            alertDialog.dismiss()
                        }

                    },
                    Response.ErrorListener {
                        Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["reward"] = rewardText.text.toString().trim()
                        return params
                    }
                }

                var req: RequestQueue = Volley.newRequestQueue(applicationContext)
                req.add(myReq)
            }

        }

        fetchAllRewards()

    }

    private fun fetchAllRewards() {
        var stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET,
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