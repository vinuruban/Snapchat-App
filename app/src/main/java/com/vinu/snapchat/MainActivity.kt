package com.vinu.snapchat

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
                    if (password?.text.toString().length < 6) {
                        Toast.makeText(
                            this,
                            "Password must be at least 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        signup()
                    }
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
                if (task.isSuccessful) { //SINCE THEY ARE SIGNED UP NOW, LOG THEM IN
                    var userID = task.result?.user?.uid
                    if (userID != null) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("email").setValue(email?.text.toString()) // REALTIME DATABASE - creates an "user" folder (if it wasn't previously created) and stores user details inside
                        login()
                    }
                } else {
                    // If log in fails
                    Toast.makeText(this, "Login failed. Try again", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

}