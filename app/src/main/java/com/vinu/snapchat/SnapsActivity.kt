package com.vinu.snapchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


class SnapsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var snapUsers: ArrayList<String> = ArrayList() //users who sent snap to you will be logged here
    var snaps: ArrayList<DataSnapshot> = ArrayList() //to retrieve data from the Firebase Database of the snap that's clicked on


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        setTitle(auth.currentUser?.email)

        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, snapUsers)
        snapsListView?.adapter = adapter

        /** when a snap is sent to a user, their homepage will get updated with the list of users who snapped them! **/
        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser?.uid!!).child(
            "snaps"
        ).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email =
                    snapshot.child("from").value as String //within the 'snaps' tab of Firebase Database, we retrieve the list of users who snapped them!
                snapUsers.add(email)
                snaps.add(snapshot) //to store data of the snap that was clicked on, from the Firebase Database
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

        /** When clicking on the new snap that was sent to you**/
        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot = snaps.get(position) //get data of the snap that was clicked on

            var intent = Intent(this, OpenSnapActivity::class.java)

            intent.putExtra("imageUrl", snapshot.child("imageUrl").value as String)
            intent.putExtra("uniqueImageName", snapshot.child("imageName").value as String)
            intent.putExtra("caption", snapshot.child("caption").value as String)
            intent.putExtra("snapUUID", snapshot.key) //to delete the snap after being viewed

            startActivity(intent)
        }

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
                Toast.makeText(
                    applicationContext,
                    "Logged out ${auth.currentUser?.email}",
                    Toast.LENGTH_SHORT
                ).show()
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
        Toast.makeText(
            applicationContext,
            "Logged out ${auth.currentUser?.email}",
            Toast.LENGTH_SHORT
        ).show()
        auth.signOut()
    }
}