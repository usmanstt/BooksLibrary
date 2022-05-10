package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
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
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.hmomeni.progresscircula.ProgressCircula
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UploadBooks : AppCompatActivity() {
    private lateinit var bNameET: EditText
    private lateinit var bRefernceNumber: EditText
    private lateinit var spinnerBookType: Spinner
    private lateinit var spinnerBookLevel: Spinner
    private lateinit var progressCircula: ProgressCircula
    private lateinit var authorET: EditText
    private lateinit var publisherET: EditText
    private lateinit var btnUpload: Button
    private lateinit var imageFUri: Uri
    private lateinit var imageBUri: Uri
    private lateinit var imageCUri: Uri
    var bitmap1: Bitmap? = null
    var bitmap2: Bitmap? = null
    var bitmap3: Bitmap? = null
    var bEncoded: String = ""
    var cEncoded: String = ""
    private lateinit var fEncoded: String
    var ref: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_books)

        bNameET = findViewById(R.id.bookName)
        bRefernceNumber = findViewById(R.id.refernceNumber)
        spinnerBookType = findViewById(R.id.bookType)
        spinnerBookLevel = findViewById(R.id.bookLevel)
        authorET = findViewById(R.id.author)
        publisherET = findViewById(R.id.publisher)
        btnUpload = findViewById(R.id.btnUpload)
        val fronCover: Button = findViewById(R.id.frontcover)
        val backCover: Button = findViewById(R.id.backcover)
        val contentCover: Button = findViewById(R.id.contentCover)
//        val submitInfo: Button = findViewById(R.id.submitBookInfo)
        fEncoded = ""

        var arrayofType: ArrayList<String> = arrayListOf("Science","Fiction/Story/Novels","Math","Language",
            "Religious","History","Information Technology","Magazines & News","School Books")

        var arrayofLevel: ArrayList<String> = arrayListOf("Primary","Middle","High","FA/FSC",
            "Graduation")

        var typeAdaper: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayofType)
        var levelAdapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayofLevel)

        spinnerBookType.adapter = typeAdaper
        spinnerBookLevel.adapter = levelAdapter

        progressCircula = findViewById(R.id.progress)
        progressCircula.visibility = View.GONE

        spinnerBookLevel.prompt = "Select Book Level"
        spinnerBookType.prompt = "Select Book Type"

        fronCover.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1000)

        }

        backCover.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1001)
        }

        contentCover.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1002)
        }

        btnUpload.setOnClickListener {

            if (bNameET.text.toString().isEmpty()){
                bNameET.setError("Please enter book name!")
            }
            else if(bRefernceNumber.text.toString().isEmpty()) {
                bRefernceNumber.setError("Please enter refernce number!")
            }
            else {

//                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//                val factory: LayoutInflater = LayoutInflater.from(this)
//                val view: View = factory.inflate(R.layout.dialogadminbookcover, null);
//                builder.setView(view)
//
//                val alertDialog = builder.create()
//                alertDialog.show()
//
//                val fronCover: Button = view.findViewById(R.id.frontcover)
//                val backCover: Button = view.findViewById(R.id.backcover)
//                val contentCover: Button = view.findViewById(R.id.contentCover)
//                val submitInfo: Button = view.findViewById(R.id.submitBookInfo)
//
//
//
//
//                fronCover.setOnClickListener {
//                    val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(openGallery, 1000)
//
//                }
//
//                backCover.setOnClickListener {
//                    val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(openGallery, 1001)
//                }
//
//                contentCover.setOnClickListener {
//                    val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(openGallery, 1002)
//                }
//                submitInfo.setOnClickListener {
//                uploadBack(bEncoded)
//                uploadFront(fEncoded)
//                    progressCircula.visibility = View.VISIBLE
//                    alertDialog.dismiss()
                    uploadInfo()
//                }
            }
        }

    }

    private fun uploadInfo() {
        ref = bRefernceNumber.text.toString().trim()
        progressCircula.visibility = View.VISIBLE
        val myReq: StringRequest = object : StringRequest(Method.POST,
                "https://usmansorion.000webhostapp.com/uploadCovers.php",
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                        progressCircula.visibility = View.GONE
                        startActivity(Intent(applicationContext, AdminPortal::class.java))
                        finish()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["book_name"] = bNameET.text.toString().trim()
                params["ref_number"] = ref
                params["book_type"] = spinnerBookType.selectedItem.toString()
                params["book_level"] = spinnerBookLevel.selectedItem.toString()
                params["author"] = authorET.text.toString().trim()
                if (publisherET.text.toString().trim().isEmpty()){
                    params["publisher"] = "Not Available"
                }
                else {
                    params["publisher"] = publisherET.text.toString().trim()
                }
                if (authorET.text.toString().trim().isEmpty()){
                    params["author"] = "Not Available"
                }
                else {
                    params["author"] = authorET.text.toString().trim()
                }
                params["image_linkB"] = bEncoded.toString()
                params["image_link"] = fEncoded.toString()
                params["image_linkC"] = cEncoded.toString()
                params["rating"] = "0"
                params["loan_status"] = "Available"
                return params
            }
        }

        var req: RequestQueue = Volley.newRequestQueue(applicationContext)
        req.add(myReq).setRetryPolicy( object : RetryPolicy{
            override fun getCurrentTimeout(): Int {
                return 5000
            }

            override fun getCurrentRetryCount(): Int {
                return 0
            }

            override fun retry(error: VolleyError?) {
                TODO("Not yet implemented")
            }

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //checking for the right activity for image
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(applicationContext, "Front Cover Fetched!",Toast.LENGTH_LONG).show()
                imageFUri = data!!.data!!
                try {
                    var inputStream: InputStream = contentResolver.openInputStream(imageFUri) as InputStream
                    bitmap1 = BitmapFactory.decodeStream(inputStream)

                    imageFDecode(bitmap1)


                }
                catch (ex: Exception){

                }
            }
        }

        if (requestCode == 1001) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(applicationContext, "Back Cover Fetched!",Toast.LENGTH_LONG).show()
                imageBUri = data!!.data!!
                try {
                    var inputStream: InputStream = contentResolver.openInputStream(imageBUri) as InputStream
                    bitmap2 = BitmapFactory.decodeStream(inputStream)


                    imageBDecode(bitmap2)

                }
                catch (ex: Exception){

                }
            }
        }

        if (requestCode == 1002) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(applicationContext, "Content Cover Fetched!",Toast.LENGTH_LONG).show()
                imageCUri = data!!.data!!
                try {
                    var inputStream: InputStream = contentResolver.openInputStream(imageCUri) as InputStream
                    bitmap3 = BitmapFactory.decodeStream(inputStream)


                    imageCDecode(bitmap3)

                }
                catch (ex: Exception){

                }
            }
        }

    }

    private fun imageCDecode(bitmap3: Bitmap?) {
        var byteOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap3!!.compress(Bitmap.CompressFormat.JPEG,60,byteOutputStream)
        var imageBytes = byteOutputStream.toByteArray()

        cEncoded = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
        progressCircula.visibility = View.GONE
    }

    private fun imageBDecode(bitmap2: Bitmap?) {
        var byteOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap2!!.compress(Bitmap.CompressFormat.JPEG,60,byteOutputStream)
        var imageBytes = byteOutputStream.toByteArray()

        bEncoded = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
        progressCircula.visibility = View.GONE

    }


    private fun imageFDecode(bitmap1: Bitmap?) {
        var byteOutputStream1: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap1!!.compress(Bitmap.CompressFormat.JPEG,60,byteOutputStream1)
        var imageBytes1 = byteOutputStream1.toByteArray()

        fEncoded = android.util.Base64.encodeToString(imageBytes1, android.util.Base64.DEFAULT)

    }


}