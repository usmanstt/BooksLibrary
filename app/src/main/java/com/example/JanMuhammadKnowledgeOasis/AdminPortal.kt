package com.example.JanMuhammadKnowledgeOasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdminPortal : AppCompatActivity() {
    private lateinit var btnAllbooks: Button
    private lateinit var btnAllOrders: Button
    private lateinit var btnAllRiders: Button
    private lateinit var btnAllRewards: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_portal)

        btnAllbooks = findViewById(R.id.btnAllBooks)
        btnAllOrders = findViewById(R.id.allOrders)
        btnAllRiders = findViewById(R.id.deliveryRiders)
        btnAllRewards = findViewById(R.id.rewardBtn)


        btnAllbooks.setOnClickListener {
            startActivity(Intent(applicationContext, AllBooksAdmin::class.java))
        }

        btnAllOrders.setOnClickListener {
            startActivity(Intent(applicationContext, AllOrdersAdmin::class.java))
        }
        btnAllRiders.setOnClickListener {
            startActivity(Intent(applicationContext, AllRiders::class.java))
        }
        btnAllRewards.setOnClickListener {
            startActivity(Intent(applicationContext, RewardsAdmin::class.java))
        }

    }
}