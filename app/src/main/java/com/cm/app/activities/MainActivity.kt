package com.cm.app.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cm.app.R
import com.cm.app.fragments.FavoriteFragment
import com.cm.app.fragments.HistoryFragment
import com.cm.app.fragments.HomeFragment
import com.cm.app.fragments.SearchFragment
import com.cm.app.fragments.SettingFragment
import com.cm.app.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var frameLayout: FrameLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private var currentFragment: Fragment? = null
    private val db = Firebase.firestore
    private lateinit var fragmentManager : FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager
        frameLayout = findViewById(R.id.progressBar)
        bottomNavigation = findViewById(R.id.layoutBottomBar)
        bottomNavigation.visibility = View.GONE
        this.listeners()
        getDomain()
//        setDomain()
    }

//    fun setDomain(){
//        val user = hashMapOf(
//            "domain" to "https://www.nettruyenss.com/",
//            "name" to "Net Truyện",
//        )
//
//// Add a new document with a generated ID
//        db.collection("constants")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d("Database","ok");
//            }
//            .addOnFailureListener { e ->
//                Log.d("Database","loi te le roi");
//            }
//    }

    fun getDomain(): Boolean {
        db.collection("constants")
            .document("vZp2YVkLM1yPG3SUjepu")
            .get()
            .addOnSuccessListener { result ->
                if (Constants.BASE_COMIC_URL != result.get("domain").toString()) {
                    Constants.BASE_COMIC_URL = result.get("domain").toString()
                }
                bottomNavigation.visibility = View.VISIBLE
                initFragment(HomeFragment())
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@MainActivity, "Can't connect server!", Toast.LENGTH_LONG).show()
                bottomNavigation.visibility = View.VISIBLE
                initFragment(HomeFragment())
            }
        return true
    }

    @SuppressLint("ResourceType")
    private fun listeners() {
        bottomNavigation.itemIconTintList =
            ContextCompat.getColorStateList(this, R.drawable.bottom_bar_color)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (currentFragment !is HomeFragment) {
                        replaceFragment(HomeFragment(), R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                    true
                }

                R.id.navigation_history -> {
                    if (currentFragment !is HistoryFragment) {
                        if (currentFragment is SearchFragment || currentFragment is FavoriteFragment){
                            replaceFragment(HistoryFragment(), R.anim.slide_in_left, R.anim.slide_out_right)
                        }else{
                            replaceFragment(HistoryFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                    true
                }

                R.id.navigation_search -> {
                    if (currentFragment !is SearchFragment) {
                        if (currentFragment is FavoriteFragment){
                            replaceFragment(SearchFragment(), R.anim.slide_in_left, R.anim.slide_out_right)
                        }else{
                            replaceFragment(SearchFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                    true
                }

                R.id.navigation_favorite -> {
                    if (currentFragment !is FavoriteFragment) {
                        if (currentFragment is SettingFragment){
                            replaceFragment(FavoriteFragment(), R.anim.slide_in_left, R.anim.slide_out_right)
                        }else{
                            replaceFragment(FavoriteFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                    true
                }

                R.id.navigation_setting -> {
                    if (currentFragment !is SettingFragment) {
                        replaceFragment(SettingFragment(), R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, enterAnim: Int, exitAnim: Int) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(enterAnim, exitAnim)
        // Replace the fragment
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Add this line if you want to support back navigation
        transaction.commit()

        // Update the currentFragment reference
        currentFragment = fragment
    }
    private fun initFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
        // Update the currentFragment reference
        currentFragment = fragment
    }

    fun hideAndShowProgressBar(view: Int) {
        this.frameLayout.visibility = view
    }
}