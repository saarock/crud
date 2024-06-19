package com.example.com.example.todo.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.com.example.todo.BossMainActivity
import com.example.com.example.todo.EmployeeMainActivity
import com.example.com.example.todo.R
import com.example.com.example.todo.databinding.ActivitySignInBinding
import com.example.com.example.todo.databinding.ForgetPasswordDialogBinding
import com.example.com.example.todo.databinding.FragmentEmployeesBinding
import com.example.com.example.todo.models.User
import com.example.com.example.todo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Utils.hideDialog()
                Utils.showToast(this@SignInActivity, "UserEmail and Password requried!")
            }

        }

        binding.textView7.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }

        binding.tvForgetPassword.setOnClickListener {
            showForgetPasswordDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun loginUser(email:String, password: String) {
        Utils.showDialog(this)
        val fireBaseAuth = FirebaseAuth.getInstance()
        lifecycleScope.launch {
            try {
                val authResult = fireBaseAuth.signInWithEmailAndPassword(email, password).await()
                val currentUser = authResult.user?.uid
                val verifyEmail = fireBaseAuth.currentUser?.isEmailVerified
                if (verifyEmail == true) {
                    if (currentUser != null) {
                        FirebaseDatabase.getInstance().getReference("Users").child(currentUser).addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val currentUserData = snapshot.getValue(User::class.java)
                                    when (currentUserData?.userType) {
                                        "Boss" -> {
                                            startActivity(Intent(this@SignInActivity, BossMainActivity::class.java))
                                            finish()
                                        }
                                        "Employee" -> {
                                            startActivity(Intent(this@SignInActivity,  EmployeeMainActivity::class.java))
                                            finish()
                                        } else -> {
                                        startActivity(Intent(this@SignInActivity,  EmployeeMainActivity::class.java))
                                        finish()
                                    }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Utils.hideDialog()
                                    Utils.showToast(this@SignInActivity, error.message)
                                }
                            })
                    } else {
                        Utils.hideDialog()
                        Utils.showToast(this@SignInActivity, "User Not Found Pleased Register to login")
                    }
                } else {
                    Utils.hideDialog()
                    Utils.showToast(this@SignInActivity, "Email is not verified")
                }

            } catch (e: Exception) {
                Utils.hideDialog()
                Utils.showToast(this@SignInActivity, e.message.toString())
            }
        }
    }

    private fun showForgetPasswordDialog() {
        val dialog = ForgetPasswordDialogBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialog.root)
            .create()
        alertDialog.show()
        dialog.forgetEmail.requestFocus()
        dialog.forgetPasswordBtn.setOnClickListener {
            val email = dialog.forgetEmail.text.toString();
            alertDialog.dismiss()
            resetPassword(email)
        }

    }

    private fun resetPassword(email: String) {
        lifecycleScope.launch {
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                Utils.showToast(this@SignInActivity, "Pleased checkout email and reset your password")
            } catch (e: Exception) {
                Utils.showToast(this@SignInActivity, e.message.toString())
            }
        }
    }
}