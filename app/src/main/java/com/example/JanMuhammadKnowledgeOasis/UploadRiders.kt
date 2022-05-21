package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.hmomeni.progresscircula.ProgressCircula

class UploadRiders : AppCompatActivity() {
    lateinit var riderName: EditText
    lateinit var riderCNIC: EditText
    lateinit var riderPhone: EditText
    lateinit var riderEmail: EditText
    lateinit var addBtn: Button
    lateinit var backBtn: ImageView
    private lateinit var progressCircula: ProgressCircula
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_riders)

        riderName = findViewById(R.id.riderFullName)
        riderCNIC = findViewById(R.id.riderCNIC)
        riderPhone = findViewById(R.id.riderPhone)
        riderEmail = findViewById(R.id.riderEmail)
        addBtn = findViewById(R.id.btnAdd)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, AllRiders::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
        }

        progressCircula = findViewById(R.id.progress)
        progressCircula.visibility = View.GONE

        addBtn.setOnClickListener {
            progressCircula.visibility = View.VISIBLE
            var rname = riderName.text.toString().trim()
            var remail = riderEmail.text.toString().trim()
            var rphone = riderPhone.text.toString().trim()
            var rcnic = riderCNIC.text.toString().trim()

            if (rname.isEmpty() || remail.isEmpty() || rphone.isEmpty() || rphone.isEmpty() || rcnic.isEmpty()){
                Toast.makeText(applicationContext, "Please fill all fields", Toast.LENGTH_LONG).show()
            }
            else {
                val myReq: StringRequest = object : StringRequest(Method.POST,
                        "https://usmansorion.000webhostapp.com/insertRiders.php",
                        object : Response.Listener<String> {
                            override fun onResponse(response: String?) {
                                progressCircula.visibility = View.GONE
                                Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                        }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        val params: MutableMap<String, String> = HashMap()
                        params["rider_name"] = rname
                        params["rider_email"] = remail
                        params["rider_phone"] = rphone
                        params["rider_cnic"] = rcnic
                        return params
                    }
                }

                var req: RequestQueue = Volley.newRequestQueue(applicationContext)
                req.add(myReq)
            }

        }



    }
}