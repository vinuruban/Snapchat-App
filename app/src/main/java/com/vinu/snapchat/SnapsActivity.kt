package com.vinu.snapchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SnapsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        setTitle("Snap Page")
    }
}