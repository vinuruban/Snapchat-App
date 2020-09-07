package com.vinu.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseUserActivity : AppCompatActivity() {

    var listView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var keys: ArrayList<String> = ArrayList() //the uuid of users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        setTitle("Send to...")

        listView = findViewById(R.id.listView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        listView?.adapter = adapter

        /** To get data from Firebase Database and adds them to the 'emails' ArrayList **/
        /** THIS IS CALLED WHEN THERE IS A CHANGE TO DATA IN THE DATABASE OF USERS (WHEN DATA IS ADDED, REMOVED...) **/
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").value as String
                emails.add(email)
                keys.add(snapshot.key.toString()) //adds the uuid of the user for the 'onItemClickListener' below
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

        /** When a user is clicked on **/
        listView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            //create Map objects of type String - easy to pass a set of data
            val snapMap: Map<String, String> = mapOf(
                "from" to FirebaseAuth.getInstance().currentUser!!.email!!, // the '!!' confirms its definitely not null!
                "imageName" to intent.getStringExtra("uniqueImageName"),
                "imageUrl" to intent.getStringExtra("imageUrl"),
                "caption" to intent.getStringExtra("caption")
                )

            //this adds the snap in Firebase DB > 'users' tab > user that was clicked on > 'snaps' tab > push() creates a tab for the snap with a UUID
            FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap)

            Toast.makeText(applicationContext, "Snap sent!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //remove back button history!
            startActivity(intent)
        }
    }
}