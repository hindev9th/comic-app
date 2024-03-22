package com.cm.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cm.app.R
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.cm.app.fragments.HomeFragment
import com.cm.app.utils.Constants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        getDomain()
    }

    fun getDomain(): Boolean {
        db.collection("constants")
            .document("vZp2YVkLM1yPG3SUjepu")
            .get()
            .addOnSuccessListener { result ->
                if (Constants.BASE_COMIC_URL != result.get("domain").toString()) {
                    Constants.BASE_COMIC_URL = result.get("domain").toString()
                }
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@SplashActivity, "Can't connect server!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        return true
    }
}