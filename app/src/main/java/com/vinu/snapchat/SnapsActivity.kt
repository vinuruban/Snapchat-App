package com.vinu.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SnapsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        setTitle(auth.currentUser?.email)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_homepage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_snap -> {
                val intent = Intent(this, CreateSnapActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.log_out -> {
                Toast.makeText(applicationContext, "Logged out ${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()
                auth.signOut() //user signed out
                finish() //moves back to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(applicationContext, "Logged out ${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()
        auth.signOut()
    }
}