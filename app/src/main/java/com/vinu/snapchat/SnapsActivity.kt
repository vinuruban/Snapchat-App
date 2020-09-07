package com.vinu.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var snapUsers: ArrayList<String> = ArrayList() //users who sent snap to you will be logged here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        setTitle(auth.currentUser?.email)

        Toast.makeText(applicationContext,       auth.currentUser?.uid!!, Toast.LENGTH_SHORT).show()

        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, snapUsers)
        snapsListView?.adapter = adapter

        /** when a snap is sent to a user, their homepage will get updated with the list of users who snapped them! **/
        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser?.uid!!).child("snaps").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("from").value as String //within the 'snaps' tab of Firebase Database, we retrieve the list of users who snapped them!
                snapUsers.add(email)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //not needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //not needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //not needed
            }

            override fun onCancelled(error: DatabaseError) {
                //not needed
            }

        })

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