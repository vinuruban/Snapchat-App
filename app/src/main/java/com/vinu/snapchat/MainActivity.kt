package com.vinu.snapchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    var email: EditText? = null //EditText is optional and initially set to null
    var password: EditText? = null //EditText is optional and initially set to null
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        if (auth.currentUser != null) { //IF USER WAS PREVIOUSLY LOGGED IN...
            login()
        }

    }

    fun onClickButton(view: View) {
        auth.signInWithEmailAndPassword(email?.text.toString(), password?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { //LOGIN
                    login()
                } else {
                    signup()
                }

                // ...
            }
    }

    fun login() {
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }

    fun signup() {
        auth.createUserWithEmailAndPassword(email?.text.toString(), password?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { //LOGIN
                    login()
                } else {
                    // If log in fails
                    Toast.makeText(this, "Login failed. Try again", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

}