package com.example.instaclonkotlinfb.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.instaclonkotlinfb.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = Firebase.auth

        // auth dan önce yazarsan app çöker
        var currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this@MainActivity , FeedActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    fun signinClicked( view : View){
        val email = binding.emailText.text.toString()
        val  password = binding.passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                val intent = Intent(this@MainActivity , FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(this@MainActivity , "False email or password", Toast.LENGTH_LONG).show()
        }


    }
    fun signupClicked( view : View) {
        val email = binding.emailText.text.toString()
        val  password = binding.passwordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){ // email and passwor boş değilse
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                // fb den veri çekmerken success (başarılı olduğunda)
                val intent = Intent(this@MainActivity , FeedActivity::class.java )
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                // fail olduğunca
                Toast.makeText(this@MainActivity , it.localizedMessage , Toast.LENGTH_LONG).show()
            }

        }else{ // email veya password boşsa Toast mesajı ile kullanıcıya bildir
            Toast.makeText(this , "Enter email and password please", Toast.LENGTH_LONG).show()
        }


    }


}