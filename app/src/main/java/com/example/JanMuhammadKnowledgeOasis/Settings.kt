package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONException

class Settings : AppCompatActivity() {
    var shp: SharedPreferences? = null
    lateinit var textFname: TextView
    lateinit var textUname: TextView
    lateinit var textEmail: TextView
    lateinit var textAddress: TextView
    lateinit var changeUName : TextView
    lateinit var changeUPass: TextView
    lateinit var cnic: TextView
    var password = ""
    private lateinit var usernames: ArrayList<String>
    var phone = ""
    var id: Int? = null
    var uname = ""
    var userN = ""
    lateinit var backBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        textFname = findViewById(R.id.name)
        textUname = findViewById(R.id.username)
        textEmail = findViewById(R.id.email)
        textAddress = findViewById(R.id.address)
        changeUName = findViewById(R.id.changeUsername)
        cnic = findViewById(R.id.cnic)
        changeUPass = findViewById(R.id.changePassword)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, Profile::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }

        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        var Name = shp!!.getString("name", "") as String
        userN = Name

        fetchRegisteredUsers()

        fetchUserInfo()

        changeUName.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val factory: LayoutInflater = LayoutInflater.from(this)
            val view: View = factory.inflate(R.layout.dialogchangeusername, null);
            builder.setView(view)
            val alertDialog = builder.create()
            alertDialog.show()
            val usernameET = view.findViewById<EditText>(R.id.it)
            val usernameChange = view.findViewById<EditText>(R.id.newUsername)
            val submit = view.findViewById<Button>(R.id.btnChangeU)

            usernameET.setText(Name)



            submit.setOnClickListener {
                uname = usernameChange.text.toString()
                if (!usernames.contains(uname)){
                    val myReq: StringRequest = object : StringRequest(Method.POST,
                        "https://usmansorion.000webhostapp.com/updateUsername.php",
                        object : Response.Listener<String> {
                            override fun onResponse(response: String?) {
                                Toast.makeText(applicationContext, response + "Please log In again!", Toast.LENGTH_LONG).show()
                                try {
                                    if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                                    var shpEditor: SharedPreferences.Editor = shp!!.edit()
                                    shpEditor.putString("name", "")
                                    shpEditor.commit()
                                    val i = Intent(applicationContext, Login::class.java)
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(i)
                                    finish()
                                } catch (ex: Exception) {
                                    Toast.makeText(applicationContext, ex.message.toString(), Toast.LENGTH_LONG).show()
                                }
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
                            params["phone"] = phone
                            params["username"] = uname
                            return params
                        }
                    }

                    var req: RequestQueue = Volley.newRequestQueue(this)
                    req.add(myReq)

                    updateInCart()
                    updateInOrders()
                    fetchUserInfo()

                }
                else {
                    Toast.makeText(applicationContext,"Username already exists", Toast.LENGTH_LONG).show()
                }
            }


        }

        changeUPass.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val factory: LayoutInflater = LayoutInflater.from(this)
            val view: View = factory.inflate(R.layout.dialogchangepassword, null);
            builder.setView(view)

            val alertDialog = builder.create()
            alertDialog.show()

            val orgPass: EditText = view.findViewById(R.id.it)
            val newpass1: EditText = view.findViewById(R.id.newPass1)
            val newpass2: EditText = view.findViewById(R.id.newPass2)
            val change: Button = view.findViewById(R.id.btnChangeP)

            orgPass.setText(password)


            change.setOnClickListener {
                var newpassword1: String = newpass1.text.toString().trim()
                var newpassword2: String = newpass2.text.toString().trim()

                if (newpassword1.equals(newpassword2) && newpassword1.length >= 8){
                    val myReq: StringRequest = object : StringRequest(Method.POST,
                            "https://usmansorion.000webhostapp.com/updatePassword.php",
                            object : Response.Listener<String> {
                                override fun onResponse(response: String?) {
                                    Toast.makeText(applicationContext, "Password Changed, " + "Please log In again!", Toast.LENGTH_LONG).show()
                                    try {
                                        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                                        var shpEditor: SharedPreferences.Editor = shp!!.edit()
                                        shpEditor.putString("name", "")
                                        shpEditor.commit()
                                        val i = Intent(applicationContext, Login::class.java)
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(i)
                                        finish()
                                    } catch (ex: Exception) {
                                        Toast.makeText(applicationContext, ex.message.toString(), Toast.LENGTH_LONG).show()
                                    }
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
                            params["password"] = newpassword1
                            params["username"] = uname
                            return params
                        }
                    }

                    var req: RequestQueue = Volley.newRequestQueue(this)
                    req.add(myReq)
                }
            }

        }

    }
    private fun fetchRegisteredUsers() {
        usernames = ArrayList()

        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
            "https://usmansorion.000webhostapp.com/fetchUsers.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        //converting the string to json array object
                        val array = JSONArray(response)
                        usernames.clear()

                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            //getting product object from json array
                            val users = array.getJSONObject(i)


                            //adding the product to product lis
                            usernames.add(users.getString("username"))

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


    private fun updateInOrders() {
        val myReq: StringRequest = object : StringRequest(Method.POST,
            "https://usmansorion.000webhostapp.com/updateUDataOrders.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {

                }

            },
            Response.ErrorListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["contact_num"] = phone
                params["buyer"] = uname
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(this)
        req.add(myReq)
    }

    private fun updateInCart() {
        val myReq: StringRequest = object : StringRequest(Method.POST,
            "https://usmansorion.000webhostapp.com/updateUDataCart.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {

                }

            },
            Response.ErrorListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()

            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["userID"] = id.toString()
                params["username"] = uname
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(this)
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


                            if (user.getString("username") == userN){
                                textFname.setText(user.getString("fullname"))
                                textUname.setText(user.getString("username"))
                                textEmail.setText(user.getString("email"))
                                if (user.getString("address").equals("null")){
                                    textAddress.setText("Address Not Available Yet!")
                                }
                                else{
                                    textAddress.setText(user.getString("address"))
                                }
                                cnic.setText(user.getString("CNIC"))

                                phone = user.getString("phone")
                                password = user.getString("password")
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

}