package com.example.JanMuhammadKnowledgeOasis

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class Login : AppCompatActivity() {
    private lateinit var signp: TextView
    private lateinit var adminText: TextView
    private lateinit var btnLogin: AppCompatButton
    private lateinit var username: EditText
    private lateinit var password: EditText
    var shp: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signp = findViewById(R.id.signUpText)
        btnLogin = findViewById(R.id.btnLogin)
        username = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        adminText = findViewById(R.id.adminText)

        checkifLoggedIn()

        adminText.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val factory: LayoutInflater = LayoutInflater.from(this)
            val view: View = factory.inflate(R.layout.dialogadminlogin, null);
            builder.setView(view)

            val alertDialog = builder.create()
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var username = view.findViewById<EditText>(R.id.loginUsernameAdmin)
            var password = view.findViewById<EditText>(R.id.loginPasswordAdmin)
            var btnLoginAdmin = view.findViewById<AppCompatButton>(R.id.btnLoginAdmin)

            btnLoginAdmin.setOnClickListener {
                var name = username.text.toString().trim()
                var passw = password.text.toString().trim()

                loginadmin(name,passw)
                alertDialog.dismiss()

            }

        }

        shp = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);


        btnLogin.setOnClickListener {
            var user = username.text.toString().trim()
            var pass = password.text.toString().trim()

            login(user, pass)
        }

        signp.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

    }

    private fun loginadmin(name: String, passw: String) {
        val myReq: StringRequest = object : StringRequest(Method.POST,
            "https://usmansorion.000webhostapp.com/adminlogin.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response.equals("Failed")) {
                        Toast.makeText(this@Login, response, Toast.LENGTH_LONG).show()
                    } else {
                        startActivity(Intent(applicationContext, AdminPortal::class.java))
                        finish()
                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    }
                }

            },
            Response.ErrorListener {
                Toast.makeText(this@Login, it.toString(), Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = name
                params["password"] = passw
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(this@Login)
        req.add(myReq)


    }

    private fun checkifLoggedIn() {
        if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)

        val userName: String = shp!!.getString("name", "") as String

        if (userName != null && userName != "") {
            val i = Intent(this@Login, Home::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun login(user: String, pass: String) {
        val myReq: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/login.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        if (response.equals("Failed")) {
                            Toast.makeText(this@Login, response, Toast.LENGTH_LONG).show()
                        } else {
                            startActivity(Intent(applicationContext, Home::class.java))
                            finish()
                            Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        }
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this@Login, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = user
                params["password"] = pass
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(this@Login)
        req.add(myReq)

        try {
            if (password.text.toString().trim() == pass) {
                if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)
                var shpEditor: SharedPreferences.Editor = shp!!.edit()
                shpEditor.putString("name", user)
                shpEditor.commit()
            }
            else {
                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_LONG).show()
            }
        } catch (ex: Exception) {
            password.setText(ex.message.toString())
        }

    }
}