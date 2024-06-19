package com.example.com.example.todo.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.com.example.todo.R
import com.example.com.example.todo.databinding.AccountDialogBinding
import com.example.com.example.todo.databinding.ActivitySignUpBinding
import com.example.com.example.todo.models.User
import com.example.com.example.todo.models.Employee
import com.example.com.example.todo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var userType: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // boss or employee checked
        binding.radioGroup.setOnCheckedChangeListener { _,
                                                        checkedId ->
            userType = findViewById<RadioButton>(checkedId).text.toString()
            Log.d("TT", userType)
        }

        binding.buttonRegister.setOnClickListener {
            createUser()
        }
        binding.textView3.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createUser() {
        if (!isFinishing) {
            Utils.showDialog(this)
        }
//        com.example.com.example.todo.utils.Utils.showDialog(this)
        val name = binding.eName.text.toString()
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextNumberPassword.text.toString()
        val confrimPassword = binding.editTextNumberPassword2.text.toString()
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confrimPassword.isNotEmpty() && userType.isNotEmpty()) {
            // saved the user data at the database
            if (password == confrimPassword) {
                upload(name, email, password)
            } else {
                Utils.hideDialog()
                Utils.showToast(this, "Password are not matching")
            }
        } else {
            Utils.hideDialog()
            Utils.showToast(this, "Empty Fields are not allowed")
        }

    }

    private fun upload(name: String, email: String, password: String) {

//        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        lifecycleScope.launch {
            try {
                saveUserData(name, email, password)
            } catch (e: Exception) {
                Utils.hideDialog()
                showToast(e.message.toString())
            }
        }
    }

    private fun saveUserData(name: String, email: String, password: String) {
//        if (userType == "Boss") {
            lifecycleScope.launch {
                try {
                val db = FirebaseDatabase.getInstance().getReference("Users")

                    val fireBaseAuth =
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .await()
                    if (fireBaseAuth.user != null) {
                        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnSuccessListener {
                            val dialog = AccountDialogBinding.inflate(LayoutInflater.from(this@SignUpActivity))
                            val alertDialog = AlertDialog.Builder(this@SignUpActivity)
                                .setView(dialog.root)
                                .create()
                            alertDialog.show()
                            dialog.buttonOk.setOnClickListener {
                                alertDialog.dismiss()
                                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                                finish()
                            }
                        }

                        val uId = fireBaseAuth.user?.uid.toString()
                        val boss = User(userType= userType, empId = uId, empName = name, empEmail =  email, empPassword =  password)
                        db.child(uId).setValue(boss).await()
                    } else {
                        Utils.hideDialog()
                        Utils.showToast(this@SignUpActivity, "Signup Failed")
                    }
                } catch (e: Exception) {
                    Utils.hideDialog()
                    Utils.showToast(this@SignUpActivity,  e.message.toString())
                    print(e.message.toString())
                }

//            }

        }

    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        Utils.hideDialog() // Dismiss the dialog
        super.onDestroy()
    }
}