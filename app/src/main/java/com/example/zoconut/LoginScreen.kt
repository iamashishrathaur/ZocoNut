package com.example.zoconut

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.zoconut.Modal.User
import com.example.zoconut.databinding.ActivityLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginScreen : AppCompatActivity() {
   private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    lateinit var name:String
    lateinit var email:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences: SharedPreferences =getSharedPreferences("user", MODE_PRIVATE)
        name= sharedPreferences.getString("name","")!!
        email= sharedPreferences.getString("email","")!!

        firebaseAuth=FirebaseAuth.getInstance()
        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this@LoginScreen,googleSignInOptions)

        binding.logIn.setOnClickListener{
            val email=binding.loginEmail.text.toString()
            val password=binding.loginPassword.text.toString()
            if (email.isNotEmpty()){
                if (password.isNotEmpty()){
                    logIn(email,password)
                }
            }
        }
        binding.signUpClick.setOnClickListener{
            startActivity(Intent(this@LoginScreen,SignupScreen::class.java))
        }
        binding.googleLogin.setOnClickListener{
               signInGoogle()
        }

    }

    private fun signInGoogle() {
       val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }

    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account:GoogleSignInAccount? =task.result
            if (account !=null){
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this@LoginScreen,task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                val user= User(account.displayName, email,"")
                val myRef= FirebaseDatabase.getInstance().getReference("user")
                myRef.child(account.displayName!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()){
                            myRef.child(account.displayName!!).setValue(user).addOnSuccessListener {
                                val sharedPreferences: SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                editor.putString("name", account.displayName)
                                editor.putString("email", account.email)
                                editor.apply()
                                startActivity(Intent(this@LoginScreen,HomeScreen::class.java))
                                finish()
                            }
                        }
                        else{
                            val sharedPreferences: SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString("name", account.displayName)
                            editor.putString("email", account.email)
                            editor.apply()
                            startActivity(Intent(this@LoginScreen,HomeScreen::class.java))
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
            else{
                Toast.makeText(this@LoginScreen,it.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun logIn(email:String,password:String){
        val myRef= FirebaseDatabase.getInstance().getReference("user")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val useremail:String = snapshot.child(email).child("email").value.toString()
                    val userpassword:String = snapshot.child(email).child("password").value.toString()
                    if (useremail == email){
                        if (userpassword == password){
                            val username:String = snapshot.child(email).child("name").value.toString()
                            val sharedPreferences:SharedPreferences=getSharedPreferences("user", MODE_PRIVATE)
                            val editor:SharedPreferences.Editor=sharedPreferences.edit()
                            editor.putString("name",useremail)
                            editor.putString("email",username)
                            editor.apply()
                            startSecondPage()
                        }
                    }
                    else{
                        Toast.makeText(applicationContext,"user not exist",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun startSecondPage() {
        val intent=Intent(applicationContext,HomeScreen::class.java)
        startActivity(intent)
        finish()
    }
}