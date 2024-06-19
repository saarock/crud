package com.example.com.example.todo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.com.example.todo.auth.SignInActivity
import com.example.com.example.todo.databinding.ActivityEmployeeMainBinding
import com.example.com.example.todo.databinding.FragmentEmployeesBinding
import com.google.firebase.auth.FirebaseAuth

class EmployeeMainActivity : AppCompatActivity() {
private lateinit var binding: ActivityEmployeeMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_employee_main)
        binding = ActivityEmployeeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tbEmployee.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logOut -> {
                    showLogOutDialog()
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showLogOutDialog() {
        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()
        builder.setTitle("Log Out")
            .setMessage("Are you sure you want to logout")
            .setPositiveButton("Yes") {_, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                this.finish()
            }
            .setNegativeButton("No") {_, _ ->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

}