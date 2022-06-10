package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
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
import kotlin.collections.set

class Signup : AppCompatActivity() {

    private lateinit var loginText: TextView
    private lateinit var btnSignUp: AppCompatButton
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var fname: EditText
    private lateinit var password: EditText
    private lateinit var cnic: EditText
    private lateinit var usersList: ArrayList<UserModel>
    private lateinit var usernames: ArrayList<String>
    private lateinit var emails: ArrayList<String>
    private lateinit var phones: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        loginText = findViewById(R.id.loginText)
        btnSignUp = findViewById(R.id.btnSignup)

        username = findViewById(R.id.signupUsername)
        email = findViewById(R.id.signupEmail)
        phone = findViewById(R.id.signupPhone)
        password = findViewById(R.id.signupPassword)
        fname = findViewById(R.id.signupFullName)
        cnic = findViewById(R.id.signupCNIC)

        btnSignUp.setOnClickListener {
//            if (fname.text.isNotEmpty() && username.text.isNotEmpty() && email.text.isNotEmpty() && phone.text.isNotEmpty() && password.text.isNotEmpty()){
            insertData()
//            }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
            finish()
        }

        fetchRegisteredUsers()

    }

    private fun fetchRegisteredUsers() {
        usersList = ArrayList()
        usernames = ArrayList()
        emails = ArrayList()
        phones = ArrayList()
        var stringRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "https://usmansorion.000webhostapp.com/fetchUsers.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        try {
                            //converting the string to json array object
                            val array = JSONArray(response)
                            usersList.clear()
                            emails.clear()
                            usernames.clear()
                            phones.clear()
                            //traversing through all the object
                            for (i in 0 until array.length()) {

                                //getting product object from json array
                                val users = array.getJSONObject(i)


                                //adding the product to product list
                                usersList.add(
                                        UserModel(
                                                users.getInt("id"),
                                                users.getString("username"),
                                                users.getString("email"),
                                                users.getString("phone"),
                                                users.getString("password")
                                        )
                                )

                                emails.add(users.getString("email"))
                                usernames.add(users.getString("username"))
                                phones.add(users.getString("phone"))
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                },
                Response.ErrorListener {

                }) {

        }
        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(stringRequest)
    }

    private fun insertData() {
        var uname = username.text.toString().trim()
        var uemail = email.text.toString().trim()
        var uphone = phone.text.toString().trim()
        var upass = password.text.toString().trim()
        var fullname = fname.text.toString().trim()
        var cNIC = cnic.text.toString().trim()

        if (usernames.contains(uname) || uname.isEmpty()) {
            username.setError("Choose different username!")
        } else if (phones.contains(uphone) || uphone.isEmpty() || uphone.length < 11) {
            phone.setError("Choose different phone number!")
        } else if (emails.contains(uemail) || uemail.isEmpty()) {
            email.setError("Choose different email!")
        } else if (fullname.isEmpty()) {
            fname.setError("Enter Full Name")
        } else if (upass.isEmpty() || upass.length < 8) {
            password.setError("Password length should be greater than 8")
        } else {
//
            val intent: Intent = Intent(applicationContext, VerifyOTP::class.java)
            intent.putExtra("username", uname)
            intent.putExtra("email", uemail)
            intent.putExtra("phone", uphone)
            intent.putExtra("password", upass)
            intent.putExtra("fullname", fullname)
            intent.putExtra("CNIC", cNIC)

            startActivity(intent)
            finish()
        }

    }
}