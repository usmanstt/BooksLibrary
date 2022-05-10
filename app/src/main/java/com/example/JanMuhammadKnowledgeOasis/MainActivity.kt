package com.example.JanMuhammadKnowledgeOasis

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.hmomeni.progresscircula.ProgressCircula

class MainActivity : AppCompatActivity() {
    lateinit var progressCircula: ProgressCircula
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressCircula = findViewById(R.id.progressBarC)

        checkForInternet(this)

    }

    private fun checkForInternet(mainActivity: MainActivity) {

        val ConnectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = ConnectionManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected == true) {
            Handler().postDelayed({
//                logozoomAnim()
                progressCircula.visibility = View.VISIBLE
                var intent = Intent(this, Home::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()

            }, 3000)




        } else {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle("No Internet Connection")
            //set message for alert dialog
            builder.setMessage("Please Connect to Internet")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                finish();
                System.exit(0);
            })
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()

        }
    }

    private fun logozoomAnim() {

//        val buttonZoomIn: Button = findViewById(R.id.zoomIn)

//        buttonZoomIn.setOnClickListener(View.OnClickListener {
        val text: TextView = findViewById(R.id.app_title)
        val animZoomIn = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.splashscreen_zoomin
        )
        // assigning that animation to
        // the image and start animation
        text.startAnimation(animZoomIn)

        var intent = Intent(this, Home::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
//        })


    }
}