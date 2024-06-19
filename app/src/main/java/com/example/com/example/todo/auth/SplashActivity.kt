package com.example.com.example.todo.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.com.example.todo.BossMainActivity
import com.example.com.example.todo.EmployeeMainActivity
import com.example.com.example.todo.R
import com.example.com.example.todo.models.User
import com.example.com.example.todo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        Handler (Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser?.uid;
            if (currentUser != null) {
                lifecycleScope.launch {
                    try {
                            FirebaseDatabase.getInstance().getReference("Users").child(currentUser).addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val currentUserData = snapshot.getValue(User::class.java)
                                        Utils.showToast(this@SplashActivity, currentUserData?.userType.toString())

                                        when (currentUserData?.userType) {
                                            "Boss" -> {
                                                startActivity(Intent(this@SplashActivity, BossMainActivity::class.java))
                                                finish()
                                            }
                                            "Employee" -> {
                                                startActivity(Intent(this@SplashActivity,  EmployeeMainActivity::class.java))
                                                finish()
                                            } else -> {
                                            startActivity(Intent(this@SplashActivity,  EmployeeMainActivity::class.java))
                                            finish()
                                        }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Utils.hideDialog()
                                        Utils.showToast(this@SplashActivity, error.message)
                                    }
                                })
                    } catch (error: Exception) {
                        Utils.showToast(this@SplashActivity, error.message.toString())
                    }
                }
            } else {
                startActivity(Intent(this, SignInActivity::class.java))
            }
//            startActivity(Intent(this, BossMainActivity::class.java))
//            finish()
        }, 3000)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}