package com.example.zoconut

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zoconut.Modal.User
import com.example.zoconut.databinding.ActivitySignupScreenBinding
import com.google.firebase.database.FirebaseDatabase

class SignupScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivitySignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signUp.setOnClickListener{
            val name:String=binding.userName.text.toString()
            val email:String=binding.userEmail.text.toString()
            val password:String=binding.userPassword.text.toString()
            val confirmPass:String=binding.userConfirmPassword.text.toString()
            if (name.isNotEmpty()) {

                if (email.isNotEmpty()) {
                    if (password.isNotEmpty()) {
                        if (confirmPass.isNotEmpty()) {
                            if (password == confirmPass) signUp(name,email, password) else Toast.makeText(this, "wrong confirm password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        binding.logInClick.setOnClickListener{
            startActivity(Intent(this,LoginScreen::class.java))
        }

    }
    private fun signUp(name:String,email: String, pass: String) {
        val myRef= FirebaseDatabase.getInstance().getReference("user")
        val user= User(name, email,pass)

        myRef.setValue(user).addOnSuccessListener {
            val sharedPreferences: SharedPreferences =getSharedPreferences("user", MODE_PRIVATE)
            val editor: SharedPreferences.Editor=sharedPreferences.edit()
            editor.putString("name",name)
            editor.putString("email",email)
            editor.apply()
            startActivity(Intent(this,HomeScreen::class.java))
            finish()
        }
    }
}

