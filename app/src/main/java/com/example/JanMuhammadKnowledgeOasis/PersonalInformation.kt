package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PersonalInformation : AppCompatActivity() {
    var shp: SharedPreferences? = null
    lateinit var textFname: TextView
    lateinit var textUname: TextView
    lateinit var textEmail: TextView
    lateinit var textAddress: TextView
    lateinit var changeUName :TextView
    lateinit var changeUPass: TextView
    lateinit var cnic: TextView
    lateinit var imageView: ImageView
    var password = ""
    private lateinit var usernames: ArrayList<String>
    var phone = ""
    var id: Int? = null
    var uname = ""
    var user = ""
    lateinit var backBtn: ImageView
    lateinit var pfpUri : Uri
    var bitmap1: Bitmap? = null
    private lateinit var fEncoded: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)

        textFname = findViewById(R.id.fullname)
        textUname = findViewById(R.id.username)
        textEmail = findViewById(R.id.email)
        textAddress = findViewById(R.id.addressU)
        changeUName = findViewById(R.id.changeUsername)
        cnic = findViewById(R.id.CNICU)
        changeUPass = findViewById(R.id.changePassword)
        imageView = findViewById(R.id.profile)
        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
        var Name = shp!!.getString("name", "") as String
        user = Name
        
        pfpUri = Uri.EMPTY
        fEncoded = ""

        fetchRegisteredUsers()

        backBtn = findViewById(R.id.text)

        backBtn.setOnClickListener {
            finish()
        }
        
        imageView.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1000)
        }

        
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
                            params["username"] = user
                            return params
                        }
                    }

                    var req: RequestQueue = Volley.newRequestQueue(this)
                    req.add(myReq)
                }
            }

        }

        fetchUserInfo()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //checking for the right activity for image
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(applicationContext, "Front Cover Fetched!", Toast.LENGTH_LONG).show()
                pfpUri = data!!.data!!
                try {
                    var inputStream: InputStream = contentResolver.openInputStream(pfpUri) as InputStream
                    bitmap1 = BitmapFactory.decodeStream(inputStream)

                    imageFDecode(bitmap1)


                } catch (ex: Exception) {

                }
            }
        }
    }

    private fun imageFDecode(bitmap1: Bitmap?) {
        var byteOutputStream1: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap1!!.compress(Bitmap.CompressFormat.JPEG,60,byteOutputStream1)
        var imageBytes1 = byteOutputStream1.toByteArray()

        fEncoded = android.util.Base64.encodeToString(imageBytes1, android.util.Base64.DEFAULT)

        uploadprofile(fEncoded)
    }

    private fun uploadprofile(fEncoded: String?) {
        val myReq: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/uploadprofile.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        fetchUserInfo()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()

                params["profilepic"] = fEncoded.toString()
                params["username"] = user

                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq)
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

                        if (shp == null) shp = applicationContext.getSharedPreferences("myPreferences", AppCompatActivity.MODE_PRIVATE)
                        var userName = shp!!.getString("name", "") as String
                        //traversing through all the object
                        for (i in 0 until array.length()) {

                            val user = array.getJSONObject(i)

                            var profile = user.getString("profilepic")
                            var url = "https://usmansorion.000webhostapp.com/profilepic/"+profile

                            if (user.getString("username") == userName){
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
                                Glide.with(applicationContext).load(url).into(imageView)
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
}