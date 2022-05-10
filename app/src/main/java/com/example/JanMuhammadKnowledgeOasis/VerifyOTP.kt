package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*


class VerifyOTP : AppCompatActivity() {
    lateinit var btnSubmitOTP: Button
    var otpCode = ""
    lateinit var etOTP: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_o_t_p)

        var number: String = intent.getStringExtra("phone").toString()

        val random = Random()
        otpCode = java.lang.String.format("%04d", random.nextInt(10000))
        var key = "UAbQCTmSZbAAMclGKcLH"

        val myReq: StringRequest = object : StringRequest(
                Method.POST,
                "https://www.hajanaone.com/api/sendsms.php?apikey=UAbQCTmSZbAAMclGKcLH&phone=$number&sender=SmartSMS&message=Your%20OTP%20For%20Registeration%20On%20Jan%20Muhammad%20Knowledge%20Oasis%20is%20$otpCode",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext, response + " You'll recieve verification code soon!", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }) {
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)


        btnSubmitOTP = findViewById(R.id.btnVerify)
        etOTP = findViewById(R.id.otpText)

        btnSubmitOTP.setOnClickListener {
            if (etOTP.text.toString().trim() == otpCode) {
                signUp()
            }
        }
    }


    private fun signUp() {
        var number: String = intent.getStringExtra("phone").toString()
        var username: String = intent.getStringExtra("username").toString()
        var fullname: String = intent.getStringExtra("fullname").toString()
        var email: String = intent.getStringExtra("email").toString()
        var password: String = intent.getStringExtra("password").toString()
        var cnic: String = intent.getStringExtra("CNIC").toString()

        val myReq: StringRequest = object : StringRequest(Method.POST,
            "https://usmansorion.000webhostapp.com/insert.php",
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    val intent: Intent = Intent(this@VerifyOTP, Login::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = username
                params["email"] = email
                params["phone"] = number
                params["password"] = password
                params["fullname"] = fullname
                params["CNIC"] = cnic
                params["profilepic"] = "none"
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)

    }


}