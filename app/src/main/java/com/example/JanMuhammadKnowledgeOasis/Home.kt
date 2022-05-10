package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class Home : AppCompatActivity() {
    private lateinit var shp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        val bottomMenu = findViewById<BottomNavigationView>(R.id.navigation)
//        val navController = findNavController(R.id.fragment)
//        bottomMenu.setupWithNavController(navController)



        shp = getSharedPreferences("myPreferences", MODE_PRIVATE)

        checkLogin()

    }

    private fun logout() {
        try {
            if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)
            var shpEditor: SharedPreferences.Editor = shp.edit()
            shpEditor.putString("name", "")
            shpEditor.commit()
            val i = Intent(this@Home, Login::class.java)
            startActivity(i)
            finish()
        } catch (ex: Exception) {
            Toast.makeText(this@Home, ex.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun checkLogin() {

        if (shp == null) shp = getSharedPreferences("myPreferences", MODE_PRIVATE)


        val userName: String = shp.getString("name", "") as String

        if (userName != null && userName != "") {
            Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show()
        } else {
            val i = Intent(this@Home, Login::class.java)
            startActivity(i)
            finish()
        }
    }
}